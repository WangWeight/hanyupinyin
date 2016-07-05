package com.ev4ngel.myapplication;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/7/4.
 */
public class Tools {
    public static void showToast(Context c,String msg)
    {
        Toast.makeText(c,msg,Toast.LENGTH_LONG).show();
    }
}
