package com.kochiu.se.core.quartz.job;

import com.kochiu.se.common.exception.SystemException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kochiu.se.core.context.SpringContextHolder;
import com.kochiu.se.core.quartz.source.DynamicQuartz;

public class JobProxy implements Job {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobClassName = context.getJobDetail().getJobDataMap().getString(DynamicQuartz.JOB_CLASS_NAME);

		if (jobClassName == null) {
			throw new JobExecutionException("jobClassName is null !");
		}

		Job job = getJob(jobClassName);

		if (job == null) {
			throw new SystemException("Can not get a correct job, jobClassName=" + jobClassName);
		}

		job.execute(context);
	}

	private Job getJob(String jobClassName) {
		if (jobClassName == null) {
			throw new SystemException("jobClassName is null !");
		}

		Job job = null;
		String errorLog = null;

		try {
			job = SpringContextHolder.applicationContext.getBean(jobClassName, Job.class);
		} catch (Exception e1) {
			errorLog = e1.getMessage();
			Class<?> jobClass = null;

			try {
				jobClass = Class.forName(jobClassName);

				if (jobClass != null) {
					try {
						job = (Job) SpringContextHolder.applicationContext.getBean(jobClass);
					} catch (Exception e3) {
						errorLog = errorLog + " | " + e3.getMessage();
					}

					if (job == null) {
						try {
							job = (Job) jobClass.newInstance();
						} catch (Exception e4) {
							errorLog = errorLog + " | " + e4.getMessage();
						}
					}
				}
			} catch (Exception e2) {
				errorLog = errorLog + " | " + e2.getMessage();
			}
		}

		if (job == null) {
			log.error("Look for job class error, class name=" + jobClassName + errorLog);
		}

		return job;
	}
}
