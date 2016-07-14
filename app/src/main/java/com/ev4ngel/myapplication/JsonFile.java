package com.ev4ngel.myapplication;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.*;
class JsonFile{
	private FileOutputStream mFos;
	private String mContent;
	private String mFilename;
	public JsonFile()
	{
	}
	public JsonFile(String filename)
	{
		mFilename=filename;
	}
	public void open()
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
                mContent="";
            }
        }
        try {
                mFos = new FileOutputStream(mFilename);
            }catch(IOException e)
            {
                
			}
	}
	public void save()
	{
		try {
			mFos.write(mContent.getBytes());
            mFos.flush();
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

		}
	}
}