package com.ev4ngel.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.model.LatLng;
import com.ev4ngel.autofly_prj.FPView_frg;
import com.ev4ngel.autofly_prj.OnMissionListener;
import com.ev4ngel.autofly_prj.OnNewPictureGenerateListener;
import com.ev4ngel.autofly_prj.PositionFrg;
import com.ev4ngel.autofly_prj.ProjectDatabase;
import com.ev4ngel.autofly_prj.ProjectFragment;
import com.ev4ngel.autofly_prj.ProjectInstance;
import com.ev4ngel.autofly_prj.StateFrg;
import com.ev4ngel.autofly_prj.StateHandler;
import com.ev4ngel.autofly_prj.WayPoint;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import dji.common.camera.DJICameraSettingsDef;
import dji.common.flightcontroller.DJIAttitude;
import dji.common.flightcontroller.DJILocationCoordinate2D;
import dji.common.flightcontroller.DJILocationCoordinate3D;
import dji.common.gimbal.DJIGimbalAngleRotation;
import dji.common.gimbal.DJIGimbalAttitude;
import dji.common.gimbal.DJIGimbalRotateAngleMode;
import dji.common.gimbal.DJIGimbalRotateDirection;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.battery.DJIBattery;
import dji.sdk.camera.DJICamera;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.gimbal.DJIGimbal;
import dji.sdk.missionmanager.DJICustomMission;
import dji.sdk.missionmanager.DJIMission;
import dji.sdk.missionmanager.DJIMissionManager;
import dji.sdk.remotecontroller.DJIRemoteController;
import dji.sdk.base.DJIBaseProduct;
import dji.common.error.DJIError;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Toolbar.OnMenuItemClickListener,
        View.OnClickListener,
        OnNewPictureGenerateListener,
        OnMissionListener,
        CustomMission.OnGimbalOperationListener,
        CustomMission.OnMissionProcessListener,
ProjectInstance.OnProjectOperationListener{
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
    String cameraView_frg_tag="cameraView_frg";
    //Project mProject=null;
    //ProjectsConfig mPrjsCfg;
    //ProjectConfig mCurrentPrjCfg;
    ProjectDatabase mPrjDB=null;
    ProjectInstance mProjectInstance;
    //views
    private ArrayList<ArrayList<DJILocationCoordinate3D>> missionLocations;
    private Toolbar toolbar;
    private ProjectFragment mProjectFrg;
    private MapFrg mMapFrg;
    private FragmentShower mFrgShow;
    private StateFrg mStateFrg;
    private PositionFrg mPositionFrg;
    private MissionFrg mMissionFrg;
    private FPView_frg mFPViewfrg;
    private StateHandler mStateHandler;
    private ArrayList<WayPoint> mWayPoints;
    BaseFpvView fpv_view;

    LatLng mHomeLatlng=null;
    DJILocationCoordinate2D mHomeLocaion=null;
    //tools
    //T log;
    private CustomMission mCM = null;

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            T.i( "Receive Broadcast,air connect is " + AutoflyApplication.isAircraftConnected());
            if (AutoflyApplication.isAircraftConnected()) {
                getCamera();
                getGimbal();
                getBattery();
                getFlightController();
                getRemoteController();
                registerComponentListener();
                //fpv_view=new BaseFpvView(AutoflyApplication.getContext(),null);
                //((FrameLayout)findViewById(R.id.fpv_view)).addView(fpv_view);
                if (mMissonManager == null) {
                    mMissonManager = DJIMissionManager.getInstance();
                }
                //mCM.initComponet(flightController,gimbal,camera);
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
        mProjectFrg.setOnLoadItemListener(this);
        //mProject.setOnLoadListener(this);
        mMapFrg.setOnSaveWayPointListener(this);
        mMissionFrg.setOnPrepareMissionListener(this);

        mProjectFrg.set_prj_list(mPrjDB.getProjects());
    }

    private DJIBaseProduct getProduct() {
        try {
            return AutoflyApplication.getProductInstance();
        } catch (Exception e) {

        }
        return null;
    }

    private void getGimbal()//初始化云台
    {
        if (AutoflyApplication.isAircraftConnected() && gimbal == null) {
            gimbal = getProduct().getGimbal();
            gimbal.setCompletionTimeForControlAngleAction(0.5);
            /*
            if (gimbal == null) {
                T.i( "Init Gimbal Fail");
            }
            else{
                T.i("Init Gimbal Successful");
            }
            */
        }

    }

    private void getCamera()//初始化相机
    {
        if (AutoflyApplication.isAircraftConnected() && camera == null) {
            camera = getProduct().getCamera();

            if (camera == null) {

            } else{

            }

        }
    }

    private void getBattery()//初始化电池
    {
        if (AutoflyApplication.isAircraftConnected() && battery == null) {
            battery = getProduct().getBattery();
            /*
            if (battery == null) {
                T.i("Init Battery Fail");
            }
            T.i("Init Battery successful");
            */
        }

    }
    private void getRemoteController()
    {
        if(AutoflyApplication.getHandHeldInstance()!=null){
            if(remote==null)
                remote=AutoflyApplication.getAircraftInstance().getRemoteController();

        }
    }
    private void getFlightController()//初始化飞控
    {
        if (AutoflyApplication.getAircraftInstance() != null) {
            if (flightController == null)
                flightController = AutoflyApplication.getAircraftInstance().getFlightController();
              /*
                if (flightController == null) {
                    T.i( "Init flightc Fail");
                } else {
                    T.i("Init flightc Successful");
                }
                */
        }
    }

    private void prepareMissions() {

        if (mMissonManager != null && mMission != null) {
            mMissonManager.prepareMission(mMission, new DJIMission.DJIMissionProgressHandler() {
                @Override
                public void onProgress(DJIMission.DJIProgressType djiProgressType, float v) {
                    Log.i("e","on progress");
                }
            }, new DJICommonCallbacks.DJICompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        Log.i("e","Prepare mission OK");
                        onPrepareMission(0,0,MissionOptSignal.MOS_START);
                    } else {
                        Log.i("e","Prepare mission fail on result");
                    }
                }
            });
        }else{

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
        mFPViewfrg=new FPView_frg();
        mFrgShow=new FragmentShower(getFragmentManager());
        mFrgShow.add(R.id.prj_frg,mProjectFrg,prj_frg_tag)
                .add(R.id.map_frg,mMapFrg,map_frg_tag)
                .add(R.id.mission_frg, mMissionFrg, mission_frg_tag)
                .add(R.id.position_frg, mPositionFrg, position_frg_tag)
                .add(R.id.state_frg, mStateFrg, state_frg_tag, true)
                .add(R.id.fpv_view,mFPViewfrg, cameraView_frg_tag)
                .show(new String[]{prj_frg_tag});

        //getFragmentManager().beginTransaction().add(R.id.state_frg,mStateFrg,"state_frg").commit();
        if(mCM==null) {
            mCM = new CustomMission();
            mCM.setOnNewPictureListener(this);
            mCM.setGimbalListener(this);
            mCM.setOnMissionProcessListener(this);
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        initParams();
        _registerReceiver();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mPrjDB=ProjectDatabase.getInstance(this);
        mPrjDB.openRecentProject();
        T.l("Prj:" + T.join(mPrjDB.getProjectList()));
        T.l("Prj:xxxxx" + mPrjDB.getCurrent_project_id());
        T.l(mPrjDB.getWaylines(null));
        mWayPoints=mPrjDB.getWaypoints(null);
        if(mWayPoints.size()>0){
            mMapFrg.setWayPoints(mWayPoints);
            mMapFrg.drawline(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerUiListener();
        //onLoadProject(mProject.current_project_name);
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
        } else if (id == R.id.nav_cameraView) {
            mFrgShow.show(new String[]{cameraView_frg_tag});
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
    public void resetCameraPosition(CountDownLatch cdl) {
        if (gimbal != null) {
            try {
                DJIGimbalAngleRotation
                        mPitchRotation = new DJIGimbalAngleRotation(true, -90, DJIGimbalRotateDirection.Clockwise),
                        mYawRotation = new DJIGimbalAngleRotation(true, 0, DJIGimbalRotateDirection.Clockwise),
                        mRollRotation = new DJIGimbalAngleRotation(true, 0, DJIGimbalRotateDirection.Clockwise);
                gimbal.rotateGimbalByAngle(DJIGimbalRotateAngleMode.AbsoluteAngle,
                        mPitchRotation,
                        mYawRotation,
                        mRollRotation,
                        new CameraCompletionCallback(cdl));
            } catch (Exception e) {
                Log.i("e","In reset,exception" + e.getMessage());
            }
        } else {
            Log.i("e","In reset,gimbal null");
        }
    }
    public void rotateCamera_90_cw(CountDownLatch cdl) {
        gimbal.rotateGimbalByAngle(DJIGimbalRotateAngleMode.RelativeAngle,
                CustomMission.mRotateRelative_90_cw,
                null,
                null,
                new CameraCompletionCallback(cdl));
    }
    public void rotateCamera_90_cc(CountDownLatch cdl){
        gimbal.rotateGimbalByAngle(DJIGimbalRotateAngleMode.RelativeAngle,
                CustomMission.mRotateRelative_90_cc,
                null,
                null,
                new CameraCompletionCallback(cdl));
    }
    public void rotateCamera_45_cc(CountDownLatch cdl){
        gimbal.rotateGimbalByAngle(DJIGimbalRotateAngleMode.RelativeAngle,
                null,
                null,
                CustomMission.mPitchRelative_45_cc,
                new CameraCompletionCallback(cdl));
    }
    public void rotateCamera_45_cw(CountDownLatch cdl) {
        gimbal.rotateGimbalByAngle(DJIGimbalRotateAngleMode.RelativeAngle,
                null,
                null,
                CustomMission.mPitchRelative_45_cw,
                new CameraCompletionCallback(cdl));
    }
    public void takePhoto(CountDownLatch cdl) {
        camera.startShootPhoto(DJICameraSettingsDef.CameraShootPhotoMode.Single,
                new CameraCompletionCallback(cdl));
    }
    public void setHomeLocation(){
        if(flightController!=null) {
            final CountDownLatch cdl = new CountDownLatch(1);
            flightController.getHomeLocation(new DJICommonCallbacks.DJICompletionCallbackWith<DJILocationCoordinate2D>() {
                @Override
                public void onSuccess(DJILocationCoordinate2D t) {
                    if (t.getLongitude() > 0 && t.getLatitude() > 0)
                        mHomeLocaion = new DJILocationCoordinate2D(t.getLatitude(), t.getLongitude());
                    onHomeLocation(mHomeLocaion);
                    cdl.countDown();
                }

                @Override
                public void onFailure(DJIError error) {
                    cdl.countDown();
                }
            });
            cd_wait(cdl,3);
        }
    }
    public void onHomeLocation(DJILocationCoordinate2D loc){

    }
    public CountDownLatch cd_wait(CountDownLatch cd,int seconds){
        try{
            cd.await(seconds,TimeUnit.SECONDS);
        }catch (Exception e){

        }finally {
            return new CountDownLatch(1);
        }
    }
    public void rotate_camera_cw(CountDownLatch cdx){
        takePhoto(cdx);
        cdx=cd_wait(cdx,3);
        rotateCamera_45_cc(cdx);
        takePhoto(cdx);
        cdx=cd_wait(cdx,3);
        for(int i=0;i<4;i++) {
            rotateCamera_90_cw(cdx);
            cdx = cd_wait(cdx, 3);
            takePhoto(cdx);
            cdx = cd_wait(cdx, 3);
        }
    }
    public void rotate_camera_cc(CountDownLatch cdx){
        takePhoto(cdx);
        cdx = cd_wait(cdx, 3);
        for(int i=0;i<4;i++) {
            rotateCamera_90_cc(cdx);
            cdx = cd_wait(cdx, 3);
            takePhoto(cdx);
            cdx = cd_wait(cdx, 3);
        }
        rotateCamera_45_cw(cdx);
        cdx=cd_wait(cdx,3);
        takePhoto(cdx);
        cdx=cd_wait(cdx,3);
    }
    public void take1Phot(){
        if(camera!=null){
            DJICameraSettingsDef.CameraPhotoIntervalParam param=new DJICameraSettingsDef.CameraPhotoIntervalParam();
            param.timeIntervalInSeconds=2;
            //camera.setPhotoIntervalParam(param,new CameraCompletionCallback());
            //camera.startShootPhoto(DJICameraSettingsDef.CameraShootPhotoMode.Interval);
        }
    }
    public void take3Photos(int mode){
        if(camera!=null&&gimbal!=null){
            DJIGimbalAttitude attitude= gimbal.getAttitudeInDegrees();
            for(int i=0;i<3;i++) {
                final CountDownLatch cd=new CountDownLatch(1);

                gimbal.rotateGimbalByAngle(DJIGimbalRotateAngleMode.AbsoluteAngle,
                        new DJIGimbalAngleRotation(true, attitude.pitch, DJIGimbalRotateDirection.Clockwise),
                        new DJIGimbalAngleRotation(true, 0, DJIGimbalRotateDirection.Clockwise),
                        new DJIGimbalAngleRotation(true, 15 * mode, DJIGimbalRotateDirection.Clockwise),
                        new DJICommonCallbacks.DJICompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError == null) {
                                    camera.startShootPhoto(DJICameraSettingsDef.CameraShootPhotoMode.Single, new DJICommonCallbacks.DJICompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            cd.countDown();
                                        }
                                    });
                                }
                            }
                        });
                try{
                    cd.await(2, TimeUnit.SECONDS);
                }catch (Exception e){

                }
            }
        }
    }

    @Override
    public void onStartMission() {

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
                    gimbal.resetGimbal(new DJICommonCallbacks.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                CountDownLatch cd=new CountDownLatch(1);
                                resetCameraPosition(cd);
                                cd_wait(cd,3);
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
        mMission=(DJICustomMission)mCM.generateGoHomeMission(null,0);
        final CountDownLatch cdl=new CountDownLatch(1);
        mMissonManager.prepareMission(mMission, null, new DJICommonCallbacks.DJICompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                cdl.countDown();
            }
        });
        try {
            cdl.await(3,TimeUnit.SECONDS);
            mMissonManager.startMissionExecution(new DJICommonCallbacks.DJICompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {

                }
            });
        }catch (Exception e){

        }
    }
    @Override
    public void onStopMission() {
        if(mMissonManager!=null){
            mMissonManager.stopMissionExecution(new DJICommonCallbacks.DJICompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {

                }
            });
        }
    }

    @Override
    public int onDeleteProject(String prj_name) {
        mPrjDB.deleteProject(prj_name);
       return  0;
    }

    @Override
    public void onNewProject(String prj_name) {
        mPrjDB.addProject(prj_name);
    }
    public void onLoadProject(String prj_name)
    {
        mPrjDB.openProject(prj_name);
        TextView tv1=((TextView) findViewById(R.id.nav_prj_name));
    }

    @Override
    public void onSaveWayPoint(String fname,ArrayList<WayPoint> wps) {//当点击保存航线按钮确认后会调用
        if(wps!=null)
        {
            if(mProjectFrg!=null)
            {
                mPrjDB.addWayline(fname);
                mPrjDB.addWaypoints(wps);
                mWayPoints=wps;
            }
        }
    }
    public void onLoadWaypoint(String wpfile)
    {
        mWayPoints=mPrjDB.getWaypoints(wpfile);
        //mNavHeadFrg.update();
        mMapFrg.setWayPoints(mWayPoints).drawline(true);
    }

    @Override
    public void onNewPicture(String pname) {
        if(flightController!=null) {
            //用于计算飞机位置与上一拍摄点距离
            DJILocationCoordinate2D loc=flightController.getCurrentState().getAircraftLocation().getCoordinate2D();
            mStateHandler.setPhotoTakenPosition(loc);
            //获取飞机的姿态
            DJIAttitude att=flightController.getCurrentState().getAttitude();
            //保存到当前
            mPrjDB.addPhotoInfo(pname,loc.getLatitude(),loc.getLongitude(),att.yaw,att.pitch);
        }
    }
    public void onReachTarget(int index) {

    }

    @Override
    public void onTakePhoto(int index) {

    }

    @Override
    public void onGotoStep() {

    }

    @Override
    public void onLeftTarget(int index) {
        mWayPoints.get(index).status=WayPointStatus.Done;
    }

    @Override
    public float getHeading() {
        if(flightController!=null)
            return (float)flightController.getCompass().getHeading();
        return 0;
    }
}