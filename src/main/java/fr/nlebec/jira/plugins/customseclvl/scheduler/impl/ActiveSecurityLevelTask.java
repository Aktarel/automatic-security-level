package fr.nlebec.jira.plugins.customseclvl.scheduler.impl;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;

import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;
import fr.nlebec.jira.plugins.customseclvl.scheduler.ActiveSecurityLevel;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityLevelJobService;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleApplicationService;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleService;

@Scanned
public class ActiveSecurityLevelTask implements ActiveSecurityLevel {

	private SecurityRuleApplicationService applicationManager;

	private Logger log = Logger.getLogger(ActiveSecurityLevelTask.class);

	private SecurityRuleService securityRuleService;

	private SecurityLevelJobService securityLevelJobService;

	public ActiveSecurityLevelTask(SecurityRuleApplicationService applicationManager, SecurityRuleService service,
			SecurityLevelJobService securityLevelJobService) {
		this.applicationManager = applicationManager;
		this.securityRuleService = service;
		this.securityLevelJobService = securityLevelJobService;
	}

	public JobRunnerResponse runJob(JobRunnerRequest req) {
		JobRunnerResponse resp = JobRunnerResponse.success("Job scheduled");
		Map<String, Serializable> params = req.getJobConfig().getParameters();
		int idSr = (int) params.get("idSecurityRule");
		SecurityRules sr = null;
		try {
			sr = securityRuleService.getSecurityRule(idSr);
			applicationManager.applyRuleOnWholeStock(sr);
			if (sr != null) {
				sr.setActive(Boolean.TRUE);
				securityRuleService.updateSecurityRule(sr);
			}
			securityLevelJobService.deleteJobEntry(req.getJobId().toString(), idSr);
		} catch (SQLException e1) {
			resp = JobRunnerResponse.failed(e1.getMessage());
		}

		return resp;
	}

}
