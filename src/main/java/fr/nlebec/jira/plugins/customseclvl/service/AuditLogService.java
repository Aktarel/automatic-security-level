package fr.nlebec.jira.plugins.customseclvl.service;

import java.util.List;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;

@Scanned
public interface AuditLogService {
	
	public void addAuditLog(String message, ApplicationUser user, int idSecurityRule);
	public List<fr.nlebec.jira.plugins.customseclvl.model.AuditLog> getAuditLog();
}
	
