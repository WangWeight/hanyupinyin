package com.ev4ngel.myapplication;

import android.content.Context;
import android.widget.Toast;

import dji.sdk.util.DJILocationCoordinate2D;

/**
 * Created by Administrator on 2016/7/4.
 */
public class Tools {
    public static void showToast(Context c,String msg)
    {
        Toast.makeText(c,msg,Toast.LENGTH_SHORT).show();
    }
    public static void i(Context c,String msg)
    {
        Toast.makeText(c,msg,Toast.LENGTH_SHORT).show();
    }
    public DJILocationCoordinate2D getLocationByDistanceAndAngle(DJILocationCoordinate2D loc,float angle)
    {
        double lat=loc.latitude+0.01;
        double lng=loc.longitude+0.01;
        return new DJILocationCoordinate2D(lat,lng);
    }
}