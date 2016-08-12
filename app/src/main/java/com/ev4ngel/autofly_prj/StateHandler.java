package com.ev4ngel.autofly_prj;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.amap.api.maps2d.model.LatLng;
import com.ev4ngel.myapplication.CalcBox;
import com.ev4ngel.myapplication.MapFrg;

import dji.sdk.Battery.DJIBattery;
import dji.sdk.Camera.DJICamera;
import dji.sdk.FlightController.DJIFlightControllerDataType;
import dji.sdk.FlightController.DJIFlightControllerDelegate;
import dji.sdk.Gimbal.DJIGimbal;
import dji.sdk.RemoteController.DJIRemoteController;

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
    public void setPhotoTakenPosition(DJIFlightControllerDataType.DJILocationCoordinate2D p2d)
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
        switch (msg.what)
        {
            case vol_id:{
                mSF.setVol_tv_text(msg.getData().getString("text"));
            }break;
            case salt_id:{
                Bundle b=msg.getData();
                mPF.set_pos(b.getDouble("lat"), b.getDouble("lng"), b.getFloat("alt"));
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
                mMap.updatePlane(new LatLng(b.getDouble("lat"),b.getDouble("lng")),b.getInt("heading"));
            }break;
            case space_id:{
                mSF.setSpace_tv_text(msg.getData().getString("text"));
            }break;
            case rc_id:{
                if(position_aircraft!=null && position_rc!=null)
                {
                    mPF.set_rc_dist(new CalcBox().coorNageCalcDistance(position_aircraft, position_rc));
                    mPF.set_rc_direction(new CalcBox().coorNageCalcAngle(position_aircraft,position_rc));
                }
            }break;
        }
    }
    @Override
    public void onResult(DJIBattery.DJIBatteryState djiBatteryState) {
        Message m=new Message();
        Bundle b=new Bundle();
        b.putString("text", String.format("%.2f", djiBatteryState.getCurrentVoltage() / 1000.) + "V/" + djiBatteryState.getBatteryEnergyRemainingPercent() + "%");
        m.setData(b);
        m.what=vol_id;
        this.sendMessage(m);
    }

    @Override
    public void onResult(DJICamera.CameraSDCardState cameraSDCardState) {
        Message m=new Message();
        Bundle b=new Bundle();
        b.putString("text", cameraSDCardState.getRemainingSpaceInMegaBytes()+"M/"+cameraSDCardState.getTotalSpaceInMegaBytes()+"M");
        m.setData(b);
        m.what=space_id;
        this.sendMessage(m);
    }

    @Override
    public void onResult(DJIFlightControllerDataType.DJIFlightControllerCurrentState djiFlightControllerCurrentState) {
        DJIFlightControllerDataType.DJILocationCoordinate3D tmp=djiFlightControllerCurrentState.getAircraftLocation();
        Message m=new Message();
        Bundle b = new Bundle();
        b.putDouble("lat", tmp.getLatitude());
        b.putDouble("lng", tmp.getLongitude());
        b.putFloat("alt", tmp.getAltitude());
        b.putInt("heading", djiFlightControllerCurrentState.getAircraftHeadDirection());
        heading=djiFlightControllerCurrentState.getAircraftHeadDirection();
        b.putDouble("sat_num", djiFlightControllerCurrentState.getSatelliteCount());
        m.setData(b);
        m.what=salt_id;
        //position_aircraft=new LatLng(tmp.getLatitude(),tmp.getLongitude());
        this.sendMessage(m);
    }

    @Override
    public void onGpsDataUpdate(DJIRemoteController djiRemoteController, DJIRemoteController.DJIRCGPSData djircgpsData) {
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
    public void onGimbalAdvancedSettingsStateUpdate(DJIGimbal djiGimbal, DJIGimbal.DJIGimbalAdvancedSettingsState djiGimbalAdvancedSettingsState) {

    }

    @Override
    public void onGimbalStateUpdate(DJIGimbal djiGimbal, DJIGimbal.DJIGimbalState djiGimbalState) {
        DJIGimbal.DJIGimbalAttitude i= djiGimbalState.getAttitudeInDegrees();
        Log.i("gimbal","P:"+i.pitch+"/R:"+i.roll+"/Y:"+i.yaw+"/"+djiGimbalState.getRollFineTuneInDegrees()+"/delta:"+(i.yaw-heading) );
    }
}
