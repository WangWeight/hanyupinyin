package com.ev4ngel.autofly_prj;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dji.sdk.sample.battery.PositionFrg;
import com.dji.sdk.sample.battery.StateFrg;

import dji.sdk.Battery.DJIBattery;
import dji.sdk.Camera.DJICamera;
import dji.sdk.FlightController.DJIFlightControllerDataType;
import dji.sdk.FlightController.DJIFlightControllerDelegate;

/**
 * Created by Administrator on 2016/8/2.
 */
public class StateHandler extends Handler implements
        DJIBattery.DJIBatteryStateUpdateCallback,
        DJICamera.CameraUpdatedSDCardStateCallback,
        DJIFlightControllerDelegate.FlightControllerUpdateSystemStateCallback
{
    StateFrg mSF;
    PositionFrg mPF;
    final int vol_id=0x01;
    final int salt_id=0x02;
    final int space_id=0x03;

    public void setPF(PositionFrg PF) {
        mPF = PF;
    }

    public void setSF(StateFrg SF) {
        mSF = SF;
    }

    @Override
    public void handleMessage(Message msg) {
        Log.i("E", "xxxxxx" + msg.what);
        switch (msg.what)
        {
            case vol_id:{
                mSF.setVol_tv_text(msg.getData().getString("text"));
            }break;
            case salt_id:{
                Bundle b=msg.getData();
                mPF.set_pos(b.getDouble("lat"),b.getDouble("lng"),b.getDouble("alt"));
                mSF.setSalt_tv_text(b.getDouble("sat_num"));
            }break;
            case space_id:{
                mSF.setSpace_tv_text(msg.getData().getString("text"));
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
        Bundle b=new Bundle();
        b.putDouble("lat",tmp.getLatitude());
        b.putDouble("lng", tmp.getLongitude());
        b.putFloat("alt", tmp.getAltitude());
        b.putDouble("sat_num", djiFlightControllerCurrentState.getSatelliteCount());
        m.setData(b);
        m.what=salt_id;
        this.sendMessage(m);
    }
}
