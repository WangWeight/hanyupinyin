package com.ev4ngel.myapplication;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.*;
class JsonFile{
	protected FileOutputStream mFos;
	protected String mContent;
	protected String mFilename;
	public JsonFile()
	{
	}
	public JsonFile(String filename)
	{
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
			close();
		}catch(IOException ioe)
		{
		}
	}
	public void close()
	{

		try{
			mFos.close();
		}catch(IOException e)
		{
			Log.i("e","JSON close fail:"+e.getMessage());
		}
	}
}