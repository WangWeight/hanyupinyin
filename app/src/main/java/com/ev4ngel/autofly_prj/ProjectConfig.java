package com.ev4ngel.autofly_prj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/21.
 * ["recent_file":"xxx","files":{"name":"xxxx","time":"xxx","is_finished":"true","last_pos":"10"},{"name":"xxxx","time":"xxx"},]
 * 直接管理三个文件：区划文件，航点文件，照片文件
 */
public class ProjectConfig  extends JsonFile{
    private String mPrjName;
    private ArrayList<WPStatus> mFileStatList;
    public static String item_recent_file="recent_file";
    public static String item_files="files";
    public static String item_name="name";
    public static String item_time="time";
    public static String item_isfinished="is_finished";
    public static String item_last="last_pos";
    public String recent_file="";
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
        if(parse_content_to_object()) {
            JSONArray ja=null;
            try {
                recent_file=mJSONObject.getString(item_recent_file);
                ja = mJSONObject.getJSONArray(item_files);
            }catch (JSONException e){
            }
            for (int i = 0;ja!=null&& i < ja.length(); i++) {
                WPStatus ws;
                try {
                    ws = new WPStatus(((JSONObject) ja.get(i)).getString(ProjectConfig.item_name),
                            ((JSONObject) ja.get(i)).getInt(ProjectConfig.item_isfinished),
                            ((JSONObject) ja.get(i)).getLong(ProjectConfig.item_time),
                            ((JSONObject) ja.get(i)).getBoolean(ProjectConfig.item_isfinished));
                } catch (JSONException je) {
                    ws = new WPStatus();
                }
                mFileStatList.add(ws);
            }
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
        try {
            mJSONObject.putOpt(item_files, convert_array_to_json(mFileStatList));
            mJSONObject.putOpt(item_recent_file, recent_file);
        }catch (JSONException e){}
        mContent=mJSONObject.toString();
        save();
    }
    public void add_waypoint_file(String name)
    {
        WPStatus wps=new WPStatus();
        wps.setName(name);
        mFileStatList.add(wps);
    }
    public void set_recent_file(String name){
        recent_file=name;
    }
    public boolean isExistsWaypoint(String name)
    {
        if(new File(name).exists())
            return true;
        return false;
    }
    public boolean remove_waypoint_file(String name){
        int index=get_wps_index_by_name(name);
        if(index==-1) return false;
        mFileStatList.remove(index);
        if(recent_file.equals(name)){
            if(mFileStatList.size()>0){
                recent_file=mFileStatList.get(mFileStatList.size()-1).getName();
            }else{
                recent_file="";
            }
        }
        return true;
    }
    public WPStatus get_wps_by_name(String name){
        int index=get_wps_index_by_name(name);
        if(index==-1)
            return null;
        return mFileStatList.get(index);
    }
    public int get_wps_index_by_name(String name){
        int index=-1;
        for(WPStatus wp:mFileStatList){
            index++;
            if(wp.getName().equals(name)){
                break;
            }
        }
        if(index==mFileStatList.size()) return -1;
        return index;
    }
}
