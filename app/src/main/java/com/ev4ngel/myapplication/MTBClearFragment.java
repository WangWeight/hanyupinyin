package com.ev4ngel.myapplication;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Administrator on 2016/7/22.
 */
public class MTBClearFragment extends Fragment {
    Button clr_map;
    Button clr_one;
    Button clr_all;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v=(View)inflater.inflate(R.layout.frag_mtb_clear,container,false);
        clr_map=(Button)v.findViewById(R.id.clear_map_bt);
        clr_one=(Button)v.findViewById(R.id.clear_one_bt);
        clr_all=(Button)v.findViewById(R.id.clear_all_bt);
        if(getParentFragment()!=null)
        {
            clr_map.setOnClickListener((View.OnClickListener)getParentFragment());
            clr_one.setOnClickListener((View.OnClickListener)getParentFragment());
            clr_all.setOnClickListener((View.OnClickListener)getParentFragment());
        }
        return v;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getParentFragment()!=null)
        {
            //v.findViewById(R.id.clear_map_bt).setOnClickListener((View.OnClickListener)getParentFragment());
            Log.i("e","oc");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getParentFragment()!=null)
        {
            //v.findViewById(R.id.clear_map_bt).setOnClickListener((View.OnClickListener)getParentFragment());
            Log.i("e","os");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getParentFragment()!=null)
        {
            //v.findViewById(R.id.clear_map_bt).setOnClickListener((View.OnClickListener)getParentFragment());
            Log.i("e","iac");
        }
    }
}
