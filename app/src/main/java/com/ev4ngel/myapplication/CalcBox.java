package com.ev4ngel.myapplication;

import com.amap.api.maps2d.model.LatLng;

import java.util.ArrayList;

import dji.sdk.FlightController.DJIFlightControllerDataType;

/**
 * Created by jason on 2016/7/5.
 * version 0.3.1
 */

public class CalcBox {


    public dian gaussProjCal(double longitude, double latitude)
    {
        dian point=new dian();
        int ProjNo = 0; int ZoneWide; ////带宽
        double longitude1, latitude1, longitude0, latitude0, X0, Y0, xval, yval;
        double a, f, e2, ee, NN, T, C, A, M, iPI;
        iPI = 0.0174532925199433; ////3.1415926535898/180.0;
        ZoneWide = 6; ////6度带宽
        a = 6378245.0; f = 1.0 / 298.3; //54年北京坐标系参数
        ////a=6378140.0; f=1/298.257; //80年西安坐标系参数
        ProjNo = (int)(longitude / ZoneWide);
        longitude0 = ProjNo * ZoneWide + ZoneWide / 2;
        longitude0 = longitude0 * iPI;
        latitude0 = 0;
        longitude1 = longitude * iPI; //经度转换为弧度
        latitude1 = latitude * iPI; //纬度转换为弧度
        e2 = 2 * f - f * f;
        ee = e2 * (1.0 - e2);
        NN = a / Math.sqrt (1.0 - e2 * Math.sin(latitude1) * Math.sin(latitude1));
        T = Math.tan(latitude1) * Math.tan(latitude1);
        C = ee * Math.cos(latitude1) * Math.cos(latitude1);
        A = (longitude1 - longitude0) * Math.cos(latitude1);


        M = a * ((1 - e2 / 4 - 3 * e2 * e2 / 64 - 5 * e2 * e2 * e2 / 256) * latitude1 - (3 * e2 / 8 + 3 * e2 * e2 / 32 + 45 * e2 * e2
                * e2 / 1024) * Math.sin(2 * latitude1)
                + (15 * e2 * e2 / 256 + 45 * e2 * e2 * e2 / 1024) * Math.sin(4 * latitude1) - (35 * e2 * e2 * e2 / 3072) * Math.sin(6 * latitude1));
        xval = NN * (A + (1 - T + C) * A * A * A / 6 + (5 - 18 * T + T * T + 72 * C - 58 * ee) * A * A * A * A * A / 120);
        yval = M + NN * Math.tan(latitude1) * (A * A / 2 + (5 - T + 9 * C + 4 * C * C) * A * A * A * A / 24
                + (61 - 58 * T + T * T + 600 * C - 330 * ee) * A * A * A * A * A * A / 720);
        X0 = 1000000L * (ProjNo + 1) + 500000L;
        Y0 = 0;
        xval = xval + X0; yval = yval + Y0;
        point.setX(yval);//北方向
        point.setY(xval);
        return point;
        //X = xval;
        //Y = yval;
    }

    //高斯投影由大地坐标(Unit:Metres)反算经纬度(Unit:DD)
    //y应该是北坐标x应该是东坐标
    public LatLng gaussProjInvCal(double Y,double X)
    {
        //LatLng point=new LatLng();
        int ProjNo; int ZoneWide; ////带宽
        double longitude1, latitude1, longitude0, latitude0, X0, Y0, xval, yval;
        double e1, e2, f, a, ee, NN, T, C, M, D, R, u, fai, iPI;
        iPI = 0.0174532925199433; ////3.1415926535898/180.0;
        a = 6378245.0; f = 1.0 / 298.3; //54年北京坐标系参数
        ////a=6378140.0; f=1/298.257; //80年西安坐标系参数
        ZoneWide = 6; ////6度带宽
        ProjNo = (int)(X / 1000000L); //查找带号
        longitude0 = (ProjNo - 1) * ZoneWide + ZoneWide / 2;
        longitude0 = longitude0 * iPI; //中央经线
        X0 = ProjNo * 1000000L + 500000L;
        Y0 = 0;
        xval = X - X0; yval = Y - Y0; //带内大地坐标
        e2 = 2 * f - f * f;
        e1 = (1.0 - Math.sqrt(1 - e2)) / (1.0 + Math.sqrt(1 - e2));
        ee = e2 / (1 - e2);
        M = yval;
        u = M / (a * (1 - e2 / 4 - 3 * e2 * e2 / 64 - 5 * e2 * e2 * e2 / 256));
        fai = u + (3 * e1 / 2 - 27 * e1 * e1 * e1 / 32) * Math.sin(2 * u) + (21 * e1 * e1 / 16 - 55 * e1 * e1 * e1 * e1 / 32) * Math.sin(
                4 * u)
                + (151 * e1 * e1 * e1 / 96) * Math.sin(6 * u) + (1097 * e1 * e1 * e1 * e1 / 512) * Math.sin(8 * u);
        C = ee * Math.cos(fai) * Math.cos(fai);
        T = Math.tan(fai) * Math.tan(fai);
        NN = a / Math.sqrt(1.0 - e2 * Math.sin(fai) * Math.sin(fai));
        R = a * (1 - e2) / Math.sqrt((1 - e2 * Math.sin(fai) * Math.sin(fai)) * (1 - e2 * Math.sin(fai) * Math.sin(fai)) * (1 - e2 * Math.sin
                (fai) * Math.sin(fai)));
        D = xval / NN;
        //计算经度(Longitude) 纬度(Latitude)
        longitude1 = longitude0 + (D - (1 + 2 * T + C) * D * D * D / 6 + (5 - 2 * C + 28 * T - 3 * C * C + 8 * ee + 24 * T * T) * D
                * D * D * D * D / 120) / Math.cos(fai);
        latitude1 = fai - (NN * Math.tan(fai) / R) * (D * D / 2 - (5 + 3 * T + 10 * C - 4 * C * C - 9 * ee) * D * D * D * D / 24
                + (61 + 90 * T + 298 * C + 45 * T * T - 256 * ee - 3 * C * C) * D * D * D * D * D * D / 720);
        //转换为度 DD
        //point.setLatitude();
        //point.setLongitude();
        //longitude = longitude1 / iPI;
        //latitude = latitude1 / iPI;
        return new LatLng(latitude1/iPI,longitude1/iPI);
    }

    //直角坐标系下计算
    private dian zuoBiaoZhengSuan(double x,double y,double distance ,double angle){
        dian point=new dian();
        point.setX(x+distance*Math.cos(angle));
        point.setY(y+distance*Math.sin(angle));
        return point;
    }

    //直角坐标系下计算
    private double zuoBiaoFanSuan(dian point1,dian point2){
        double dx=point1.getX()-point2.getX();
        double dy=point1.getY()-point2.getY();
        double ddx=Math.pow(dx,2);
        double ddy=Math.pow(dy,2);
        double distance=Math.sqrt(ddx+ddy);
        return distance;
    }
    //计算方位角
    private double jisuanfangwei(double xa, double xb, double ya, double yb)
    {
        double dy = yb - ya;                   //y的变化量
        double dx = xb - xa;                   //x的变化量
        double AA = 0;                              //方位角及象限角划到象限角

        //if (dy == 0 )
        //{
        //    if (dx > 0)
        //    {
        //        AA = 0;
        //    }
        //    else if (dx < 0)
        //    {
        //        AA = Math.PI;
        //    }
        //}

        double A = Math.abs(dy) / Math.abs(dx);          //象限角

        A = Math.atan(A);
        if (dx >= 0)                           //判断方位角
        {
            if (dy >= 0)
            {
                AA = A;
            }
            else
            {
                AA = 2 * Math.PI;
                AA = AA - A;
            }

        }
        else
        {
            if (dy >= 0)
            {
                AA = Math.PI;
                AA = AA - A;
            }
            else
            {
                AA = Math.PI;
                AA = AA + A;
            }
            //AA = A + AA;
            // return AA;
        }
        return AA;
    }

    //单位是度
    public LatLng coorPosiCalc(double longitude,double latitude,double distance,double angle){
        LatLng resultePoint;//=new LatLng();
        dian tempPoint=new dian();
        dian point=new dian();

        point=gaussProjCal(longitude,latitude);
        tempPoint=zuoBiaoZhengSuan(point.getX(),point.getY(),distance,angle);
        resultePoint=gaussProjInvCal(tempPoint.getX(),tempPoint.getY());
        return resultePoint;
    }

    //单位是度
    public double coorNageCalcDistance(LatLng point1,LatLng point2){
        dian pointA=gaussProjCal(point1.longitude,point1.latitude);
        dian pointB=gaussProjCal(point2.longitude,point2.latitude);

        return zuoBiaoFanSuan(pointA,pointB);
    }
    public double coorNageCalcAngle(LatLng point1,LatLng point2){
        //dian pointA=gaussProjCal(point1.latitude,point1.longitude);
        //dian pointB=gaussProjCal(point2.latitude,point2.longitude);
        dian pointA=gaussProjCal(point1.longitude,point1.latitude);
        dian pointB=gaussProjCal(point2.longitude,point2.latitude);
        return jisuanfangwei(pointA.getX(),pointB.getX(),pointA.getY(),pointB.getY());
    }

    public static LatLng panduandms(LatLng point)
    {
        LatLng a = new LatLng(point.latitude,point.longitude);
        try
        {
         if ((a.longitude > 0 && a.longitude < 100 && a.latitude > 100 && a.latitude < 180))
        {
            //double temp = a.latitude;
            a=new LatLng(a.longitude,a.latitude);
        }
        }
        catch (Exception e)
        { }
        return a;

    }
    //1-----2
    //|     |
    //4-----3
    //点位示意
    public ArrayList<LatLng> calcPlanPointList(LatLng point1,LatLng point2,LatLng point3,double dianJianGe){
        ArrayList<LatLng> pointList=new ArrayList<>();
        ArrayList<LatLng> tempList=new ArrayList<>();
        ArrayList<LatLng> shu14List=new ArrayList<>();
        ArrayList<LatLng> shu23List=new ArrayList<>();

        dian pointA=gaussProjCal(point1.longitude,point1.latitude);
        dian pointB=gaussProjCal(point2.longitude,point2.latitude);
        //dian pointC=gaussProjCal(point3.latitude,point3.longitude);

        double distance12=coorNageCalcDistance(point1,point2);


        double angle12=jisuanfangwei(pointA.getX(),pointB.getX(),pointA.getY(),pointB.getY());

        LatLng point4=coorPosiCalc(point3.longitude,point3.latitude,distance12,angle12+Math.PI);
        //dian pointD=gaussProjCal(point4.latitude,point4.longitude);
        double distance14=coorNageCalcDistance(point1,point4);
        //double angle14=jisuanfangwei(pointA.getX(),pointD.getX(),pointA.getY(),pointD.getY());


        if(distance12==0||distance14==0) return null;
        if (distance12>=distance14) {
            shu14List = calcOneLinePlanPointList(point1, point4, dianJianGe);
            shu23List = calcOneLinePlanPointList(point2, point3, dianJianGe);
        }
        else {
            shu14List = calcOneLinePlanPointList(point1, point2, dianJianGe);
            shu23List = calcOneLinePlanPointList(point4, point3, dianJianGe);
        }
        if (shu14List.size()==0||shu23List.size()==0) return null;
        for (int i=0;i<shu14List.size();i++) {//是不是应该有等于？
            tempList=null;

            tempList=calcOneLinePlanPointList(shu14List.get(i),shu23List.get(i),dianJianGe);
            //隔条航带逆序
            if (i%2==0){
                for (LatLng item:tempList
                        ) {
                    pointList.add(item);
                }
            }
            else {
                //tempList=calcOneLinePlanPointList(shu24List.get(i),shu13List.get(i),dianJianGe);
                for (int j= tempList.size()-1;j>=0;j--){
                    pointList.add(tempList.get(j));
                }
            }

        }
        return pointList;
    }
    private ArrayList<LatLng> calcPlanPointList1(LatLng point1,LatLng point2, LatLng point3,LatLng point4,double dianJianGe,double pangXiangJianGe){
        ArrayList<LatLng> shu13List=calcOneLinePlanPointList(point1,point3,dianJianGe);
        ArrayList<LatLng> shu24List=calcOneLinePlanPointList(point2,point4,dianJianGe);
        ArrayList<LatLng> tempList=new ArrayList<>();
        ArrayList<LatLng> pointList=new ArrayList<>();

        double distance13=coorNageCalcDistance(point1,point3);
        double distance12=coorNageCalcDistance(point1,point2);


        if (distance12>=distance13) {
            if (pangXiangJianGe!=0){
                shu13List = calcOneLinePlanPointList(point1, point3, pangXiangJianGe);
                shu24List = calcOneLinePlanPointList(point2, point4, pangXiangJianGe);
            }
            else {
                shu13List = calcOneLinePlanPointList(point1, point3, dianJianGe);
                shu24List = calcOneLinePlanPointList(point2, point4, dianJianGe);
            }
        } else {
            if (pangXiangJianGe != 0) {
                shu13List = calcOneLinePlanPointList(point1, point2, pangXiangJianGe);
                shu24List = calcOneLinePlanPointList(point3, point4, pangXiangJianGe);
            }
            else {
                shu13List = calcOneLinePlanPointList(point1, point2, dianJianGe);
                shu24List = calcOneLinePlanPointList(point3, point4, dianJianGe);
            }
        }


        for (int i=0;i<shu13List.size();i++) {//是不是应该有等于？
            tempList=null;

            tempList=calcOneLinePlanPointList(shu13List.get(i),shu24List.get(i),dianJianGe);
            //隔条航带逆序
            if (i%2==0){
                for (LatLng item:tempList
                        ) {
                    pointList.add(item);
                }
            }
            else {
                //tempList=calcOneLinePlanPointList(shu24List.get(i),shu13List.get(i),dianJianGe);
                for (int j=tempList.size()-1;j>=0;j--){
                    pointList.add(tempList.get(j));
                }
            }

        }

        return pointList;

    }
    public ArrayList<LatLng> calcOneLinePlanPointList(LatLng point1,LatLng point2,double dianJianGe) {
        double distance = coorNageCalcDistance(point1, point2);
        double angle = coorNageCalcAngle(point1, point2);
        if (distance<dianJianGe) return null;
        double count = distance / dianJianGe;

        ArrayList<LatLng> resultlist=new ArrayList<>();
        LatLng tempPoint;
        resultlist.add(panduandms(point1));
        tempPoint=point1;
        for (double i=1;i<=count;i=i+1){
            tempPoint=coorPosiCalc(tempPoint.longitude,tempPoint.latitude,dianJianGe,angle);
            resultlist.add(panduandms(tempPoint));
        }
        //判断距离除以间隔距离是否整除，如果不整除则把point2加到list里
        if (distance%dianJianGe!=0){
            resultlist.add(point2);
        }
        return resultlist;
        //tempPoint=coorPosiCalc(point1.latitude,point1.longitude,dianJianGe,angle);
    }

    /*
    *point点位分布
    *1---2
    * |   |
    * 4---3
    * */
    public ArrayList<LatLng> calcNearestPlanPointList(LatLng point1,LatLng point2,LatLng point3,double dianJianGe, LatLng startPoint){
        ArrayList<LatLng> sortList=calcNearest(point1,point2,point3,coorPosiCalc(point3.longitude,point3.latitude,coorNageCalcDistance(point1,point2),coorNageCalcAngle(point2,point1)),startPoint);
        ArrayList<LatLng> resulteList=calcPlanPointList1(sortList.get(0),sortList.get(2),sortList.get(1),sortList.get(3),dianJianGe,0);

        return resulteList;
        //DJILocationCoordinate2D point4=coorPosiCalc()
    }
    public ArrayList<LatLng> calcNearestPlanPointList(LatLng point1,LatLng point2,LatLng point3,double dianJianGe,
                                                      LatLng startPoint,
                                                      double pangxiangjiange
    ){
        LatLng point4=calc4thPoint(point1,point2,point3);
        ArrayList<LatLng> sortList=calcNearest(point1,point2,point3,point4,startPoint);
        ArrayList<LatLng> resulteList=calcPlanPointList1(sortList.get(0),sortList.get(2),sortList.get(1),sortList.get(3),dianJianGe,pangxiangjiange);

        return resulteList;
    }

    public LatLng calc4thPoint(LatLng point1,LatLng point2,LatLng point3){
        return coorPosiCalc(point3.longitude, point3.latitude,coorNageCalcDistance(point1,point2),coorNageCalcAngle(point2,point1));
    }

    private ArrayList<LatLng> calcNearest(LatLng point1,LatLng point2,LatLng point3,LatLng point4,LatLng startPoint){
        ArrayList<LatLng> resulteList=new ArrayList<LatLng>();
        resulteList.add(point1);
        resulteList.add(point2);
        resulteList.add(point3);
        resulteList.add(point4);
        if(startPoint.longitude==0&&startPoint.latitude==0) startPoint=point1;
        for (int i=0;i<4;i++){
            for (int j=i;j<4;j++){
                if (coorNageCalcDistance(startPoint,resulteList.get(i))  >coorNageCalcDistance(startPoint,resulteList.get(j)) ){
                    LatLng temp= resulteList.get(i);
                    resulteList.set(i,resulteList.get(j));
                    resulteList.set(j,temp);
                }
            }
        }
        return resulteList;
    }


}