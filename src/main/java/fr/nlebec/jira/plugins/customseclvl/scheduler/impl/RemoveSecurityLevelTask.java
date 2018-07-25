package fr.nlebec.jira.plugins.customseclvl.scheduler.impl;

import java.io.Serializable;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Map;

import javax.inject.Named;

import org.apache.log4j.Logger;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;

import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;
import fr.nlebec.jira.plugins.customseclvl.scheduler.RemoveSecurityLevel;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityLevelJobService;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleApplicationService;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleService;
import fr.nlebec.jira.plugins.customseclvl.service.impl.DefaultSecurityLevelJobManager;
import fr.nlebec.jira.plugins.customseclvl.service.impl.SecurityRuleApplicationManager;

@Scanned
public class RemoveSecurityLevelTask implements RemoveSecurityLevel {

	private SecurityRuleApplicationService securityRuleApplicationService;
	
	private Logger log = Logger.getLogger(RemoveSecurityLevelTask.class);

	private SecurityRuleService securityRuleService;
	
	private SecurityLevelJobService jobServices;
	
	public RemoveSecurityLevelTask(SecurityRuleApplicationService securityRuleApplicationService, SecurityRuleService service, SecurityLevelJobService jobServices) {
		this.securityRuleApplicationService = securityRuleApplicationService; 
		this.securityRuleService = service;
		this.jobServices = jobServices;
	}
	
	public JobRunnerResponse runJob(JobRunnerRequest req) {
		JobRunnerResponse resp = JobRunnerResponse.success("Job scheduled");
		Map<String, Serializable> params = req.getJobConfig().getParameters();
		int idSr = (int) params.get("idSecurityRule");
		SecurityRules sr = null;
		try {
			sr = securityRuleService.getSecurityRule(idSr);
			securityRuleApplicationService.removeRuleOnWholeStock(sr);
			if(sr != null) {
				sr.setDisableDate(ZonedDateTime.now());
				sr.setDeleted(Boolean.TRUE);
				sr.setActive(Boolean.FALSE);
				securityRuleService.updateSecurityRule(sr);
			}
			jobServices.deleteJobEntry(req.getJobId().toString(),idSr);
		} catch (SQLException e1) {
			resp = JobRunnerResponse.failed(e1.getMessage());
		}
		
		return resp;
	}
	
}
