package com.ev4ngel.myapplication;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/7/19.
 */
public class MTBSelectWidthFragment extends Fragment implements SeekBar.OnSeekBarChangeListener{
    private View main;
    private SeekBar sb1;
    private SeekBar sb2;
    private Button sbb;
    private MapFrg mMapFrg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void init_ui()
    {
        sb1=(SeekBar)main.findViewById(R.id.line_dir_sb);
        sb1.setOnSeekBarChangeListener(this);
        sb2=(SeekBar)main.findViewById(R.id.line_side_sb);
        sb2.setOnSeekBarChangeListener(this);
        sbb=(Button)main.findViewById(R.id.show_line);
        sbb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapFrg=(MapFrg)getParentFragment();
                if (mMapFrg!=null &&mMapFrg.mArea.getCount() > 3) {
                    mMapFrg.drawline(false);
                }
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main=inflater.inflate(R.layout.frag_mtb_change_width,container,false);
        init_ui();
        return main;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId())
        {
            case R.id.line_dir_sb:
            {
                ((TextView)main.findViewById(R.id.line_dir_width)).setText(progress+"m");
            }break;
            case R.id.line_side_sb:
            {
                ((TextView)main.findViewById(R.id.line_side_width)).setText(progress+"m");
            }break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public int getDirectionWidth()
    {
        return sb1.getProgress();
    }
    public int getSideWidth()
    {
        return sb2.getProgress();
    }

}
