package com.ev4ngel.myapplication;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
        SeekBar.OnSeekBarChangeListener{


    private DJIGimbal gimbal = null;
    private DJICamera camera = null;
    private DJIBattery battery = null;
    private DJIFlightController flightController = null;
    private DJIMissionManager mMissonManager = null;
    private DJICustomMission mMission = null;

    //views
    private ArrayList<ArrayList<DJIFlightControllerDataType.DJILocationCoordinate3D>> missionLocations;
    private Toolbar toolbar;
    private View projectPage;//project页面
    private View airlinePage;//航线页面
    private View settingPage;//设置页面
    private View camOrMapPage;//相机/地图页面
    private TextView gpsCountView;//gps数量显示
    private TextView batteryRemainView;//电池电量显示
    private TextView batteryVolView;//
    private TextView isConnectedView;
    private TextView velX;
    private TextView velY;
    private TextView velZ;
    private TextView posLat;//
    private TextView posLng;
    private TextView posHeight;//
    private TextView attitudeView;//
    private TextView batteryTempView;
    private FloatingActionButton startFab;
    private FloatingActionButton pauseFab;
    private FloatingActionButton resumeFab;
    private PhotoWayPointFile mPWPFile;
    private BatteryStateUpdateCallback batterCallback;
    private int line_width=0;
private ArrayList<DJIFlightControllerDataType.DJILocationCoordinate2D> yArray=null;
    DJIFlightControllerDataType.DJILocationCoordinate3D[] mArray={
            new DJIFlightControllerDataType.DJILocationCoordinate3D(41.803368,123.427194,0),
            new DJIFlightControllerDataType.DJILocationCoordinate3D(41.802,123.42736,0),
            new DJIFlightControllerDataType.DJILocationCoordinate3D(41.80197,123.427923,0),
            new DJIFlightControllerDataType.DJILocationCoordinate3D(41.8036444,123.4270,40),
            new DJIFlightControllerDataType.DJILocationCoordinate3D(41.80307748,123.42775835,40),
            new DJIFlightControllerDataType.DJILocationCoordinate3D(41.80296111,123.42776389,40),
            new DJIFlightControllerDataType.DJILocationCoordinate3D(41.801,123.429,40),
            new DJIFlightControllerDataType.DJILocationCoordinate3D(41.80308740,123.42791413,40),
            new DJIFlightControllerDataType.DJILocationCoordinate3D(41.80320436,123.42790864,40),
            new DJIFlightControllerDataType.DJILocationCoordinate3D(41.80321389,123.42805833,40),
            new DJIFlightControllerDataType.DJILocationCoordinate3D(41.80309692,123.42806390,40),
            new DJIFlightControllerDataType.DJILocationCoordinate3D(41.801,123.4268,40)};

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //refreshUI();
            Tools.showToast(getApplicationContext(),"Receive Broadcast,air connect is "+AutoflyApplication.isAircraftConnected());
            if (AutoflyApplication.isAircraftConnected()) {
                //camFab.setBackgroundColor(getResources().getDrawable(R.drawable.box));
                //gimbalFab.setEnabled(true);

                //Tools.showToast(getApplicationContext(), "Connection Success");
                getCamera();
                getGimbal();
                getBattery();
                getFlightController();
                registerComponentListener();
                isConnectedView.setText(Common.is_connected_yes);
                if(mMissonManager==null) {
                    mMissonManager = DJIMissionManager.getInstance();
                    //prepareMissions();
                }
            } else {
                isConnectedView.setText(Common.is_connected_no);
                mMissonManager=null;
                camera=null;
                battery=null;
                gimbal=null;
                flightController=null;
            }
        }
    };

    public void registerComponentListener() {
        //battery.setBatteryStateUpdateCallback(this);
        //camera.setDJICameraGeneratedNewMediaFileCallback(this);
        //flightController.setUpdateSystemStateCallback(this);

    }


    public void registerUiListener(){
        startFab.setOnClickListener(this);
        pauseFab.setOnClickListener(this);
        resumeFab.setOnClickListener(this);

   }

    private DJIBaseProduct getProduct()
    {
        try {
            return AutoflyApplication.getProductInstance();
        }catch(Exception e)
        {
            Tools.showToast(getApplicationContext(),e.getMessage());
        }
        return null;
    }
    private void getGimbal()//初始化云台
    {
        if( AutoflyApplication.isAircraftConnected()&&gimbal==null)
        {
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
        if(AutoflyApplication.isAircraftConnected() && camera==null)
        {
            camera = getProduct().getCamera();

            if (camera == null) {
                Tools.showToast(getApplicationContext(), "Init Camera Fail");
            }
            else
                Tools.showToast(getApplicationContext(),"Init Camera successful");
                /**/
    }

    }
    private void getBattery()//初始化电池
    {
        if(AutoflyApplication.isAircraftConnected()&&battery==null)
        {
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
                if(flightController==null)
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
    private void prepareMissions()
    {
        if(mMissonManager!=null && mMission!=null)
        mMissonManager.prepareMission(mMission, new DJIMission.DJIMissionProgressHandler() {
            @Override
            public void onProgress(DJIMission.DJIProgressType djiProgressType, float v) {
                Tools.i(getApplicationContext(), "on progress");
            }
        }, new DJIBaseComponent.DJICompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if(djiError==null) {
                    Tools.i(getApplicationContext(), "Prepare mission OK");
                }
                else
                {
                    Tools.i(getApplicationContext(), "Prepare mission fail on result");
                }
            }
        });
    }

    private void generateMissions()
    {
        CalBox cb=new CalBox();
        yArray=cb.calcPlanPointList(mArray[0].getCoordinate2D(),mArray[1].getCoordinate2D(),mArray[2].getCoordinate2D(),line_width);
        ArrayList<DJIMissionStep> steps=new ArrayList<DJIMissionStep>() ;
        for(int i=0;i<yArray.size();i++)
        {
            final int ii=i;
            if(yArray.get(ii)!=null) {
                DJIGoToStep gotoStep = new DJIGoToStep(yArray.get(ii).getLatitude(), yArray.get(ii).getLongitude(), new DJIBaseComponent.DJICompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if(djiError==null)
                            Tools.i(getApplicationContext(), "Finish step "+ii);
                        else
                        {
                            Tools.i(getApplicationContext(), "Fail step :"+djiError.getDescription());
                        }
                    }
                });
                steps.add(gotoStep);
                //摄像头角度下视
                steps.add(new DJIGimbalAttitudeStep(DJIGimbal.DJIGimbalRotateAngleMode.AbsoluteAngle,
                        new DJIGimbal.DJIGimbalAngleRotation(true, -90, DJIGimbal.DJIGimbalRotateDirection.Clockwise),
                        new DJIGimbal.DJIGimbalAngleRotation(false, 0, DJIGimbal.DJIGimbalRotateDirection.Clockwise),
                        new DJIGimbal.DJIGimbalAngleRotation(false, 0, DJIGimbal.DJIGimbalRotateDirection.Clockwise),
                        new DJIBaseComponent.DJICompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                camera.startShootPhoto(DJICameraSettingsDef.CameraShootPhotoMode.Single, new DJIBaseComponent.DJICompletionCallback() {
                                    @Override
                                    public void onResult(DJIError djiError) {
                                        if(djiError==null)
                                        {
                                            T.i(getApplicationContext(),"Photo ok");
                                        }
                                    }
                                });
                            }
                        }
                ));
                ///摄像头角度45度
                steps.add(new DJIGimbalAttitudeStep(DJIGimbal.DJIGimbalRotateAngleMode.AbsoluteAngle,
                        new DJIGimbal.DJIGimbalAngleRotation(true, -45, DJIGimbal.DJIGimbalRotateDirection.Clockwise),
                        new DJIGimbal.DJIGimbalAngleRotation(false, 0, DJIGimbal.DJIGimbalRotateDirection.Clockwise),
                        new DJIGimbal.DJIGimbalAngleRotation(false, 0, DJIGimbal.DJIGimbalRotateDirection.Clockwise),
                        new DJIBaseComponent.DJICompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                camera.startShootPhoto(DJICameraSettingsDef.CameraShootPhotoMode.Single, new DJIBaseComponent.DJICompletionCallback() {
                                    @Override
                                    public void onResult(DJIError djiError) {
                                        if(djiError==null)
                                        {
                                            T.i(getApplicationContext(),"Photo ok");
                                        }
                                    }
                                });
                            }
                        }
                ));
                for(int j=0;j<3;j++) {//三次旋转拍照
                    DJIAircraftYawStep yawStep = new DJIAircraftYawStep(90, 20, new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if(djiError==null)//成功后拍照
                            {
                                camera.startShootPhoto(DJICameraSettingsDef.CameraShootPhotoMode.Single, new DJIBaseComponent.DJICompletionCallback() {
                                    @Override
                                    public void onResult(DJIError djiError) {
                                        if(djiError==null)
                                        {
                                            T.i(getApplicationContext(),"Photo ok");
                                        }
                                    }
                                });
                            }

                        }
                    });
                    steps.add(yawStep);
                }
            }/**/
        }


        mMission=new DJICustomMission(steps);
    }

    private void resetCameraPosition()
    {
        if(gimbal!=null)
        {
            try {
                DJIGimbal.DJIGimbalAngleRotation
                        mPitchRotation = new DJIGimbal.DJIGimbalAngleRotation(true, -90, DJIGimbal.DJIGimbalRotateDirection.Clockwise),
                        mYawRotation = new DJIGimbal.DJIGimbalAngleRotation(true, 0, DJIGimbal.DJIGimbalRotateDirection.Clockwise),
                        mRollRotation = new DJIGimbal.DJIGimbalAngleRotation(true, 0, DJIGimbal.DJIGimbalRotateDirection.Clockwise);
                gimbal.rotateGimbalByAngle(DJIGimbal.DJIGimbalRotateAngleMode.AbsoluteAngle, mPitchRotation, mYawRotation, mRollRotation, null);//
            }
            catch (Exception e)
            {
                Tools.showToast(getApplicationContext(),"In reset,exception"+e.getMessage());
            }
        }
        else
        {
            Tools.showToast(getApplicationContext(),"In reset,gimbal null");
        }
    }


    private void initUi()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        projectPage=findViewById(R.id.project);
        airlinePage=findViewById(R.id.airline);//).setVisibility(View.GONE);
        camOrMapPage=findViewById(R.id.cam_or_map);//).setVisibility(View.GONE);
        settingPage=findViewById(R.id.setting);//).setVisibility(View.GONE);
        gpsCountView=(TextView)findViewById(R.id.gps_count);
        batteryRemainView=(TextView)findViewById(R.id.battery_view);
        batteryVolView=(TextView)findViewById(R.id.battery_vol_view);
        batteryTempView=(TextView)findViewById(R.id.battery_temp_view);
        startFab=(FloatingActionButton)findViewById(R.id.start_fab);
        pauseFab=(FloatingActionButton)findViewById(R.id.pause_fab);
        resumeFab=(FloatingActionButton)findViewById(R.id.resume_fab);
        //camFab.setBackground(getResources().getDrawable(R.drawable.box,getTheme()));
        isConnectedView=(TextView)findViewById(R.id.is_connected);
        velZ=(TextView)findViewById(R.id.y_speed_view);
        //gimbalFab.setBackgroundColor(getResources().getColor(R.color.fad_invalid,getTheme()));



    }

    private void initParams()
    {
        missionLocations=new ArrayList<ArrayList<DJIFlightControllerDataType.DJILocationCoordinate3D>>();
        //mPWPFile=new PhotoWayPointFile(getApplicationContext(),"");

        missionLocations.add(new ArrayList<DJIFlightControllerDataType.DJILocationCoordinate3D>());
        missionLocations.add(new ArrayList<DJIFlightControllerDataType.DJILocationCoordinate3D>());
        missionLocations.add(new ArrayList<DJIFlightControllerDataType.DJILocationCoordinate3D>());
        for(int i=0;i<mArray.length/3;i++)
        {
            missionLocations.get(i/3).add(mArray[i]);
        }
        //getGimbal();
        //getCamera();
        //getBattery();
        initUi();
        setPageInvisibility();//设置所有page不可见
        projectPage.setVisibility(View.VISIBLE);//将project设置可见
        registerUiListener();
    }
    private void setPageInvisibility()
    {
        projectPage.setVisibility(View.GONE);
        airlinePage.setVisibility(View.GONE);
        camOrMapPage.setVisibility(View.GONE);
        settingPage.setVisibility(View.GONE);
    }
    private void _registerReceiver()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AutoflyApplication.FLAG_CONNECTION_CHANGE);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initParams();
        _registerReceiver();
        batterCallback=new BatteryStateUpdateCallback(batteryVolView,batteryRemainView,batteryTempView);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ((SeekBar)findViewById(R.id.width_sb)).setOnSeekBarChangeListener(this);
        //for(int i=0;i<mArray.length;i++)
        //    yArray.add(mArray[i].getCoordinate2D());

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
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        String msg = "";
        switch (item.getItemId()) {
            case R.id.action_settings:
                msg += "Click setting";
                break;
        }

        if(!msg.equals("")) {
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
            case R.id.start_fab: {
                if (mMissonManager == null) {
                    Tools.i(getApplicationContext(), "MissionManager Not OK");
                } else {
                    mMissonManager.stopMissionExecution(new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                T.i(getApplicationContext(), "Mission stop");
                            } else {
                                T.i(getApplicationContext(), "Mission Stop Fail:" + djiError.getDescription());

                            }
                        }
                    });
                }
            }
            ;
            break;
            case R.id.pause_fab: {
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
        line_width=progress;
        velZ.setText("距离："+progress+"m");
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
            case R.id.start_fab: {
                if (mMissonManager == null)
                {
                    Tools.i(getApplicationContext(),"MissionManager Not OK");
                }
                else{
                    Tools.i(getApplicationContext(),"Starting Mission...");
                    mMissonManager.setMissionExecutionFinishedCallback(new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                T.i(getApplicationContext(), "Finish");
                            }
                        }
                    });
                    mMissonManager.startMissionExecution(new DJIBaseComponent.DJICompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if(djiError==null)
                        {
                            Tools.i(getApplicationContext(),"Start mission");
                        }else
                        {
                            Tools.i(getApplicationContext(),"Start Mission Fail:"+djiError.getDescription());
                        }

                    }
                });}
                /*
                if (AutoflyApplication.isAircraftConnected() && camera != null) {
                    camera.startShootPhoto(DJICameraSettingsDef.CameraShootPhotoMode.Single, new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if(djiError!=null)
                            {
                                Tools.showToast(getApplicationContext(),"TakePhotoF");
                            }else
                            {
                                Tools.showToast(getApplicationContext(),"TakePhotoS");
                            }
                        }
                    });
                } else {
                    Tools.showToast(getApplicationContext(), "Fad Camera Fail");
                }*/
            };break;
            case R.id.pause_fab:
            {
                if (mMissonManager == null)
                {
                    Tools.i(getApplicationContext(),"MissionManager Not OK");
                }
                else {

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
                /*
                if(AutoflyApplication.isAircraftConnected())
                {
                    //new GotoCompletionCallback(getApplicationContext(),gimbal,camera,clickcount%2).onResult(null);
                    //clickcount++;
                    mMissonManager.startMissionExecution(new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            _i("Finished mission");
                        }
                    });
                }
                else
                Tools.showToast(getApplicationContext(),"Fad mission Inconnect");
                */
            };break;
            case R.id.resume_fab:
            {
                if (mMissonManager == null)
                {
                    Tools.i(getApplicationContext(),"MissionManager Not OK");
                }
                else {
                    getCamera();
                    if(mMissonManager.mIsCustomMissionExecuting)
                        mMissonManager.stopMissionExecution(new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if(djiError!=null)
                            {
                                T.i(getApplicationContext(),"StopMissionFail");
                            }
                        }
                    });
                    generateMissions();
                    prepareMissions();
                    /*
                    mMissonManager.prepareMission(mMission, new DJIMission.DJIMissionProgressHandler() {
                        @Override
                        public void onProgress(DJIMission.DJIProgressType djiProgressType, float v) {

                        }
                    }, new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if(djiError!=null)
                            {
                                T.i(getApplicationContext(),djiError.getDescription());
                            }
                        }
                    });
                    mMissonManager.resumeMissionExecution(new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                Tools.i(getApplicationContext(), "Resume Mission");
                            } else {
                                Tools.i(getApplicationContext(), "Resume Mission Fail");
                            }
                        }
                    });*/
                }
                /*
                if(AutoflyApplication.isAircraftConnected())
                {
                    Tools.showToast(getApplicationContext(),"connect");
                    resetCameraPosition();
                }
                else
                    Tools.showToast(getApplicationContext(),"Inconnect");
                    */

            };break;
        }
    }
}
