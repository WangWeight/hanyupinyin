package com.ev4ngel.autofly_prj;

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
    private ArrayList<PhotoWayPoint> pWayPoints;
    private String file_name;
    public static PhotoWayPointFile load(String prj_name)
    {
        return new PhotoWayPointFile(prj_name);
    }
    private PhotoWayPointFile(String _prj_name)
    {
        super(_prj_name);
        pWayPoints=new ArrayList<PhotoWayPoint>();
    }
    public void read(String fname) {//Read one of many files
        mFilename=Project.root_dirname+mPrjname+Project.photopoints_dirname+fname;
        open(true);
        JSONArray ja=parse_content_to_array();
        for(int i=0;i<ja.length();i++)
        {
            PhotoWayPoint wp;
            try {
                wp = new PhotoWayPoint((JSONObject) ja.get(i));
            }catch (JSONException je)
            {
                wp=new PhotoWayPoint();
            }
            pWayPoints.add(wp);
        }
    }
    public void write()
    {
        mContent=convert_array_to_json(pWayPoints).toString();
        save();
    }
    public PhotoWayPointFile addPhotoWayPoint(PhotoWayPoint wp)
    {
        pWayPoints.add(wp);
        write();
        return this;
    }
    public PhotoWayPoint getPhotoWayPoint(int index)
    {
        return pWayPoints.get(index);
    }
}
