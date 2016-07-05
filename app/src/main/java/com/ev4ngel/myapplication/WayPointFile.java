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
public class WayPointFile {
    private String fileName="";
    public FileOutputStream fos=null;
    public ArrayList<WayPoint> WayPoints;
    private Context mContext;
    public WayPointFile(Context context,String wpfName)
    {
        mContext=context;
        fileName=wpfName;
        WayPoints=new ArrayList<WayPoint>();
        readFile();

    }
    public void writeFile(String fn)
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
            Toast.makeText(mContext, "Success", Toast.LENGTH_LONG).show();
        }catch(Exception e)
        {

        }
    }
    public void readFile()
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
                        WayPoint wp=new WayPoint(mContext,(JSONObject)jWaypoints.get(i));
                        WayPoints.add(wp);
                    }
                    Toast.makeText(mContext, ""+WayPoints.size(),Toast.LENGTH_LONG).show();
                }
                catch(JSONException e)
                {
                    Toast.makeText(mContext,"fk", Toast.LENGTH_LONG).show();
                }
            }
            catch(IOException e)
            {
                Toast.makeText(mContext,"fk1", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            try {
                fos = new FileOutputStream(f);
            }catch(IOException e)
            {
                Toast.makeText(mContext,"fk2", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void addWayPoint(WayPoint wp)
    {
        WayPoints.add(wp);
        writeFile(fileName);
    }
}
