package com.ev4ngel.myapplication;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.*;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Created by Administrator on 2016/7/1.
 * Save as the format:
 * [ {
    "Position" : {
    "Latitude" : 23,
    "Longitude" : 123,
    "Altitude" : 23},
 "Heading" : 111,
 "StartTime" : 1111111111,
 "StopTime" : 1111111,
 "Photos" : [{"Name" : "DJI_111.jpg","Yaw" : 1,"Pitch" : 3}]}}
 ]*
 */

public class PhotoWayPointFile extends JsonFile {
    private ArrayList<PhotoWayPoint> WayPoints;
    private JSONArray jWaypoints;
    private String file_name;
    private String path_name;
    public static PhotoWayPointFile load(String path_name)
    {
        return new PhotoWayPointFile(path_name);
    }
    private PhotoWayPointFile(String wpfName)
    {
        super(wpfName);
        if(!wpfName.endsWith("/"))
            mFilename+="/";
        path_name=mFilename;
        WayPoints=new ArrayList<PhotoWayPoint>();
    }
    public void read(String fname) {//Read one of many files
        mFilename+=fname;
        file_name=fname;
        open(true);
        if (mContent.length()!=0){//Read the content if the file is not empty
            try {
                jWaypoints = new JSONArray(new JSONTokener(mContent));
                for(int i=0;i<jWaypoints.length();i++)
                {
                    PhotoWayPoint wp=new PhotoWayPoint((JSONObject)jWaypoints.get(i));
                    WayPoints.add(wp);
                }
            }
            catch(JSONException e)
            {
            }
        }else
        {
            jWaypoints = new JSONArray();
        }
    }

    public void addWayPoint(PhotoWayPoint wp)
    {
        WayPoints.add(wp);
        jWaypoints.put(wp.toJson());
        mContent=jWaypoints.toString();
        save();
    }
    public PhotoWayPoint getWayPoint(int index)
    {
        return WayPoints.get(index);
    }
}
