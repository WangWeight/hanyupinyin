package com.ev4ngel.myapplication;

import dji.sdk.base.DJIBaseComponent;
import dji.sdk.base.DJIError;

/**
 * Created by Administrator on 2016/8/29.
 */
public class CompletionCallback implements DJIBaseComponent.DJICompletionCallback {
    int count=0;
    int mCompletionStatus;
    int max_count;

    public CompletionCallback(int max_count){
        this.max_count=max_count;
    }
    @Override
    public void onResult(DJIError djiError) {
        count++;
        if (djiError == null) {

        }
        if(count==max_count){

        }
    }
}
