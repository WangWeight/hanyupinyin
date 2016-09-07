package com.ev4ngel.autofly_prj;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/26.
 */
public class AreaFile extends JsonFile{
    private ArrayList<PhotoWayPoint> WayPoints;
    private JSONArray jWaypoints;
    private String file_name;
    private String path_name;
    public static AreaFile load(String path_name)
    {
        return new AreaFile(path_name);
    }
    private AreaFile(String wpfName)
    {
        super(wpfName);
        if(!wpfName.endsWith("/"))
            mFilename+="/";
        path_name=mFilename;
        WayPoints=new ArrayList<PhotoWayPoint>();
    }
}
