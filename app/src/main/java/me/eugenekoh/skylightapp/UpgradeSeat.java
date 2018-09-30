package me.eugenekoh.skylightapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.PorterDuff;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import me.eugenekoh.skylightapp.R;
import me.eugenekoh.skylightapp.utils.Tools;
import me.eugenekoh.skylightapp.utils.ViewAnimation;

public class UpgradeSeat extends AppCompatActivity {

    private TabLayout tab_layout;
    private ActionBar actionBar;
    private NestedScrollView nested_scroll_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flights_upgrade);

        initToolbar();
        initComponent();
        initCardView();
    }
    private void initCardView(){
        CardView card_view = (CardView) findViewById(R.id.flightCard); // creating a CardView and assigning a value.
        card_view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpgradeSeat.this, Options.class));
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Flights");
        actionBar.setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_20);
    }

    private void initComponent() {
        nested_scroll_view = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        tab_layout = (TabLayout) findViewById(R.id.tab_layout);

        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.ic_flight), 0);
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.ic_chat), 1);
        tab_layout.addTab(tab_layout.newTab().setIcon(R.drawable.ic_timeline), 2);

        // set icon color pre-selected
        tab_layout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.deep_orange_500), PorterDuff.Mode.SRC_IN);
        tab_layout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_IN);
        tab_layout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_IN);

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.deep_orange_500), PorterDuff.Mode.SRC_IN);
                switch (tab.getPosition()) {
                    case 0:
                        break;
                    case 1:
                        startActivity(new Intent(UpgradeSeat.this, Chat.class));
                        break;
                    case 2:
                        startActivity(new Intent(UpgradeSeat.this, Travel.class));
                        break;
                }

                ViewAnimation.fadeOutIn(nested_scroll_view);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.grey_60));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}
