package com.example.xqlim.sia_v2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;
import android.util.Log;



public class popup_main extends Activity {

    private static final String TAG = "Message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_main);

        final TextView details= (TextView) findViewById(R.id.popup_details);
        final TextView header =(TextView) findViewById(R.id.popup_header);
        header.setText("this is a header");

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //int choice = bundle.get("Info Choice");
        String choice = bundle.getString("Info choice");

        //details.setText("asdf");

        Log.i(TAG, bundle.getString("Info choice"));
        Log.i(TAG, "popup view");
        Log.i(TAG, choice);

        details.setText(choice);

        /*
        if (choice == 2000){
            System.out.println("popup_cash");
            details.setText(R.string.popup_cash);
        }
        else if (choice == 2001){
            System.out.println("popup_upgrade");
            details.setText(R.string.popup_upgrade);
        }
        else if (choice == 2002){
            System.out.println("popup_hotel");
            details.setText(R.string.popup_hotel);
        }


        switch(choice){
            case 2000:

                break;
            case 2001:

                break;
            case 2002:

                break;
        }*/




        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.8), (int)(height *0.7));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);




    }
}
