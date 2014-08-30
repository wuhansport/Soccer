package com.whs.soccer;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.os.Process;

/**
 * 
 * @author antoniochen this class is for replace application
 */
public class SoccerApplication extends Application {
	private static SoccerApplication mInstance = null;

	@Override
	public void onCreate() {
		super.onCreate();
		final String processName = getProcessName();
		if (getApplicationInfo().processName.equals(processName)) {
			SoccerAppEngine.getInstance().onCreate(this);
		}
		mInstance = this;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();

		final String processName = getProcessName();
		if (getApplicationInfo().processName.equals(processName)) {
			// TODO: relese all resource

		}
	}

	private String getProcessName() {
		final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		if (am == null) {
			return null;
		}

		final List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		if (infos == null) {
			return null;
		}

		final int pid = Process.myPid();
		for (int i = 0, size = infos.size(); i < size; ++i) {
			final RunningAppProcessInfo info = infos.get(i);
			if (info.pid == pid) {
				return info.processName;
			}
		}

		return null;
	}

	public static SoccerApplication getInstance() {
		return mInstance;
	}
}
