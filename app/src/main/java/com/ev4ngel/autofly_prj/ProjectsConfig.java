package com.ev4ngel.autofly_prj;

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
            setRecent_project(Project.prj_default_name);
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
    public ProjectConfig open_prj(String name) {
        if (!name.equals(project_names.get(project_names.size() - 1))){
            delect(name);
            return add_prj(name);
        }
        return ProjectConfig.load(name);
    }
    public ProjectConfig add_prj(String name)//No same name should be add
    {
        try {
            jObj.put(item_RP, name);
        }catch (JSONException je)
        {
            Log.w("e","Set rp fail");
        }
        JSONArray ja=null;
        try {
            ja = jObj.getJSONArray(item_PRJS);
        }catch (JSONException je)
        {
            Log.w("e","getJsonArray fail");
        }
        if (ja == null) {
            ja = new JSONArray();
            try {
                jObj.put(item_PRJS, ja);
            }catch (Exception e)
            {
                Log.w("e","xxxxgetJsonArray fail");
            }
        }
        try {
            ja.put(name);
        }catch (Exception e)
        {
            Log.w("e","xxxxgetJsonArray fail");
        }
        project_names.add(name);
        return ProjectConfig.load(name);
    }
    public boolean delect(String pname)
    {
        try {
            int index=project_names.indexOf(pname);
            jObj.getJSONArray(item_PRJS).remove(index);
            project_names.remove(index);
        }catch (Exception je)
        {
            return  false;
        }
        return true;
    }

    public void setRecent_project(String rp)
    {
        recent_project=rp;
    }
}
