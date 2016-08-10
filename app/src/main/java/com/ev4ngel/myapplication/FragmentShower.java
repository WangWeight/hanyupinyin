package com.ev4ngel.myapplication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/28.
 */
public class FragmentShower {
    FragmentManager mFm;
    ArrayList<Fragment> fragments;
    public FragmentShower(FragmentManager fm)
    {
        mFm=fm;
        fragments=new ArrayList<>();
    }
    public FragmentShower add(int container_id,Fragment f,String tag)
    {
        mFm.beginTransaction().add(container_id, f, tag).commit();
        fragments.add(f);
        return this;
    }
    public FragmentShower add(int container_id,Fragment f,String tag,boolean alwaysShow)
    {
        if(alwaysShow){
            mFm.beginTransaction().add(container_id, f, tag).commit();
        }
        return this;
    }
    public FragmentShower show(String[] tags)
    {
        FragmentTransaction ft=mFm.beginTransaction();
        for(Fragment ff:fragments)
        {
            for(String tag:tags) {
                if (ff.getTag().equals(tag)) {
                    ft.show(ff);
                } else {
                    ft.hide(ff);
                }
            }
        }
        ft.commit();
        return this;
    }
    public FragmentShower show(Fragment f)
    {
        FragmentTransaction ft=mFm.beginTransaction();
        for(Fragment ff:fragments)
        {
            if(ff.getTag().equals(f.getTag()))
            {
                ft.show(f);
            }else{
                ft.hide(ff);
            }
        }
        ft.commit();
        return this;
    }
    public FragmentShower hide(Fragment f)
    {
        FragmentTransaction ft=mFm.beginTransaction();
        for(Fragment ff:fragments)
        {
            if(ff.getTag().equals(f.getTag()))
            {
                ft.hide(f);
            }else{
                ft.show(ff);
            }
        }
        ft.commit();
        return this;
    }
}
