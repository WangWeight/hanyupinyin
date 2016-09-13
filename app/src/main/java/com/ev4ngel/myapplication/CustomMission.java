package com.ev4ngel.myapplication;

import android.util.Log;

import com.ev4ngel.autofly_prj.OnNewPictureGenerateListener;
import com.ev4ngel.autofly_prj.WayPoint;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import dji.common.flightcontroller.DJILocationCoordinate2D;

import dji.common.gimbal.DJIGimbalAngleRotation;
import dji.common.gimbal.DJIGimbalRotateAngleMode;
import dji.common.gimbal.DJIGimbalRotateDirection;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.base.DJIBaseComponent;
import dji.common.error.DJIError;
import dji.sdk.camera.DJICamera;
import dji.sdk.camera.DJIMedia;
import dji.sdk.gimbal.DJIGimbal;
import dji.sdk.missionmanager.DJICustomMission;
import dji.sdk.missionmanager.DJIMission;
import dji.sdk.missionmanager.missionstep.DJIAircraftYawStep;
import dji.sdk.missionmanager.missionstep.DJIGimbalAttitudeStep;
import dji.sdk.missionmanager.missionstep.DJIGoHomeStep;
import dji.sdk.missionmanager.missionstep.DJIGoToStep;
import dji.sdk.missionmanager.missionstep.DJIMissionStep;
import dji.sdk.missionmanager.missionstep.DJIShootPhotoStep;

/**
 * Created by Administrator on 2016/7/13.
 * code like:
 * CustomMission cm=new CustomMission(flightcontrol,gimbal,camera);
 * cm.initBoundary(points)
 * cm.generateCooridate(width)
 * mMission=cm.genrateMission()
 *
 */
public class CustomMission implements DJICamera.CameraGeneratedNewMediaFileCallback {
    public interface OnMissionProcessListener{
        void onTakePhoto(int index);
        void onGotoStep();
        void onReachTarget(int index);
        void onLeftTarget(int index);
        float getHeading();
    }
    public interface  OnGimbalOperationListener{
        public void rotate_camera_cw(CountDownLatch latch);
        public void rotate_camera_cc(CountDownLatch latch);
    }
    private ArrayList<DJILocationCoordinate2D> boundary;
    public ArrayList<WayPoint> wayPoints;
    private OnNewPictureGenerateListener mListener;
    private OnMissionProcessListener mMisProListener;
    private OnGimbalOperationListener mGimbalListener;
    public static   DJIGimbalAngleRotation  mRotateRelative_90_cw=new DJIGimbalAngleRotation(true, 90, DJIGimbalRotateDirection.Clockwise);
    public static   DJIGimbalAngleRotation  mRotateRelative_90_cc=new DJIGimbalAngleRotation(true, 90, DJIGimbalRotateDirection.CounterClockwise);
    public static   DJIGimbalAngleRotation  mPitchRelative_45_cc=new DJIGimbalAngleRotation(true, 45, DJIGimbalRotateDirection.CounterClockwise);
    public static   DJIGimbalAngleRotation  mPitchRelative_45_cw=new DJIGimbalAngleRotation(true, 45, DJIGimbalRotateDirection.Clockwise);
    public int fly_speed;
    public int return_height;
    private DJIGimbalAttitudeStep getDownCameraStep(int pitch){
        return new DJIGimbalAttitudeStep(DJIGimbalRotateAngleMode.AbsoluteAngle,
                new DJIGimbalAngleRotation(true, pitch, DJIGimbalRotateDirection.Clockwise),
                new DJIGimbalAngleRotation(true, 0, DJIGimbalRotateDirection.Clockwise),
                new DJIGimbalAngleRotation(true, 0, DJIGimbalRotateDirection.Clockwise), null);
    }

    public CustomMission()
    {
    }

    public void setGimbalListener(OnGimbalOperationListener gimbalListener) {
        mGimbalListener = gimbalListener;
    }

    public void setWayPoints(ArrayList<WayPoint> wp)
    {
        wayPoints=wp;
    }
    //public DJIMission generate
    public DJIMission generateInspireNoStopMission(){
        return new DJICustomMission(null);
    }

    public DJIMission generate3PhotoMission(){
        return new DJICustomMission(null);
    }

    /*
    使用CountDownLatch来控制异步，而不是使用MissionStep
     */
    public DJIMission generateInspireMission2(){
        ArrayList<DJIMissionStep> steps=new ArrayList<>() ;
        int count=0;
        CountDownLatch cdl=new CountDownLatch(1);
        for(WayPoint wp:wayPoints)
        {
            //GotoCompletionCallback gtc=new GotoCompletionCallback();
            //gtc.setOnComponentOperationListener(mOnComponentOperationListener);
            final int ct_count=count;
            DJIGoToStep gotoStep = new DJIGoToStep(wp.lat, wp.lng,new GotoCompletionCallback(ct_count,mGimbalListener,mMisProListener,cdl));
            gotoStep.setFlightSpeed(fly_speed);
            steps.add(gotoStep);
            count++;
        }
        return new DJICustomMission(steps);
    }
    public DJIMission generateInspireMission()
    {
        ArrayList<DJIMissionStep> steps=new ArrayList<>() ;
        int count=0;
        for(WayPoint wp:wayPoints)
        {
            //GotoCompletionCallback gtc=new GotoCompletionCallback();
            //gtc.setOnComponentOperationListener(mOnComponentOperationListener);
            final int ct_count=count;
            DJIGoToStep gotoStep = new DJIGoToStep(wp.lat, wp.lng, new DJICommonCallbacks.DJICompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if(mMisProListener!=null)
                        mMisProListener.onReachTarget(ct_count);
                }
            });
            gotoStep.setFlightSpeed(fly_speed);
            steps.add(gotoStep);

            if(count==0) {//到达第一个指定位置后初始为向下镜头
                steps.add(getDownCameraStep(-90));
                if(mMisProListener!=null&&wayPoints.size()>2){
                    double heading=mMisProListener.getHeading();
                    steps.add(new DJIAircraftYawStep(new CalcBox().coorNageCalcAngle(wayPoints.get(0).toLatLng(), wayPoints.get(1).toLatLng())-heading, 50, new DJICommonCallbacks.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {

                        }
                    }));//调整机头方向一致
                }

            }
            for(int i=0;i<5;i++) {
                final int ii=i;
                if(i!=0) {
                    DJIGimbalAngleRotation yaw=new DJIGimbalAngleRotation(true,0, DJIGimbalRotateDirection.Clockwise);
                    DJIGimbalAngleRotation pitch=new DJIGimbalAngleRotation(true,0,DJIGimbalRotateDirection.Clockwise);
                    DJIGimbalAngleRotation roll=new DJIGimbalAngleRotation(true,0, DJIGimbalRotateDirection.Clockwise);

                    if(count%2==0){//偶数顺时针，pitch逆时针
                        if(i==1) {
                            yaw.angle=0;
                            pitch.angle =45;
                            pitch.direction= DJIGimbalRotateDirection.Clockwise;
                        }
                        else {
                            yaw.angle=90;
                            yaw.direction= DJIGimbalRotateDirection.Clockwise;
                            pitch.angle = 0;
                        }
                    }else {
                        if(i==4) {
                            yaw.angle=0;
                            pitch.angle = 45;
                            pitch.direction= DJIGimbalRotateDirection.CounterClockwise;
                        } else {
                            yaw.angle=90;
                            yaw.direction= DJIGimbalRotateDirection.CounterClockwise;
                            pitch.angle = 0;
                        }
                    }

                    DJIGimbalAttitudeStep att_step = new DJIGimbalAttitudeStep(DJIGimbalRotateAngleMode.RelativeAngle,
                            pitch, roll,yaw, new DJICommonCallbacks.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            Log.i("E", "is null?" + (djiError == null));

                        }
                    });
                    att_step.completionTime=0.5;
                    steps.add(att_step);
                }
                steps.add(new DJIShootPhotoStep(new DJICommonCallbacks.DJICompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if(mMisProListener!=null) {
                            mMisProListener.onTakePhoto(ii);
                            if (ii == 4)
                                mMisProListener.onLeftTarget(ct_count);
                        }
                    }
                }));
            }
            count++;
        }
        /*DJIWaypointMission mission=new DJIWaypointMission();
        mission.setAutoFlightSpeed(10, new DJIBaseComponent.DJICompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {

            }
        });
        for(WayPoint wp:wayPoints){
            DJIWaypoint dwp=new DJIWaypoint(wp.lat,wp.lng,0);
            dwp.addAction(new DJIWaypoint.DJIWaypointAction(DJIWaypoint.DJIWaypointActionType.StartTakePhoto,1));
            mission.addWaypoint(dwp);
        }
        return mission;*/
        return new DJICustomMission(steps);
    }
    public DJIMission generatePhantomMission()
    {

        ArrayList<DJIMissionStep> steps=new ArrayList<DJIMissionStep>() ;
        for(WayPoint wp:wayPoints)
        {
            final WayPoint wwp=wp;
            DJIGoToStep goToStep=new DJIGoToStep(wwp.lat, wwp.lng, new DJICommonCallbacks.DJICompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    Log.i("E","@"+wwp.lat+","+wwp.lng);
                }
            });
            steps.add(goToStep);
        }
        return new DJICustomMission(steps);/*
        for(int i=0;i<wayPoints.size();i++)
        {
            final int ii=i;
            if(wayPoints.get(ii)!=null) {
                DJIGoToStep gotoStep = new DJIGoToStep(wayPoints.get(ii).lat, wayPoints.get(ii).lng, new DJIBaseComponent.DJICompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if(djiError==null) {
                            Log.i("ev4n","Finish step(" + ii + "/" + wayPoints.size() + ")");
                        }
                        else
                        {
                            Log.i("ev4n","Fail step :"+djiError.getDescription());
                        }
                    }
                });
                gotoStep.setFlightSpeed(fly_speed);
                steps.add(gotoStep);
                //摄像头角度下视
                steps.add(new DJIGimbalAttitudeStep(DJIGimbal.DJIGimbalRotateAngleMode.AbsoluteAngle,
                        new DJIGimbal.DJIGimbalAngleRotation(true, -90, DJIGimbal.DJIGimbalRotateDirection.Clockwise),
                        new DJIGimbal.DJIGimbalAngleRotation(false, 0, DJIGimbal.DJIGimbalRotateDirection.Clockwise),
                        new DJIGimbal.DJIGimbalAngleRotation(false, 0, DJIGimbal.DJIGimbalRotateDirection.Clockwise),
                        new DJIBaseComponent.DJICompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                mPhotopoint=new PhotoWayPoint();
                                DJIFlightControllerDataType.DJIFlightControllerCurrentState state=fc.getCurrentState();
                                DJIFlightControllerDataType.DJILocationCoordinate3D location= state.getAircraftLocation();
                                mPhotopoint.setPosition(location.getCoordinate2D());
                                if(AutoflyApplication.isInspire())
                                {

                                }else {
                                    mPhotopoint.heading=0;
                                }
                                mPhotopoint.alt=location.getAltitude();
                                mPhotopoint.startTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                            }
                        }
                ));
                steps.add(new DJIShootPhotoStep(new DJIBaseComponent.DJICompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if(djiError==null)
                        {

                        }
                    }
                }));
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
                                        if (djiError == null) {
                                            Log.i("ev4n","Photo ok");
                                        }
                                    }
                                });
                            }
                        }
                ));
                for(int j=0;j<3;j++) {//三次旋转拍照
                    DJIAircraftYawStep yawStep = new DJIAircraftYawStep(90, rotate_speed, new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if(djiError==null)//成功后拍照
                            {
                            }
                        }
                    });
                    steps.add(yawStep);
                    steps.add(new DJIShootPhotoStep(new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if(djiError==null)
                            {

                            }
                        }
                    }));
                }
            }
        }
        return new DJICustomMission(steps);
        */
    }

    public DJIMission generateMission()
    {
        if(AutoflyApplication.isInspire()) {
            return generateInspireMission();
        }
        else {
            return generatePhantomMission();
        }
    }

    public DJIMission generateGoHomeMission(DJILocationCoordinate2D latlng,int height)
    {
        ArrayList<DJIMissionStep> list=new ArrayList<>();
        list.add(getDownCameraStep(-45));
        list.add(new DJIGoHomeStep(null));
        (new DJIGoHomeStep(null)).run();
        return new DJICustomMission(list);

    }
    /*
    @parma:direction 方向，1顺时针，-1逆时针
     */
    public DJIMission generate3PhotosMission(int pitch,int direction){
        ArrayList<DJIMissionStep> list=new ArrayList<>();
        for(int i=0;i<3;i++){
            list.add(new DJIGimbalAttitudeStep(DJIGimbalRotateAngleMode.AbsoluteAngle,
                            new DJIGimbalAngleRotation(true, pitch, DJIGimbalRotateDirection.Clockwise),
                            new DJIGimbalAngleRotation(false, 0, DJIGimbalRotateDirection.Clockwise),
                            new DJIGimbalAngleRotation(false, 15 * (i-1)*direction, DJIGimbalRotateDirection.Clockwise),
                            null));
            list.add(new DJIShootPhotoStep(null));
            }
        return new DJICustomMission(list);
    }
    public void setOnNewPictureListener(OnNewPictureGenerateListener l)
    {
        mListener=l;
    }

    public void setOnMissionProcessListener(OnMissionProcessListener misProListener) {
        mMisProListener = misProListener;
    }

    @Override
    public void onResult(DJIMedia djiMedia) {
        if(djiMedia!=null)
        {
            if(mListener!=null)
                mListener.onNewPicture(djiMedia.getFileName());
        }
    }
}
