package com.ev4ngel.autofly_prj;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.amap.api.maps2d.model.LatLng;
import com.ev4ngel.myapplication.CalcBox;
import com.ev4ngel.myapplication.MapFrg;
import com.loc.p;

import dji.common.battery.DJIBatteryState;
import dji.common.flightcontroller.DJIAttitude;
import dji.common.flightcontroller.DJIFlightControllerCurrentState;
import dji.common.flightcontroller.DJIFlightControllerDataType;
import dji.common.flightcontroller.DJILocationCoordinate2D;
import dji.common.camera.CameraSDCardState;
import dji.common.flightcontroller.DJILocationCoordinate3D;
import dji.common.gimbal.DJIGimbalAdvancedSettingsState;
import dji.common.gimbal.DJIGimbalAttitude;
import dji.common.gimbal.DJIGimbalState;
import dji.common.remotecontroller.DJIRCGPSData;
import dji.sdk.battery.DJIBattery;
import dji.sdk.camera.DJICamera;
import dji.sdk.flightcontroller.DJIFlightControllerDelegate;
import dji.sdk.gimbal.DJIGimbal;
import dji.sdk.remotecontroller.DJIRemoteController;

/**
 * Created by Administrator on 2016/8/2.
 */
public class StateHandler extends Handler implements
        DJIBattery.DJIBatteryStateUpdateCallback,
        DJICamera.CameraUpdatedSDCardStateCallback,
        DJIFlightControllerDelegate.FlightControllerUpdateSystemStateCallback,
        DJIRemoteController.RCGpsDataUpdateCallback,
        DJIGimbal.GimbalAdvancedSettingsStateUpdateCallback,
        DJIGimbal.GimbalStateUpdateCallback
{

    StateFrg mSF;
    PositionFrg mPF;
    MapFrg mMap;
    public LatLng position_aircraft=null;
    public LatLng position_rc=null;
    public LatLng position_last_pic=null;
    int heading=0;
    final int vol_id=0x01;
    final int salt_id=0x02;
    final int space_id=0x03;
    final int rc_id=0x04;
    public void setPhotoTakenPosition(LatLng l){
        position_last_pic=l;
    }
    public void setPhotoTakenPosition(DJILocationCoordinate2D p2d)
    {
        position_last_pic=new LatLng(p2d.getLatitude(),p2d.getLongitude());
    }
    public void setPF(PositionFrg PF) {
        mPF = PF;
    }

    public void setSF(StateFrg SF) {
        mSF = SF;
    }

    public void setMap(MapFrg map) {
        mMap = map;
    }

    @Override
    public void handleMessage(Message msg) {
        Bundle b=msg.getData();
        switch (msg.what)
        {
            case vol_id:{
                mSF.setVol_tv_text(msg.getData().getString("text"));
            }break;
            case salt_id:{

                mPF.set_pos(b.getDouble("lat"), b.getDouble("lng"), b.getFloat("alt"));
                mPF.updatePose(b.getFloat("pitch"),b.getFloat("roll"));
                position_aircraft=new LatLng(b.getDouble("lat"), b.getDouble("lng"));
                if(position_aircraft!=null){
                    if(position_last_pic!=null)
                    {
                        Log.i("e", "last_pic" + (position_last_pic == null));
                        mPF.set_last_point_dist(new CalcBox().coorNageCalcDistance(position_aircraft,position_last_pic));
                        int a=0;
                    }
                    if(position_rc!=null)
                    {
                        mPF.set_rc_dist(new CalcBox().coorNageCalcDistance(position_aircraft, position_rc));
                        mPF.set_rc_direction(new CalcBox().coorNageCalcAngle(position_aircraft,position_rc));
                    }
                }
                mSF.setSalt_tv_text(b.getDouble("sat_num"));
                mPF.setH_speed_tv((float)b.getDouble("hspeed"));
                mPF.setV_speed_tv((float)b.getDouble("vspeed"));
                mMap.updatePlane(new LatLng(b.getDouble("lat"),b.getDouble("lng")),b.getInt("heading"));
            }break;
            case space_id:{
                mSF.setSpace_tv_text(msg.getData().getString("text"));
            }break;
            case rc_id:{
                if(position_aircraft!=null && position_rc!=null)
                {
                    mPF.set_rc_dist(new CalcBox().coorNageCalcDistance(position_aircraft, position_rc));
                    mPF.set_rc_direction(new CalcBox().coorNageCalcAngle(position_aircraft, position_rc));

                }

            }break;
        }
    }
    @Override
    public void onResult(DJIBatteryState djiBatteryState) {
        Message m=new Message();
        Bundle b=new Bundle();
        b.putString("text", String.format("%.2f", djiBatteryState.getCurrentVoltage() / 1000.) + "V/" + djiBatteryState.getBatteryEnergyRemainingPercent() + "%");
        m.setData(b);
        m.what=vol_id;
        this.sendMessage(m);
    }

    @Override
    public void onResult(CameraSDCardState cameraSDCardState) {
        Message m=new Message();
        Bundle b=new Bundle();
        b.putString("text", cameraSDCardState.getRemainingSpaceInMegaBytes()+"M/"+cameraSDCardState.getTotalSpaceInMegaBytes()+"M");
        m.setData(b);
        m.what=space_id;
        this.sendMessage(m);
    }

    @Override
    public void onResult(DJIFlightControllerCurrentState djiFlightControllerCurrentState) {
        DJILocationCoordinate3D tmp=djiFlightControllerCurrentState.getAircraftLocation();
        Message m=new Message();
        Bundle b = new Bundle();
        b.putDouble("lat", tmp.getLatitude());
        b.putDouble("lng", tmp.getLongitude());
        b.putFloat("alt", tmp.getAltitude());
        b.putInt("heading", djiFlightControllerCurrentState.getAircraftHeadDirection());
        b.putDouble("hspeed", Math.sqrt(Math.pow(djiFlightControllerCurrentState.getVelocityX(), 2) + Math.pow(djiFlightControllerCurrentState.getVelocityY(), 2)));
        b.putDouble("vspeed", djiFlightControllerCurrentState.getVelocityZ());

        heading=djiFlightControllerCurrentState.getAircraftHeadDirection();
        DJIAttitude att=djiFlightControllerCurrentState.getAttitude();
        //Log.i("e","xxxxxxxxxxx:P-"+att.pitch+",R-"+att.roll+",Y-"+att.yaw);
        b.putDouble("sat_num", djiFlightControllerCurrentState.getSatelliteCount());
        b.putFloat("pitch",(float)att.pitch);
        b.putFloat("roll",(float)att.roll);

        m.setData(b);
        m.what=salt_id;
        //position_aircraft=new LatLng(tmp.getLatitude(),tmp.getLongitude());
        this.sendMessage(m);
    }

    @Override
    public void onGpsDataUpdate(DJIRemoteController djiRemoteController, DJIRCGPSData djircgpsData) {
        Message m=new Message();
        Bundle b=new Bundle();
        b.putDouble("lat",djircgpsData.latitude);
        b.putDouble("lng", djircgpsData.longitude);
        m.setData(b);
        m.what=rc_id;
        Log.i("e", djircgpsData.latitude + ";" + djircgpsData.longitude);
        position_rc=new LatLng(djircgpsData.latitude,djircgpsData.longitude);
        this.sendMessage(m);
    }

    @Override
    public void onGimbalAdvancedSettingsStateUpdate(DJIGimbal djiGimbal,DJIGimbalAdvancedSettingsState djiGimbalAdvancedSettingsState) {

    }

    @Override
    public void onGimbalStateUpdate(DJIGimbal djiGimbal, DJIGimbalState djiGimbalState) {
        DJIGimbalAttitude i= djiGimbalState.getAttitudeInDegrees();
        Log.i("gimbal","GIMBAL++++++P:"+i.pitch+"/R:"+i.roll+"/Y:"+i.yaw+"/"+djiGimbalState.getRollFineTuneInDegrees()+"/delta:"+(i.yaw-heading) );
    }
}
