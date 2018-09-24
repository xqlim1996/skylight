package com.example.xqlim.sia_v2;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class info_dialog extends DialogFragment {

    private TextView info_details;
    private Button info_ok;

    /*

    Intent intent = getIntent();
    Bundle bundle = intent.getExtras();
    int choice = bundle.getInt("Info Choice");
    */


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.info_dialog, container, false);
        info_details = view.findViewById(R.id.info_dialog_details);
        info_ok = view.findViewById(R.id.info_dialog_ok);
        info_details.setText("test details");

        info_ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return view;
    }


}
