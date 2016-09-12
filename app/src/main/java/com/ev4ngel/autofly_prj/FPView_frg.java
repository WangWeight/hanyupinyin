package com.ev4ngel.autofly_prj;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.ev4ngel.myapplication.R;

/**
 * Created by jason on 2016/9/9.
 */
public class FPView_frg extends Fragment {
    TextureView fpv_view=null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.frag_fpv_layout,container,false);
        return  v;
    }
    public void onClick(View v) {
//        switch (v.getId()) {
//        }

    }
}
