package com.ev4ngel.autofly_prj;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ev4ngel.myapplication.R;

/**
 * Created by Administrator on 2016/8/1.
 * 使用此fragment，需要将flightcontroller与camera，baterry注册。
 * if(DJISampleApplication.getAircraftInstance()!=null&&DJISampleApplication.getAircraftInstance().getBattery()!=null){
 DJISampleApplication.getAircraftInstance().getBattery().setBatteryStateUpdateCallback(msf);
 }
 if(DJISampleApplication.getAircraftInstance()!=null&&DJISampleApplication.getAircraftInstance().getFlightController()!=null)
 {
 DJISampleApplication.getAircraftInstance().getFlightController().setUpdateSystemStateCallback(msf);
 }
 if(DJISampleApplication.getAircraftInstance()!=null&&DJISampleApplication.getAircraftInstance().getCamera()!=null)
 {
 DJISampleApplication.getAircraftInstance().getCamera().setDJIUpdateCameraSDCardStateCallBack(msf);
 }
 */
public class StateFrg extends Fragment

{
    TextView space_tv;
    String space_tv_text="";
    TextView vol_tv;
    TextView salt_tv;
    String salt_tv_text="";
    TextView connect_tv;
    String connect_tv_text="";
    String vol_tv_text="";
    int min_vol_percent;
    float min_vol_value;
    int min_sat_num;
    int min_space_left;
    int vol_id=0x01;
    int salt_id=0x02;
    int space_id=0x03;
    Handler mHandler;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.statefrg_layout,container,false);
        space_tv=(TextView)v.findViewById(R.id.space_tv_id);
        vol_tv=(TextView)v.findViewById(R.id.vol_tv_id);
        salt_tv=(TextView)v.findViewById(R.id.salt_tv_id);
        connect_tv=(TextView)v.findViewById(R.id.connect_tv_id);
        return v;
    }

    public void setLostConnection()
    {
        String unknown="未知";
        space_tv.setText(unknown);
        vol_tv.setText(unknown);
        salt_tv.setText(unknown);
        connect_tv.setText(unknown);
    }
    public void setLimitVolPercent(int num){
        min_vol_percent=num;
    }
    public void setLimitVolNumber(float num) {
        min_vol_value=num;
    }
    public void setLimitSpaceLeft(int num){
        min_space_left=num;
    }
    public void setLimitSatNumber(int num){
        min_sat_num=num;
    }
    public void setVol_tv_text(String vol_tv_text) {
        if(vol_tv!=null)
        {
            vol_tv.setText(vol_tv_text);
        }
    }

    public void setConnect_tv_text(String connect_tv_text) {
        if(connect_tv!=null) connect_tv.setText(connect_tv_text);
    }

    public void setSalt_tv_text(double salt_tv_text) {
        if(salt_tv!=null) salt_tv.setText(""+salt_tv_text);
    }

    public void setSpace_tv_text(String space_tv_text) {
        if(space_tv!=null)
        {
            space_tv.setText(space_tv_text);
        }
    }
}
