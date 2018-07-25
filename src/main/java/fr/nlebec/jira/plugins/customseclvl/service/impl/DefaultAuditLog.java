package fr.nlebec.jira.plugins.customseclvl.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import fr.nlebec.jira.plugins.customseclvl.ao.converters.ItemConverter;
import fr.nlebec.jira.plugins.customseclvl.ao.model.AuditLogAO;
import fr.nlebec.jira.plugins.customseclvl.service.AuditLogService;

@Named(value="auditLogService")
public class DefaultAuditLog implements AuditLogService {

	private final static Logger LOG = Logger.getLogger(DefaultAuditLog.class);

	public ActiveObjects persistenceManager;

	@Inject
	public DefaultAuditLog( 	@ComponentImport ActiveObjects persistenceManager) {
		this.persistenceManager = checkNotNull(persistenceManager);
	}

	public void addAuditLog(String message, ApplicationUser user, int idSecurityRule) {
		LOG.debug("Add audit log");
		AuditLogAO auditLog = persistenceManager.create(AuditLogAO.class);
		auditLog.setMessage(message);
		auditLog.setAuthor(user.getDisplayName());
		auditLog.setEventDate(new Date());
		auditLog.setIdSecurityRule(idSecurityRule);
		auditLog.save();
	}
	
	public List<fr.nlebec.jira.plugins.customseclvl.model.AuditLog> getAuditLog() {
		LOG.debug("Get all logs");
		AuditLogAO[] auditLogAo = persistenceManager.find(AuditLogAO.class);
		List<fr.nlebec.jira.plugins.customseclvl.model.AuditLog> auditLogs = new ArrayList<>();
		
		for (int i = 0; i < auditLogAo.length; i++) {
			auditLogs.add(ItemConverter.convertActiveObjectToPOJO(auditLogAo[i]));
		}
		return auditLogs;
	}
	
}