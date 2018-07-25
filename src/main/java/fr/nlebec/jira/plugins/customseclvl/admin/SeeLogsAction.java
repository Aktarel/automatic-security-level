package fr.nlebec.jira.plugins.customseclvl.admin;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserRole;
import com.google.common.collect.Lists;

import fr.nlebec.jira.plugins.customseclvl.model.AuditLog;
import fr.nlebec.jira.plugins.customseclvl.service.AuditLogService;
import fr.nlebec.jira.plugins.customseclvl.service.impl.DefaultAuditLog;

@Scanned
public class SeeLogsAction extends JiraWebActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(SeeLogsAction.class);
	
	private GlobalPermissionManager globalPermissionManager;
	private final LoginUriProvider loginUriProvider;
	private AuditLogService auditLogService;
	private List<AuditLog> auditLogs;

	@Inject
	public SeeLogsAction(@ComponentImport GlobalPermissionManager globalPermissionManager,
			@ComponentImport LoginUriProvider loginUriProvider, AuditLogService auditLogService) {
		this.auditLogService = auditLogService;
		this.globalPermissionManager = globalPermissionManager;
		this.loginUriProvider = loginUriProvider;

	}

	protected String doExecute() throws Exception {
		ApplicationUser loggedInUser = this.getLoggedInUser();
		if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, loggedInUser)) {
			URI uri = URI.create(this.getHttpRequest().getRequestURI());
			return this.forceRedirect(loginUriProvider.getLoginUriForRole(uri, UserRole.ADMIN).toASCIIString());
		}

		this.setAuditLogs(Lists.newArrayList(this.auditLogService.getAuditLog()));
		return INPUT;
	}

	public List<AuditLog> getAllAuditLog() {
		return auditLogs;
	}

	public void setAuditLogs(List<AuditLog> auditLogs) {
		this.auditLogs = auditLogs;
	}

}
