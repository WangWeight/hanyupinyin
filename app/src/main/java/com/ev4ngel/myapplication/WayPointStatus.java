package com.ev4ngel.myapplication;

import android.graphics.BitmapFactory;

import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;

/**
 * Created by Administrator on 2016/7/18.
 */
public class WayPointStatus {
    public static float Wait=BitmapDescriptorFactory.HUE_RED;
    public static float Dealing=BitmapDescriptorFactory.HUE_YELLOW;
    public static float Done= BitmapDescriptorFactory.HUE_GREEN;

    public static float Side=BitmapDescriptorFactory.HUE_VIOLET;

    public static BitmapDescriptor BeWait()
    {
        return BitmapDescriptorFactory.defaultMarker(WayPointStatus.Wait);
    }
    public static BitmapDescriptor BeDealing()
    {
        return BitmapDescriptorFactory.defaultMarker(WayPointStatus.Dealing);
    }
    public static BitmapDescriptor BeDone()
    {
        return BitmapDescriptorFactory.defaultMarker(WayPointStatus.Done);
    }
    public static BitmapDescriptor BeSide()
    {
        return BitmapDescriptorFactory.defaultMarker(WayPointStatus.Side);
    }
}
