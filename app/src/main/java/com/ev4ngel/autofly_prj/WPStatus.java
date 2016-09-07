package com.ev4ngel.autofly_prj;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Administrator on 2016/7/26.
 */
public class WPStatus implements IJson{
    private String mName;
    private int mLastPos;
    private Long mTime;
    private boolean mStatus;

    public WPStatus(String name, int lastPos, Long time, boolean status) {
        mName = name;
        mLastPos = lastPos;
        mTime = time;
        mStatus = status;
    }
    public WPStatus() {
        mName = "";
        mLastPos = 0;
        mTime = new Date().getTime();
        mStatus = false;
    }
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getLastPos() {
        return mLastPos;
    }

    public void setLastPos(int lastPos) {
        mLastPos = lastPos;
    }

    public Long getTime() {
        return mTime;
    }

    public void setTime(Long time) {
        mTime = time;
    }

    public boolean isStatus() {
        return mStatus;
    }

    public void setStatus(boolean status) {
        mStatus = status;
    }

    @Override
    public JSONObject toJson()
    {
        JSONObject jo=new JSONObject();
        try{
            jo.put(ProjectConfig.item_name,mName);
            jo.put(ProjectConfig.item_time,mTime);
            jo.put(ProjectConfig.item_isfinished,mStatus);
            jo.put(ProjectConfig.item_last,mLastPos);
        }catch (JSONException je)
        {

        }
        return jo;
    }


    public void fromJson(JSONObject jo) {
        try{
            mName=jo.getString(ProjectConfig.item_name);
            mTime=jo.getLong(ProjectConfig.item_time);
            mStatus=jo.getBoolean(ProjectConfig.item_isfinished);
            mLastPos=jo.getInt(ProjectConfig.item_last);
        }catch (JSONException je)
        {
        }

    }
}
