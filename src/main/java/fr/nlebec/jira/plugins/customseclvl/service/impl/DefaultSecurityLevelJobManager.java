package fr.nlebec.jira.plugins.customseclvl.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.scheduler.config.JobId;

import fr.nlebec.jira.plugins.customseclvl.ao.model.JobEntryAO;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityLvlJob;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityLevelJobService;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleService;
import net.java.ao.Query;

@Named("securityLevelJobService")
public class DefaultSecurityLevelJobManager implements SecurityLevelJobService {

	private final static Logger LOG = Logger.getLogger(DefaultSecurityLevelJobManager.class);

	public ActiveObjects persistenceManager;
	
	public SecurityRuleService securityRuleService;

	@Inject
	public DefaultSecurityLevelJobManager( 	@ComponentImport ActiveObjects persistenceManager, SecurityRuleService securityRuleService) {
		this.persistenceManager = checkNotNull(persistenceManager);
		this.securityRuleService = securityRuleService;
	}

	public void deleteJobEntry(String jobId, int securityRuleId) {
		JobEntryAO[] jobEntries = this.persistenceManager.find(JobEntryAO.class,Query.select().where("JOB_ID = ? AND SECURITY_RULE_ID = ?",jobId,securityRuleId));
		this.persistenceManager.delete(jobEntries[0]);
	}
	
	public JobId deleteJobEntry(int securityRuleId) {
		JobEntryAO[] jobEntries = this.persistenceManager.find(JobEntryAO.class,Query.select().where("SECURITY_RULE_ID = ?",securityRuleId));
		String jobId ;
		if( jobEntries.length > 0) {
			jobId= jobEntries[0].getJobId();
			this.persistenceManager.delete(jobEntries[0]);
		}
		else {
			throw new NoSuchElementException("No job exist for security rule : "+securityRuleId);
		}
		return JobId.of(jobId);
	}

	public void persistJobEntry(String jobId, String jobName, int securityLvlId) {
		JobEntryAO jobEntry = this.persistenceManager.create(JobEntryAO.class);
		jobEntry.setSecurityRuleId(securityLvlId);
		jobEntry.setJobName(jobName);
		jobEntry.setJobId(jobId);
		jobEntry.save();
	}

	public List<SecurityLvlJob> getPendingJobs() {
		JobEntryAO[] jobEntries = this.persistenceManager.find(JobEntryAO.class,Query.select());
		List<SecurityLvlJob> jobs = new ArrayList<>(); 
		for (JobEntryAO jobEntryAO : jobEntries) {
			SecurityRules sr = null;
			sr = securityRuleService.getSecurityRule(jobEntryAO.getSecurityRuleId());
			SecurityLvlJob job = new SecurityLvlJob();
			job.setSecurityRule(sr);
			job.setJobId(jobEntryAO.getJobId());
			job.setJobName(jobEntryAO.getJobName());
			jobs.add(job);
		}
		return jobs;
	}
	public SecurityLvlJob getPendingJob(SecurityRules sr) {
		JobEntryAO[] jobEntries = this.persistenceManager.find(JobEntryAO.class,Query.select().where("SECURITY_RULE_ID = ?",sr.getId()));
		SecurityLvlJob job = new SecurityLvlJob();
		job.setSecurityRule(sr);
		job.setJobId(jobEntries[0].getJobId());
		job.setJobName(jobEntries[0].getJobName());
		return job;
	}


}