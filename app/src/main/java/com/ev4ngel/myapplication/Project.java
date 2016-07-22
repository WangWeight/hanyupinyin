package com.ev4ngel.myapplication;

import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/21.
 * mainly used to manipulate the projects like build,open.All projects will be saved by itself.
 * p=new Project()
 * p.new_project() or
 * p.load_project()
 */
public class Project {
    public static String root_dirname="/mnt/sdcard/autofly_prj/";
    public static String waypoints_dirname="waypoints/";
    public static String area_dirname="area/";
    public  static String photopoints_dirname="photowaypoints/";
    public static String prj_config_fname="config.txt";
    private ProjectsConfig mPsc;
    private ProjectConfig pc;
    private PhotoWayPointFile mPwf;
    private String current_project="";
    public Project()
    {
        if(!isExistProject(Project.root_dirname))
        {
            if(!new File(root_dirname).mkdir())
            {
                Log.i("E","buildFail");
            }
        }
        mPsc=ProjectsConfig.load(Project.root_dirname+prj_config_fname);

    }
    public static boolean isExistProject(String name)
    {
        return new File(root_dirname+name).exists();
    }
    public  ArrayList<String> getProjects()
    {
        ArrayList<String> pns=new ArrayList<>();
        for(String s:new File(root_dirname).list())
        {
            if(new File(root_dirname+s).isDirectory())
            {
                pns.add(s);
            }
        }
        return pns;
    }
    public  boolean remove_project(String name)
    {
        if(isExistProject(root_dirname+name))
        {
            try{
                mPsc.delect(name);//Delete from config file
                return new File(root_dirname+name).delete();
            }catch (Exception e)
            {
                Log.i("e","Remove Prj fail");
                return  false;
            }
        }
        return  true;
    }
    public int new_project(String name)
    {
        //name.("[\w]*")
        if(!name.endsWith("/"))
        {
            name+="/";
        }
        if(isExistProject(root_dirname + name))
        {
            return 1;
        }else {
            try {
                new File(root_dirname + name).mkdir();
                new File(root_dirname + name+Project.waypoints_dirname).mkdir();
                new File(root_dirname + name+Project.area_dirname).mkdir();
                new File(root_dirname + name+Project.photopoints_dirname).mkdir();

            }catch (Error e)
            {
                Log.i("e","Make project dir fail"+name);
                return 2;
            }
        }
        return 0;
    }
    public int load_project(String name)
    {
        if(!name.endsWith("/"))
        {
            name+="/";
        }
        if(!isExistProject(name))
        {
            new_project(name);
        }
        current_project=name;
        mPwf=PhotoWayPointFile.load(root_dirname+name+photopoints_dirname);
        mPsc.setRecent_project(name);
        mPsc.write();
        return 0;
    }
    public int new_waypoints(String name)
    {
        return 0;
    }
    public void close()
    {
        mPsc.close();
        mPwf.close();
    }
    public void add_photowaypoint(PhotoWayPoint pw)
    {
        mPwf.addWayPoint(pw);

    }
    public void load_recent_project()
    {
        if(mPsc.recent_project.isEmpty())
        {

        }else
        {
            load_project(mPsc.recent_project);
        }
    }

}
