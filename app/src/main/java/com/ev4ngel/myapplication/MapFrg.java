package com.ev4ngel.myapplication;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.ev4ngel.autofly_prj.OnSaveWayPointListener;
import com.ev4ngel.autofly_prj.WayDesignFrg;
import com.ev4ngel.autofly_prj.WayPoint;

import java.util.ArrayList;
import java.util.Date;

import dji.common.flightcontroller.DJILocationCoordinate2D;
/**
 * Created by Administrator on 2016/7/15.
 */
public class MapFrg extends Fragment implements
        AMap.OnMapClickListener,
        AMapLocationListener,
        LocationSource,
        AMap.OnMarkerClickListener,
        AMap.OnMarkerDragListener,
        DialogInterface.OnClickListener,
        View.OnClickListener,
WayDesignFrg.OnSelectListener{
    MapView mMapView = null;
    AMap mMap = null;
    Marker mPlane = null;
    WayPointArea mArea = null;
    private OnSaveWayPointListener mSvListener=null;
    private OnLoadNewWayPointsListener mLdListener=null;
    public ArrayList<LatLng> mWayPoints_latlng = null;
    ArrayList<WayPoint> mWayPoints = null;
    ArrayList<String> mWayPoints_string = null;
    ArrayList<Marker> mWayPoints_marker=null;
    Polyline mLine = null;
    MTBSelectWidthFragment selectWidthFrg = null;
    MTBDesignFragment designFrg=null;
    MTBClearFragment clearFrg=null;
    WayDesignFrg mWayDesignFrg=null;
    LatLng defalut_latlng;
    int mMapOptStatus = MapOperationStatus.Design;//右侧按钮点击状态
    ArrayList<Fragment> sub_fragments;
    EditText save_name_et;//init when ask dialog showup on fab_save clicked
    AlertDialog save_line_ad;//init when ask
    AlertDialog pick_line_ad;
    AlertDialog show_line_ad;
    private boolean isLocating=false;
    private MapMode mm=MapMode.Design;
    private LatLng startPoint;//Set it to special point in someday
    Fragment current_show_frg=null;
    public void setMode(MapMode mode) {
        mm=mode;
        if(mode==MapMode.Design){
            getChildFragmentManager().beginTransaction().show(mWayDesignFrg).commit();
            if(current_show_frg!=null)
                showMapbar(current_show_frg);
        }else
        {
            getChildFragmentManager().beginTransaction().hide(mWayDesignFrg).commit();

        }
    }

    public void moveTo(LatLng pos)
    {
        mMap.moveCamera(CameraUpdateFactory.changeLatLng(pos));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
    }
    public void updatePlane(LatLng pos,float angle)
    {
        LatLng tmp=MapFrg.fromGPSToMar(pos);
        if(mPlane==null)
            mPlane=mMap.addMarker(init_plane(null));
        mPlane.setPosition(tmp);
        mPlane.setRotateAngle(angle * (-1));
        if(!isLocating)
            moveTo(tmp);
        isLocating=true;
    }

    public void cal_waypoints()
    {
        CalcBox cb = new CalcBox();
        if (startPoint == null){
            if(mPlane!=null){
                startPoint = GPS.mar2GPS(mPlane.getPosition());
            }else{
                startPoint = mArea.area_points.get(0);
            }
        }

        if (mWayPoints_string.size() > 0) {
            clear();
        }
        mWayPoints_latlng = cb.calcNearestPlanPointList(mArea.area_points.get(0),
                mArea.area_points.get(1),
                mArea.area_points.get(2),
                selectWidthFrg.getDirectionWidth(),
                startPoint,
                selectWidthFrg.getSideWidth());
        if(mWayPoints==null ||mWayPoints.size()>0)
            mWayPoints=new ArrayList<>();
        for(LatLng loc:mWayPoints_latlng)
        {
            mWayPoints.add(new WayPoint(loc,WayPointStatus.Wait));
        }
    }
    private  void _draw_line(final boolean isPointsReady)
    {
            if (!isPointsReady)
                cal_waypoints();
            for (WayPoint loc : mWayPoints) {
                Marker m = mMap.addMarker(init_waypoint_status(loc.status));

                m.setPosition(MapFrg.fromGPSToMar(loc.toLatLng()));
                m.setTitle(m.getId());
                mWayPoints_string.add(m.getId());
            }
            if (mLine == null) {
                mLine = mMap.addPolyline(new PolylineOptions());
                mLine.setColor(Color.argb(100, 0, 0, 255));
                mLine.setWidth(2);
            }
            mLine.setPoints(mWayPoints_latlng);//I should convert DJICoordinate2D to LatLng type,boring
    }
    public void drawline(boolean isPointsReady)//若isPointsReady,不会调用计算的方法，否则会根据mArea的点重新计算
    {
        int maybe_number=mWayPoints.size();
        if(!isPointsReady)
            maybe_number=(int)(AMapUtils.calculateLineDistance(mArea.area_points.get(0),mArea.area_points.get(1))*AMapUtils.calculateLineDistance(mArea.area_points.get(2),mArea.area_points.get(1))/selectWidthFrg.getDirectionWidth()/selectWidthFrg.getSideWidth());
        if(maybe_number>Common.MAX_NUMBER_OF_WAYPOINTS)
        {

            show_line_ad=new AlertDialog.Builder(getActivity())
                    .setTitle("是不是有点多?")
                    .setMessage("大概能有"+maybe_number+"个点(比设置的["+Common.MAX_NUMBER_OF_WAYPOINTS+"]多不少)\n这约莫会占用很长时间(或者当掉你的app)")
                    .setPositiveButton("继续呗", this)
                    .setNegativeButton("算了吧", null)
                    .show();
        }else {
            _draw_line(isPointsReady);
        }
    }
    public Marker makeMarkerFromWaypoint(WayPoint wp){
        MarkerOptions mo=new MarkerOptions();
        mo.position(new LatLng(wp.lat, wp.lng));
        return mMap.addMarker(mo);
    }
    public Marker findMarkerById(String id)
    {
        return makeMarkerFromWaypoint(findWayPointById(id));
    }
    public WayPoint findWayPointById(String id)
    {
        return mWayPoints.get(mWayPoints_string.indexOf(id));
    }
    public void updateWayPointByIndex(int id,float status)
    {
        makeMarkerFromWaypoint(mWayPoints.get(id)).setIcon(BitmapDescriptorFactory.defaultMarker(status));;
    }
    public void clear(){
        LatLng plane_latlng=defalut_latlng;
        if(mPlane!=null)
            plane_latlng=mPlane.getPosition();
        mMap.clear();
        mArea.clear();
        mPlane=mMap.addMarker(init_plane(plane_latlng));
    }
    private MarkerOptions init_plane(LatLng pos)
    {
        if(pos==null)
            pos=defalut_latlng;
        MarkerOptions mo = new MarkerOptions();
        mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.plane));
        mo.anchor(0.5f, 0.5f);
        mo.position(pos);
        return mo;
    }
    private MarkerOptions init_waypoint_status(float wps)
    {
        MarkerOptions mo=new MarkerOptions();
        mo.icon(BitmapDescriptorFactory.defaultMarker(wps)) ;
        return mo;
    }
    private void init_map() {
        mMap.setMapType(AMap.MAP_TYPE_SATELLITE);
        mMap.setOnMapClickListener(this);
        //mMap.setLocationSource(this);
        //mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(this);
        UiSettings us=mMap.getUiSettings();
        us.setCompassEnabled(false);
        //us.setMyLocationButtonEnabled(true);
        us.setScaleControlsEnabled(true);
        us.setZoomPosition(2);
        mArea =new WayPointArea();
        setWayPoints(new ArrayList<WayPoint>());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View map_view= inflater.inflate(R.layout.imap_layout,container,false);
        defalut_latlng=new LatLng(41.803200,123.428000);
        mMapView=(MapView)map_view.findViewById(R.id.imap_id);
        return map_view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapView.onCreate(savedInstanceState);
        mMap=mMapView.getMap();
        init_map();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        //moveTo(latLng);
        if(mm==MapMode.Design) {
            if (mWayDesignFrg.mStatus==WayDesignFrg.Status.isDesignning) {
                if (mArea.getCount() > 3) {
                    clear();
                    setWayPoints(new ArrayList<WayPoint>());
                }
                mArea.add(new GPS().mar2GPS(latLng));
                if (mArea.getCount() == 3) {
                    LatLng _4th = new CalcBox().calc4thPoint(mArea.area_points.get(0),
                            mArea.area_points.get(1),
                            mArea.area_points.get(2));
                    mArea.add(_4th);
                }
                mArea.updateArea(mMap);
            }else if(mWayDesignFrg.mStatus==WayDesignFrg.Status.isChoosing){

            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        sub_fragments=new ArrayList<>();
        mWayDesignFrg=(WayDesignFrg)getChildFragmentManager().findFragmentById(R.id.waypoint_design_frg);
        mWayDesignFrg.setOnSelectListener(this);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if(designFrg==null)
        {
            designFrg=new MTBDesignFragment();
            ft.add(R.id.tool_bar_container, designFrg,""+R.layout.frag_mtb_design);
            ft.hide(designFrg);
            sub_fragments.add(designFrg);
        }
        if (selectWidthFrg==null) {
            selectWidthFrg = new MTBSelectWidthFragment();
            ft.add(R.id.tool_bar_container, selectWidthFrg, "" + R.layout.frag_mtb_change_width);
            ft.hide(selectWidthFrg);
            sub_fragments.add(selectWidthFrg);
        }

        if(clearFrg==null)
        {
            clearFrg=new MTBClearFragment();
            ft.add(R.id.tool_bar_container, clearFrg,""+R.layout.frag_mtb_clear);
            ft.hide(clearFrg);
            sub_fragments.add(clearFrg);
        }
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        try {
            getFragmentManager().beginTransaction().remove(selectWidthFrg).remove(designFrg).remove(clearFrg).commit();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onDestroyView();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(mm==MapMode.Design) {
            switch (mMapOptStatus) {
                case MapOperationStatus.Clear: {//Maybe i can add more operation to this
                    marker.remove();
                    int index=mWayPoints_string.indexOf(marker.getId());
                    mWayPoints.remove(index);
                    mWayPoints_latlng.remove(index);
                    mWayPoints_string.remove(index);
                    mLine.setPoints(mWayPoints_latlng);
                }
                break;
                case MapOperationStatus.Design: {
                }
                break;
            }
        }
        return false;
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mArea.updateArea(mMap);
        //mArea.updateSide(marker.getId(), marker.getPosition());
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

    }
    public static LatLng fromGPSToMar(LatLng loc)
    {
        //LatLng d=GPS.mar2GPS(loc.latitude,loc.longitude);
        CoordinateConverter cc=new CoordinateConverter(AutoflyApplication.getContext());
        cc.from(CoordinateConverter.CoordType.GPS);
        try {
            cc.coord(new DPoint(loc.latitude, loc.longitude));
            DPoint dd=cc.convert();
            return new LatLng(dd.getLatitude(),dd.getLongitude());
        }catch (Exception e)
        {

        }
        return null;
    }

    public  void showMapbar(Fragment f)
    {//如果f现在可见，则将其不可见，并恢复fab_save可见
        // 如果f不可见，将其他不可见并将其可见，fab_save不可见
        FragmentTransaction ft=getFragmentManager().beginTransaction();
        for(Fragment sf:sub_fragments)
        {
            if(f==null) {
                ft.hide(sf);
                continue;
            }
            if(!sf.getTag().equals(f.getTag()))
            {
                ft.hide(sf);
            }
            else
            {
                current_show_frg=f;
                ft.show(f);
            }
        }

        ft.commit();
    }

    @Override
    public void onClick(View v) {
       if(v.getId()==R.id.clear_map_bt)
       {
           clear();
       }
    }
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(dialog!=null) {
            /*
            if (dialog.toString().equals(save_line_ad.toString())) {
                if(which==DialogInterface.BUTTON_POSITIVE){
                    String text=save_name_et.getText().toString();
                    if(!text.isEmpty()) {
                        if (mSvListener != null)
                            mSvListener.onSaveWayPoint(text, mWayPoints);
                        Log.i("E","+"+mWayPoints.toString());
                    }
                    //Toast.makeText(AutoflyApplication.getContext(),save_name_et.getText().toString(),Toast.LENGTH_SHORT).show();
                }
            } else if (dialog.toString().equals(pick_line_ad.toString())) {
                if(mLdListener!=null)
                    mLdListener.onLoadNewWayPoints(mWayPoints_string.get(which));
            } else */if(dialog.toString().equals(show_line_ad.toString())){
                if (which == DialogInterface.BUTTON_POSITIVE)
                    _draw_line(false);
            }
        }
    }
    public static ArrayList<LatLng> convertFrom2D(ArrayList<DJILocationCoordinate2D> s)
    {
        ArrayList<LatLng> r=new ArrayList<>();
        for(DJILocationCoordinate2D ss:s)
        {
            r.add(MapFrg.fromGPSToMar(new LatLng(ss.getLatitude(), ss.getLongitude())));
        }
        return r;
    }
    public  MapFrg setWayPoints(ArrayList<WayPoint> aaa)
    {
        mWayPoints=aaa;
        drawline(true);
        if(mWayPoints_string==null || mWayPoints_string.size()!=0)
            mWayPoints_string=new ArrayList<>();
        if(mWayPoints_marker==null||mWayPoints_marker.size()!=0)
            mWayPoints_marker=new ArrayList<>();
        if(mWayPoints_latlng==null||mWayPoints_latlng.size()!=0)
            mWayPoints_latlng=new ArrayList<>();
        return this;
    }

    public void setOnSaveWayPointListener(OnSaveWayPointListener listener)
    {
        mSvListener=listener;
    }

    @Override
    public void onMenuItemSelect(WayDesignFrg.Status status) {
        switch (status){
            case isChoosing:{
                showMapbar(selectWidthFrg);
            }break;
            case isClearing:{
               showMapbar(clearFrg);
            }break;
            case isDesignning:{
                showMapbar(designFrg);
            }break;
            case isSaving:{
                if (mArea.getCount() == 0) {
                    Toast.makeText(AutoflyApplication.getContext(), "请先圈定范围", Toast.LENGTH_SHORT).show();
                } else {
                    mSvListener.onSaveWayPoint(new Date().toString(),mWayPoints);
                    /*
                    View vv = LayoutInflater.from(AutoflyApplication.getContext()).inflate(R.layout.save_line_dialog, null);
                    save_name_et = (EditText) vv.findViewById(R.id.save_line_et);
                    save_line_ad = new AlertDialog.Builder(getActivity())
                            .setTitle("输入航线名称")
                            .setView(vv)
                            .setPositiveButton("保存", MapFrg.this)
                            .setNegativeButton("取消", null).create();
                    save_line_ad.show();
                    */
                }
            }break;
            case isHidding:{
                showMapbar(null);
            }break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden && mm==MapMode.Design&&current_show_frg!=null){
            getChildFragmentManager().beginTransaction().show(current_show_frg).commit();
        }
    }
}
