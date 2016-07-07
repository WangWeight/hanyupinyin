package com.ev4ngel.myapplication;

import android.content.Context;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
		mFos.write(mContent.getBytes());
	}
	public void close()
	{
		mFos.close();
	}
}