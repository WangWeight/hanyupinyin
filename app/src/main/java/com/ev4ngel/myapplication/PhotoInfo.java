package com.ev4ngel.myapplication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/7/1.
 */
public class PhotoInfo {
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
    public void fromJson(JSONObject jObj)
    {
        try {
            Name = jObj.getString("Name");
            Yaw=jObj.getDouble("Yaw");
            Pitch=jObj.getDouble("Pitch");
        }catch(JSONException e)
        {
        }
    }
    public JSONObject toJson()
    {
        JSONObject jObj=new JSONObject();
        try {
            jObj.put("Name", Name);
            jObj.put("Yaw", Yaw);
            jObj.put("Pitch", Pitch);
        }catch(JSONException e)
        {
        }
        return jObj;
    }
}
