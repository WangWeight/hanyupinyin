package com.ev4ngel.autofly_prj;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/20.
 */
public class ProjectInstance {
    String name;
    private Date create_time;
    private Date access_time;
    private ArrayList<String> mWaylineList=null;

    public ProjectInstance(String name, Date create_time, Date access_time, ArrayList<String> waylineList) {
        this.name = name;
        this.create_time = create_time;
        this.access_time = access_time;
        mWaylineList = waylineList;
    }
}
