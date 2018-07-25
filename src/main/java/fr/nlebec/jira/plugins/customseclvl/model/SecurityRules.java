package fr.nlebec.jira.plugins.customseclvl.model;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.security.IssueSecurityLevel;
import com.atlassian.jira.user.ApplicationUser;

public class SecurityRules implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private ApplicationUser creationUser;
	private ZonedDateTime creationDate;
	private ApplicationUser disableUser;
	private ZonedDateTime applicationDate;
	private ZonedDateTime disableDate;
	private Boolean active;
	private Integer priority;
	private Long jiraSecurityId;
	private List<Event> events;
	private String jql;
	private Boolean deleted;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ApplicationUser getCreationUser() {
		return creationUser;
	}
	public void setCreationUser(ApplicationUser creationUser) {
		this.creationUser = creationUser;
	}
	public ZonedDateTime getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(ZonedDateTime creationDate) {
		this.creationDate = creationDate;
	}
	public ApplicationUser getDisableUser() {
		return disableUser;
	}
	public void setDisableUser(ApplicationUser disableUser) {
		this.disableUser = disableUser;
	}
	public ZonedDateTime getDisableDate() {
		return disableDate;
	}
	public void setDisableDate(ZonedDateTime disableDate) {
		this.disableDate = disableDate;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public Long getJiraSecurityId() {
		return jiraSecurityId;
	}
	public void setJiraSecurityId(Long jiraSecurityId) {
		this.jiraSecurityId = jiraSecurityId;
	}
	public List<Event> getEvents() {
		return events;
	}
	public void setEvents(List<Event> events) {
		this.events = events;
	}
	public String getJql() {
		return jql;
	}
	public void setJql(String jql) {
		this.jql = jql;
	}
	
	public String getJiraSecurityName() {
		IssueSecurityLevel level = ComponentAccessor.getIssueSecurityLevelManager().getSecurityLevel(getJiraSecurityId());
		String ret = "Unknown";
		if(level != null) {
			ret = level.getName();
		}
		return ret;
		
	}
	public String getSimpleEventsList() {
		StringBuilder ret = new StringBuilder("[");
		for(Event event : this.events) {
			ret.append( event.getJiraEventId() );
			ret.append( "," );
		}
		ret.replace(ret.lastIndexOf(","),ret.lastIndexOf(",")+1,"]");
		return ret.toString();
		
	}
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	public ZonedDateTime getApplicationDate() {
		return applicationDate;
	}
	public void setApplicationDate(ZonedDateTime applicationDate) {
		this.applicationDate = applicationDate;
	}
	@Override
	public String toString() {
		return "SecurityRules [id=" + id + ", name=" + name + ", creationUser=" + creationUser + ", creationDate="
				+ creationDate + ", disableUser=" + disableUser + ", applicationDate=" + applicationDate
				+ ", disableDate=" + disableDate + ", active=" + active + ", priority=" + priority + ", jiraSecurityId="
				+ jiraSecurityId + ", events=" + events + ", jql=" + jql + ", deleted=" + deleted + "]";
	}
}
