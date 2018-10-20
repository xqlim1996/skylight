package me.eugenekoh.skylightapp;


import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import me.eugenekoh.skylightapp.utils.Tools;

public class Bumpbid extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences("bumpbid",MODE_PRIVATE);
        updateBid();
//      checkConfirmation();
        setContentView(R.layout.activity_bumpbid);
        initToolbar();
        initInfo();
        initConfirmation();

        //init onClickListener on qr
        if(sp.getBoolean("accepted",false)) {
            initQR();
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bumpbid");
        Tools.setSystemBarColor(this, R.color.grey_20);
        Tools.setSystemBarLight(this);
    }

    private void initInfo(){
        ImageButton imgButton = findViewById(R.id.imagebutton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Bumpbid.this, BumpbidInfo.class));
                finish();
            }
        });
    }
    private void initConfirmation(){
        CardView accept = (CardView) findViewById(R.id.accept); // creating a CardView and assigning a value.
        CardView reject = (CardView) findViewById(R.id.reject); // creating a CardView and assigning a value.
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Bumpbid.this, Flights.class));
            }
        });
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });
    }
    private void addConfirmationInfo(){
        LinearLayout confirmation = findViewById(R.id.confirmation);
        confirmation.setVisibility(View.GONE);
        LinearLayout mContainerView = findViewById(R.id.bumpbid_main);
        View myView = getLayoutInflater().inflate(R.layout.fragment_confirmation,null);
        mContainerView.addView(myView);
    }

    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("BumpBid acceptance confirmation");
        builder.setMessage("Do you wish to accept the BumpBid offer?");
        builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addConfirmationInfo();
                updateCustomerAccepted();
                // to confirm that user accepted
                SharedPreferences sp = getSharedPreferences("bumpbid",MODE_PRIVATE);
                sp.edit().putBoolean("accepted",true).apply();
            }
        });
        builder.setNegativeButton("Disagree", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateBid(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("bids").document("SQ890");
        final String TAG = "XiaoQi";
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {

                    Log.d(TAG, "DocumentSnapshot data: else loop" + snapshot.getData().get("CurrentBid"));
                    long bid = (long) snapshot.getData().get("CurrentBid");
                    long voucher = (long) snapshot.getData().get("CurrentVoucher");

                    TextView view = findViewById(R.id.current_bid);
                    view.setText("SGD " + bid);
                    view = findViewById(R.id.current_voucher);
                    view.setText("+ SGD " + voucher + " cash voucher");
                    Log.d(TAG, "Current data: " + snapshot.getData());
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }
    private void updateCustomerAccepted(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("bids").document("SQ890");
        final String TAG = "XiaoQi";
        int numAccepted = 0;
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    long numAccepted = (long) document.get("CustomersAccepted") + 1;
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        docRef
                .update("CurrentVoucher", numAccepted)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

    }

//    private void checkConfirmation(){
//        SharedPreferences sp = getSharedPreferences("bumpbid",MODE_PRIVATE);
//        if(sp.getBoolean("accept",false)){
//            addConfirmationInfo();
//        }
//    }

    private void initQR(){
        LinearLayout qr = findViewById(R.id.qr_code);
        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder imageDialog = new AlertDialog.Builder(Bumpbid.this);
                imageDialog.setTitle("QR Code");
                ImageView showImage = new ImageView(Bumpbid.this);
                imageDialog.setView(showImage);
                imageDialog.setNegativeButton("Close",null);
            }
        });
    }
}

