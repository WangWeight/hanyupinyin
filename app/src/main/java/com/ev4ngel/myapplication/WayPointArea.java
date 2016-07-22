package com.ev4ngel.myapplication;

import android.graphics.Color;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Polygon;
import com.amap.api.maps2d.model.PolygonOptions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/7/18.
 */
public class WayPointArea {
    public ArrayList<LatLng> area_points;//GPS location,can be used by aircraft
    private ArrayList<Marker> area_side;//Maker with AMap location
    private MarkerOptions mMo=null;
    private PolygonOptions mPo=null;
    private Polygon mPolygon=null;
    public  WayPointArea()
    {
        area_points=new ArrayList<>();
        mMo=new MarkerOptions();
        mMo.draggable(true);
        mMo.icon(WayPointStatus.BeSide());

        mPo=new PolygonOptions();
        mPo.fillColor(Color.argb(100,255,0,0));
        mPo.strokeColor(Color.argb(200,0,255,0));
        mPo.strokeWidth(0.1f);

        area_side=new ArrayList<>();
    }
    public void add(LatLng loc) {
        area_points.add(loc);
    }
    public void updateArea(AMap map)
    {
        for(int i=area_side.size();i<area_points.size();i++) {
            Marker m = map.addMarker(new MarkerOptions());
            m.setPosition(iMap.fromGPSToMar(area_points.get(i)));
            m.setDraggable(true);
            area_side.add(m);
        }
        if(getCount()>3) {
            if (mPolygon == null)
                mPolygon = map.addPolygon(mPo);
            ArrayList<LatLng> tmp=new ArrayList<>();
            for(LatLng loc:area_points)
            {
                tmp.add(iMap.fromGPSToMar(loc));
            }
            mPolygon.setPoints(tmp);
        }
        map.invalidate();
    }

    public int getCount() {
        return area_points.size();
    }

    public void clear()//clear polygon,markers,and arraylist
    {
        area_points=new ArrayList<>();
        if(mPolygon!=null) {
            mPolygon.remove();
            mPolygon = null;
        }
        for(Marker m:area_side)
        {
            m.remove();
            m.destroy();
        }
        area_side=new ArrayList<>();
    }

}
