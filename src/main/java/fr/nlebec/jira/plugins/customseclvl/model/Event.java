package fr.nlebec.jira.plugins.customseclvl.model;

import java.io.Serializable;

public class Event implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long jiraEventId;
	
	private String jiraEventName;

	public Long getJiraEventId() {
		return jiraEventId;
	}

	public void setJiraEventId(Long jiraEventId) {
		this.jiraEventId = jiraEventId;
	}

	public String getJiraEventName() {
		return jiraEventName;
	}

	public void setJiraEventName(String jiraEventName) {
		this.jiraEventName = jiraEventName;
	}

	@Override
	public String toString() {
		return "Event [jiraEventId=" + jiraEventId + ", jiraEventName=" + jiraEventName + "]";
	}
	
	

}
