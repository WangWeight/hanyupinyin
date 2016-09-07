package com.ev4ngel.autofly_prj;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ev4ngel.myapplication.R;


/**
 * Created by Administrator on 2016/8/2.
 */
public class PositionFrg extends Fragment {
    TextView lat_tv;
    TextView lng_tv;
    TextView alt_tv;
    TextView last_pt_dist_tv;//距离上一点距离
    TextView rc_dist_tv;//距离上一点距离
    TextView rc_direction_tv;//距离上一点距离
    TextView h_speed_tv;
    TextView v_speed_tv;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.positionfrg_layout,container,false);
        lat_tv=(TextView)v.findViewById(R.id.pos_lat_tv_id);
        lng_tv=(TextView)v.findViewById(R.id.pos_lng_tv_id);
        alt_tv=(TextView)v.findViewById(R.id.pos_alt_tv_id);
        last_pt_dist_tv=(TextView)v.findViewById(R.id.pos_distance_tv_id);
        rc_dist_tv=(TextView)v.findViewById(R.id.rc_distance_tv_id);
        rc_direction_tv=(TextView)v.findViewById(R.id.rc_direction_tv_id);
        h_speed_tv=(TextView)v.findViewById(R.id.hsp_tv_id);
        v_speed_tv=(TextView)v.findViewById(R.id.vsp_tv_id);
        return v;
    }
    public void set_pos(double lat,double lng,float alt)
    {
        String lat_str="未定位";
        String lng_str="未定位";
        if(!Double.isNaN(lat)&& !Double.isNaN(lng))
        {
            lat_str=String.format("%.6f",lat)+"N";
            lng_str=String.format("%.6f",lng)+"E";
        }
        if(lat_tv!=null) lat_tv.setText(lat_str);
        if(lng_tv!=null) lng_tv.setText(lng_str);
        if(alt_tv!=null) alt_tv.setText(""+alt+"m");
    }
    public void set_last_point_dist(double dist)
    {
        if(last_pt_dist_tv!=null) last_pt_dist_tv.setText(String.format("%.2f",dist)+"m");
    }
    public void set_rc_dist(double dist){
        if(rc_dist_tv!=null) rc_dist_tv.setText(String.format("%.2f",dist)+"m");
    }
    public void set_rc_direction(double dist){
        String value="";
        if(rc_direction_tv!=null) rc_direction_tv.setText(String.format("%.1f",dist)+"度");
    }

    public void setH_speed_tv(float h_speed) {
        if(h_speed_tv!=null)
            h_speed_tv.setText(String.format("%.1f",h_speed)+"m/s");
    }

    public void setV_speed_tv(float v_speed) {
        if(v_speed_tv!=null)
            v_speed_tv.setText(String.format("%.1f",v_speed)+"m/s");

    }
}
