package fr.nlebec.jira.plugins.customseclvl.model;

import java.util.Date;

public class AuditLog {

	private String authorName;
	
	private Date eventDate;
	
	private Integer idSecurityRule;
	
	private String message;

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public Integer getIdSecurityRule() {
		return idSecurityRule;
	}

	public void setIdSecurityRule(Integer idSecurityRule) {
		this.idSecurityRule = idSecurityRule;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
