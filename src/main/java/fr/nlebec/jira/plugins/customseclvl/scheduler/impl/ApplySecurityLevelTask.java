package fr.nlebec.jira.plugins.customseclvl.scheduler.impl;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;

import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;
import fr.nlebec.jira.plugins.customseclvl.scheduler.ApplySecurityLevel;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityLevelJobService;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleApplicationService;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleService;

@Scanned
public class ApplySecurityLevelTask implements ApplySecurityLevel {

	private SecurityRuleApplicationService securityRuleApplicationService;

	private Logger log = Logger.getLogger(ApplySecurityLevelTask.class);

	private SecurityRuleService securityRuleService;

	private SecurityLevelJobService securityLevelJobService;

	public ApplySecurityLevelTask(SecurityRuleApplicationService securityRuleApplicationService,
			SecurityRuleService service, SecurityLevelJobService securityLevelJobService) {
		this.securityRuleApplicationService = securityRuleApplicationService;
		this.securityRuleService = service;
		this.securityLevelJobService = securityLevelJobService;
	}

	public JobRunnerResponse runJob(JobRunnerRequest req) {
		JobRunnerResponse resp = JobRunnerResponse.success("Job scheduled");
		Map<String, Serializable> params = req.getJobConfig().getParameters();
		int idSr = (int) params.get("idSecurityRule");
		SecurityRules sr = securityRuleService.getSecurityRule(idSr);
		securityRuleApplicationService.applyRuleOnWholeStock(sr);
		securityLevelJobService.deleteJobEntry(req.getJobId().toString(), idSr);
		return resp;
	}

}
