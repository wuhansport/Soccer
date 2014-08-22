package com.whs.soccer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {
    
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_tv:
                Log.d(TAG, "profile click");
                break;
            case R.id.team_tv:
                Log.d(TAG, "team click");
                break;
            case R.id.match_tv:
                Log.d(TAG, "match click");
                break;
            case R.id.reserve_tv:
                Log.d(TAG, "reserve click");
                break;
            case R.id.finance_tv:
                Log.d(TAG, "finance click");
                break;
        }
    }
}
