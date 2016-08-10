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
        return v;
    }
    public void set_pos(double lat,double lng,double alt)
    {
        if(lat_tv!=null) lat_tv.setText(""+lat);
        if(lng_tv!=null) lng_tv.setText(""+lng);
        if(alt_tv!=null) alt_tv.setText(""+alt);
    }
    public void set_last_point_dist(double dist)
    {
        if(last_pt_dist_tv!=null) last_pt_dist_tv.setText(dist+"m");
    }
    public void set_rc_dist(double dist){
        if(rc_dist_tv!=null) rc_dist_tv.setText(dist+"m");
    }
    public void set_rc_direction(double dist){
        String value="";
        if(rc_direction_tv!=null) rc_direction_tv.setText(dist+"度");
    }

}