package com.ev4ngel.myapplication;

import android.util.Log;

import com.ev4ngel.autofly_prj.PhotoInfo;
import com.ev4ngel.autofly_prj.PhotoWayPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dji.sdk.Camera.DJICamera;
import dji.sdk.Camera.DJICameraSettingsDef;
import dji.sdk.Camera.DJIMedia;
import dji.sdk.FlightController.DJIFlightController;
import dji.sdk.FlightController.DJIFlightControllerDataType;
import dji.sdk.Gimbal.DJIGimbal;
import dji.sdk.MissionManager.DJICustomMission;
import dji.sdk.MissionManager.DJIMission;
import dji.sdk.MissionManager.MissionStep.DJIAircraftYawStep;
import dji.sdk.MissionManager.MissionStep.DJIGimbalAttitudeStep;
import dji.sdk.MissionManager.MissionStep.DJIGoToStep;
import dji.sdk.MissionManager.MissionStep.DJIMissionStep;
import dji.sdk.MissionManager.MissionStep.DJIShootPhotoStep;
import dji.sdk.base.DJIBaseComponent;
import dji.sdk.base.DJIError;
import dji.sdk.util.DJILocationCoordinate2D;

/**
 * Created by Administrator on 2016/7/13.
 * code like:
 * CustomMission cm=new CustomMission(flightcontrol,gimbal,camera);
 * cm.initBoundary(points)
 * cm.generateCooridate(width)
 * mMission=cm.genrateMission()
 *
 */
public class CustomMission implements DJICamera.CameraGeneratedNewMediaFileCallback{
    private DJICamera camera=null;
    private DJIGimbal gimbal=null;
    private DJIFlightController fc=null;
    private DJIMission mMission=null;
    private ArrayList<DJIFlightControllerDataType.DJILocationCoordinate2D> boundary;
    public ArrayList<DJIFlightControllerDataType.DJILocationCoordinate2D> wayPoints;
    //private PhotoWayPointFile mPhotofile;
    private PhotoWayPoint mPhotopoint=null;

    public int rotate_speed;
    public int fly_speed;
    private T log;
    public CustomMission(T t)
    {
        log=t;
        wayPoints=new ArrayList<>();
        //mPhotofile=new PhotoWayPointFile("wangwei.txt");
        rotate_speed=50;
        fly_speed=10;
    }

    public DJIMission generateInspireMission()
    {
        ArrayList<DJIMissionStep> steps=new ArrayList<DJIMissionStep>() ;
        for(int i=0;i<wayPoints.size();i++) {
            final int ii=i;
            if(wayPoints.get(ii)!=null) {
                DJIGoToStep gotoStep = new DJIGoToStep(wayPoints.get(ii).getLatitude(), wayPoints.get(ii).getLongitude(), new GotoCompletionCallback(fc, gimbal, camera, ii % 2, null));
                steps.add(gotoStep);
                gotoStep.setFlightSpeed(fly_speed);
            }
        }
        return new DJICustomMission(steps);
    }
    public DJIMission generatePhantomMission()
    {
        ArrayList<DJIMissionStep> steps=new ArrayList<DJIMissionStep>() ;
        for(int i=0;i<wayPoints.size();i++)
        {
            final int ii=i;
            if(wayPoints.get(ii)!=null) {
                DJIGoToStep gotoStep = new DJIGoToStep(wayPoints.get(ii).getLatitude(), wayPoints.get(ii).getLongitude(), new DJIBaseComponent.DJICompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if(djiError==null) {
                            Log.i("ev4n","Finish step(" + ii + "/" + wayPoints.size() + ")");
                            //在飞到下一个点后才存储，以防调用onNewMedia有延迟
                            //mPhotofile.addWayPoint(mPhotopoint);
                            //mPhotofile.write();
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
    }
    public  void initComponet(DJIFlightController _fc, DJIGimbal _g,DJICamera _c)
    {
        camera=_c;
        gimbal=_g;
        fc=_fc;
    }
    public DJIMission generateMission()
    {
        if(camera==null ||gimbal==null||fc==null) {
            Log.i("ev4n","FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
            Log.i("ev4n","null happend");
        }else
        {
            Log.i("ev4n","Init component ok");
        }
        if(AutoflyApplication.isInspire()) {
            return generateInspireMission();
        }
        else {
            return generatePhantomMission();
        }
    }

    @Override
    public void onResult(DJIMedia djiMedia) {
        if(djiMedia==null)
        {
            DJIGimbal.DJIGimbalAttitude att=gimbal.getAttitudeInDegrees();
            PhotoInfo pi=new PhotoInfo();
            pi.Name=djiMedia.getFileName();
            pi.Pitch=att.pitch;
            if(AutoflyApplication.isInspire())
            {
                pi.Yaw=att.yaw;
            }else
            {
                pi.Yaw=fc.getCompass().getHeading();
            }
            if(mPhotopoint!=null)
            {
                mPhotopoint.addPhoto(pi);
            }
        }
    }
}
