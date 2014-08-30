package com.whs.soccer;

import android.content.Context;

import com.whs.soccer.util.http.HttpEngine;
import com.whs.soccer.utils.AsyncImageLoader;

/**
 * 
 * @author antoniochen this class is for manager modules
 */
public class SoccerAppEngine {
	private static SoccerAppEngine mInstance;

	public static SoccerAppEngine getInstance() {
		if (mInstance == null) {
			mInstance = new SoccerAppEngine();
		}
		return mInstance;
	}

	private SoccerDataProvider mSoccerDataPrivader;
	private SoccerUpgrader mFacesUpgrader;
	private HttpEngine mEngine;
	private AsyncImageLoader mAsyncImageLoader;
	private SoccerAppEngine() {

	}

	public void onCreate(Context context) {
		mSoccerDataPrivader = new SoccerDataProvider(context);
	}

	/**
	 * for data 
	 * @return
	 */
	public SoccerDataProvider getSoccerDataPrivader() {
		return mSoccerDataPrivader;
	}
	
	/**
	 * for update
	 * @return upgrader
	 */
	public SoccerUpgrader getUpgrader() {
		if (mFacesUpgrader == null) {
			mFacesUpgrader = new SoccerUpgrader();
		}
		return mFacesUpgrader;
	}
	
	/**
	 * for load image
	 * @return
	 */
	public AsyncImageLoader getAsyncImageLoader() {
		if(mAsyncImageLoader == null) {
			mAsyncImageLoader = new AsyncImageLoader(SoccerApplication.getInstance());
		}
		return mAsyncImageLoader;
	}
	
	/**
	 * for http request
	 * @return Http Engine
	 */
	public HttpEngine getHttpEngine() {
		if (mEngine == null) {
			mEngine = new HttpEngine();
		}
		return mEngine;
	}

}
