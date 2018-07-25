package fr.nlebec.jira.plugins.customseclvl.admin;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.event.type.EventTypeManager;
import com.atlassian.jira.issue.security.IssueSecurityLevel;
import com.atlassian.jira.issue.security.IssueSecurityLevelManager;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserRole;

import fr.nlebec.jira.plugins.customseclvl.model.CSLConfiguration;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;
import fr.nlebec.jira.plugins.customseclvl.service.CSLConfigurationService;

@Scanned
public class ConfigureCSLAction extends JiraWebActionSupport {

	private final Logger LOG = Logger.getLogger(ConfigureCSLAction.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CSLConfigurationService CSLConfigurationService;
	private GlobalPermissionManager globalPermissionManager;
	private final LoginUriProvider loginUriProvider;
	private IssueSecurityLevelManager issueSecurityLevelManager;
	private EventTypeManager eventManager;

	private Boolean active;
	private String dateFormat;
	private String layout;
	private Boolean silent;
	private String message;

	private Collection<IssueSecurityLevel> securityLevels;
	private Collection<EventType> eventTypes;
	private CSLConfiguration configuration;

	@Inject
	public ConfigureCSLAction(CSLConfigurationService CSLConfigurationService,
			@ComponentImport GlobalPermissionManager globalPermissionManager,
			@ComponentImport LoginUriProvider loginUriProvider,
			@ComponentImport IssueSecurityLevelManager issueSecurityLevelManager,
			@ComponentImport EventTypeManager eventTypeManager) {
		this.issueSecurityLevelManager = issueSecurityLevelManager;
		this.CSLConfigurationService = CSLConfigurationService;
		this.globalPermissionManager = globalPermissionManager;
		this.loginUriProvider = loginUriProvider;
		this.eventManager = eventTypeManager;

	}

	protected String doExecute() throws Exception {
		ApplicationUser loggedInUser = this.getLoggedInUser();
		if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, loggedInUser)) {
			URI uri = URI.create(this.getHttpRequest().getRequestURI());
			return this.forceRedirect(loginUriProvider.getLoginUriForRole(uri, UserRole.ADMIN).toASCIIString());
		}

		this.configuration = CSLConfigurationService.getConfiguration();
		this.securityLevels = issueSecurityLevelManager.getAllIssueSecurityLevels();
		this.eventTypes = this.eventManager.getEventTypes();

		return INPUT;
	}

	public String doSave() throws Exception {
		ApplicationUser loggedInUser = this.getLoggedInUser();
		if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, loggedInUser)) {
			URI uri = URI.create(this.getHttpRequest().getRequestURI());
			return this.forceRedirect(loginUriProvider.getLoginUriForRole(uri, UserRole.ADMIN).toASCIIString());
		}
		// Faire controles
		if (LOG.isDebugEnabled()) {
			LOG.debug("Active : " + active);
			LOG.debug("dateFormat :" + dateFormat);
			LOG.debug("Layout :" + layout);
			LOG.debug("Silent :" + silent);
		}
		this.CSLConfigurationService.updateConfiguration(getActive(), getDateFormat(), getLayout(), getSilent());
		this.securityLevels = issueSecurityLevelManager.getAllIssueSecurityLevels();

		return INPUT;
	}

	public Boolean getActive() {
		if (active == null) {
			active = Boolean.FALSE;
		}
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public CSLConfiguration getConfiguration() {
		return CSLConfigurationService.getConfiguration();
	}

	public List<SecurityRules> getActivesSecurityRules() {
		return getConfiguration().getActivesSecurityRules();
	}

	public List<SecurityRules> getInactivesSecurityRules() {
		return getConfiguration().getInactivesSecurityRules();
	}

	public List<SecurityRules> getDeletedSecurityRules() {
		return getConfiguration().getDeletedSecurityRules();
	}

	public Collection<IssueSecurityLevel> getLevelList() {
		return securityLevels;
	}

	public Collection<EventType> getEventTypes() {
		return eventTypes;
	}

	public void setEventTypes(Collection<EventType> eventTypes) {
		this.eventTypes = eventTypes;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getLayout() {
		if (layout == null) {
			layout = "tab";
		}
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public Boolean getSilent() {
		if (silent == null) {
			silent = Boolean.FALSE;
		}
		return silent;
	}

	public void setSilent(Boolean silent) {
		this.silent = silent;
	}

}
