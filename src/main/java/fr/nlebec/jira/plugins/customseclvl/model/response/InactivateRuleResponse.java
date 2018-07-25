package fr.nlebec.jira.plugins.customseclvl.model.response;

public class InactivateRuleResponse extends CSLResponse {

	public String location;
	
	public InactivateRuleResponse() {
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
