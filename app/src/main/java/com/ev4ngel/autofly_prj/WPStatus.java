package com.ev4ngel.autofly_prj;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Administrator on 2016/7/26.
 */
public class WPStatus {
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

}
