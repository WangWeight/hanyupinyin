package com.ev4ngel.myapplication;

import com.amap.api.maps2d.model.LatLng;

/**
 * Created by jason on 2016/7/18.
 */
public class GPS {
    static double PI = 3.14159265358979324;
    static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
    static dian delta(double lat,double lon){
        // Krasovsky 1940
        //
        // a = 6378245.0, 1/f = 298.3
        // b = a * (1 - f)
        // ee = (a^2 - b^2) / a^2;
        double a = 6378245.0; //  a: 卫星椭球坐标投影到平面地图坐标系的投影因子。
        double ee = 0.00669342162296594323; //  ee: 椭球的偏心率。
        double dLat = GPS.transformLat(lon - 105.0, lat - 35.0);
        double dLon = GPS.transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * GPS.PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * GPS.PI);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * GPS.PI);
        dian result=new dian();
        result.setX(dLat);
        result.setY(dLon);
        return result;
        //return {'lat': dLat, 'lon': dLon};
    }

    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * GPS.PI) + 20.0 * Math.sin(2.0 * x * GPS.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * GPS.PI) + 40.0 * Math.sin(x / 3.0 * GPS.PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * GPS.PI) + 300.0 * Math.sin(x / 30.0 * GPS.PI)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * GPS.PI) + 20.0 * Math.sin(2.0 * x * GPS.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * GPS.PI) + 40.0 * Math.sin(y / 3.0 * GPS.PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * GPS.PI) + 320 * Math.sin(y * GPS.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    public static  LatLng mar2GPS(double gcjLat,double gcjLon){
        double initDelta = 0.01;
        double threshold = 0.000000001;
        double dLat = initDelta, dLon = initDelta;
        double mLat = gcjLat - dLat, mLon = gcjLon - dLon;
        double pLat = gcjLat + dLat, pLon = gcjLon + dLon;
        double wgsLat, wgsLon, i = 0;
        while (true) {
            wgsLat = (mLat + pLat) / 2;
            wgsLon = (mLon + pLon) / 2;
            dian tmp = GPS.gcj_encrypt(wgsLat, wgsLon);
            dLat = tmp.getX() - gcjLat;
            dLon = tmp.getY() - gcjLon;
            if ((Math.abs(dLat) < threshold) && (Math.abs(dLon) < threshold))
                break;

            if (dLat > 0) pLat = wgsLat; else mLat = wgsLat;
            if (dLon > 0) pLon = wgsLon; else mLon = wgsLon;

            if (++i > 10000) break;
        }
        return new LatLng(Math.abs(dLat),Math.abs(dLon));
    }
    public static LatLng mar2GPS(LatLng loc)
    {
        return mar2GPS(loc.latitude,loc.longitude);
    }
    private static dian gcj_encrypt(double gcjLat, double gcjLon) {
        dian d = GPS.delta(gcjLat, gcjLon);
        return d;
        //{'lat': gcjLat - d.lat, 'lon': gcjLon - d.lon};
    }
}