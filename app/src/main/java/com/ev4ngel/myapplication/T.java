package com.ev4ngel.myapplication;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.List;

public class T {
	public static void i(String msg)
	{
		Toast.makeText(AutoflyApplication.getContext(), msg,Toast.LENGTH_SHORT).show();
	}
	public static void i(List msg)
	{
		T.i(join(msg));
	}
	public static String join(List msg){
		return TextUtils.join(",", msg);
	}

}
