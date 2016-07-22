package com.ev4ngel.myapplication;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/7/18.
 */
public class WayPoint {
    static String item_lat="latitude";
    static String item_lng="longitude";
    static String item_status="status";
    public Marker mkr;
    public float status;
    public  WayPoint(Marker m,float s)
    {
        mkr=m;
        status=s;
    }
    public void clear()
    {
        if(mkr!=null)
        {
            mkr.remove();
            mkr.destroy();
        }
    }
    public JSONObject toJson()
    {
        JSONObject obj=new JSONObject();
        LatLng pos=mkr.getPosition();
        try {
            obj.put(item_lat, pos.latitude);
            obj.put(item_lng, pos.longitude);
            obj.put(item_status, status);
        }catch (JSONException je)
        {
        }
        return obj;
    }
    public void fromJson(JSONObject jObj)
    {
        MarkerOptions mo=new MarkerOptions();
        try {
            mo.position(new LatLng((float) jObj.getDouble(item_lat), (float) jObj.getDouble(item_lng)));
        }catch (JSONException je)
        {
        }
    }
}
