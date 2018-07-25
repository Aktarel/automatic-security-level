package fr.nlebec.jira.plugins.customseclvl.model.response;

public class AddSecurityRuleResponse extends CSLResponse {

	public String location;
	
	public AddSecurityRuleResponse() {
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
