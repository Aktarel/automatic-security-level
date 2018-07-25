package fr.nlebec.jira.plugins.customseclvl.model;

import java.util.List;

public class CSLConfiguration {

	private Integer id;
	
	private List<SecurityRules> activesSecurityRules;
	
	private List<SecurityRules> deletedSecurityRules;
	
	private List<SecurityRules> inactivesSecurityRules;

	private String dateFormat;

	private String layout;
	
	private Boolean active;
	
	private Boolean silent;
	
    public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public List<SecurityRules> getActivesSecurityRules() {
		return activesSecurityRules;
	}

	public void setActivesSecurityRules(List<SecurityRules> activesSecurityRules) {
		this.activesSecurityRules = activesSecurityRules;
	}

	public List<SecurityRules> getDeletedSecurityRules() {
		return deletedSecurityRules;
	}

	public void setDeletedSecurityRules(List<SecurityRules> deletedSecurityRules) {
		this.deletedSecurityRules = deletedSecurityRules;
	}

	public List<SecurityRules> getInactivesSecurityRules() {
		return inactivesSecurityRules;
	}

	public void setInactivesSecurityRules(List<SecurityRules> inactivesSecurityRules) {
		this.inactivesSecurityRules = inactivesSecurityRules;
	}

	public Boolean getSilent() {
		return silent;
	}

	public void setSilent(Boolean silent) {
		this.silent = silent;
	}
	
}
