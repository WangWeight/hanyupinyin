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
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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
import dji.sdk.MissionManager.DJIMissionManager;
import dji.sdk.MissionManager.MissionStep.DJIAircraftYawStep;
import dji.sdk.MissionManager.MissionStep.DJIFollowmeMissionStep;
import dji.sdk.MissionManager.MissionStep.DJIGoToStep;
import dji.sdk.MissionManager.MissionStep.DJIMissionStep;
import dji.sdk.SDKManager.DJISDKManager;
import dji.sdk.base.DJIBaseComponent;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.base.DJIError;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DJIBattery.DJIBatteryStateUpdateCallback,
        DJIBaseComponent.DJICompletionCallback,

        Toolbar.OnMenuItemClickListener,
        DJIFlightControllerDelegate.FlightControllerUpdateSystemStateCallback,
        View.OnClickListener {

    private int clickcount=0;
    private DJIGimbal gimbal = null;
    private DJICamera camera = null;
    private DJIBattery battery = null;
    private DJIFlightController flightController = null;
    private DJIMissionManager mMissonManager = null;
    private DJICustomMission mMission = null;
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
    private FloatingActionButton camFab;
    private FloatingActionButton gimbalFab;
    private PhotoWayPointFile mPWPFile;


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

            } else {
                batteryTempView.setText("Connection Lost");
                isConnectedView.setText(Common.is_connected_no);
            }
        }
    };

    public void registerComponentListener() {
        //battery.setBatteryStateUpdateCallback(this);
        //camera.setDJICameraGeneratedNewMediaFileCallback(this);
        //flightController.setUpdateSystemStateCallback(this);
    }


    public void registerUiListener(){
       camFab.setOnClickListener(this);
       gimbalFab.setOnClickListener(this);
   }


      private void _i(String msg){
          Tools.showToast(getApplicationContext(), msg);
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
        if( AutoflyApplication.isAircraftConnected())
        {
            gimbal = getProduct().getGimbal();
            if (gimbal == null) {
                Tools.showToast(getApplicationContext(), "Init Gimbal Fail");
            }
            else{
                Tools.showToast(getApplicationContext(),"Get Gimbal Successful");
            }
        }

    }
    private void getCamera()//初始化相机
    {
        if(AutoflyApplication.isAircraftConnected())
        {
            camera = getProduct().getCamera();
            if (camera == null) {
                Tools.showToast(getApplicationContext(), "Init Camera Fail");
            }
            Tools.showToast(getApplicationContext(),"Camera successful");
    }
    else {
            Tools.showToast(getApplicationContext(),"Camera Fail");
        }
    }
    private void getBattery()//初始化电池
    {
        if(AutoflyApplication.isAircraftConnected())
        {
            battery = getProduct().getBattery();
            if (battery == null) {
                Tools.showToast(getApplicationContext(), "Init Battery Fail");
            }
            Tools.showToast(getApplicationContext(),"Battery successful");
        }
        else {
            Tools.showToast(getApplicationContext()," Battery Fail");
        }
    }
    private void getFlightController()//初始化飞控
    {
            if (AutoflyApplication.getAircraftInstance() != null) {
                flightController = AutoflyApplication.getAircraftInstance().getFlightController();
                if (flightController == null) {
                    Tools.showToast(getApplicationContext(), "Init flightc Fail");
                } else {
                    Tools.showToast(getApplicationContext(), "Get flightc Successful");
                }
            } else {
                Tools.showToast(getApplicationContext(), "Get flightc Fail");
            }
    }

    private void generateMissions()
    {
        ArrayList<DJIMissionStep> steps=new ArrayList<DJIMissionStep>() ;

        for(int i=0;i<missionLocations.size();i++) {//航线
            ArrayList<DJIFlightControllerDataType.DJILocationCoordinate3D> line=missionLocations.get(i);
            for(int j=0;j<line.size();j++) {//航线中的航点
                final DJIFlightControllerDataType.DJILocationCoordinate3D location=line.get(j);
                if(i==0) {
                    if(j==0) {
                        DJIFlightControllerDataType.DJILocationCoordinate3D loc = flightController.getCurrentState().getAircraftLocation();
                        
                    }
                 }
                //DJIGoToStep gotoStep = new DJIGoToStep(location.getLatitude(),location.getLongitude(),new GotoCompletionCallback(getApplicationContext(),flightController,gimbal,camera,i%2,mPWPFile));
                DJIGoToStep gotoStep = new DJIGoToStep(location.getLatitude(), location.getLongitude(), new DJIBaseComponent.DJICompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        _i("I am @"+location.getLatitude()+","+location.getLongitude());
                    }
                });
                steps.add(gotoStep);
                if(j==(line.size()-1))//在结束时需要转向
                {
                    steps.add(new DJIAircraftYawStep(0,20,null));
                }
            }
        }
        mMissonManager.prepareMission(new DJICustomMission(steps), null, this);
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
        camFab=(FloatingActionButton)findViewById(R.id.camer_fab);
        gimbalFab=(FloatingActionButton)findViewById(R.id.gimbal_fab);
        //camFab.setBackground(getResources().getDrawable(R.drawable.box,getTheme()));
        isConnectedView=(TextView)findViewById(R.id.is_connected);
        //gimbalFab.setBackgroundColor(getResources().getColor(R.color.fad_invalid,getTheme()));
    }

    private void initParams()
    {
        missionLocations=new ArrayList<ArrayList<DJIFlightControllerDataType.DJILocationCoordinate3D>>();
        //mPWPFile=new PhotoWayPointFile(getApplicationContext(),"");
        DJIFlightControllerDataType.DJILocationCoordinate3D[] a={
                new DJIFlightControllerDataType.DJILocationCoordinate3D(41.80319444,123.42775278,0),
                new DJIFlightControllerDataType.DJILocationCoordinate3D(41.80307748,123.42775835,0),
                new DJIFlightControllerDataType.DJILocationCoordinate3D(41.80296111,123.42776389,0),
                new DJIFlightControllerDataType.DJILocationCoordinate3D(41.80297307,123.42791949,0),
                new DJIFlightControllerDataType.DJILocationCoordinate3D(41.80308740,123.42791413,0),
                new DJIFlightControllerDataType.DJILocationCoordinate3D(41.80320436,123.42790864,0),
                new DJIFlightControllerDataType.DJILocationCoordinate3D(41.80321389,123.42805833,0),
                new DJIFlightControllerDataType.DJILocationCoordinate3D(41.80309692,123.42806390,0),
                new DJIFlightControllerDataType.DJILocationCoordinate3D(41.80298458,123.42806925,0)};
        missionLocations.add(new ArrayList<DJIFlightControllerDataType.DJILocationCoordinate3D>());
        missionLocations.add(new ArrayList<DJIFlightControllerDataType.DJILocationCoordinate3D>());
        missionLocations.add(new ArrayList<DJIFlightControllerDataType.DJILocationCoordinate3D>());
        for(int i=0;i<a.length/3;i++)
        {
            missionLocations.get(i/3).add(a[i]);
        }
        mMissonManager=DJIMissionManager.getInstance();
        getGimbal();
        getCamera();
        getBattery();
        initUi();
        registerUiListener();
    }
    private void setPageInvisibility()
    {
        projectPage.setVisibility(View.GONE);
        airlinePage.setVisibility(View.GONE);
        camOrMapPage.setVisibility(View.GONE);
        settingPage.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initParams();
        setPageInvisibility();//设置所有page不可见
        projectPage.setVisibility(View.VISIBLE);//将project设置可见

        IntentFilter filter = new IntentFilter();
        filter.addAction(AutoflyApplication.FLAG_CONNECTION_CHANGE);
        registerReceiver(mReceiver, filter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
    public void onResult(DJIBattery.DJIBatteryState state) {
        batteryVolView.setText(state.getCurrentVoltage()+"mV");
        batteryRemainView.setText(state.getBatteryEnergyRemainingPercent()*100+"%");
        batteryTempView.setText(state.getBatteryTemperature()+"C");
        Tools.showToast(getApplicationContext(),""+state.getBatteryEnergyRemainingPercent()+","+state.getCurrentVoltage());
    }

    @Override
    public void onResult(DJIError djiError) {
        Tools.showToast(getApplicationContext(),djiError.getDescription());
    }
  @Override
    protected void onDestroy() {
        ///getApplicationContext().unregisterReceiver(mReceiver);
        super.onDestroy();
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
        gpsCountView.setText(""+state.getSatelliteCount());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab: {
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
                }
            };break;
            case R.id.camer_fab:
            {
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
            };break;
            case R.id.gimbal_fab:
            {
                if(AutoflyApplication.isAircraftConnected())
                {
                    Tools.showToast(getApplicationContext(),"connect");
                    resetCameraPosition();
                }
                else
                    Tools.showToast(getApplicationContext(),"Inconnect");
            };break;
        }
    }
}
