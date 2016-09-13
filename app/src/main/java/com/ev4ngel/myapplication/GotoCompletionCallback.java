package com.ev4ngel.myapplication;

import android.util.Log;

import java.util.concurrent.CountDownLatch;

import dji.common.gimbal.DJIGimbalAngleRotation;
import dji.common.gimbal.DJIGimbalRotateDirection;
import dji.common.util.DJICommonCallbacks;
import dji.common.error.DJIError;

/**
 * Created by Administrator on 2016/7/7.
 * 到达指定位置后执行拍照操作。
 * 第一个顺序为：下-前-右-后-左，顺时针，
 * 下一点顺序为：左-后-右-前-下，逆时针
 * 同时记录当前姿态信息给文件
 */
public class GotoCompletionCallback implements DJICommonCallbacks.DJICompletionCallback {
    public interface OnComponentOperationListener{
        void rotateCamera(int a,int b);
        void resetCamera();
    }
    private int mCompletionStatus;
    private OnComponentOperationListener mListener;
    private int mDirection=1;//旋转方向，1为逆时针，0为顺时针
    int mIndex=0;
    CountDownLatch mCountDownLatch;
    CountDownLatch mCd;
    CustomMission.OnGimbalOperationListener mGimbalOperationListener;
    CustomMission.OnMissionProcessListener mMissionProcessListener;
    public GotoCompletionCallback(int index,CustomMission.OnGimbalOperationListener listener1,CustomMission.OnMissionProcessListener listener2,CountDownLatch cd)
    {
        mGimbalOperationListener=listener1;
        mIndex=index;
        mMissionProcessListener=listener2;
        mCountDownLatch=cd;
    }
    public void setOnComponentOperationListener(OnComponentOperationListener l){
        mListener=l;
    }

    @Override
    public void onResult(DJIError djiError) {
        if (djiError == null){
            if(mMissionProcessListener!=null) {
                mMissionProcessListener.onReachTarget(mIndex);
            }
            if(mGimbalOperationListener!=null)
                if(mIndex%2==0)
                    mGimbalOperationListener.rotate_camera_cw(mCountDownLatch);
                else
                    mGimbalOperationListener.rotate_camera_cc(mCountDownLatch);
        }else {

            /* //I REALLY want to write here,but what the fuck two kind of LocationCoordinate2D???
            try {
                mFile.addWayPoint(pwPoint.setPosition(mFC.getCurrentState().getAircraftLocation().getCoordinate2D()));
            }catch(Exception e)
            {

            }
            */
            //pwPoint.startTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            //pwPoint.heading= mFC.getCompass().getHeading();
            DJIGimbalRotateDirection direction=(mDirection==0)? DJIGimbalRotateDirection.Clockwise: DJIGimbalRotateDirection.CounterClockwise;
            DJIGimbalAngleRotation
                    mPitchRotation=new DJIGimbalAngleRotation(true,0,direction),
                    mRollRotation=new DJIGimbalAngleRotation(true,0,direction),
                    mYawRotation=new DJIGimbalAngleRotation(true,0,direction);
            for(int i=0;i<5;i++) {
                if(i==0){//第一个位置不转相机
                    //mListener.rotateCamera();
                    /*if (mCamera != null) {
                        mCamera.startShootPhoto(
                                DJICameraSettingsDef.CameraShootPhotoMode.Single,
                                new DJIBaseComponent.DJICompletionCallback() {
                                    @Override
                                    public void onResult(DJIError djiError) {
                                        if (null == djiError)
                                            Log.i("e", "PhotoSuccess");
                                        else
                                            Log.i("e", djiError.getDescription());
                                    }
                                }
                        );
                    }*/
                    //mYawRotation.angle = 0;
                    //mPitchRotation.angle = 0;
                    //mRollRotation.angle = 0;
                }else {
                    if (mDirection == 0) {
                        if (i == 1)//当shun时针时第二张需要抬头45度
                        {
                            mPitchRotation.angle = Common.camPitchDeltaAngle;
                            mYawRotation.angle = 0;
                        } else {
                            mPitchRotation.angle =0;
                            mYawRotation.angle=Common.camYawDeltaAngle;
                        }
                    } else {
                        if(i == 4)//ni时针时低头45度
                        {
                            mPitchRotation.angle =Common.camPitchDeltaAngle;
                            mYawRotation.angle = 0;
                        }else
                        {
                            mPitchRotation.angle =0;
                            mYawRotation.angle=Common.camYawDeltaAngle;
                        }
                    }
                }
                /*mGimbal.rotateGimbalByAngle(DJIGimbal.DJIGimbalRotateAngleMode.RelativeAngle, mPitchRotation, mRollRotation, mYawRotation, new DJIBaseComponent.DJICompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (null == djiError) {
                            //Take photo
                            if (mCamera != null) {
                                mCamera.startShootPhoto(
                                        DJICameraSettingsDef.CameraShootPhotoMode.Single,
                                        new DJIBaseComponent.DJICompletionCallback() {
                                            @Override
                                            public void onResult(DJIError djiError) {
                                                if (null == djiError)
                                                    Log.i("e", "PhotoSuccess");
                                                else
                                                    Log.i("e", djiError.getDescription());
                                            }
                                        }
                                );
                            }
                        } else {

                        }
                    }
                });*/
            }
            //pwPoint.stopTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            mListener.resetCamera();
        }
    }

    //@Override
    //public void onResult(DJIMedia djiMedia) {
        //DJIGimbal.DJIGimbalAttitude att=mGimbal.getAttitudeInDegrees();
        //pwPoint.addPhoto(new PhotoInfo(djiMedia.getFileName(),att.yaw,att.pitch));
        //mFile.write();
    //}
}
