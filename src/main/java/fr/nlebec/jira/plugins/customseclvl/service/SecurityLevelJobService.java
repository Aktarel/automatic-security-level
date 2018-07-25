package fr.nlebec.jira.plugins.customseclvl.service;

import java.util.List;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.scheduler.config.JobId;

import fr.nlebec.jira.plugins.customseclvl.model.SecurityLvlJob;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;

@Scanned
public interface SecurityLevelJobService {

	public void deleteJobEntry(String jobId, int securityRuleId);

	public JobId deleteJobEntry(int securityRuleId);

	public void persistJobEntry(String jobId, String jobName, int securityLvlId);

	public List<SecurityLvlJob> getPendingJobs();

	public SecurityLvlJob getPendingJob(SecurityRules sr);
}