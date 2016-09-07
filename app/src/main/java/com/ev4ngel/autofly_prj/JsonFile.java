package com.ev4ngel.autofly_prj;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
class JsonFile<T>{
	protected FileOutputStream mFos;
	protected String mContent;
	protected String mFilename;
	protected String mPrjname;
	protected JSONObject mJSONObject;
	public JsonFile()
	{
	}
	public JsonFile(String filename)
	{
		mPrjname=filename;
		mFilename=filename;
		mContent="";
	}
	public void open(boolean needRead)
	{
		if(needRead)
		{
			File f=new File(mFilename);
			if(f.exists()) {
				try {
					FileInputStream fis=new FileInputStream(f);
					byte[] b=new byte[fis.available()];
					fis.read(b);
					fis.close();
					mContent=new String(b);
				}
				catch(IOException e)
				{
					Log.i("e","Open a file");
					mContent="";
				}
			}
		}
        try {
			mFos = new FileOutputStream(mFilename);
			if(mFos==null)
				Log.i("evan","mFos is null");
			else
				Log.i("evan","mFos is not null");
		}catch(IOException e)
		{
			Log.i("evan","Except e "+e.getMessage());
		}
	}
	public void save()
	{
		try {
			open(false);
			mFos.write(mContent.getBytes());
            mFos.flush();
		}catch(IOException ioe)
		{
		}
	}
	public void close()
	{
		try{
			if(mFos!=null)
				mFos.close();
		}catch(Exception e)
		{
			Log.i("e","JSON close fail:"+e.getMessage());
		}
	}
	public JSONArray parse_content_to_array()
	{
		JSONArray ja=new JSONArray();
		if (mContent.length()!=0){//Read the content if the file is not empty
			try {
				ja = new JSONArray(new JSONTokener(mContent));
			} catch(JSONException e)
			{
			}
		}
		return ja;
	}
	public boolean parse_content_to_object(){
		if (mContent.length()!=0){//Read the content if the file is not empty
			try {
				mJSONObject = new JSONObject(new JSONTokener(mContent));
				return true;
			} catch(JSONException e)
			{
				mJSONObject=new JSONObject();
			}
		}
		mJSONObject=new JSONObject();
		return false;
	}
	/*
	public ArrayList<T> parse_content_to_array()
	{
		JSONArray ja=new JSONArray();
		ArrayList<T> al=new ArrayList<>();
		if (mContent.length()!=0){//Read the content if the file is not empty
			try {
				ja = new JSONArray(new JSONTokener(mContent));
				for(int i=0;i<ja.length();i++)
				{
					al.add(T.fromJson(ja.get(i)));
				}
			} catch(JSONException e)
			{
			}
		}
		return al;
	}*/
	public JSONArray convert_array_to_json(ArrayList<? extends IJson> al){
		JSONArray ja=new JSONArray();
		for(IJson ij:al)
		{
			ja.put(ij.toJson());
		}
		return ja;
	}

}