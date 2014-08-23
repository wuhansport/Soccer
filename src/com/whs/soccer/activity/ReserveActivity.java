package com.whs.soccer.activity;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.whs.soccer.R;
import com.whs.soccer.ui.base.SubBaseActivity;

public class ReserveActivity extends SubBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);
        ((TextView)findViewById(R.id.title)).setText(R.string.reserve);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reserve, menu);
        return true;
    }

}
