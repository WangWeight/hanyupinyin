package com.ev4ngel.autofly_prj;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/28.
 */
public interface OnSaveWayPointListener {
    void onSaveWayPoint(String filename,ArrayList<WayPoint> wps);
}
