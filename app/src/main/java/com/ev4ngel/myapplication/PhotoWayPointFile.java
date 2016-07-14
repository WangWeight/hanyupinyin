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

public class PhotoWayPointFile {
    private String fileName="";
    public FileOutputStream fos=null;
    public ArrayList<PhotoWayPoint> WayPoints;

    public PhotoWayPointFile(String wpfName)
    {

        fileName=PhotoWayPointFile.get_way_points_dir()+wpfName;
        WayPoints=new ArrayList<PhotoWayPoint>();
        read();

    }
    public static String get_way_points_dir()
    {

        String full_wpf_dir= Common.app_dir+Common.wpf_dir;
        File ffull=new File(full_wpf_dir);
        if(!ffull.exists())
        {
            try{
                ffull.mkdirs();
            }catch (SecurityException se)
            {

            }
        }
        return full_wpf_dir;
    }
    public void write(String fn)
    {
         File ff=new File(fn);
        try {
            fos = new FileOutputStream(ff);
            JSONArray jwps = new JSONArray();
            for (int i = 0; i < WayPoints.size(); i++) {
                jwps.put(WayPoints.get(i).toJson());
            }
            fos.write(jwps.toString().getBytes());
            fos.close();
        }catch(Exception e)
        {

        }
    }
    public void write()
    {
        write(fileName);
    }
    public void read()
    {
        File f=new File(fileName);
        if(f.exists()) {
            try {
                FileInputStream fis=new FileInputStream(f);
                byte[] b=new byte[fis.available()];
                fis.read(b);
                fis.close();
                //Toast.makeText(mContext,new String(b), Toast.LENGTH_LONG).show();
                try {
                    JSONArray jWaypoints = new JSONArray(new JSONTokener(new String(b)));
                    for(int i=0;i<jWaypoints.length();i++)
                    {
                        PhotoWayPoint wp=new PhotoWayPoint((JSONObject)jWaypoints.get(i));
                        WayPoints.add(wp);
                    }
                }
                catch(JSONException e)
                {

                }
            }
            catch(IOException e)
            {

            }
        }
        else
        {
            try {
                fos = new FileOutputStream(f);
            }catch(IOException e)
            {

            }
        }
    }

    public void addWayPoint(PhotoWayPoint wp)
    {
        WayPoints.add(wp);
        write(fileName);
    }
}
