package com.ev4ngel.myapplication;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Timer;

/**
 * Created by Administrator on 2016/7/21.
 */
public class ProjectFragment extends Fragment implements
        FloatingActionButton.OnClickListener,
        DialogInterface.OnClickListener,
        ListView.OnItemClickListener,
        ListView.OnItemLongClickListener{
    FloatingActionButton Prj_add;
    EditText prjEt;//init in onClick
    TextView prjTv;
    Project mPrj;
    ListView mLv;
    AlertDialog new_prj_ad;
    AlertDialog del_prj_ad;
    AlertDialog open_prj_ad;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.project_frag_layout,container,false);
        Prj_add=(FloatingActionButton)v.findViewById(R.id.prj_add_fab);
        Prj_add.setOnClickListener(this);
        mLv=(ListView)v.findViewById(R.id.prj_list_prj);
        mPrj=new Project();
        //mPrj.load_recent_project();
        ArrayAdapter<String> aa=new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_list_item_1,mPrj.getProjects());
        mLv.setAdapter(aa);
        mLv.setOnItemClickListener(this);
        View vv=LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.project_dialog_prjname,null);
        prjEt=(EditText)vv.findViewById(R.id.new_prj_name_et);
        prjTv=(TextView)vv.findViewById(R.id.new_prj_tip_tv);
        new_prj_ad=new AlertDialog.Builder(getActivity())
                .setTitle("新建项目")
                .setView(vv)
                .setPositiveButton("新建", this)
                .setNegativeButton("取消", this).create();
        del_prj_ad=new AlertDialog.Builder(getActivity())
                .setTitle("删除项目")
                .setMessage("确定删除此项目？")
                .setPositiveButton("确定", this)
                .setNegativeButton("取消",this).create();
        open_prj_ad=new AlertDialog.Builder(getActivity())
                .setTitle("打开项目")
                .setMessage("打开此项目？")
                .setPositiveButton("确定", this)
                .setNegativeButton("取消",this).create();
        ((MainActivity) getActivity()).onLoadProject(mPrj);
        return v;

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onClick(View v) {
                new_prj_ad.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(dialog.toString().equals(open_prj_ad.toString()))//Open close
        {

        }else {
            if(dialog.toString().equals(new_prj_ad.toString()))//new build
            {

            }
            else
            {

            }
        }
/*
        switch(dialog.toString())
        {
            case DialogInterface.BUTTON_NEGATIVE:{
                Log.i("e","xxx"+dialog.toString());
            }break;
            case DialogInterface.BUTTON_POSITIVE:{
                String prjName=prjEt.getText().toString();
                if(Project.isExistProject(prjName))
                {
                    prjTv.setVisibility(View.VISIBLE);
                    //prjEt.setBackgroundColor(Color.argb(100,255,0,0));

                }else
                {
                    mPrj.load_project(prjName);
                    ((MainActivity)getActivity()).onLoadProject(mPrj);
                }
                Log.i("e",prjEt.getText().toString());
            }break;
        }*/


    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        del_prj_ad.show();
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        open_prj_ad.show();
    }
}
