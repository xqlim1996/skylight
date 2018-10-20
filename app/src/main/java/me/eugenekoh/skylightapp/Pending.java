package me.eugenekoh.skylightapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Pending extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);
        final String TAG = "XiaoQi";

        final DocumentReference docRef = db.collection("bids").document("SQ890");
        final TextView bid = (TextView)findViewById(R.id.pending_bid_value);
        final TextView rewards2 = (TextView)findViewById(R.id.rewards2);
        final TextView bid_value_header = (TextView)findViewById(R.id.bid_value_header);
        final TextView pending_header = (TextView)findViewById(R.id.pending_header);
        final TextView pending_description = (TextView)findViewById(R.id.pending_description);
        final TextView pending_button_description = (TextView)findViewById(R.id.pending_button_description);
        final TextView pending_button = (Button)findViewById(R.id.pending_button);
        final TextView pending_selected_header = (TextView)findViewById(R.id.pending_selected_header);



        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());


                    docRef.update("UserBid", snapshot.getData().get("OldBid"), "CustomersAccepted", 4)
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

                    final long userBid = (long) snapshot.getData().get("UserBid");
                    bid.setText("$" + userBid);

                    long winningBid = (long) snapshot.getData().get("WinningBid");
                    boolean isOngoing = (boolean) snapshot.getData().get("isOngoing");

                    long topUpBid = winningBid - userBid;

                    if (winningBid >= userBid && isOngoing == false){
                        pending_header.setText("Bumpbid Confirmed");
                        pending_description.setText("Congratulations! You have been selected as one of our passengers to be offloaded");
                        pending_description.setBackgroundColor(Color.parseColor("#a5d6a7"));
                        pending_selected_header.setBackgroundColor(Color.parseColor("#a5d6a7"));

                        bid_value_header.setText("The finalised compensation you will receive is:");
                        bid.setText("$" + winningBid);
                        rewards2.setText("2. $" + topUpBid + " worth in Cash");
                        pending_button_description.setVisibility(View.VISIBLE);
                        pending_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(Pending.this, Chat.class));
                                finish();
                            }
                        });


                        pending_button.setVisibility(View.VISIBLE);


                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });


    }
}
