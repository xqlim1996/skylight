package me.eugenekoh.skylightapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import me.eugenekoh.skylightapp.utils.Tools;

public class Options extends AppCompatActivity {

    private static final int MAX_STEP = 3;

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        final String TAG = "XiaoQi";

        final DocumentReference docRef = db.collection("bids").document("SQ890");
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
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        viewPager = (ViewPager) findViewById(R.id.view_pager);

        // adding bottom dots
        bottomProgressDots(0);

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }


    private void bottomProgressDots(int current_index) {
        LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        ImageView[] dots = new ImageView[MAX_STEP];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle);
            dots[i].setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[current_index].setImageResource(R.drawable.shape_circle);
            dots[current_index].setColorFilter(getResources().getColor(R.color.light_green_600), PorterDuff.Mode.SRC_IN);
        }
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(final int position) {
            bottomProgressDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private Button btnNext;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final String TAG = "XiaoQi";

            if (position == 0) {
                View view = layoutInflater.inflate(R.layout.item_card_wizard_bg, container, false);
                btnNext = (Button) view.findViewById(R.id.btn_next);
                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(1);
                    }
                });
                container.addView(view);
                return view;
            }else if(position == 1){
                final View view = layoutInflater.inflate(R.layout.item_card_wizard_bg2, container, false);
                Button btnYes = (Button) view.findViewById(R.id.btn_yes);
                Button btnNo = (Button) view.findViewById(R.id.btn_no);
                final TextView title2 = (TextView) view.findViewById(R.id.title2);

                final DocumentReference docRef = db.collection("bids").document("SQ890");
                docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {

                            Log.d(TAG, "DocumentSnapshot data: else loop" + snapshot.getData().get("Current Bid"));
                            long bid = (long) snapshot.getData().get("Current Bid");

                            ((TextView) view.findViewById(R.id.title2)).setText("$" + bid);

                            Log.d(TAG, "Current data: " + snapshot.getData());
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), Pending.class));
                    }
                });
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), Flights.class));
                    }
                });
                container.addView(view);
                return view;
            }else{
                final View view = layoutInflater.inflate(R.layout.item_card_wizard_bg3, container, false);
                final View view1 = layoutInflater.inflate(R.layout.item_card_wizard_bg2, container, false);
                Button btnYes = (Button) view.findViewById(R.id.btn_yes);
                Button btnNo = (Button) view.findViewById(R.id.btn_no);
                final TextView title3 = (TextView) view.findViewById(R.id.title3);


                final DocumentReference docRef = db.collection("bids").document("SQ890");
                docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {

                            Log.d(TAG, "DocumentSnapshot data: else loop" + snapshot.getData().get("Current Bid"));
                            long bid = (long) snapshot.getData().get("Current Bid");

                            ((TextView) view.findViewById(R.id.title3)).setText("$" + bid);
                            ((TextView) view1.findViewById(R.id.title2)).setText("$" + bid);

                            Log.d(TAG, "Current data: " + snapshot.getData());
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), Pending.class));
                    }
                });
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), Flights.class));
                    }
                });
                container.addView(view);
                return view;
            }


        }

        @Override
        public int getCount() {
            return 3;
        }
        // this is the number of slides

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
