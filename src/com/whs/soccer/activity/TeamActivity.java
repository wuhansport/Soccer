package com.whs.soccer.activity;

import com.whs.soccer.R;
import com.whs.soccer.R.layout;
import com.whs.soccer.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TeamActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.team, menu);
        return true;
    }

}
