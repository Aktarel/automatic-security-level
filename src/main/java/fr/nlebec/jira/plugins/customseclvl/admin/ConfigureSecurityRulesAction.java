package fr.nlebec.jira.plugins.customseclvl.admin;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.atlassian.core.util.ObjectUtils;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.event.type.EventTypeManager;
import com.atlassian.jira.issue.security.IssueSecurityLevel;
import com.atlassian.jira.issue.security.IssueSecurityLevelManager;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserRole;

import fr.nlebec.jira.plugins.customseclvl.CSLInitializer;
import fr.nlebec.jira.plugins.customseclvl.model.CSLConfiguration;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityLvlJob;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;
import fr.nlebec.jira.plugins.customseclvl.scheduler.ApplySecurityLevel;
import fr.nlebec.jira.plugins.customseclvl.scheduler.impl.ActiveSecurityLevelTask;
import fr.nlebec.jira.plugins.customseclvl.scheduler.impl.ApplySecurityLevelTask;
import fr.nlebec.jira.plugins.customseclvl.scheduler.impl.InactiveSecurityLevelTask;
import fr.nlebec.jira.plugins.customseclvl.scheduler.impl.RemoveSecurityLevelTask;
import fr.nlebec.jira.plugins.customseclvl.service.CSLConfigurationService;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityLevelJobService;

@Scanned
public class ConfigureSecurityRulesAction extends JiraWebActionSupport {

	
	private final Logger LOG = Logger.getLogger(ConfigureSecurityRulesAction.class);
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private CSLConfigurationService configurationService;
    private GlobalPermissionManager globalPermissionManager;
    private final LoginUriProvider loginUriProvider;
    private IssueSecurityLevelManager issueSecurityLevelManager;
    private EventTypeManager eventManager;

    private Collection<IssueSecurityLevel> securityLevels;
    private Collection<EventType> eventTypes;
    
    private List<String> messages;
    private CSLConfiguration configuration;
    private String message;
    private String messageRuleName; 
    private I18nHelper i18n;
	private SecurityLevelJobService securityLevelJobService;

    @Inject
    public ConfigureSecurityRulesAction( CSLConfigurationService configurationService,
                              @ComponentImport GlobalPermissionManager globalPermissionManager,
                              @ComponentImport LoginUriProvider loginUriProvider,
                              @ComponentImport IssueSecurityLevelManager issueSecurityLevelManager,
                              @ComponentImport EventTypeManager eventTypeManager,
                              @ComponentImport I18nHelper i18nHelper,
                              SecurityLevelJobService securityLevelJobService
    		)
    {
    	this.i18n = i18nHelper;
    	this.issueSecurityLevelManager = issueSecurityLevelManager;
        this.configurationService = configurationService;
        this.globalPermissionManager = globalPermissionManager;
        this.loginUriProvider = loginUriProvider;
        this.eventManager = eventTypeManager ; 
        this.securityLevelJobService = securityLevelJobService;
    }

    protected String doExecute() throws Exception {
        ApplicationUser loggedInUser = this.getLoggedInUser();
        if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, loggedInUser)) {
            URI uri = URI.create(this.getHttpRequest().getRequestURI());
            return this.forceRedirect(loginUriProvider.getLoginUriForRole(uri, UserRole.ADMIN).toASCIIString());
        }
        
        this.configuration = configurationService.getConfiguration();
        this.securityLevels = issueSecurityLevelManager.getAllIssueSecurityLevels();
        this.eventTypes = this.eventManager.getEventTypes();
        
        return INPUT;
    }



	public CSLConfiguration getConfiguration() {
		return configurationService.getConfiguration();
	}
	
	public List<SecurityRules> getActivesSecurityRules(){
		return getConfiguration().getActivesSecurityRules();
	}
	public List<SecurityRules> getInactivesSecurityRules(){
		return getConfiguration().getInactivesSecurityRules();
	}
	public List<SecurityRules> getDeletedSecurityRules(){
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
	public String getMessage() {
		String ret = "";
		if(this.message != null){
			if( this.messageRuleName == null ) {
				this.messageRuleName = "Inconnu";
			}
			ret = i18n.getText(this.message,this.messageRuleName);
		}
		return ret;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public String formatDate(ZonedDateTime zdt) {
		String ret = "-";
		if( ObjectUtils.isNotEmpty(zdt) ) {
			ret = zdt.format(CSLInitializer.getDateTimeFormatter());
		}
		return ret;
	}
	public String formatDateAsDefault(ZonedDateTime zdt) {
		String ret = "-";
		if( ObjectUtils.isNotEmpty(zdt) ) {
			ret = zdt.toLocalDate().format(CSLInitializer.getTechnicalDateFormatter());
		}
		return 	ret;
	}
	
	public String formatTime(ZonedDateTime zdt) {
		String ret = "-";
		if( ObjectUtils.isNotEmpty(zdt) ) {
			ret = zdt.toLocalTime().format(CSLInitializer.getTechnicalTimeFormatter());
		}
		return 	ret;
	}
	
	public boolean hasPendingStatus(SecurityRules securityrule) {
		boolean ret = false;
		if( ZonedDateTime.now().isBefore(securityrule.getApplicationDate())) {
			ret = true;
		}
		return ret;
	}

	public String getMessageRuleName() {
		return messageRuleName;
	}

	public void setMessageRuleName(String messageRuleName) {
		this.messageRuleName = messageRuleName;
	}
	
	public String getTooltip(SecurityRules securityrule) {
		
		String ret = "";
		String applicationDate = "" ;
		if (hasPendingStatus(securityrule)) {
			SecurityLvlJob job = this.securityLevelJobService.getPendingJob(securityrule);
			if( securityrule.getApplicationDate() != null) {
				applicationDate = securityrule.getApplicationDate().format(CSLInitializer.getDateTimeFormatter());
			}
			if( InactiveSecurityLevelTask.class.getName().equals(job.getJobName())) {
				ret = i18n.getText("csl.admin.securityrule.pendingjob.inactive.tooltip", applicationDate);
			}
			if( ActiveSecurityLevelTask.class.getName().equals(job.getJobName())) {
				ret = i18n.getText("csl.admin.securityrule.pendingjob.active.tooltip", applicationDate);
			}
			if( RemoveSecurityLevelTask.class.getName().equals(job.getJobName())) {
				ret = i18n.getText("csl.admin.securityrule.pendingjob.delete.tooltip", applicationDate);
			}
			if( ApplySecurityLevelTask.class.getName().equals(job.getJobName())) {
				ret = i18n.getText("csl.admin.securityrule.pendingjob.apply.tooltip", applicationDate);
			}
		}
		
		return ret;
		
	}
}


