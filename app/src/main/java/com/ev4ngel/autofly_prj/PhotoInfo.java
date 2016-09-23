package com.ev4ngel.autofly_prj;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/7/1.
 */
public class PhotoInfo{
   public String Name="";
    public double Yaw=0;
    public double Pitch=0;
   public PhotoInfo(String name,float yaw,float pitch)
    {
        Name=name;
        Yaw=yaw;
        Pitch=pitch;
    }
    public PhotoInfo()
    {
    }

}
