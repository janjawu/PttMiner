package com.janja.pttminer;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.janja.pttminer.R;
import com.janja.pttminer.activity.PttActivity;

public class HomeActivity extends ActionBarActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(false);

        findViewById(R.id.ptt_stock_button).setOnClickListener(this);
        findViewById(R.id.ptt_nba_button).setOnClickListener(this);
        findViewById(R.id.ptt_nba_film_button).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Bundle args = new Bundle();

        if (v.getId() == R.id.ptt_stock_button) {
            args.putString(PttActivity.ACTIVITY_NAME,
                    PttActivity.ACTIVITY_STOCK);
        } else if (v.getId() == R.id.ptt_nba_button) {
            args.putString(PttActivity.ACTIVITY_NAME, PttActivity.ACTIVITY_NBA);
        } else if (v.getId() == R.id.ptt_nba_film_button) {
            args.putString(PttActivity.ACTIVITY_NAME,
                    PttActivity.ACTIVITY_NBA_FILM);
        }

        startActivity(new Intent(HomeActivity.this, PttActivity.class)
                .putExtras(args));
    }
}
