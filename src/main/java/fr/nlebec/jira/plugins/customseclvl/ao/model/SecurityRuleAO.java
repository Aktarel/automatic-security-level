package fr.nlebec.jira.plugins.customseclvl.ao.model;

import java.util.Date;


import net.java.ao.Entity;
import net.java.ao.ManyToMany;
import net.java.ao.Preload;
import net.java.ao.schema.Default;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;

@Preload
@Table("CSL_SECURITY_RULES")
public interface SecurityRuleAO extends Entity{
	
	public String getName();
	public void setName(String name);
	public Long getCreationUser();
	public void setCreationUser(Long creationUser);
	public Date getApplicationDate();
	public void setApplicationDate(Date applicationDate);
	public Date getCreationDate();
	public void setCreationDate(Date creationDate);
	public Long getDisableUser();
	public void setDisableUser(Long disableUser);
	public Date getDisableDate();
	public void setDisableDate(Date disableDate);
	public Boolean getActive();
	public void setDeleted(Boolean disableDate);
	public Boolean getDeleted();
	
	
	public void setActive(Boolean active);
	
	public Integer getPriority();
	
	@Default(value="1")
	@NotNull
	public void setPriority(Integer priority);
	public Long getJiraSecurityId();
	public void setJiraSecurityId(Long jiraSecurityId);
	
	@ManyToMany(value = EventToSecurityRule.class)
	public EventAO[] getEvents();
	
	public String getJql();
	public void setJql(String jql);
	
	public void setCSLConfigurationAO(CSLConfigurationAO CSLConfigurationAO);
	public CSLConfigurationAO getCSLConfigurationAO();
}

