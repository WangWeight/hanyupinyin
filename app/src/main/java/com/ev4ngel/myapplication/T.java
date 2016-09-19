package com.ev4ngel.myapplication;

import android.content.Context;
import android.widget.Toast;

public class T {
	public static void i(String msg)
	{
		Toast.makeText(AutoflyApplication.getContext(), msg,Toast.LENGTH_SHORT).show();
	}

}
