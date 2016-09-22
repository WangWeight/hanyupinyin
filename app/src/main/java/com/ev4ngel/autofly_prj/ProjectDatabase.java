package com.ev4ngel.autofly_prj;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/19.
 */
public class ProjectDatabase extends SQLiteOpenHelper {
    public static final String db_name="project.db";
    public static final String full_db_name= Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+db_name;
    String table_prj="project";
    String table_wayline="wayline";
    String table_photoinfo="photoinfo";
    String table_waypoint="waypoint";
    public static final int db_version=1;
    private int current_project_id=-1;
    private ArrayList<String> mProjectList=null;
    private ArrayList<String> mWaylineList=null;
    private ArrayList<WayPoint> mWayPointList=null;
    private int current_wayline_id=-1;
    private ProjectDatabase mInstance;
    private ProjectDatabase(Context c, String prj_path){
        super(c,full_db_name,null,db_version);
        getProjects();
    }
    public static ProjectDatabase getInstance(Context c){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)&&Environment.isExternalStorageEmulated())
        {
            return new ProjectDatabase(c,Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+db_name);
        }else {
            return new ProjectDatabase(c,db_name);
        }
    }
    public ArrayList<String> getProjectList() {
        return mProjectList;
    }

    public ArrayList<String> getWaylineList() {
        return mWaylineList;
    }

    public ArrayList<WayPoint> getWayPointList() {
        return mWayPointList;
    }

    public int getCurrent_project_id() {
        return current_project_id;
    }

    public int getCurrent_wayline_id() {
        return current_wayline_id;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String[] sqls={"CREATE TABLE "+table_prj+"(id integer primary key ,name text,create_time TIMESTAMP DEFAULT  CURRENT_TIMESTAMP,access_time TIMESTAMP DEFAULT  CURRENT_TIMESTAMP);",
                "CREATE TABLE "+table_wayline+"(id integer primary key,name text,prj_id int,create_time TIMESTAMP DEFAULT  CURRENT_TIMESTAMP,access_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP);" ,
                "CREATE TABLE "+table_waypoint+"(id integer primary key,_index int,lat float,lng float,alt float,wayline_id int,status int);" ,
                "CREATE TABLE "+table_photoinfo+"(id integer primary key,name text,lat float,lng float,yaw float,pitch float,wayline_id int,take_time TIMESTAMP DEFAULT  CURRENT_TIMESTAMP);"};
        for(String sql:sqls)
            db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void exportPhotosInfo(String line){
        int id=current_wayline_id;
        if(line!=null)
            id=getIndexByWaylineName(line);
        //getReadableDatabase().query(table_photoinfo,)
    }
    private int getIndexByName(String name,String table){

        String where="name=?";
        String[] params=new String[]{name};
        if(table.equals(table_wayline)) {
            where += " and prj_id=? ";
            params=new String[]{name,""+current_project_id};
        }
        Cursor c=getReadableDatabase().query(table,new String[]{"id"},where,params,null,null,null,"1");
        if(c.moveToFirst()) {
            return c.getInt(c.getColumnIndex("id"));
        }else{
            return -1;
        }
    }
    public int getIndexByProjectName(String name){
        return getIndexByName(name,table_prj);
    }
    public int getIndexByWaylineName(String name){
        return getIndexByName(name,table_wayline);
    }
    public ArrayList<String> getProjects(){
        Cursor c=getReadableDatabase().rawQuery("select name from " + table_prj + " order by access_time desc", null);
        //getReadableDatabase().query("project", new String[]{"id", "name", "create_time"}, null, null, null, null, "access_time");
        mProjectList=new ArrayList<>();
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                mProjectList.add(c.getString(0));
                c.moveToNext();
            }
        }
        return mProjectList;
    }
    public ProjectInstance getDetailProject(String name){
        Cursor c=getReadableDatabase().query(table_prj, new String[]{"id", "strftime('%s',create_time)", "strftime('%s',access_time)"}, "name=?", new String[]{name}, null, null, null);
        if(c.moveToFirst()){
            return new ProjectInstance(name,new Date(c.getLong(1)*1000),new Date(c.getLong(2)*1000),getWaylines(c.getInt(0)));
        }
        return null;
    }
    public ArrayList<String> getWaylines(String prj_name){
        int prj_id=current_project_id;
        if(prj_name!=null){//查询给定项目的wayline
            prj_id=getIndexByProjectName(prj_name);
        }
        return getWaylines(prj_id);
    }
    public ArrayList<String> getWaylines(int id){
        mWaylineList=new ArrayList<>();
        if(id>=0){
            Cursor c=getReadableDatabase().query(table_wayline,new String[]{"name","id"},"prj_id=?",new String[]{""+id},null,null,"create_time desc");
            if (c.moveToFirst()) {
                for (int i = 0; i < c.getCount(); i++) {
                    mWaylineList.add(c.getString(c.getColumnIndex("name")));
                    c.moveToNext();
                }
            }
        }
        return mWaylineList;
    }

    public void openRecentWayline(){
        if(mWaylineList!=null&&mWaylineList.size()>0){
            openWayline(mWaylineList.get(0));
        }
    }
    private int addItem(String name,String table){
        //try {
            StringBuilder sb=new StringBuilder();
            sb.append("INSERT INTO ");
            sb.append(table);
            sb.append("(name");
            if(table.equals(table_wayline))
                sb.append(",prj_id");
            sb.append(") values('");
            sb.append( name+"'" );
            if(table.equals(table_wayline))
                sb.append(","+current_project_id);
            sb.append(")");
            getWritableDatabase().execSQL(sb.toString());
            return getIndexByName(name,table);
        //}catch (SQLException e){
           // return -1;
        //}
    }
    public int addProject(String name){
        int rlt=-1;
        if(!mProjectList.contains(name)) {
            rlt=addItem(name, table_prj);
            mProjectList.add(name);
        }
        return rlt;
    }
    public int openProject(String name){
        //try {
        getWritableDatabase().execSQL("UPDATE project SET access_time=CURRENT_TIMESTAMP where name='"+name+"'");
            current_project_id=getIndexByProjectName(name);
            getWaylines(name);
        openRecentWayline();
            return current_project_id;
        //}catch (SQLException e){
        //    return -1;
        //}

    }
    public void openRecentProject(){
        if(mProjectList.size()>0){
            openProject(mProjectList.get(0));
        }
    }
    public void deleteProject(String name){
        if(mProjectList.contains(name)) {
            getWritableDatabase().delete("project", "name=?", new String[]{name});
            mProjectList.remove(mProjectList.indexOf(name));
        }
    }
    public int addWayline(String name){
        int rlt=-1;
        if(!mWaylineList.contains(name)){
            rlt=addItem(name,table_wayline);
            mWaylineList.add(name);
        }
        return rlt;
    }

    public void deleteWayline(String name){
        if(mWaylineList.contains(name)){
            int wayline_id=getIndexByWaylineName(name);
            getWritableDatabase().delete(table_wayline, "id=?", new String[]{""+wayline_id});
            //getWritableDatabase().delete(table_wayline, "id=?", new String[]{""+wayline_id});
            mWaylineList.remove(mWaylineList.indexOf(name));
        }
    }
    public int openWayline(String name){
        current_wayline_id=getIndexByWaylineName(name);
        getWritableDatabase().execSQL("UPDATE "+table_wayline+" SET access_time=CURRENT_TIMESTAMP where prj_id='"+current_project_id+"'");
        getWaypoints(null);
        return current_wayline_id;
    }
    public int addWayPoint(int index,WayPoint wp){
        return addWayPoint(index,wp.lat,wp.lng,wp.alt,wp.status);
    }
    public int addWayPoint(int index,double lat,double lng,double alt,float status){
        getWritableDatabase().execSQL("INSERT INTO "+table_waypoint+"(_index,lat,lng,alt,status,wayline_id) values("+index+","+lat+","+lng+","+alt+","+status+","+current_wayline_id+")");
        return 0;
    }
    public void deleteWayPoint(int index){
        getWritableDatabase().delete(table_waypoint,"_index=?",new String[]{""+index});
        mWayPointList.set(index,null);
    }
    public void addWaypoints(ArrayList<WayPoint> wps){
        for(int i=0;i<wps.size();i++)
            addWayPoint(i,wps.get(i));
        mWayPointList.addAll(wps);
    }
    public ArrayList<WayPoint> getWaypoints(String wayline){//如果传入wayline为null，则加载已经打开
        int ix=current_wayline_id;
        if(wayline!=null)
            ix=getIndexByWaylineName(wayline);
        mWayPointList=new ArrayList<>();
        if(ix>=0){
            Cursor c=getReadableDatabase().query(table_waypoint, new String[]{ "lat", "lng", "alt", "status"}, "wayline_id=?", new String[]{"" + ix}, null, null, "_index asc");
            if(c.moveToFirst()){
                for(int i=0;i<c.getCount();i++){
                    WayPoint wp=new WayPoint(c.getFloat(0),c.getFloat(1),c.getFloat(2),c.getFloat(3));
                    mWayPointList.add(wp);
                    c.moveToNext();
                }
            }
        }
        return mWayPointList;
    }
    public void addPhotoInfo(String file_name,float lat,float lng,float yaw,float pitch){
        if(current_project_id==-1||current_wayline_id==-1)
            return;
        else{
            getWritableDatabase().execSQL("INSERT INTO "+table_photoinfo+"(name,lat,lng,yaw,pitch,wayline_id) values('"+file_name+"',"+lat+","+lng+","+yaw+","+pitch+","+current_wayline_id+")");
        }
    }

}
