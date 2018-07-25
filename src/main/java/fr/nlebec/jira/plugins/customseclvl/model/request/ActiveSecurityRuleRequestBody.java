package fr.nlebec.jira.plugins.customseclvl.model.request;

import java.time.ZonedDateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import fr.nlebec.jira.plugins.customseclvl.CSLInitializer;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class ActiveSecurityRuleRequestBody {

	@XmlElement(name="id")
	private Integer idSecurityRule;
	
	public Integer getIdSecurityRule() {
		return idSecurityRule;
	}

	public void setIdSecurityRule(Integer idSecurityRule) {
		this.idSecurityRule = idSecurityRule;
	}
	@XmlElement(name="applicationDate")
	private String applicationDate;

	public String getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(String applicationDate) {
		this.applicationDate = applicationDate;
	}
	public ZonedDateTime getApplicationDateAsZoneDateTime() {
		return ZonedDateTime.parse(applicationDate, CSLInitializer.getDefaultDateTimeFormatter());
	}
	
}
