package fr.nlebec.jira.plugins.customseclvl.model.response;

public class CancelPendingActionSecurityRuleResponse extends CSLResponse {

	public String location;
	
	public CancelPendingActionSecurityRuleResponse() {
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
