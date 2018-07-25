package fr.nlebec.jira.plugins.customseclvl.service.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.UpdateIssueRequest;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.issue.security.IssueSecurityLevelManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.thirdparty.api.Activity;
import com.atlassian.streams.thirdparty.api.ActivityQuery;
import com.atlassian.streams.thirdparty.api.ActivityService;

import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleApplicationService;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleService;

@Named(value="securityRuleApplicationService")
public class SecurityRuleApplicationManager implements SecurityRuleApplicationService{

	@ComponentImport
	public UserManager userManager;
	@ComponentImport
	public IssueManager issueManager;
	@ComponentImport
	public SearchService searchService;
	@ComponentImport
	public EventPublisher eventPublisher;
	@ComponentImport
	public IssueSecurityLevelManager issueSecurityLevelManager;
	
	public DefaultCSLConfiguration configurationService;

	public SecurityRuleService securityRuleService;

	private final Logger LOG = Logger.getLogger(SecurityRuleApplicationManager.class);

	@ComponentImport
	private ActivityService activityService; 
	
	@Inject
	public SecurityRuleApplicationManager(EventPublisher eventPublisher, IssueManager issueManager,
			UserManager userManager, SearchService searchService, SecurityRuleService securityRuleService,
			IssueSecurityLevelManager issueSecurityLevelManager,DefaultCSLConfiguration configurationService,ActivityService activityService) {
		this.eventPublisher = eventPublisher;
		this.issueManager = issueManager;
		this.searchService = searchService;
		this.userManager = userManager;
		this.issueSecurityLevelManager = issueSecurityLevelManager;
		this.securityRuleService = securityRuleService;
		this.configurationService = configurationService; 
		this.activityService=activityService;

	}

	public void removeRuleOnWholeStock(SecurityRules securityRule) {
		this.applyOrRemoveRuleOnJQLExp(securityRule, securityRule.getJql(), true);
	}
	
	public void applyRuleOnWholeStock(SecurityRules securityRule) {
		this.applyOrRemoveRuleOnJQLExp(securityRule, securityRule.getJql(), false);
	}

	public void applyRule(SecurityRules securityRule, String issueKey) {
		String jqlQuery = securityRule.getJql() + " AND key = " + issueKey;
		this.applyOrRemoveRuleOnJQLExp(securityRule, jqlQuery, false);

	}

	private void applyOrRemoveRuleOnJQLExp(SecurityRules securityRule, String jqlQuery, boolean removeJiraSecurityLevel) {
		boolean appliedSecurityLevel = false;
		ApplicationUser createdUser = securityRule.getCreationUser();
		final SearchService.ParseResult parseResult = searchService.parseQuery(createdUser, jqlQuery);
		if (parseResult.isValid()) {
			try {
				final SearchResults results = searchService.search(createdUser, parseResult.getQuery(),
						com.atlassian.jira.web.bean.PagerFilter.getUnlimitedFilter());
				final List<Issue> issues = results.getIssues();
				for (Issue issue : issues) {
					MutableIssue mi = issueManager.getIssueByCurrentKey(issue.getKey());
					if (issueSecurityLevelManager.getSecurityLevel(securityRule.getJiraSecurityId()) != null) {
						LOG.info("Apply security level : " + securityRule.getJiraSecurityId() + " on issue "
								+ mi.getKey());
						Long securityLvl = securityRule.getJiraSecurityId();
						if( removeJiraSecurityLevel ) {
							securityLvl = null;
						}
						mi.setSecurityLevelId(securityLvl );
					} else {
						LOG.info("Security level does not exist anymore : " + securityRule.getJiraSecurityId());
						LOG.info("No security level has been applied");
					}
					if(Boolean.TRUE.equals(this.configurationService.getConfiguration().getSilent())) {
						LOG.debug("No history metadata for this modification - see event log");
						UpdateIssueRequest updateIssueReq = UpdateIssueRequest.builder().eventDispatchOption(EventDispatchOption.DO_NOT_DISPATCH).sendMail(false).historyMetadata(null).build();
						issueManager.updateIssue(createdUser, mi, updateIssueReq);
						for (Activity activity : activityService.activities(ActivityQuery.builder().addEntityFilter("key", mi.getKey()).startDate(Option.option(new Date())).build())) {
							activityService.delete(activity.getActivityId().get());
						}
					}
					else {
						issueManager.updateIssue(createdUser, mi, EventDispatchOption.DO_NOT_DISPATCH,false);
					}
				}
			} catch (SearchException e) {
				LOG.error("Error running search", e);
			}
		} else {
			LOG.error("Error parsing jqlQuery: " + parseResult.getErrors());
		}
	}

}
