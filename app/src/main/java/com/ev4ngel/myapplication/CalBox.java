package com.ev4ngel.myapplication;
import java.util.ArrayList;
import dji.sdk.FlightController.DJIFlightControllerDataType;


/**
 * Created by jason on 2016/7/5.
 */

public class CalBox {

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
    public DJIFlightControllerDataType.DJILocationCoordinate2D gaussProjInvCal(double Y,double X)
    {
        DJIFlightControllerDataType.DJILocationCoordinate2D point=new DJIFlightControllerDataType.DJILocationCoordinate2D();
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
        point.setLatitude(latitude1/iPI);
        point.setLongitude(longitude1/iPI);
        //longitude = longitude1 / iPI;
        //latitude = latitude1 / iPI;
        return  point;
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
    public DJIFlightControllerDataType.DJILocationCoordinate2D coorPosiCalc(double longitude,double latitude,double distance,double angle){
        DJIFlightControllerDataType.DJILocationCoordinate2D resultePoint=new DJIFlightControllerDataType.DJILocationCoordinate2D();
        dian tempPoint=new dian();
        dian point=new dian();

        point=gaussProjCal(longitude,latitude);
        tempPoint=zuoBiaoZhengSuan(point.getX(),point.getY(),distance,angle);
        resultePoint=gaussProjInvCal(tempPoint.getX(),tempPoint.getY());
        return resultePoint;
    }
    //单位是度
    public double coorNageCalcDistance(DJIFlightControllerDataType.DJILocationCoordinate2D point1,DJIFlightControllerDataType.DJILocationCoordinate2D point2){
        dian pointA=gaussProjCal(point1.getLongitude(),point1.getLatitude());
        dian pointB=gaussProjCal(point2.getLongitude(),point2.getLatitude());

        return zuoBiaoFanSuan(pointA,pointB);
    }
    public double coorNageCalcAngle(DJIFlightControllerDataType.DJILocationCoordinate2D point1,DJIFlightControllerDataType.DJILocationCoordinate2D point2){
        dian pointA=gaussProjCal(point1.getLongitude(),point1.getLatitude());
        dian pointB=gaussProjCal(point2.getLongitude(),point2.getLatitude());

        return jisuanfangwei(pointA.getX(),pointB.getX(),pointA.getY(),pointB.getY());
    }

    public static DJIFlightControllerDataType.DJILocationCoordinate2D panduandms(DJIFlightControllerDataType.DJILocationCoordinate2D point)
    {
        DJIFlightControllerDataType.DJILocationCoordinate2D a = new DJIFlightControllerDataType.DJILocationCoordinate2D();
        try
        {
            a.setLongitude(point.getLongitude());
            a.setLatitude(point.getLatitude());

            if ((a.getLongitude() > 100 && a.getLongitude() < 180 && a.getLatitude() > 0 && a.getLatitude() < 100))
            {

            }
            else if ((a.getLongitude() > 0 && a.getLongitude() < 100 && a.getLatitude() > 100 && a.getLatitude() < 180))
            {
                double temp = a.getLongitude();
                a.setLongitude(a.getLongitude());
                a.setLatitude(temp);
            }
        }
        catch (Exception e)
        { }
        return a;

    }
    //1-----2
    //|     |
    //3-----4
    //点位示意
    public ArrayList<DJIFlightControllerDataType.DJILocationCoordinate2D> calcPlanPointList(DJIFlightControllerDataType.DJILocationCoordinate2D point1,DJIFlightControllerDataType.DJILocationCoordinate2D point2,DJIFlightControllerDataType.DJILocationCoordinate2D point3,double dianJianGe){
        dian pointA=gaussProjCal(point1.getLongitude(),point1.getLatitude());
        dian pointB=gaussProjCal(point2.getLongitude(),point2.getLatitude());
        dian pointC=gaussProjCal(point3.getLongitude(),point3.getLatitude());
        ArrayList<DJIFlightControllerDataType.DJILocationCoordinate2D> pointList=new ArrayList<>();
        ArrayList<DJIFlightControllerDataType.DJILocationCoordinate2D> tempList=new ArrayList<>();
        ArrayList<DJIFlightControllerDataType.DJILocationCoordinate2D> shu13List=new ArrayList<>();
        ArrayList<DJIFlightControllerDataType.DJILocationCoordinate2D> shu24List=new ArrayList<>();

        double distance12=coorNageCalcDistance(point1,point2);
        double distance13=coorNageCalcDistance(point1,point3);

        double angle12=jisuanfangwei(pointA.getX(),pointB.getX(),pointA.getY(),pointB.getY());
        double angle13=jisuanfangwei(pointA.getX(),pointC.getX(),pointA.getY(),pointC.getY());

        if(distance12==0||distance13==0) return null;
        if (distance12>=distance13) {
            shu13List = calcOneLinePlanPointList(point1, point3, dianJianGe);
            shu24List = calcOneLinePlanPointList(point2, coorPosiCalc(point2.getLongitude(), point2.getLatitude(), coorNageCalcDistance(point1,point3), angle13), dianJianGe);
        }
        else {
            shu13List = calcOneLinePlanPointList(point1, point2, dianJianGe);
            shu24List = calcOneLinePlanPointList(point3, coorPosiCalc(point3.getLongitude(), point3.getLatitude(), coorNageCalcDistance(point1,point2), angle13), dianJianGe);
        }
        for (int i=0;i<shu13List.size();i++) {//是不是应该有等于？
            tempList=new ArrayList<>();

            tempList=calcOneLinePlanPointList(shu13List.get(i),shu24List.get(i),dianJianGe);
            //隔条航带逆序
            if (i%2==0){
                for (DJIFlightControllerDataType.DJILocationCoordinate2D item:tempList
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

    public ArrayList<DJIFlightControllerDataType.DJILocationCoordinate2D> calcOneLinePlanPointList(DJIFlightControllerDataType.DJILocationCoordinate2D point1,DJIFlightControllerDataType.DJILocationCoordinate2D point2,double dianJianGe) {
        double distance = coorNageCalcDistance(point1, point2);
        double angle=coorNageCalcAngle(point1,point2);
        double count = distance / dianJianGe;

        ArrayList<DJIFlightControllerDataType.DJILocationCoordinate2D> resultlist=new ArrayList<>();
        DJIFlightControllerDataType.DJILocationCoordinate2D tempPoint;
        resultlist.add(panduandms(point1));
        tempPoint=point1;
        for (double i=1;i<=count;i=i+1){
            tempPoint=coorPosiCalc(tempPoint.getLongitude(),tempPoint.getLatitude(),dianJianGe,angle);
            resultlist.add(panduandms(tempPoint));
        }
        //判断距离除以间隔距离是否整除，如果不整除则把point2加到list里
        if (distance%dianJianGe!=0){
            resultlist.add(point2);
        }
        return resultlist;
        //tempPoint=coorPosiCalc(point1.getLongitude(),point1.getLatitude(),dianJianGe,angle);
    }
}
