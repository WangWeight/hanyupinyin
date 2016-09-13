package com.ev4ngel.myapplication;

import java.util.concurrent.CountDownLatch;

import dji.common.util.DJICommonCallbacks;
import dji.sdk.base.DJIBaseComponent;
import dji.common.error.DJIError;

/**
 * Created by Administrator on 2016/7/7.
 */
public class CameraCompletionCallback implements DJICommonCallbacks.DJICompletionCallback {
    private CountDownLatch mCdl;
    public CameraCompletionCallback(CountDownLatch cdl){
        mCdl=cdl;
    }
    @Override
    public void onResult(DJIError djiError) {
        mCdl.countDown();
    }
}
