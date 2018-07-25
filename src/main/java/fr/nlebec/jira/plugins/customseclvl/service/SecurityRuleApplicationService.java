package fr.nlebec.jira.plugins.customseclvl.service;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;

import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;

@Scanned
public interface SecurityRuleApplicationService {

	public void removeRuleOnWholeStock(SecurityRules securityRule);	
	public void applyRuleOnWholeStock(SecurityRules securityRule);
	public void applyRule(SecurityRules securityRule, String issueKey);
	
}
