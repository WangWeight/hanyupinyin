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
import android.util.Log;
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
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Polygon;
import com.ev4ngel.autofly_prj.OnLoadProjectListener;
import com.ev4ngel.autofly_prj.OnNewPictureGenerateListener;
import com.ev4ngel.autofly_prj.OnPrepareMissionListener;
import com.ev4ngel.autofly_prj.OnSaveWayPointListener;
import com.ev4ngel.autofly_prj.PhotoWayPoint;
import com.ev4ngel.autofly_prj.PositionFrg;
import com.ev4ngel.autofly_prj.Project;
import com.ev4ngel.autofly_prj.ProjectFragment;
import com.ev4ngel.autofly_prj.StateFrg;
import com.ev4ngel.autofly_prj.StateHandler;
import com.ev4ngel.autofly_prj.WayPoint;

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
import dji.sdk.RemoteController.DJIRemoteController;
import dji.sdk.SDKManager.DJISDKManager;
import dji.sdk.base.DJIBaseComponent;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.base.DJIError;
import dji.sdk.util.DJIParamCapability;
import dji.sdk.util.DJIParamMinMaxCapability;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Toolbar.OnMenuItemClickListener,
        View.OnClickListener,
        OnLoadProjectListener,
        OnSaveWayPointListener,
        OnNewPictureGenerateListener,
        OnPrepareMissionListener{
    private DJIGimbal gimbal = null;
    private DJICamera camera = null;
    private DJIBattery battery = null;
    private DJIFlightController flightController = null;
    private DJIMissionManager mMissonManager = null;
    private DJICustomMission mMission = null;
    private DJIRemoteController remote=null;

    ///tags setting
    String prj_frg_tag="prj_frg";
    String map_frg_tag="map_frg";
    String state_frg_tag="state_frg";
    String position_frg_tag="position_frg";
    String mission_frg_tag="mission_frg";
    Project mProject=null;
    //views
    private ArrayList<ArrayList<DJIFlightControllerDataType.DJILocationCoordinate3D>> missionLocations;
    private Toolbar toolbar;
    private ProjectFragment mProjectFrg;
    private MapFrg mMapFrg;
    private FragmentShower mFrgShow;
    private StateFrg mStateFrg;
    private PositionFrg mPositionFrg;
    private MissionFrg mMissionFrg;
    private StateHandler mStateHandler;
    private ArrayList<WayPoint> mWayPoints;
    //tools
    T log;
    private CustomMission mCM = null;

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Tools.showToast(getApplicationContext(), "Receive Broadcast,air connect is " + AutoflyApplication.isAircraftConnected());
            if (AutoflyApplication.isAircraftConnected()) {
                getCamera();
                getGimbal();
                getBattery();
                getFlightController();
                getRemoteController();
                registerComponentListener();
                if (mMissonManager == null) {
                    mMissonManager = DJIMissionManager.getInstance();
                }
                mCM.initComponet(flightController,gimbal,camera);
            } else {
                mMissonManager = null;
                camera = null;
                battery = null;
                gimbal = null;
                flightController = null;
                remote=null;
            }
        }
    };

    public void registerComponentListener() {
        mStateHandler=new StateHandler();
        mStateHandler.setSF(mStateFrg);
        mStateHandler.setPF(mPositionFrg);
        mStateHandler.setMap(mMapFrg);
        if(battery!=null)
        battery.setBatteryStateUpdateCallback(mStateHandler);
        camera.setDJICameraGeneratedNewMediaFileCallback(mCM);
        if(camera!=null)
        camera.setDJIUpdateCameraSDCardStateCallBack(mStateHandler);
        if(flightController!=null)
        flightController.setUpdateSystemStateCallback(mStateHandler);
        if(remote!=null)
        remote.setGpsDataUpdateCallback(mStateHandler);
        if(gimbal!=null)
            gimbal.setGimbalStateUpdateCallback(mStateHandler);
        else Log.i("e","Remote is null");
    }


    public void registerUiListener() {
        mProjectFrg.setOnLoadProjectListener(this);
        mProjectFrg.getProjectInstance().setOnProjectLoad(this);
        mMapFrg.setOnSaveWayPointListener(this);
        mMissionFrg.setOnPrepareMissionListener(this);
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

            gimbal.setCompletionTimeForControlAngleAction(1);
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
    private void getRemoteController()
    {
        if(AutoflyApplication.getHandHeldInstance()!=null){
            if(remote==null)
                remote=AutoflyApplication.getAircraftInstance().getRemoteController();
            if(remote==null)
                Tools.showToast(getApplicationContext(),"RC fail");
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
        if (mMissonManager != null && mMission != null) {
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
                        onPrepareMission(0,0,MissionOptSignal.MOS_START);
                    } else {
                        log.i("Prepare mission fail on result");
                    }
                }
            });
        }else{

        }
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
    }

    private void initParams() {
        initUi();
        mProjectFrg=new ProjectFragment();
        mMapFrg=new MapFrg();
        mStateFrg=new StateFrg();
        mPositionFrg=new PositionFrg();
        mMissionFrg=new MissionFrg();
        mFrgShow=new FragmentShower(getFragmentManager());
        mFrgShow.add(R.id.prj_frg,mProjectFrg,prj_frg_tag)
                .add(R.id.map_frg,mMapFrg,map_frg_tag)
                .add(R.id.mission_frg, mMissionFrg, mission_frg_tag)
                .add(R.id.position_frg, mPositionFrg, position_frg_tag)
                .add(R.id.state_frg, mStateFrg, state_frg_tag, true)
                .show(new String[]{prj_frg_tag});

        //getFragmentManager().beginTransaction().add(R.id.state_frg,mStateFrg,"state_frg").commit();
        if(mCM==null) {
            mCM = new CustomMission();
            mCM.setOnNewPictureListener(this);
        }

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
        //mMapFrg.onCreate(savedInstanceState);
        //init in initUi
        _registerReceiver();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        log = new T(getApplicationContext());

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerUiListener();
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
            mFrgShow.show(new String[]{prj_frg_tag});
        } else if (id == R.id.nav_airline) {
            mMapFrg.setMode(MapMode.Design);
            mFrgShow.show(new String[]{map_frg_tag});
        } else if (id == R.id.nav_cam_or_map) {
            mMapFrg.setMode(MapMode.Reference);
            mFrgShow.show(new String[]{map_frg_tag,position_frg_tag,mission_frg_tag});
        } else if (id == R.id.nav_settings) {

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
        //mMap.onDestroy();
        mMapFrg.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mMap.onPause();
        mMapFrg.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        //mMap.onSaveInstanceState(outState);
        mMapFrg.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mMap.onResume();
        mMapFrg.onResume();
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
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
    public void onLoadProject()
    {
        TextView tv1=((TextView) findViewById(R.id.nav_prj_name));
        if(tv1!=null)
            tv1.setText("当前项目:" + mProjectFrg.getProjectInstance().current_project_name);
        TextView tv2=((TextView)findViewById(R.id.nav_prj_detail));
        if(tv2!=null)
            tv2.setText(mProjectFrg.getProjectInstance().current_project_name);

    }

    @Override
    public void onSaveWayPoint(String fname,ArrayList<WayPoint> wps) {//当点击保存航线按钮确认后会调用
        if(wps!=null)
        {
            if(mProjectFrg!=null)
            {
                mProjectFrg.getProjectInstance().new_airway(fname).get_wp_file().set_waypoints(wps);
                mWayPoints=wps;
            }
        }
    }
    public void onLoadNewWayPoints(String wpfile)
    {
        mProjectFrg.getProjectInstance().get_wp_file().read(wpfile);
        mWayPoints=mProjectFrg.getProjectInstance().get_wp_file().get_waypoints();
        //mNavHeadFrg.update();
        mMapFrg.setWayPoints(mWayPoints).drawline(true);
    }

    @Override
    public void onNewPicture(String pname) {
        PhotoWayPoint pwp=new PhotoWayPoint();
        //pwp.addPhoto(pname,(float)flightController.getCompass().getHeading(),flightController);
        //mProjectFrg.getProjectInstance().get_pwp_file().addPhotoWayPoint(pwp);

        if(flightController!=null) {
            mStateHandler.setPhotoTakenPosition(flightController.getCurrentState().getAircraftLocation().getCoordinate2D());
        }
    }

    @Override
    public void onPrepareMission(int speed,int height,int signal) {
        if(mWayPoints==null ||mWayPoints.size()==0)
        {
            Toast.makeText(MainActivity.this, "请先规划或者选择航线", Toast.LENGTH_SHORT).show();
        }else {
            switch (signal)
            {
                case MissionOptSignal.MOS_PREPARE:{
                    mCM.setWayPoints(mWayPoints);
                    mCM.fly_speed=speed;
                    mCM.return_height=height;
                    mMission = (DJICustomMission) mCM.generateMission();
                    prepareMissions();
                }break;
                case MissionOptSignal.MOS_START:{
                    //resetCameraPosition();
                    gimbal.resetGimbal(new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                resetCameraPosition();
                                if (mMissonManager.isMissionReadyToExecute)
                                    mMissonManager.startMissionExecution(null);
                                else
                                    Log.i("e","Not ready to start");
                            }
                        }
                    });
                }break;
                case MissionOptSignal.MOS_STOP:{
                    if(mMissonManager.mIsCustomMissionExecuting)
                        mMissonManager.stopMissionExecution(null);
                }break;
            }
        }
    }

    @Override
    public void onGoHomeMission() {
        //mMission=mCM.ge
    }
}
