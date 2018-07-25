package fr.nlebec.jira.plugins.customseclvl.model.request;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import fr.nlebec.jira.plugins.customseclvl.CSLInitializer;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class AddSecurityRuleRequestBody {

	@XmlElement
	private Boolean active;

	@XmlElement
	private List<Long> events;
	
	@XmlElement
	private String ruleName;
	
	@XmlElement
	private String jql;
	
	@XmlElement(required=false)
	private String applicationDate;

	@XmlElement
	private Long securityLvl;
	
	@XmlElement
	private Integer priority;
	
	public List<Long> getEvents() {
		return events;
	}
	public void setEvents(List<Long> events) {
		this.events = events;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getJql() {
		return jql;
	}
	public void setJql(String jql) {
		this.jql = jql;
	}
	public Long getSecurityLvl() {
		return securityLvl;
	}
	public void setSecurityLvl(Long securityLvl) {
		this.securityLvl = securityLvl;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	} 
	
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public ZonedDateTime getApplicationDateAsInstant()  {
		return ZonedDateTime.parse(applicationDate, CSLInitializer.getDefaultDateTimeFormatter());
	}
	
	public String getApplicationDate() {
		return applicationDate;
	}
	public void setApplicationDate(String applicationDate) {
		this.applicationDate = applicationDate;
	}
}
