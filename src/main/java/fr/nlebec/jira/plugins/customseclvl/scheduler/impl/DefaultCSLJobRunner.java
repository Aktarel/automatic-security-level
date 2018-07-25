package fr.nlebec.jira.plugins.customseclvl.scheduler.impl;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;

import fr.nlebec.jira.plugins.customseclvl.model.SecurityLvlJob;
import fr.nlebec.jira.plugins.customseclvl.scheduler.ActiveSecurityLevel;
import fr.nlebec.jira.plugins.customseclvl.scheduler.ApplySecurityLevel;
import fr.nlebec.jira.plugins.customseclvl.scheduler.InactiveSecurityLevel;
import fr.nlebec.jira.plugins.customseclvl.scheduler.RemoveSecurityLevel;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityLevelJobService;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleApplicationService;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleService;

@ExportAsService
@Component
public class DefaultCSLJobRunner implements LifecycleAware {

	private Logger log = Logger.getLogger(DefaultCSLJobRunner.class);

	private SchedulerService schedulerService;

	private SecurityRuleApplicationService securityRuleApplicationService;

	private SecurityLevelJobService securityLevelJobService;
	
	private SecurityRuleService securityRuleService;


	@Inject
	public DefaultCSLJobRunner(@ComponentImport SchedulerService schedulerService,
			SecurityRuleApplicationService securityRuleApplicationService, SecurityRuleService securityRulesService,
			SecurityLevelJobService securityLevelJobService, SecurityRuleService securityRuleService) {
		this.schedulerService = schedulerService;
		this.securityRuleApplicationService = securityRuleApplicationService;
		this.securityLevelJobService = securityLevelJobService;
		this.securityRuleService = securityRuleService;
	}

	public void addSecurityLevelJob(Integer idSecurityRule, ZonedDateTime applicationDate)
			throws SchedulerServiceException {
		JobConfig jobConfig = this.createJobConfig(idSecurityRule, applicationDate, ApplySecurityLevel.APPLY_SL_JOB);
		JobId jobId = schedulerService.scheduleJobWithGeneratedId(jobConfig);
		securityLevelJobService.persistJobEntry(jobId.toString(), ApplySecurityLevel.APPLY_SL_JOB.toString(), idSecurityRule);
		log.info("Persist Job Entry  " + ApplySecurityLevel.APPLY_SL_JOB.toString() + " : " + jobId.toString());
	}

	public void removeSecurityLevelJob(Integer idSecurityRule, ZonedDateTime applicationDate)
			throws SchedulerServiceException {
		JobConfig jobConfig = this.createJobConfig(idSecurityRule, applicationDate, RemoveSecurityLevel.REMOVE_SL_JOB);
		JobId jobId = schedulerService.scheduleJobWithGeneratedId(jobConfig);
		securityLevelJobService.persistJobEntry(jobId.toString(), RemoveSecurityLevel.REMOVE_SL_JOB.toString(), idSecurityRule);
		log.info("Persist Job Entry  " + RemoveSecurityLevel.REMOVE_SL_JOB.toString() + " : " + jobId.toString());
	}

	public void disableSecurityLevelJob(Integer idSecurityRule, ZonedDateTime applicationDate)
			throws SchedulerServiceException {
		JobConfig jobConfig = this.createJobConfig(idSecurityRule, applicationDate,
				InactiveSecurityLevel.INACTIVE_SL_JOB);
		JobId jobId = schedulerService.scheduleJobWithGeneratedId(jobConfig);
		securityLevelJobService.persistJobEntry(jobId.toString(), InactiveSecurityLevel.INACTIVE_SL_JOB.toString(), idSecurityRule);
		log.info("Persist Job Entry  " + InactiveSecurityLevel.INACTIVE_SL_JOB.toString() + " : " + jobId.toString());
	}

	public void removeTaskFromScheduler(JobId jobid) {
		log.info("Remove Task from scheduler : " + jobid.toString());
		schedulerService.unscheduleJob(jobid);
	}
	public void activateSecurityLevelJob(Integer idSecurityRule, ZonedDateTime applicationDate)
			throws SchedulerServiceException {
		JobConfig jobConfig = this.createJobConfig(idSecurityRule, applicationDate, ActiveSecurityLevel.ACTIVE_SL_JOB);
		JobId jobId = schedulerService.scheduleJobWithGeneratedId(jobConfig);
		securityLevelJobService.persistJobEntry(jobId.toString(), ActiveSecurityLevel.ACTIVE_SL_JOB.toString(), idSecurityRule);
		log.info("Persist Job Entry  " + ActiveSecurityLevel.ACTIVE_SL_JOB.toString() + " : " + jobId.toString());
	}

	public void onStop() {
		log.info("*********************************** onStop");
		schedulerService.unregisterJobRunner(ApplySecurityLevel.APPLY_SL_JOB);
		schedulerService.unregisterJobRunner(RemoveSecurityLevel.REMOVE_SL_JOB);
		schedulerService.unregisterJobRunner(InactiveSecurityLevel.INACTIVE_SL_JOB);
		schedulerService.unregisterJobRunner(ActiveSecurityLevel.ACTIVE_SL_JOB);

		List<SecurityLvlJob> pendingJobs = securityLevelJobService.getPendingJobs();
		for (SecurityLvlJob pendingJob : pendingJobs) {
			schedulerService.unscheduleJob(JobId.of(pendingJob.getJobId()));
		}
	}

	private JobConfig createJobConfig(int idSecurityRule, ZonedDateTime instant, JobRunnerKey jobRunnerKey) {
		Map<String, Serializable> params = new HashMap<>();
		params.put("idSecurityRule", idSecurityRule);
		final JobConfig jobConfig = JobConfig.forJobRunnerKey(jobRunnerKey)
				.withSchedule(Schedule.runOnce(Date.from(instant.toInstant()))).withRunMode(RunMode.RUN_LOCALLY)
				.withParameters(params);
		return jobConfig;
	}

	public void onStart() {
		log.info("*********************************** onStart");
		schedulerService.registerJobRunner(ApplySecurityLevel.APPLY_SL_JOB,
				new ApplySecurityLevelTask(securityRuleApplicationService, securityRuleService, securityLevelJobService));
		schedulerService.registerJobRunner(RemoveSecurityLevel.REMOVE_SL_JOB,
				new RemoveSecurityLevelTask(securityRuleApplicationService, securityRuleService, securityLevelJobService));
		schedulerService.registerJobRunner(InactiveSecurityLevel.INACTIVE_SL_JOB,
				new InactiveSecurityLevelTask(securityRuleApplicationService, securityRuleService, securityLevelJobService));
		schedulerService.registerJobRunner(ActiveSecurityLevel.ACTIVE_SL_JOB,
				new ActiveSecurityLevelTask(securityRuleApplicationService, securityRuleService, securityLevelJobService));

		List<SecurityLvlJob> pendingJobs = securityLevelJobService.getPendingJobs();

		for (SecurityLvlJob pendingJob : pendingJobs) {
			log.info("pendingJob.getSecurityRule() : " + pendingJob.getSecurityRule());
			JobConfig jobConfig = createJobConfig(pendingJob.getSecurityRule().getId(),
					pendingJob.getSecurityRule().getApplicationDate(), JobRunnerKey.of(pendingJob.getJobName()));
			try {
				schedulerService.scheduleJob(JobId.of(pendingJob.getJobId()), jobConfig);
			} catch (SchedulerServiceException e) {
				log.error(e.getMessage());
			}
		}

	}

	

}
