package com.whs.soccer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.component.logger.Logger;
import com.whs.soccer.R;

public class MainActivity extends Activity {
    
    private static final String TAG = "MainActivity";
    private long mCurrentBackTime = 0; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.back).setVisibility(View.GONE);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_tv:
                Logger.d(TAG, "profile click");
                startActivityWithAnimator(ProfileActivity.class, true);
                break;
            case R.id.team_tv:
                Logger.d(TAG, "team click");
                startActivityWithAnimator(TeamActivity.class, true);
                break;
            case R.id.match_tv:
                Logger.d(TAG, "match click");
                startActivityWithAnimator(MatchActivity.class, true);
                break;
            case R.id.reserve_tv:
                Logger.d(TAG, "reserve click");
                startActivityWithAnimator(ReserveActivity.class, true);
                break;
            case R.id.finance_tv:
                Logger.d(TAG, "finance click");
                startActivityWithAnimator(FinanceActivity.class, true);
                break;
        }
    }
    
    private void startActivityWithAnimator(Class<?> activity, boolean withAnimator) {
    	Intent intent = new Intent(this, activity);
    	startActivity(intent);
    	if(withAnimator) {
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    	}
    }
    
    @Override
    public void onBackPressed() {
    	long current = System.currentTimeMillis();
    	if(current - mCurrentBackTime > 3000 || mCurrentBackTime == 0) {
    		mCurrentBackTime = System.currentTimeMillis();
    		Toast.makeText(this, R.string.quit_tips, Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	super.onBackPressed();
    }
}
