package com.ev4ngel.myapplication;

import android.content.Context;
import android.widget.Toast;

public class T {
	private Context cc;
	public T(Context c)
	{
		cc=c;
	}
	public void i(String msg)
	{
		Toast.makeText(cc, msg,Toast.LENGTH_SHORT).show();
	}

}
