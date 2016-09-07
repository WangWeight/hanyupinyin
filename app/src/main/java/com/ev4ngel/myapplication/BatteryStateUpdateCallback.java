package com.ev4ngel.myapplication;

import android.widget.TextView;

import dji.sdk.Battery.DJIBattery;

/**
 * Created by Administrator on 2016/7/11.
 */
public class BatteryStateUpdateCallback implements DJIBattery.DJIBatteryStateUpdateCallback {
    private TextView batteryVolView;
    private TextView batteryRemainView;
    private TextView batteryTempView;
    public BatteryStateUpdateCallback(TextView bv,TextView brv,TextView bt)
    {
        batteryVolView=bv;
        batteryRemainView=brv;
        batteryTempView=bt;
    }

    @Override
    public void onResult(DJIBattery.DJIBatteryState state) {
        batteryVolView.setText(state.getCurrentVoltage() + "mV");
        batteryRemainView.setText(state.getBatteryEnergyRemainingPercent()*100+"%");
        batteryTempView.setText(state.getBatteryTemperature()+"C");

    }
}
