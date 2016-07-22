package com.ev4ngel.autofly_prj;

import com.amap.api.maps2d.model.LatLng;

/**
 * Created by Administrator on 2016/7/19.
 */
public class DJILocationCoordinate2D {
    private double lat;
    private double lng;
    public double getLongitude()
    {
        return lng;
    }
    public double getLatitude()
    {
        return lat;
    }
    public void setLongitude(double _lng)
    {
        lng=_lng;
    }
    public void setLatitude(double _lat)
    {
        lat=_lat;
    }
public DJILocationCoordinate2D()
{

}
    public DJILocationCoordinate2D(LatLng loc)
    {
        lat=loc.latitude;
        lng=loc.longitude;
    }
}
