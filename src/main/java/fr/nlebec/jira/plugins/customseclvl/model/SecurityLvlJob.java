package fr.nlebec.jira.plugins.customseclvl.model;

public class SecurityLvlJob {



	private String jobId;
	
	private String jobName;
	
	private SecurityRules securityRule;


	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public SecurityRules getSecurityRule() {
		return securityRule;
	}

	public void setSecurityRule(SecurityRules securityRule) {
		this.securityRule = securityRule;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
}
