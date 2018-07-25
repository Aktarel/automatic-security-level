package fr.nlebec.jira.plugins.customseclvl.ao.model;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Preload
@Table("JOB_ENTRIES")
public interface JobEntryAO extends Entity {

	public void setJobId(String jobId);
	
	public String getJobId();
	
	public void setJobName(String jobName);
	
	public String getJobName();
	
	public void setSecurityRuleId(Integer securityRuleId);
	
	public Integer getSecurityRuleId();
}
