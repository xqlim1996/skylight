package com.example.xqlim.sia_v2;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class compensation_main extends AppCompatActivity {

    private static final String TAG = "Message";

    private static final int cash_radio_id = 1000;//first radio button id
    private static final int upgrade_radio_id = 1001;//second radio button id
    private static final int hotel_radio_id = 1002;//third radio button id

    private static final int cash_info_id = 2000;//first radio button id
    private static final int upgrade_info_id = 2001;//second radio button id
    private static final int hotel_info_id = 2002;//third radio button id

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.compensation_main);

        final RadioButton cash_radio = (RadioButton)findViewById(R.id.cash_radio);
        final RadioButton upgrade_radio = (RadioButton)findViewById((R.id.upgrade_radio));
        final RadioButton hotel_radio = (RadioButton)findViewById(R.id.hotel_radio);
        final RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radio_group);
        final Button confirm_button = (Button)findViewById(R.id.confirm);
        final Button hotel_info = (Button)findViewById(R.id.hotel_info);
        final Button upgrade_info = (Button)findViewById(R.id.upgrade_info);
        final Button cash_info = (Button)findViewById(R.id.cash_info);

        cash_radio.setId(cash_radio_id);
        upgrade_radio.setId(upgrade_radio_id);
        hotel_radio.setId(hotel_radio_id);

        cash_info.setId(cash_info_id);
        upgrade_info.setId(upgrade_info_id);
        hotel_info.setId(hotel_info_id);

        radioGroup.clearCheck();


        cash_info.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(compensation_main.this, popup_main.class);

                /*info_dialog dialog = new info_dialog();
                dialog.show(getFragmentManager(), "info_dialog");
                */
                Bundle bundle = new Bundle();
                bundle.putString("Info choice", getString(R.string.popup_cash));
                Log.i(TAG, bundle.getString("Info choice"));
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        upgrade_info.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(compensation_main.this, popup_main.class);
                Bundle bundle = new Bundle();
                bundle.putString("Info choice", getString(R.string.popup_upgrade));
                Log.i(TAG, bundle.getString("Info choice"));
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        hotel_info.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(compensation_main.this, popup_main.class);
                Bundle bundle = new Bundle();
                bundle.putString("Info choice", getString(R.string.popup_hotel));
                Log.i(TAG, bundle.getString("Info choice"));
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        confirm_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(compensation_main.this, confirm_main.class);
                Bundle bundle = new Bundle();
                bundle.putInt("Compensation Choice", radioGroup.getCheckedRadioButtonId());
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }

}
