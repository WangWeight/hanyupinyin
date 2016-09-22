package com.ev4ngel.myapplication;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ev4ngel.autofly_prj.OnMissionListener;

/**
 * Created by Administrator on 2016/8/9.
 */
public class MissionFrg extends Fragment implements DialogInterface.OnClickListener,
        SeekBar.OnSeekBarChangeListener,
        View.OnClickListener{
    OnMissionListener mListener=null;
    SeekBar fly_speed_sb;
    SeekBar rotate_speed_sb;
    SeekBar return_height_sb;
    TextView fly_speed_tv;
    TextView rotate_speed_tv;
    TextView return_height_tv;
    View mission_opt_tb;
    Boolean isMissionClick=false;
    View gohome_opt_tb;
    Boolean isGoHomeClick=false;
    AlertDialog ask_dialog;
    AlertDialog ask_home_dialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout=inflater.inflate(R.layout.frg_mission_layout,container,false);
        mission_opt_tb=layout.findViewById(R.id.mission_opt_tb);
        gohome_opt_tb=layout.findViewById(R.id.gohome_opt_tb);
        View v=LayoutInflater.from(getActivity()).inflate(R.layout.frg_missionfrg_dialog,null);
        ask_dialog=new AlertDialog.Builder(getActivity())
                .setTitle("确定信息")
                .setView(v)
                .setPositiveButton("确认",this)
                .setNegativeButton("取消",null)
                .create();
        ask_home_dialog=new AlertDialog.Builder(getActivity())
                .setTitle("选择返航位置")
                .setView(LayoutInflater.from(getActivity()).inflate(R.layout.home_dialog_layout,null))
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
        gohome_opt_tb.setOnClickListener(this);
        mission_opt_tb.setOnClickListener(this);

    }
    public void setFly_speed(float value)
    {
        fly_speed_tv.setText(value + "m/s");
        fly_speed_sb.setProgress((int) value);
    }
    public void setReturn_height(int value)
    {
        return_height_tv.setText(value + "m/s");
        return_height_sb.setProgress(value);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(dialog!=null )
            if(dialog.toString().equals(ask_dialog.toString()))
            {
                if(which==DialogInterface.BUTTON_POSITIVE){
                    mListener.onPrepareMission(fly_speed_sb.getProgress(), return_height_sb.getProgress(), MissionOptSignal.MOS_PREPARE);
                    isMissionClick=!isMissionClick;
                    mission_opt_tb.setBackgroundColor(Color.GREEN);

                }else{

                }
            }else if(dialog.toString().equals(ask_home_dialog.toString())){
                mListener.onGoHomeMission();
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
    private void resetColor(){
        gohome_opt_tb.setBackgroundColor(Color.RED);
        mission_opt_tb.setBackgroundColor(Color.RED);
    }
    @Override
    public void onClick(View v) {
        resetColor();
        if(v.getId()==mission_opt_tb.getId()){
            if(!isMissionClick) {
                ask_dialog.show();
            }else{
                isMissionClick=!isMissionClick;

            }
        }else if(v.getId()==gohome_opt_tb.getId()){
            if(!isGoHomeClick){
                isGoHomeClick=!isGoHomeClick;
                gohome_opt_tb.setBackgroundColor(Color.GREEN);
            }else{
                isGoHomeClick=!isGoHomeClick;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    public void setOnPrepareMissionListener(OnMissionListener l)
    {
        mListener=l;
    }
}
