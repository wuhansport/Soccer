package com.component.log;

import com.component.logger.LoggerBaseConfig;

public class LoggerConfig extends LoggerBaseConfig {

	@Override
	protected void init() {
		this.logMode = LoggerBaseConfig.LOG_BOTH;
		this.packageName = "com.whs.soccer";
	}

}
