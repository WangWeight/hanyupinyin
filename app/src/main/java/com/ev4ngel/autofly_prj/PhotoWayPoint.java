package com.ev4ngel.autofly_prj;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dji.common.flightcontroller.DJILocationCoordinate2D;

/**
 * Created by Administrator on 2016/7/1.
 */
public class PhotoWayPoint {
    private String _pos="Position";
    private String _lat="Latitude";
    private String _lng="Longitude";
    private String _alt="Altitude";
    private String _heading="Heading";
    private String _stt="StartTime";
    private String _spt="StopTime";
    private String _ps="Photos";

    public String name="";
    public double lat=0;
    public double lng=0;
    public double alt=0;
    public double heading=0;
    public String startTime="";
    public String stopTime="";
    public ArrayList<PhotoInfo> photos;
    public PhotoWayPoint()
    {
        photos=new ArrayList<PhotoInfo>();
    }

    public void addPhoto(String pname,float yaw,float pitch)
    {
        photos.add(new PhotoInfo(pname, yaw, pitch));
    }
    public void setPosition(DJILocationCoordinate2D loc)
    {
        lat=loc.getLatitude();
        lng=loc.getLongitude();
    }
    public void addPhoto(PhotoInfo pi)
    {
        photos.add(pi);
    }

}
