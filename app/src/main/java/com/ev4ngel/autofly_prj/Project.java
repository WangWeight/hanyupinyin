package com.ev4ngel.autofly_prj;

import android.util.Log;

import java.io.File;

/**
 * Created by Administrator on 2016/7/21.
 * mainly used to manipulate the projects like build,open.All projects will be saved by itself.
 * p=new Project()
 * p.new_project() or
 * p.load_project() or
 * p.load_airway(airway_name)
 * Project为数据入口类，管理文件/文件夹生成，数据保存，
 * 总的Activity保存一个此对象即可，
 */
public class Project {
    public interface OnLoadItemListener {
        void onLoadProject(String prj_name);
        void onLoadWaypoint(String waypoint_name);
        void onNewProject(String prj_name);
        int onDeleteProject(String prj_name);
    }

    public static String root_dirname="/mnt/sdcard/autofly_prj/";
    public static String waypoints_dirname="waypoints/";
    public static String area_dirname="area/";
    public  static String photopoints_dirname="photowaypoints/";
    public static String prj_config_fname="config.txt";
    public static String prj_default_name="默认项目/";
    //private ProjectsConfig mPsc;
    private ProjectConfig mPc;
    private PhotoWayPointFile mPwf;
    private String current_airway_name;//航点，区域，存储的全部文件为此名字
    private WayPointFile mWpf;
    public String current_project_name="";
    private OnLoadItemListener mOnLoader=null;
    public Project()
    {
        if(!isExistProject(Project.root_dirname))
        {
            if(!new File(root_dirname).mkdir())
            {
                Log.i("E","buildFail");
            }
        }
        //mPsc=ProjectsConfig.load(Project.root_dirname+prj_config_fname);
    }
    public static boolean isExistProject(String name)
    {
        return new File(root_dirname+fix_name(name)).exists();
    }
    public void set_current_airway(String name)
    {
        current_airway_name=name;
    }
    public void delete_airway(String name)
    {
        if(current_airway_name.equals(name))
        {
            close_airway();
        }
    }
    public void load_airway(String name)
    {
        mWpf.read(name);
        mPwf.read(name);
        //mAf.read(name);
    }
    public WayPointFile get_wp_file()
    {
        return mWpf;
    }
    public PhotoWayPointFile get_pwp_file()
    {
        return mPwf;
    }
    public  int remove_project(String name)
    {
        if(name.equals(prj_default_name))//若是删除默认项目，则不成功
            return 2;
        if(isExistProject(name))
        {
            try{
                File f=new File(root_dirname+unfix_name(name)+"."+System.currentTimeMillis()+"/");
                boolean rlt=new File(root_dirname+name).renameTo(f);
                if(rlt)
                {
                    //mPsc.delect(name);//Delete from config file
                    //mPsc.write();
                    close_airway();
                    mPc.close();
                    return 0;
                }else{
                    return 1;
                }
            }catch (Exception e)
            {
                Log.i("e","Remove Prj fail");
                return  1;
            }
        }
        return  0;
    }
    public int new_project(String name,boolean load_after_new)
    {
        //name.("[\w]*")
        name=fix_name(name);
        if(isExistProject(name))
        {
            return 1;
        }else {
            try {
                new File(root_dirname + name).mkdir();
                new File(root_dirname + name+Project.waypoints_dirname).mkdir();
                new File(root_dirname + name+Project.area_dirname).mkdir();
                new File(root_dirname + name+Project.photopoints_dirname).mkdir();
                //mPsc.add_prj(name);
                //mPsc.write();
            }catch (Error e)
            {
                Log.i("e","Make project dir fail"+name);
                return 2;
            }
        }
        if(load_after_new)
            load_project(name);
        return 0;
    }
    public static String fix_name(String name)
    {
        if(!name.endsWith("/"))
        {
            name+="/";
        }
        return name;
    }
    public static String unfix_name(String name)
    {
       return  name.replace("/","");
    }
    public int load_project(String name)//the name must exists
    {
        name=fix_name(name);
        if(name.equals(current_project_name))
            return 1;
        if(!isExistProject(name))
        {
            new_project(name,false);
        }
        current_project_name=name;
        mPc=ProjectConfig.load(name);
        mPwf=PhotoWayPointFile.load(name);
        mWpf = WayPointFile.load(name);
        if(!mPc.recent_file.isEmpty() &&mPc.isExistsWaypoint(root_dirname+name+waypoints_dirname+mPc.recent_file)){
            load_waypoint(name);
        }
        //ProjectConfig.load(name);
        //mPc=mPsc.open_prj(name);
        //mPsc.setRecent_project(name);
        //mPsc.write();
        //if(mOnLoader!=null)
        //    mOnLoader.onLoadProject(name);
        return 0;
    }
    public void load_waypoint(String name){
        mPc.recent_file=name;
        mPwf.read(mPc.recent_file);
        mWpf.read(mPc.recent_file);
        //if(mOnLoader!=null)
        //    mOnLoader.onLoadWaypoint(name);
    }
    public Project new_waypoint(String name)
    {
        mPc.add_waypoint_file(name);
        load_waypoint(name);
        return this;
    }
    public void close_airway()
    {
        current_airway_name="";
        if(mPwf!=null)
            mPwf.close();
        if(mWpf!=null)
            mWpf.close();
    }
    public void save()
    {
        //if(mPsc!=null)
        //    mPsc.write();
        if(mPc!=null)
            mPc.write();
        if(mPwf!=null)
            mPwf.write();
        if(mWpf!=null)
            mWpf.write();
    }
    public void close()
    {
        current_project_name="";
        //if(mPsc!=null)
        //    mPsc.close();
        if(mPc!=null)
            mPc.close();
    }
/*
    public void load_recent_project()
    {
        if(mPsc.recent_project==null ||mPsc.recent_project.isEmpty())
        {
            load_project(prj_default_name);
        }else
        {
            load_project(mPsc.recent_project);
        }
    }
    */
    public void setOnLoadListener(OnLoadItemListener listener) {
        mOnLoader=listener;
    }
    public void load_default_project(){
        load_project(prj_default_name);
    }

}
