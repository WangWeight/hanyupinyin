package com.ev4ngel.autofly_prj;

/**
 * Created by Administrator on 2016/7/22.
 *###in MainActivity
 * p.setOnLoadProject(this)
 * public void onLoadProject()
 * {
 *     #change title,environ
 * }
 * ###in Project
 * setOnLoadProject(listener)
 * {
 *     listener.onLoadProject();
 * }
 */
public interface OnLoadProjectListener {
    public void onLoadProject();
}
