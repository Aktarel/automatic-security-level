package fr.nlebec.jira.plugins.customseclvl.model.response;

public class RetrieveSecurityRuleResponse extends CSLResponse {

	public SecurityRuleResponse securityRule;
	
	public RetrieveSecurityRuleResponse() {
	}

	public SecurityRuleResponse getSecurityRule() {
		return securityRule;
	}

	public void setSecurityRule(SecurityRuleResponse securityRule) {
		this.securityRule = securityRule;
	}


}
