package com.ev4ngel.autofly_prj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/21.
 * [{"name":"xxxx","time":"xxx","is_finished":"true","last_pos":"10"},{"name":"xxxx","time":"xxx"},]
 * 直接管理三个文件：区划文件，航点文件，照片文件
 */
public class ProjectConfig  extends JsonFile{
    private ArrayList<PhotoWayPoint> WayPoints;
    private JSONArray mItems;
    private WayPointFile mWPF;
    private AreaFile mAF;
    private PhotoWayPoint mPWP;
    private String mPrjName;
    private ArrayList<WPStatus> mFileStatList;
    public static String item_name="name";
    public static String item_time="time";
    public static String item_isfinished="is_finished";
    public static String item_last="last_pos";
    public static ProjectConfig load(String prj_name)
    {
        return new ProjectConfig(prj_name);
    }
    private ProjectConfig(String prjname) {
        super(prjname);
        mFileStatList=new ArrayList<>();
        mPrjName = Project.fix_name(prjname);
        mFilename = Project.root_dirname + mPrjName + Project.prj_config_fname;
        open(true);
        WayPoints = new ArrayList<PhotoWayPoint>();
        JSONArray ja= parse_content_to_array();
        for (int i=0;i<ja.length();i++)
        {
            WPStatus ws;
            try {
                ws = new WPStatus(((JSONObject)ja.get(i)).getString(ProjectConfig.item_name),
                        ((JSONObject)ja.get(i)).getInt(ProjectConfig.item_isfinished),
                        ((JSONObject)ja.get(i)).getLong(ProjectConfig.item_time),
                        ((JSONObject)ja.get(i)).getBoolean(ProjectConfig.item_isfinished));
            }catch (JSONException je)
            {
                ws=new WPStatus();
            }
            mFileStatList.add(ws);
        }
    }

    public void load_waypoints(String wpname)
    {
        //这么做是不对的
        //mWPF=WayPointFile.load();
    }

    public void read(String fname) {//Read one of many files

    }
    public void write()
    {
        mContent=convert_array_to_json(mFileStatList).toString();
        save();
    }
    public void addWayPoint(PhotoWayPoint wp)
    {

    }
    public boolean isExistsAirway(String name)
    {

        return false;
    }

}
