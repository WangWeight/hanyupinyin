package com.ev4ngel.myapplication;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class T {
	public static String Tag="com.ev4ngel.myapplication.T";
	public static void i(String msg)
	{
		Toast.makeText(AutoflyApplication.getContext(), msg,Toast.LENGTH_SHORT).show();
	}
	public static void i(List msg)
	{
		T.i(join(msg));
	}
	public static void l(String msg){
		Log.i(Tag,msg);
	}
	public static void l(List msg){
		l(join(msg));
	}
	public static String join(List msg){
		return TextUtils.join(",", msg);
	}

}
