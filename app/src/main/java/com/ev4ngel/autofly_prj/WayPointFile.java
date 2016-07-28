package com.ev4ngel.autofly_prj;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/26.
 * Store the generated points made by design
 */
public class WayPointFile extends JsonFile{
    private ArrayList<WayPoint> WayPoints;
    private JSONArray jWaypoints;
    private String file_name;
    private String path_name;
    public static WayPointFile load(String prj_name)
    {
        return new WayPointFile(prj_name);
    }
    private WayPointFile(String prjName)
    {
        super(prjName);
    }
    public void read(String fname)
    {
        mFilename=Project.root_dirname+Project.fix_name(mPrjname)+Project.waypoints_dirname+fname;
        open(true);
        WayPoints= new ArrayList<>();
        JSONArray ja=parse_content_to_array();
        for(int i=0;i<ja.length();i++)
        {
            try {
                WayPoints.add(WayPoint.fromJson((JSONObject)ja.get(i)));
            }catch (JSONException e)
            {
            }
        }
    }
    public WayPointFile change_status(int index,int status)
    {
        WayPoints.get(index).status=status;
        return this;
    }
    public WayPointFile delete_by_id(int index)
    {
        WayPoints.remove(index);
        write();
        return this;
    }
    public void write()
    {
        mContent=convert_array_to_json(WayPoints).toString();
        save();
    }
    public int total_count()
    {
        return WayPoints.size();
    }
    public int index_first_id(int status)
    {
        for(int i=0;i<WayPoints.size();i++)
        {
            if(WayPoints.get(i).status==status)
                return i;
        }
        return -1;
    }
    public WayPointFile add_waypoint(WayPoint wp)
    {
        WayPoints.add(wp);
        write();
        return this;
    }
    public WayPointFile add_waypoint(double lat,double lng,int status)
    {
        add_waypoint(new WayPoint(lat, lng, status));
        return this;
    }
    public WayPointFile set_waypoints(ArrayList<WayPoint> a)
    {
        WayPoints=a;
        write();
        return this;
    }

}
