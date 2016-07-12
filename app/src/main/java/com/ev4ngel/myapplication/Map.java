package com.ev4ngel.myapplication;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.LocationSource.OnLocationChangedListener;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Polygon;
import com.amap.api.maps2d.model.PolygonOptions;


public class Map implements OnMapClickListener, AMapLocationListener ,LocationSource{
	MapView	mMapView=null;	
	AMap mMap=null;
	OnLocationChangedListener mListener=null;
	ArrayList<LatLng> mPoints;
	Polygon mArea=null;
	Marker mPlane=null;
	View mView=null;
	AMapLocationClient mLocClient=null;
	AMapLocationClientOption mLocCliOpt=null;
public void onCreate(Bundle savedInstanceState)
{
	
	mMapView.onCreate(savedInstanceState);
    mMap=mMapView.getMap();
    mMap.setMapType(AMap.MAP_TYPE_SATELLITE);
    mMap.setOnMapClickListener(this);
    mMap.setLocationSource(this);
    mMap.setMyLocationEnabled(true);
    UiSettings us=mMap.getUiSettings();
    us.setCompassEnabled(true);
    us.setMyLocationButtonEnabled(true);
    us.setScaleControlsEnabled(true);
    
    mPoints=new ArrayList<LatLng>();
    MarkerOptions planeOpt=new MarkerOptions();
    planeOpt.title("Plane");
    mPlane=new Marker(planeOpt);
    
}
	public Map(MapView mapview,View view) {
		// TODO Auto-generated constructor stub
		mMapView=mapview;
		mView=view;
	    //
	       
	}

	  protected void onDestroy() {

	    //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���
	    mMapView.onDestroy();
	  }

	 protected void onResume() {
	    //��activityִ��onResumeʱִ��mMapView.onResume ()��ʵ�ֵ�ͼ�������ڹ���
	    mMapView.onResume();
	    }

	 protected void onPause() {

	    mMapView.onPause();
	    }

	 protected void onSaveInstanceState(Bundle outState) {
	    //��activityִ��onSaveInstanceStateʱִ��mMapView.onSaveInstanceState (outState)��ʵ�ֵ�ͼ�������ڹ���
	    mMapView.onSaveInstanceState(outState);
	  }  

	    @Override
	    public void onLocationChanged(AMapLocation arg0) {
	    	// TODO Auto-generated method stub
	    	if(mListener!=null&&arg0!=null &&arg0.getErrorCode()==0)
	    	{
	    		mListener.onLocationChanged(arg0);
	    		Tools.i(mMapView.getContext(), "Locate on " + arg0.getAddress());
	    	}else
	    	{
	    		Tools.i(mMapView.getContext(),"Locate fail");
	    	}
	    }
	    
	 public void drawPlane()
	 {
		 
	 }
	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub
		
		Marker m=mMap.addMarker(new MarkerOptions());
		m.setPosition(arg0);
		m.setTitle("Lat:"+arg0.latitude+"\nLng"+arg0.longitude);
		
		if(mArea==null)
		{
			mArea=mMap.addPolygon(new PolygonOptions());
			mArea.setFillColor(Color.argb(100, 255, 0, 0));
			mArea.setStrokeColor(Color.argb(200, 0, 0, 255));
			mArea.setStrokeWidth(1);
		}
		mPoints.add(arg0);
		mArea.setPoints(mPoints);
		

		if(mPoints.size()>0 &&mPoints.size()%3==0)
		{
			
			mView.setVisibility(View.VISIBLE);
			mView.bringToFront();
		}
		else
		{
			mView.setVisibility(View.GONE);
		}
		Tools.i(mMapView.getContext(), "Lat:"+arg0.latitude+"\nLng"+arg0.longitude);
		
	}
		  
	@Override
	public void activate(OnLocationChangedListener arg0) {
		// TODO Auto-generated method stub
		if(arg0!=null)
		{
			mListener=arg0;
			mLocClient=new AMapLocationClient(mMapView.getContext());
			mLocCliOpt=new AMapLocationClientOption();
			mLocCliOpt.setLocationMode(AMapLocationMode.Hight_Accuracy);
			mLocCliOpt.setNeedAddress(true);
			mLocCliOpt.setOnceLocation(false);
			mLocCliOpt.setWifiActiveScan(true);
			mLocCliOpt.setGpsFirst(true);
			mLocCliOpt.setInterval(2000);
			mLocClient.setLocationOption(mLocCliOpt);
			mLocClient.setLocationListener(this);
			mLocClient.startLocation();
			Tools.i(mMapView.getContext(), "Acitvity success");
			
		}else
		{
			Tools.i(mMapView.getContext(), "Acitvity fail");
		}
	}
	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		mListener=null;
		if(mLocClient!=null)
		{
			mLocClient.stopLocation();
			mLocClient.onDestroy();
			mLocClient=null;
		}
		
	}

}