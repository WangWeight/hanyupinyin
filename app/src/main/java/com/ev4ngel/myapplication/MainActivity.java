package com.ev4ngel.myapplication;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.Polygon;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

import dji.sdk.Battery.DJIBattery;
import dji.sdk.Camera.DJICamera;
import dji.sdk.Camera.DJICameraParameters;
import dji.sdk.Camera.DJICameraSettingsDef;
import dji.sdk.Camera.DJIMedia;
import dji.sdk.FlightController.DJIFlightController;
import dji.sdk.FlightController.DJIFlightControllerDataType;
import dji.sdk.FlightController.DJIFlightControllerDelegate;
import dji.sdk.Gimbal.DJIGimbal;
import dji.sdk.MissionManager.DJICustomMission;
import dji.sdk.MissionManager.DJIMission;
import dji.sdk.MissionManager.DJIMissionManager;
import dji.sdk.MissionManager.MissionStep.DJIAircraftYawStep;
import dji.sdk.MissionManager.MissionStep.DJIFollowmeMissionStep;
import dji.sdk.MissionManager.MissionStep.DJIGimbalAttitudeStep;
import dji.sdk.MissionManager.MissionStep.DJIGoToStep;
import dji.sdk.MissionManager.MissionStep.DJIMissionStep;
import dji.sdk.SDKManager.DJISDKManager;
import dji.sdk.base.DJIBaseComponent;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.base.DJIError;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Toolbar.OnMenuItemClickListener,
        DJIFlightControllerDelegate.FlightControllerUpdateSystemStateCallback,
        View.OnClickListener,
        View.OnLongClickListener,
        SeekBar.OnSeekBarChangeListener {


    private DJIGimbal gimbal = null;
    private DJICamera camera = null;
    private DJIBattery battery = null;
    private DJIFlightController flightController = null;
    private DJIMissionManager mMissonManager = null;
    private DJICustomMission mMission = null;

    Project mProject=null;
    //views
    private ArrayList<ArrayList<DJIFlightControllerDataType.DJILocationCoordinate3D>> missionLocations;
    private Toolbar toolbar;
    private View projectPage;//project页面
    private View airlinePage;//航线页面
    private View settingPage;//设置页面
    private View camOrMapPage;//相机/地图页面
    private View mapPage;
    private TextView gpsCountView;//gps数量显示
    private TextView batteryRemainView;//电池电量显示
    private TextView batteryVolView;//
    private TextView isConnectedView;
    private TextView velX;
    private TextView line_space_view;
    private TextView velY;
    private TextView velZ;
    private TextView posLat;//
    private TextView posLng;
    private TextView posHeight;//
    private TextView attitudeView;//
    private TextView batteryTempView;
    //private FloatingActionButton startFab;
    //private FloatingActionButton pauseFab;
    //private FloatingActionButton resumeFab;
    private PhotoWayPointFile mPWPFile;
    private BatteryStateUpdateCallback batterCallback;
    private int line_width = 40;

    //tools
    T log;
    private Map mMap=null;
    private ArrayList<DJIFlightControllerDataType.DJILocationCoordinate2D> boundary = null;
    private CustomMission mCM = null;
    private int fly_speed = 15;
    private int rotate_speed = 90;

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Tools.showToast(getApplicationContext(), "Receive Broadcast,air connect is " + AutoflyApplication.isAircraftConnected());
            if (AutoflyApplication.isAircraftConnected()) {
                getCamera();
                getGimbal();
                getBattery();
                getFlightController();
                registerComponentListener();
                isConnectedView.setText(Common.is_connected_yes);
                if (mMissonManager == null) {
                    mMissonManager = DJIMissionManager.getInstance();
                }
                mCM.initComponet(flightController,gimbal,camera);
            } else {
                isConnectedView.setText(Common.is_connected_no);
                mMissonManager = null;
                camera = null;
                battery = null;
                gimbal = null;
                flightController = null;
            }
        }
    };

    public void registerComponentListener() {
        //battery.setBatteryStateUpdateCallback(this);
        //camera.setDJICameraGeneratedNewMediaFileCallback(mCM);
        //flightController.setUpdateSystemStateCallback(this);

    }


    public void registerUiListener() {
        ((Button) findViewById(R.id.start_bt)).setOnClickListener(this);
        ((Button) findViewById(R.id.pause_bt)).setOnClickListener(this);
        ((Button) findViewById(R.id.resume_bt)).setOnClickListener(this);
        ((Button) findViewById(R.id.stop_bt)).setOnClickListener(this);
        ((Button) findViewById(R.id.prepare_bt)).setOnClickListener(this);
        ((Button) findViewById(R.id.show_pl_bt)).setOnClickListener(this);
        ((Button) findViewById(R.id.clear_pl_bt)).setOnClickListener(this);
        ((Button) findViewById(R.id.gen_pl_bt)).setOnClickListener(this);


        //startFab.setOnClickListener(this);
        //pauseFab.setOnClickListener(this);
        //resumeFab.setOnClickListener(this);

    }

    private DJIBaseProduct getProduct() {
        try {
            return AutoflyApplication.getProductInstance();
        } catch (Exception e) {
            Tools.showToast(getApplicationContext(), e.getMessage());
        }
        return null;
    }

    private void getGimbal()//初始化云台
    {
        if (AutoflyApplication.isAircraftConnected() && gimbal == null) {
            gimbal = getProduct().getGimbal();
            /*
            if (gimbal == null) {
                Tools.showToast(getApplicationContext(), "Init Gimbal Fail");
            }
            else{
                Tools.showToast(getApplicationContext(),"Init Gimbal Successful");
            }
            */
        }

    }

    private void getCamera()//初始化相机
    {
        if (AutoflyApplication.isAircraftConnected() && camera == null) {
            camera = getProduct().getCamera();

            if (camera == null) {
                Tools.showToast(getApplicationContext(), "Init Camera Fail");
            } else
                Tools.showToast(getApplicationContext(), "Init Camera successful");
                /**/
        }

    }

    private void getBattery()//初始化电池
    {
        if (AutoflyApplication.isAircraftConnected() && battery == null) {
            battery = getProduct().getBattery();
            /*
            if (battery == null) {
                Tools.showToast(getApplicationContext(), "Init Battery Fail");
            }
            Tools.showToast(getApplicationContext(),"Init Battery successful");
            */
        }

    }

    private void getFlightController()//初始化飞控
    {
        if (AutoflyApplication.getAircraftInstance() != null) {
            if (flightController == null)
                flightController = AutoflyApplication.getAircraftInstance().getFlightController();
              /*
                if (flightController == null) {
                    Tools.showToast(getApplicationContext(), "Init flightc Fail");
                } else {
                    Tools.showToast(getApplicationContext(), "Init flightc Successful");
                }
                */
        }
    }

    private void prepareMissions() {
        if (mMissonManager != null && mMission != null)
            mMissonManager.prepareMission(mMission, new DJIMission.DJIMissionProgressHandler() {
                @Override
                public void onProgress(DJIMission.DJIProgressType djiProgressType, float v) {
                    log.i("on progress");
                }
            }, new DJIBaseComponent.DJICompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        log.i("Prepare mission OK");
                    } else {
                        log.i("Prepare mission fail on result");
                    }
                }
            });
    }

    private void resetCameraPosition() {
        if (gimbal != null) {
            try {
                DJIGimbal.DJIGimbalAngleRotation
                        mPitchRotation = new DJIGimbal.DJIGimbalAngleRotation(true, -90, DJIGimbal.DJIGimbalRotateDirection.Clockwise),
                        mYawRotation = new DJIGimbal.DJIGimbalAngleRotation(true, 0, DJIGimbal.DJIGimbalRotateDirection.Clockwise),
                        mRollRotation = new DJIGimbal.DJIGimbalAngleRotation(true, 0, DJIGimbal.DJIGimbalRotateDirection.Clockwise);
                gimbal.rotateGimbalByAngle(DJIGimbal.DJIGimbalRotateAngleMode.AbsoluteAngle, mPitchRotation, mYawRotation, mRollRotation, null);//
            } catch (Exception e) {
                log.i("In reset,exception" + e.getMessage());
            }
        } else {
            log.i("In reset,gimbal null");
        }
    }


    private void initUi() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        projectPage = findViewById(R.id.project);
        airlinePage = findViewById(R.id.airline);//).setVisibility(View.GONE);
        camOrMapPage = findViewById(R.id.cam_or_map);//).setVisibility(View.GONE);
        settingPage = findViewById(R.id.setting);//).setVisibility(View.GONE);
        mapPage=findViewById(R.id.map);
        gpsCountView = (TextView) findViewById(R.id.gps_count);
        batteryRemainView = (TextView) findViewById(R.id.battery_view);
        batteryVolView = (TextView) findViewById(R.id.battery_vol_view);
        batteryTempView = (TextView) findViewById(R.id.battery_temp_view);
        //startFab=(FloatingActionButton)findViewById(R.id.start_fab);
        //pauseFab=(FloatingActionButton)findViewById(R.id.pause_fab);
        //resumeFab=(FloatingActionButton)findViewById(R.id.resume_fab);
        //camFab.setBackground(getResources().getDrawable(R.drawable.box,getTheme()));
        isConnectedView = (TextView) findViewById(R.id.is_connected);
        line_space_view = (TextView) findViewById(R.id.line_space);
        //gimbalFab.setBackgroundColor(getResources().getColor(R.color.fad_invalid,getTheme()));
        ((SeekBar) findViewById(R.id.fly_speed_sb)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((TextView) findViewById(R.id.fly_speed_tv)).setText(progress + "m/s");
                fly_speed = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        ((SeekBar) findViewById(R.id.rotate_speed_sb)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((TextView) findViewById(R.id.rotate_speed_tv)).setText(progress + "d/s");
                rotate_speed = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mMap=new Map((MapView)findViewById(R.id.map_view));

    }

    private void initParams() {
        initUi();
        setPageInvisibility();//设置所有page不可见
        projectPage.setVisibility(View.VISIBLE);//将project设置可见
        registerUiListener();
        if(mCM==null)
            mCM = new CustomMission(log);
    }

    private void setPageInvisibility() {
        projectPage.setVisibility(View.GONE);
        airlinePage.setVisibility(View.GONE);
        camOrMapPage.setVisibility(View.GONE);
        settingPage.setVisibility(View.GONE);
        mapPage.setVisibility(View.GONE);
    }

    private void _registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AutoflyApplication.FLAG_CONNECTION_CHANGE);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initParams();

        //init in initUi
        mMap.onCreate(savedInstanceState);
        _registerReceiver();
        batterCallback = new BatteryStateUpdateCallback(batteryVolView, batteryRemainView, batteryTempView);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ((SeekBar) findViewById(R.id.width_sb)).setOnSeekBarChangeListener(this);
        log = new T(getApplicationContext());
        boundary = new ArrayList<>();
        boundary.add(new DJIFlightControllerDataType.DJILocationCoordinate2D(41.803111, 123.427709));
        boundary.add(new DJIFlightControllerDataType.DJILocationCoordinate2D(41.803104, 123.428371));
        boundary.add(new DJIFlightControllerDataType.DJILocationCoordinate2D(41.802767, 123.428412));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_project) {
            // Handle the camera action
            setPageInvisibility();
            projectPage.setVisibility(View.VISIBLE);

        } else if (id == R.id.nav_airline) {
            setPageInvisibility();
            airlinePage.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_cam_or_map) {
            setPageInvisibility();
            camOrMapPage.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_settings) {
            setPageInvisibility();
            settingPage.setVisibility(View.VISIBLE);
        }
            else if (id == R.id.nav_map) {
                setPageInvisibility();
                mapPage.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onDestroy() {
        ///getApplicationContext().unregisterReceiver(mReceiver);
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mMap.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMap.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mMap.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMap.onResume();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        String msg = "";
        switch (item.getItemId()) {
            case R.id.action_settings:
                msg += "Click setting";
                break;
        }

        if (!msg.equals("")) {
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onResult(DJIFlightControllerDataType.DJIFlightControllerCurrentState state) {
        gpsCountView.setText("" + state.getSatelliteCount());

    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.start_bt: {
                if (mMissonManager == null) {
                    log.i("MissionManager Not OK");
                } else {
                    mMissonManager.stopMissionExecution(new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                log.i("Mission stop");
                            } else {
                                log.i("Mission Stop Fail:" + djiError.getDescription());

                            }
                        }
                    });
                }
            }
            ;
            break;
            case R.id.pause_bt: {
                if (mMissonManager == null) {
                    Tools.i(getApplicationContext(), "MissionManager Not OK");
                } else {
                    mMissonManager.resumeMissionExecution(new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                Tools.i(getApplicationContext(), "Resume Mission");
                            } else {
                                Tools.i(getApplicationContext(), "Resume Mission Fail:" + djiError.getDescription());
                            }
                        }
                    });
                }

            }
            ;
            break;
        }
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        line_width = progress;
        line_space_view.setText(progress + "m");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /*
     * SeekBar开始滚动的回调函数
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gen_pl_bt:{
                if (mMissonManager == null) {
                    Tools.i(getApplicationContext(), "MissionManager Not OK");
                } else {
                    mCM.initBoundary(boundary);
                    mCM.generateCoordinates(line_width);
                    mCM.rotate_speed = rotate_speed;
                    mCM.fly_speed = fly_speed;
                    //mMission = (DJICustomMission) mCM.generateMission();
                }
            }break;
            case R.id.prepare_bt: {
                if (mMissonManager == null) {
                    Tools.i(getApplicationContext(), "MissionManager Not OK");
                } else {
                    getCamera();
                    if (mMissonManager.mIsCustomMissionExecuting)
                        mMissonManager.stopMissionExecution(new DJIBaseComponent.DJICompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError != null) {
                                    log.i("StopMissionFail");
                                }
                            }
                        });
                    /*
                    mCM.initBoundary(boundary);
                    mCM.generateCoordinates(line_width);
                    mCM.rotate_speed = rotate_speed;
                    mCM.fly_speed = fly_speed;
                    */
                    mMission = (DJICustomMission) mCM.generateMission();
                    prepareMissions();
                }
            }
            break;
            case R.id.start_bt: {
                if (mMissonManager == null) {
                    Tools.i(getApplicationContext(), "MissionManager Not OK");
                } else {
                    Tools.i(getApplicationContext(), "Starting Mission...");
                    mMissonManager.setMissionExecutionFinishedCallback(new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                log.i("Finish");
                            }
                        }
                    });
                    mMissonManager.startMissionExecution(new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                Tools.i(getApplicationContext(), "Start mission");
                            } else {
                                Tools.i(getApplicationContext(), "Start Mission Fail:" + djiError.getDescription());
                            }

                        }
                    });
                }

            }
            break;
            case R.id.pause_bt: {
                if (mMissonManager == null) {
                    Tools.i(getApplicationContext(), "MissionManager Not OK");
                } else {

                    mMissonManager.pauseMissionExecution(new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                Tools.i(getApplicationContext(), "Pause Mission");
                            } else {
                                Tools.i(getApplicationContext(), "Pause Mission Fail");
                            }
                        }
                    });
                }

            }
            break;

            case R.id.show_pl_bt:{
                if(mCM.wayPoints.size()>0) {
                    mMap.drawWaypoints(mCM.wayPoints,getApplicationContext());
                }
                else {
                    log.i("No waypoints");
                }
            }
            break;
            case R.id.clear_pl_bt:{
                mMap.clearMap();
            }break;

        }
    }
    public void onLoadProject(Project p)
    {
        mProject=p;
    }
}
