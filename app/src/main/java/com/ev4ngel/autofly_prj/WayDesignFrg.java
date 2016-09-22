package com.ev4ngel.autofly_prj;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ev4ngel.myapplication.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/18.
 */
public class WayDesignFrg extends Fragment implements View.OnClickListener{
    public enum Status{
        isDesignning,
        isChoosing,
        isClearing,
        isSaving,
        isHidding
    }
    public interface OnSelectListener{
        void onMenuItemSelect(Status status);
    }

    ArrayList<TextView> mTextViews;
    OnSelectListener mOnSelectListener=null;
    public Status mStatus;
    private Handler mHandler;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.way_design_layout,container,false);
        mTextViews=new ArrayList<>();
        mTextViews.add((TextView)v.findViewById(R.id.way_design_tv));
        mTextViews.add((TextView)v.findViewById(R.id.way_choose_tv));
        mTextViews.add((TextView)v.findViewById(R.id.way_clear_tv));
        mTextViews.add((TextView)v.findViewById(R.id.way_save_tv));
        for(TextView tv:mTextViews){
            tv.setOnClickListener(this);
        }
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(mTextViews!=null&&msg.what>1) {
                    try{
                        Thread.sleep(500);
                    }catch (Exception e){

                    }
                    mTextViews.get(msg.what).setBackgroundResource(R.drawable.way_design_item_bg);
                }
            }
        };
        return v;
    }
    private void unselect_all(){
        for(TextView tv:mTextViews){
            tv.setBackgroundResource(R.drawable.way_design_item_bg);
        }
    }
    @Override
    public void onClick(View v) {
        unselect_all();
        v.setBackgroundResource(R.drawable.way_design_item_bg_clicked);
        int index=0;
        if(v.getId()==mTextViews.get(0).getId()){
            mStatus= Status.isDesignning;
        }else if(v.getId()==mTextViews.get(1).getId()){
            mStatus= Status.isChoosing;
        }else if(v.getId()==mTextViews.get(2).getId()){
            mStatus= Status.isClearing;
            index=2;
        }else if(v.getId()==mTextViews.get(3).getId()){
            mStatus= Status.isSaving;
            index=3;
        }
        if(mOnSelectListener!=null)
            mOnSelectListener.onMenuItemSelect(mStatus);
        Message m = new Message();
        m.what=index;
        mHandler.sendMessage(m);
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        mOnSelectListener = onSelectListener;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            mOnSelectListener.onMenuItemSelect(Status.isHidding);
        }
    }
}
