package com.ev4ngel.myapplication;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ev4ngel.autofly_prj.OnLoadProjectListener;
import com.ev4ngel.autofly_prj.OnPrepareMissionListener;

/**
 * Created by Administrator on 2016/8/9.
 */
public class MissionFrg extends Fragment implements View.OnClickListener,DialogInterface.OnClickListener,SeekBar.OnSeekBarChangeListener {
    OnPrepareMissionListener mListener;
    SeekBar fly_speed_sb;
    SeekBar rotate_speed_sb;
    SeekBar return_height_sb;
    TextView fly_speed_tv;
    TextView rotate_speed_tv;
    TextView return_height_tv;

    AlertDialog ask_dialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.frg_mission_layout,container,false);
        View v=LayoutInflater.from(getActivity()).inflate(R.layout.frg_missionfrg_dialog,null);
        ask_dialog=new AlertDialog.Builder(getActivity())
                .setTitle("确定信息")
                .setView(v)
                .setPositiveButton("确认",this)
                .setNegativeButton("取消",null)
                .create();
        fly_speed_sb=(SeekBar)v.findViewById(R.id.fly_speed_sb);
        return_height_sb=(SeekBar)v.findViewById(R.id.return_height_sb);
        fly_speed_tv=(TextView)v.findViewById(R.id.fly_speed_tv);
        return_height_tv=(TextView)v.findViewById(R.id.return_height_tv);
        fly_speed_sb.setOnSeekBarChangeListener(this);
        return_height_sb.setOnSeekBarChangeListener(this);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init_listeners();
    }

    public void init_listeners()
    {

    }
    public void setFly_speed(float value)
    {
        fly_speed_tv.setText(value + "m/s");
        fly_speed_sb.setProgress((int) value);
    }
    public void setReturn_height(int value)
    {
        return_height_tv.setText(value+"m/s");
        return_height_sb.setProgress(value);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(dialog!=null && dialog.toString().equals(ask_dialog.toString()))
        {
            if(which==DialogInterface.BUTTON_POSITIVE){
                mListener.onPrepareMission(fly_speed_sb.getProgress(),return_height_sb.getProgress(),0);
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(seekBar.getId()==fly_speed_sb.getId())
        {
            fly_speed_tv.setText(progress+"m/s");
        }else{
            if(seekBar.getId()==return_height_sb.getId())
            {
                return_height_tv.setText(progress + "m");
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    public void setOnPrepareMissionListener(OnPrepareMissionListener l)
    {
        mListener=l;
    }
}
