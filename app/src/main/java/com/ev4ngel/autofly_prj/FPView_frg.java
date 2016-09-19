package com.ev4ngel.autofly_prj;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.ev4ngel.myapplication.R;
import com.google.android.gms.vision.Frame;

/**
 * Created by jason on 2016/9/9.
 */
public class FPView_frg extends Fragment implements View.OnClickListener{
    ImageButton ib=null;
    View parent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.frag_fpv_layout,container,false);
        ib=(ImageButton)v.findViewById(R.id.zoom_it_bt);
        ib.setOnClickListener(this);
        return  v;
    }
    public void onClick(View v) {
        if(v.getId()==ib.getId()){
            //ib.setVisibility(View.GONE);
            ((FrameLayout)getActivity().findViewById(R.id.fpv_view)).setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT ));
        }
    }
    public void lay_down(){
        ib.setVisibility(View.VISIBLE);
        ((FrameLayout)getActivity().findViewById(R.id.fpv_view)).setLayoutParams(new FrameLayout.LayoutParams(200,100));
    }
}
