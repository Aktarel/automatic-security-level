package fr.nlebec.jira.plugins.customseclvl.model.request;

import java.time.ZonedDateTime;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import fr.nlebec.jira.plugins.customseclvl.CSLInitializer;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class UpdateSecurityRuleRequestBody {
	
	@XmlElement
	private Integer id;

	@XmlElement
	private List<Long> events;
	
	@XmlElement
	private String ruleName;
	
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
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	} 
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
}
