package com.ev4ngel.myapplication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/21.
 * .recent_prj="prj_name"
 * in config:{"recent_project":"name"}
 */
public class ProjectsConfig extends JsonFile{
    public String recent_project;
    public ArrayList<String> project_names;
    public JSONObject jObj;
    public String item_RP="recent_project";
    public String item_PRJS="projects";
    private ProjectsConfig mPsc;
    private ProjectsConfig(String path_file)
    {
        super(path_file);
        mFilename=path_file;
        open(true);
        project_names=new ArrayList<>();
        if(!mContent.isEmpty())
        {
            try {
                jObj = new JSONObject(new JSONTokener(mContent));
                recent_project=jObj.getString(item_RP);
                JSONArray ja=jObj.getJSONArray(item_PRJS);
                for(int i=0;i<ja.length();i++)
                {
                    project_names.add(ja.getString(i));
                }
            }catch (JSONException je)
            {
                Log.i("e","error when get json obj from mContent"+mContent+":"+mContent.length());
            }
        }else {
            jObj = new JSONObject();
            setRecent_project("");
        }
    }
    public static ProjectsConfig load(String path_file)
    {
        return new ProjectsConfig(path_file);
    }
    public void write()
    {
        mContent=jObj.toString();
        this.save();
    }
    public boolean delect(String pname)
    {
        try {
            int index=project_names.indexOf(pname);
            jObj.getJSONArray(item_PRJS).remove(index);
            project_names.remove(index);
        }catch (JSONException je)
        {
            return  false;
        }
        return true;
    }

    public void setRecent_project(String rp)
    {
        recent_project=rp;
        boolean rlt=delect(rp);
        try {
            jObj.put(item_RP, rp);
        }catch (JSONException je)
        {
            Log.w("e","Set rp fail");
        }
        try {
            JSONArray ja = jObj.getJSONArray(item_PRJS);
            if (ja == null) {
                ja = new JSONArray();
                jObj.put(item_PRJS, ja);
            }
            ja.put(0, rp);
        }catch (JSONException je)
        {

        }
        project_names.add(0, rp);
    }
}
