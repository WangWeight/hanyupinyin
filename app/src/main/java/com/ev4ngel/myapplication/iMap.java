package com.ev4ngel.myapplication;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
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

import java.util.ArrayList;

import dji.sdk.FlightController.DJIFlightControllerDataType;

/**
 * Created by Administrator on 2016/7/15.
 */
public class iMap extends Fragment implements
        AMap.OnMapClickListener,
        AMapLocationListener,
        LocationSource,
        AMap.OnMarkerClickListener,
        AMap.OnMarkerDragListener,
        DialogInterface.OnClickListener,
        View.OnClickListener{
    MapView mMapView = null;
    AMap mMap = null;
    Marker mPlane = null;
    WayPointArea mArea = null;
    public ArrayList<DJIFlightControllerDataType.DJILocationCoordinate2D> mWayPoints_latlng = null;
    ArrayList<WayPoint> mWayPoints = null;
    ArrayList<String> mWayPoints_string = null;
    Polyline mLine = null;
    MTBSelectWidthFragment selectWidthFrg = null;
    MTBDesignFragment designFrg=null;
    MTBClearFragment clearFrg=null;

    int mMapOptStatus = MapOperationStatus.Design;//右侧按钮点击状态
    ArrayList<Fragment> sub_fragments;
    FloatingActionButton fab_set;
    FloatingActionButton fab_clear;
    FloatingActionButton fab_draw;
    FloatingActionButton fab_save;

    EditText save_name_et;//init when ask dialog showup on fab_save clicked
    AlertDialog save_line_ad;//init when ask
    private MapMode mm=MapMode.Design;
    private LatLng startPoint;//Set it to special point in someday
    public void setMode(MapMode mode) {
        mm=mode;
        if(mode==MapMode.Design){
            fab_save.setVisibility(View.VISIBLE);
            fab_set.setVisibility(View.VISIBLE);
            fab_draw.setVisibility(View.VISIBLE);
            fab_clear.setVisibility(View.VISIBLE);

        }else
        {
            fab_save.setVisibility(View.GONE);
            fab_set.setVisibility(View.GONE);
            fab_draw.setVisibility(View.GONE);
            fab_clear.setVisibility(View.GONE);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void moveTo(LatLng pos)
    {
        mMap.moveCamera(CameraUpdateFactory.changeLatLng(pos));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
    }
    public void updatePlane(LatLng pos,float angle)
    {
        mPlane.setPosition(iMap.fromGPSToMar(pos));
        mPlane.setRotateAngle(angle);
    }
    private  void _draw_line()
    {
        new Handler(AutoflyApplication.getContext().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                CalcBox cb = new CalcBox();
                if (startPoint == null)
                    startPoint = mArea.area_points.get(0);
                if (mWayPoints_string.size() > 0) {
                    mMap.clear();
                }
                mWayPoints_latlng = cb.calcNearestPlanPointList(new DJIFlightControllerDataType.DJILocationCoordinate2D(mArea.area_points.get(0)),
                        new DJIFlightControllerDataType.DJILocationCoordinate2D(mArea.area_points.get(1)),
                        new DJIFlightControllerDataType.DJILocationCoordinate2D(mArea.area_points.get(2)),
                        selectWidthFrg.getDirectionWidth(), new DJIFlightControllerDataType.DJILocationCoordinate2D(startPoint));
                for (DJIFlightControllerDataType.DJILocationCoordinate2D loc : mWayPoints_latlng) {
                    Marker m = mMap.addMarker(init_waypoint());
                    m.setPosition(iMap.fromGPSToMar(new LatLng(loc.getLatitude(), loc.getLongitude())));
                    mWayPoints.add(new WayPoint(m, WayPointStatus.Wait));
                    mWayPoints_string.add(m.getId());
                }
                if (mLine == null) {
                    mLine = mMap.addPolyline(new PolylineOptions());
                    mLine.setColor(Color.argb(100, 0, 0, 255));
                    mLine.setWidth(1);
                }
                mLine.setPoints(convertFrom2D(mWayPoints_latlng));//I should convert DJICoordinate2D to LatLng type,boring
            }
        });
    }
    public void drawline()
    {
        int maybe_number=(int)(AMapUtils.calculateLineDistance(mArea.area_points.get(0),mArea.area_points.get(1))*AMapUtils.calculateLineDistance(mArea.area_points.get(2),mArea.area_points.get(1))/selectWidthFrg.getDirectionWidth()/selectWidthFrg.getSideWidth());
        if(maybe_number>Common.MAX_NUMBER_OF_WAYPOINTS)
        {
            new AlertDialog.Builder(getActivity())
                    .setTitle("是不是有点多?")
                    .setMessage("大概能有"+maybe_number+"个点(比设置的["+Common.MAX_NUMBER_OF_WAYPOINTS+"]多不少)\n这约莫会占用很长时间(或者当掉你的app)")
                    .setPositiveButton("继续呗", this)
                    .setNegativeButton("算了吧", null)
                    .show();
        }else {
            _draw_line();
        }
    }

    public Marker findMarkerById(String id)
    {
        return findWayPointById(id).mkr;
    }
    public WayPoint findWayPointById(String id)
    {
        return mWayPoints.get(mWayPoints_string.indexOf(id));
    }
    public void updateWayPointByIndex(int id,float status)
    {
        mWayPoints.get(id).mkr.setIcon(BitmapDescriptorFactory.defaultMarker(status));;
    }
    private MarkerOptions init_plane()
    {
        MarkerOptions mo=new MarkerOptions();
        mo.icon(BitmapDescriptorFactory.fromResource(android.R.drawable.arrow_up_float));
        mo.anchor(0, 0);
        return mo;
    }
    private MarkerOptions init_waypoint()
    {
        MarkerOptions mo=new MarkerOptions();
        mo.icon(BitmapDescriptorFactory.defaultMarker(WayPointStatus.Wait)) ;
        return mo;
    }
    private void init_buttons(View view)
    {
        fab_draw=(FloatingActionButton)view.findViewById(R.id.start_line);
        fab_draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//Two kind of statuses,editing and adding
                showMapbar(designFrg);
                mMapOptStatus=MapOperationStatus.Design;
            }
        });
        fab_set=(FloatingActionButton)view.findViewById(R.id.set_line);
        fab_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMapbar(selectWidthFrg);
                mMapOptStatus=MapOperationStatus.Other;
            }
        });
        fab_clear=(FloatingActionButton)view.findViewById(R.id.clear_line);
        fab_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mArea.clear();
                showMapbar(clearFrg);
                mMapOptStatus=MapOperationStatus.Clear;
            }
        });
        fab_save=(FloatingActionButton) view.findViewById(R.id.save_line);
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapOptStatus = MapOperationStatus.Other;
                if (mArea.getCount() == 0) {
                    Toast.makeText(AutoflyApplication.getContext(), "请先圈定范围", Toast.LENGTH_SHORT).show();
                } else {
                    View vv = LayoutInflater.from(AutoflyApplication.getContext()).inflate(R.layout.save_line_dialog, null);
                    save_name_et = (EditText) vv.findViewById(R.id.save_line_et);
                    save_line_ad = new AlertDialog.Builder(getActivity())
                            .setTitle("输入航线名称")
                            .setView(vv)
                            .setPositiveButton("保存", null)
                            .setNegativeButton("取消", null).create();
                    save_line_ad.show();
                }
            }
        });
    }
    private void init_map() {
        mMap.setMapType(AMap.MAP_TYPE_SATELLITE);
        mMap.setOnMapClickListener(this);
        //mMap.setLocationSource(this);
        //mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(this);
        UiSettings us=mMap.getUiSettings();
        us.setCompassEnabled(true);
        us.setMyLocationButtonEnabled(true);
        us.setScaleControlsEnabled(true);
        us.setZoomPosition(2);

        mPlane=mMap.addMarker(init_plane());
        mArea=new WayPointArea();
        mWayPoints=new ArrayList<>();
        mWayPoints_string=new ArrayList<>();
        mWayPoints_latlng=new ArrayList<>();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View map_view= inflater.inflate(R.layout.imap_layout,container,false);
        mMapView=(MapView)map_view.findViewById(R.id.imap_id);
        init_buttons(map_view);
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
            if (mMapOptStatus == MapOperationStatus.Design) {
                if (mArea.getCount() > 3) {
                    mArea.clear();
                    for (WayPoint w : mWayPoints) {
                        w.clear();
                    }
                    mWayPoints = new ArrayList<>();
                    mWayPoints_latlng = new ArrayList<>();
                    mWayPoints_string = new ArrayList<>();
                }
                mArea.add(new GPS().mar2GPS(latLng));
                if (mArea.getCount() == 3) {
                    DJIFlightControllerDataType.DJILocationCoordinate2D _4th = new CalcBox().calc4thPoint(new DJIFlightControllerDataType.DJILocationCoordinate2D(mArea.area_points.get(0)),
                            new DJIFlightControllerDataType.DJILocationCoordinate2D(mArea.area_points.get(1)),
                            new DJIFlightControllerDataType.DJILocationCoordinate2D(mArea.area_points.get(2)));
                    mArea.add(new LatLng(_4th.getLatitude(), _4th.getLongitude()));
                }
                mArea.updateArea(mMap);
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        sub_fragments=new ArrayList<>();
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
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
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
                    mLine.setPoints(convertFrom2D(mWayPoints_latlng));
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
        if(f!=null)
        {
            for(Fragment sf:sub_fragments)
            {
                if(!sf.getTag().equals(f.getTag()))
                {
                    ft.hide(sf);
                }
                else
                {
                    if(f.isHidden())
                    {
                        ft.show(f);
                        fab_save.setVisibility(View.GONE);
                    }
                    else {
                        ft.hide(f);
                        fab_save.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        ft.commit();
    }

    @Override
    public void onClick(View v) {
       if(v.getId()==R.id.clear_map_bt)
       {
           mMap.clear();
           mArea.clear();
       }
    }
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (dialog.toString().equals(save_line_ad.toString())) {
            if(which==DialogInterface.BUTTON_POSITIVE){
                Toast.makeText(AutoflyApplication.getContext(),save_name_et.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        } else {
            if (which == DialogInterface.BUTTON_POSITIVE)
                _draw_line();
        }
    }
    public static ArrayList<LatLng> convertFrom2D(ArrayList<DJIFlightControllerDataType.DJILocationCoordinate2D> s)
    {
        ArrayList<LatLng> r=new ArrayList<>();
        for(DJIFlightControllerDataType.DJILocationCoordinate2D ss:s)
        {
            r.add(iMap.fromGPSToMar(new LatLng(ss.getLatitude(),ss.getLongitude())));
        }
        return r;
    }

}
