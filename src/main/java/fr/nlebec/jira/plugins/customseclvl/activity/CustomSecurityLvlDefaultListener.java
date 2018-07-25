package fr.nlebec.jira.plugins.customseclvl.activity;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.ofbiz.core.entity.GenericEntityException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import fr.nlebec.jira.plugins.customseclvl.model.CSLConfiguration;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleApplicationService;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleService;
import fr.nlebec.jira.plugins.customseclvl.util.EventUtil;

@Named
public class CustomSecurityLvlDefaultListener implements InitializingBean, DisposableBean {

	private final Logger LOG = Logger.getLogger(CustomSecurityLvlDefaultListener.class);

	@ComponentImport
	public EventPublisher eventPublisher;

	public SecurityRuleService securityRuleService;

	public SecurityRuleApplicationService securityRuleApplicationManager;

	@Inject
	public CustomSecurityLvlDefaultListener(EventPublisher eventPublisher, SecurityRuleService securityRuleService,
			SecurityRuleApplicationService securityRuleApplicationManager) {
		this.eventPublisher = eventPublisher;
		this.securityRuleService = securityRuleService;
		this.securityRuleApplicationManager = securityRuleApplicationManager;
	}

	@EventListener
	public void onIssueEvent(IssueEvent event) throws GenericEntityException {
		CSLConfiguration config = securityRuleService.getConfiguration();
		LOG.info("Is plugin active : " + config.getActive().booleanValue());
		if (Boolean.TRUE.equals(config.getActive())) {
			for (SecurityRules securityRule : config.getActivesSecurityRules()) {
				// TODO:remove following by selecting the whole
				if (EventUtil.contains(securityRule.getEvents(), event.getEventTypeId())) {
					securityRuleApplicationManager.applyRule(securityRule, event.getIssue().getKey());
				}
			}
		}
	}

	public void destroy() throws Exception {
		this.eventPublisher.unregister(this);
	}

	public void afterPropertiesSet() throws Exception {
		this.eventPublisher.register(this);
	}
}
