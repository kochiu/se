package com.sztx.se.core.tbschedule.task;

import com.sztx.se.core.tbschedule.config.ScheduleConfigServer;
import com.sztx.se.core.tbschedule.config.ScheduleParameter;

public class InitialTask {

	private ScheduleParameter scheduleParameter = new ScheduleParameter();

	private ScheduleConfigServer scheduleConfigServer;

	public ScheduleParameter getScheduleParameter() {
		return scheduleParameter;
	}

	public void setScheduleParameter(ScheduleParameter scheduleParameter) {
		this.scheduleParameter = scheduleParameter;
	}

	public ScheduleConfigServer getScheduleConfigServer() {
		return scheduleConfigServer;
	}

	public void setScheduleConfigServer(ScheduleConfigServer scheduleConfigServer) {
		this.scheduleConfigServer = scheduleConfigServer;
	}

	@Override
	public String toString() {
		return "InitialTask [scheduleParameter=" + scheduleParameter + ", scheduleConfigServer=" + scheduleConfigServer + "]";
	}

}
