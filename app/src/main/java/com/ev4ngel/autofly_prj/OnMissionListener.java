package com.ev4ngel.autofly_prj;

/**
 * Created by Administrator on 2016/8/9.
 */
public interface OnMissionListener {
    void onPrepareMission(int speed,int height,int step);
    void onStartMission();
    void onStopMission();
    void onGoHomeMission();
}
