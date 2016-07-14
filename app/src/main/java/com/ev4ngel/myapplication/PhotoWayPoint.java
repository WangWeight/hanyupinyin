package com.ev4ngel.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import dji.sdk.FlightController.DJIFlightControllerDataType;
import dji.sdk.util.DJILocationCoordinate2D;

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
    public PhotoWayPoint(JSONObject jObj)
    {
        photos=new ArrayList<PhotoInfo>();
        fromJson(jObj);
    }
    public void addPhoto(String pname,float yaw,float pitch)
    {
        photos.add(new PhotoInfo(pname, yaw, pitch));
    }
    public void setPosition(DJIFlightControllerDataType.DJILocationCoordinate2D loc)
    {
        lat=loc.getLatitude();
        lng=loc.getLongitude();
    }
    public void addPhoto(PhotoInfo pi)
    {
        photos.add(pi);
    }
    public void fromJson(JSONObject jObj)
    {
        try{
            JSONObject jPos=jObj.getJSONObject(_pos);
            lat=jPos.getDouble(_lat);
            lng=jPos.getDouble(_lng);
            alt=jPos.getDouble(_alt);
            heading=(float)jObj.getDouble(_heading);
            startTime=jObj.getString(_stt);
            stopTime=jObj.getString(_spt);
            JSONArray jPhotos=jObj.getJSONArray(_ps);
            for(int i=0;i<jPhotos.length();i++)
            {
                JSONObject jPhoto=jPhotos.getJSONObject(i);
                PhotoInfo pi=new PhotoInfo();
                pi.fromJson(jPhoto);
                addPhoto(pi);
            }
        }catch(JSONException e)
        {
            Log.d("ev4n","JSON WRONG");
        }
    }
    public JSONObject toJson()
    {
        JSONObject jObj=new JSONObject();
        JSONObject jPos=new JSONObject();
        try{
            jPos.put(_lat,lat);
            jPos.put(_lng,lng);
            jPos.put(_alt,alt);
            jObj.put(_pos,jPos);
            jObj.put(_heading,heading);
            jObj.put(_stt,startTime);
            jObj.put(_spt,stopTime);
            JSONArray jPts=new JSONArray();
            for(int i=0;i<photos.size();i++)
            {
                jPts.put(photos.get(i).toJson());
            }
            jObj.put(_ps,jPts);
        }catch(JSONException e)
        {
        }
        return jObj;
    }
}
