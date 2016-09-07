package com.ev4ngel.autofly_prj;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ev4ngel.myapplication.AutoflyApplication;
import com.ev4ngel.myapplication.R;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/7/21.
 */
public class ProjectFragment extends Fragment implements
        FloatingActionButton.OnClickListener,
        DialogInterface.OnClickListener,
        ListView.OnItemClickListener,
        ListView.OnItemLongClickListener
{
    String E="evan";
    FloatingActionButton Prj_add;
    EditText prjEt;//init in onClick
    TextView prjTv;
    Project mPrj;
            ListView mLv;
            ArrayAdapter _aa;
            AlertDialog new_prj_ad;
            AlertDialog del_prj_ad;
            AlertDialog open_prj_ad;
            ArrayList<String> prj_list;
            Project.OnLoadItemListener mListener;
            int mItem_index=0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.project_frag_layout,container,false);
        Prj_add=(FloatingActionButton)v.findViewById(R.id.prj_add_fab);
        Prj_add.setOnClickListener(this);
        mLv=(ListView)v.findViewById(R.id.prj_list_prj);
        mLv.setOnItemClickListener(this);
        mLv.setOnItemLongClickListener(this);
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
        return v;

    }

    @Override
    public void onStart() {
        super.onStart();
        //mPrj.load_recent_project();
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
        //mPrj.new_project("wangwei"+System.currentTimeMillis());
        prjEt.setText("");
        new_prj_ad.show();
    }
    public void set_project(Project prj){
        mPrj=prj;
    }
    public void set_prj_list(ArrayList<String> aa){
        prj_list=aa;
        _aa=new ArrayAdapter<String>(getActivity(),R.layout.prj_ilistview_layout,prj_list);
        mLv.setAdapter(_aa);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(dialog!=null)
        {
            if(dialog.toString().equals(open_prj_ad.toString()))//Open close
            {
                if(which==DialogInterface.BUTTON_POSITIVE)
                {
                    String s=prj_list.get(mItem_index);
                    mListener.onLoadProject(s);
                    _aa.notifyDataSetChanged();
                    Log.i(E,s);
                }
            }else {
                if(dialog.toString().equals(new_prj_ad.toString()))//new build
                {
                    if(which==DialogInterface.BUTTON_POSITIVE)
                    {
                        String text=prjEt.getText().toString();
                        if(Project.isExistProject(text))
                        {

                        }else {
                            mListener.onNewProject(text);
                            _aa.notifyDataSetChanged();
                            Log.i(E, "Prj load");
                        }
                    }
                }
                else
                {
                    if(dialog.toString().equals(del_prj_ad.toString())) {
                        Log.i(E, "Prj del show");
                        if (which == DialogInterface.BUTTON_POSITIVE)
                        {
                            int rlt =mListener.onDeleteProject(prj_list.get(mItem_index));
                            switch (rlt){
                                case 0: {
                                    _aa.notifyDataSetChanged();
                                    Toast.makeText(getActivity().getApplicationContext(),"删除项目成功", Toast.LENGTH_LONG).show();
                                }break;
                                case 1:{
                                    Toast.makeText(getActivity().getApplicationContext(),"删除项目失败", Toast.LENGTH_LONG).show();
                                }break;
                                case 2:{
                                        Toast.makeText(getActivity().getApplicationContext(), "该项目不允许删除", Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    }
                }
            }
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mItem_index=position;
        del_prj_ad.show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mItem_index=position;
        open_prj_ad.show();
    }
    public void setOnLoadItemListener(Project.OnLoadItemListener l){
        mListener=l;
    }

}
