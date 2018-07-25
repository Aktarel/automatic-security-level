package fr.nlebec.jira.plugins.customseclvl.service;

import java.sql.SQLException;
import java.time.ZonedDateTime;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;

import fr.nlebec.jira.plugins.customseclvl.model.CSLConfiguration;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;

@Scanned
public interface SecurityRuleService {

	public CSLConfiguration getConfiguration();
	public int addSecurityRule(SecurityRules securityRule) throws SQLException;
	public void updateSecurityRule(SecurityRules sr) throws SQLException;
	public void updateApplicationDate(Integer idSecurityRuleToUpdate, ZonedDateTime applicationDate);
	public SecurityRules getSecurityRule(int idSecurityRule);
	
}
