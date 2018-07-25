package fr.nlebec.jira.plugins.customseclvl.rest;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.ofbiz.core.entity.GenericEntityException;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.security.IssueSecurityLevel;
import com.atlassian.jira.issue.security.IssueSecurityLevelManager;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobId;

import fr.nlebec.jira.plugins.customseclvl.CSLInitializer;
import fr.nlebec.jira.plugins.customseclvl.ao.converters.ItemConverter;
import fr.nlebec.jira.plugins.customseclvl.model.SecurityRules;
import fr.nlebec.jira.plugins.customseclvl.model.request.ActiveSecurityRuleRequestBody;
import fr.nlebec.jira.plugins.customseclvl.model.request.AddSecurityRuleRequestBody;
import fr.nlebec.jira.plugins.customseclvl.model.request.CancelPendingActionRequestBody;
import fr.nlebec.jira.plugins.customseclvl.model.request.DeleteSecurityRuleRequestBody;
import fr.nlebec.jira.plugins.customseclvl.model.request.InactiveSecurityRuleRequestBody;
import fr.nlebec.jira.plugins.customseclvl.model.request.UpdateSecurityRuleRequestBody;
import fr.nlebec.jira.plugins.customseclvl.model.response.AddSecurityRuleResponse;
import fr.nlebec.jira.plugins.customseclvl.model.response.CancelPendingActionSecurityRuleResponse;
import fr.nlebec.jira.plugins.customseclvl.model.response.DeleteSecurityRuleResponse;
import fr.nlebec.jira.plugins.customseclvl.model.response.InactivateRuleResponse;
import fr.nlebec.jira.plugins.customseclvl.model.response.RetrieveSecurityRuleResponse;
import fr.nlebec.jira.plugins.customseclvl.model.response.SecurityRuleResponse;
import fr.nlebec.jira.plugins.customseclvl.model.response.UpdateSecurityRuleResponse;
import fr.nlebec.jira.plugins.customseclvl.scheduler.impl.DefaultCSLJobRunner;
import fr.nlebec.jira.plugins.customseclvl.service.AuditLogService;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityLevelJobService;
import fr.nlebec.jira.plugins.customseclvl.service.SecurityRuleService;

@Path("/security-rule")
@Scanned
public class SecurityRuleRestController {
	private final Logger log = Logger.getLogger(this.getClass());
	private final UserManager userManager;
	private final GlobalPermissionManager globalPermissionManager;
	private final SecurityRuleService securityRuleService;
	private final SecurityLevelJobService securityLevelJobService;
	private final I18nHelper i18nHelper;
	private SearchService searchService;
	private final DefaultCSLJobRunner CSLDefaultJobRunner;
	private IssueSecurityLevelManager issueSecurityManager;
	private IssueManager issueManager;
	private AuditLogService auditLog;

	@Inject
	public SecurityRuleRestController(@ComponentImport IssueManager issueManager,
			@ComponentImport UserManager userManager, @ComponentImport IssueSecurityLevelManager issueSecurityManager,
			@ComponentImport GlobalPermissionManager globalPermissionManager, @ComponentImport I18nHelper i18nHelper,
			SecurityRuleService securityRuleService, @ComponentImport SearchService searchService,
			DefaultCSLJobRunner CSLDefaultJobRunner, SecurityLevelJobService securityLevelJobService,
			AuditLogService auditLogService) {
		this.userManager = userManager;
		this.globalPermissionManager = globalPermissionManager;
		this.i18nHelper = i18nHelper;
		this.securityRuleService = securityRuleService;
		this.searchService = searchService;
		this.CSLDefaultJobRunner = CSLDefaultJobRunner;
		this.issueSecurityManager = issueSecurityManager;
		this.issueManager = issueManager;
		this.securityLevelJobService = securityLevelJobService;
		this.auditLog = auditLogService;
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{idSecurityRule}")
	public Response getSecurityLevel(@PathParam(value = "idSecurityRule") int idSecurityRule,
			@Context HttpServletRequest request) {
		String userName = request.getRemoteUser();
		ApplicationUser user = this.userManager.getUserByKey(userName);
		RetrieveSecurityRuleResponse response = new RetrieveSecurityRuleResponse();
		int errorCode = 200;
		if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, user)) {
			response.setError(this.i18nHelper.getText("fr.csl.admin.error.unauthorized"));
		} else {
			try {
				SecurityRuleResponse resp = ItemConverter
						.pojoToResponse(this.securityRuleService.getSecurityRule(idSecurityRule));
				response.setSecurityRule(resp);
			} catch (ValidationException e) {
				errorCode = 400;
				response.setError(e.getMessage());
			}
		}
		return Response.status(errorCode).entity(response).build();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/")
	public Response addSecurityLevel(AddSecurityRuleRequestBody ruleBody, @Context HttpServletRequest request) {
		String userName = request.getRemoteUser();
		ApplicationUser user = this.userManager.getUserByKey(userName);
		AddSecurityRuleResponse response = new AddSecurityRuleResponse();
		int errorCode = 201;
		if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, user)) {
			response.setError(this.i18nHelper.getText("fr.csl.admin.error.unauthorized"));
		} else {
			try {
				checkParameters(ruleBody, user);
				SecurityRules securityRule = ItemConverter.bodyToPojo(ruleBody, user);
				int idEntity = this.securityRuleService.addSecurityRule(securityRule);
				if (Boolean.TRUE.equals(ruleBody.getActive() && ruleBody.getApplicationDateAsInstant() != null)) {
					CSLDefaultJobRunner.addSecurityLevelJob(idEntity, ruleBody.getApplicationDateAsInstant());
				}
				response.setLocation(request.getRequestURI() + "/" + idEntity);
				this.auditLog.addAuditLog("addSecurityLevel : " + securityRule.getName(), user, idEntity);
			} catch (ValidationException e) {
				errorCode = 400;
				response.setError(e.getMessage());
			} catch (SchedulerServiceException | SQLException e) {
				errorCode = 500;
				response.setError(e.getMessage());
			}
		}
		return Response.status(errorCode).entity(response).build();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/unactivate")
	public Response disableSecurityRule(InactiveSecurityRuleRequestBody body, @Context HttpServletRequest request) {
		String userName = request.getRemoteUser();
		ApplicationUser user = this.userManager.getUserByKey(userName);
		InactivateRuleResponse response = new InactivateRuleResponse();
		SecurityRules securityRule = null;
		int errorCode = 201;

		if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, user)) {
			response.setError(this.i18nHelper.getText("fr.csl.admin.error.unauthorized"));
		} else {
			try {
				securityRule = this.securityRuleService.getSecurityRule(body.getIdSecurityRule());
				this.securityRuleService.updateApplicationDate(body.getIdSecurityRule(),
						body.getApplicationDateAsZoneDateTime());
				if (body.getApplicationDate() != null) {
					CSLDefaultJobRunner.disableSecurityLevelJob(body.getIdSecurityRule(),
							body.getApplicationDateAsZoneDateTime());
				}
				this.auditLog.addAuditLog("disableSecurityRule : " + securityRule.getName(), user,
						securityRule.getId());
			} catch (ValidationException e) {
				errorCode = 400;
				response.setError(e.getMessage());
			} catch (SchedulerServiceException e) {
				errorCode = 500;
				response.setError(e.getMessage());
			}
		}

		return Response.status(errorCode).entity(response).build();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/activate")
	public Response activateSecurityRule(ActiveSecurityRuleRequestBody body, @Context HttpServletRequest request) {
		String userName = request.getRemoteUser();
		ApplicationUser user = this.userManager.getUserByKey(userName);
		InactivateRuleResponse response = new InactivateRuleResponse();
		SecurityRules securityRule = null;
		int errorCode = 201;

		if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, user)) {
			response.setError(this.i18nHelper.getText("fr.csl.admin.error.unauthorized"));
		} else {
			try {
				securityRule = this.securityRuleService.getSecurityRule(body.getIdSecurityRule());
				this.securityRuleService.updateApplicationDate(body.getIdSecurityRule(),
						body.getApplicationDateAsZoneDateTime());
				if (body.getApplicationDate() != null) {
					CSLDefaultJobRunner.activateSecurityLevelJob(body.getIdSecurityRule(),
							body.getApplicationDateAsZoneDateTime());
				}
				this.auditLog.addAuditLog("activateSecurityRule : " + securityRule.getName(), user,
						securityRule.getId());
			} catch (ValidationException e) {
				errorCode = 400;
				response.setError(e.getMessage());
			} catch (SchedulerServiceException e) {
				errorCode = 500;
				response.setError(e.getMessage());
			}
		}

		return Response.status(errorCode).entity(response).build();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/cancel")
	public Response cancelPendingAction(CancelPendingActionRequestBody body, @Context HttpServletRequest request) {
		String userName = request.getRemoteUser();
		ApplicationUser user = this.userManager.getUserByKey(userName);
		CancelPendingActionSecurityRuleResponse response = new CancelPendingActionSecurityRuleResponse();
		SecurityRules securityRule = null;
		int errorCode = 201;

		if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, user)) {
			response.setError(this.i18nHelper.getText("fr.csl.admin.error.unauthorized"));
		} else {
			try {
				securityRule = this.securityRuleService.getSecurityRule(body.getIdSecurityRule());
				JobId jobid = securityLevelJobService.deleteJobEntry(body.getIdSecurityRule());
				CSLDefaultJobRunner.removeTaskFromScheduler(jobid);
				securityRuleService.updateApplicationDate(body.getIdSecurityRule(), ZonedDateTime.now());
				this.auditLog.addAuditLog("cancelPendingAction : " + securityRule.getName(), user,
						securityRule.getId());
			} catch (ValidationException e) {
				errorCode = 400;
				response.setError(e.getMessage());
			}
		}
		return Response.status(errorCode).entity(response).build();
	}

	@DELETE
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/")
	public Response deleteSecurityLevel(DeleteSecurityRuleRequestBody body, @Context HttpServletRequest request) {
		String userName = request.getRemoteUser();
		ApplicationUser user = this.userManager.getUserByKey(userName);
		DeleteSecurityRuleResponse response = new DeleteSecurityRuleResponse();
		int errorCode = 201;
		SecurityRules securityRule = null;

		if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, user)) {
			response.setError(this.i18nHelper.getText("fr.csl.admin.error.unauthorized"));
		} else {
			try {
				// checkParameters(body);
				securityRule = this.securityRuleService.getSecurityRule(body.getIdSecurityRuleToDelete());
				this.securityRuleService.updateApplicationDate(body.getIdSecurityRuleToDelete(),
						body.getApplicationDateAsZoneDateTime());
				if (body.getApplicationDate() != null) {
					CSLDefaultJobRunner.removeSecurityLevelJob(body.getIdSecurityRuleToDelete(),
							body.getApplicationDateAsZoneDateTime());
				}
				this.auditLog.addAuditLog("deleteSecurityLevel : " + securityRule.getName(), user,
						securityRule.getId());
			} catch (ValidationException e) {
				errorCode = 400;
				response.setError(e.getMessage());
			} catch (SchedulerServiceException e) {
				errorCode = 500;
				response.setError(e.getMessage());
			}
		}
		return Response.status(errorCode).entity(response).build();
	}

	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/")
	public Response updateSecurityLevel(UpdateSecurityRuleRequestBody body, @Context HttpServletRequest request)
			throws SQLException {
		String userName = request.getRemoteUser();
		ApplicationUser user = this.userManager.getUserByKey(userName);
		UpdateSecurityRuleResponse response = new UpdateSecurityRuleResponse();
		int errorCode = 201;
		SecurityRules securityRule = null;

		if (!this.globalPermissionManager.hasPermission(GlobalPermissionKey.ADMINISTER, user)) {
			response.setError(this.i18nHelper.getText("fr.csl.admin.error.unauthorized"));
		} else {
			try {
				securityRule = this.securityRuleService.getSecurityRule(body.getId());
				checkParameters(body, user);
				this.securityRuleService.updateSecurityRule(ItemConverter.bodyToPojo(body, user));
				response.setLocation(request.getRequestURI() + "/" + body.getId());
				this.auditLog.addAuditLog("updateSecurityLevel : " + securityRule.getName(), user,
						securityRule.getId());
			} catch (SQLException e) {
				errorCode = 500;
				response.setError(e.getMessage());
			} catch (ValidationException e) {
				errorCode = 400;
				response.setError(e.getMessage());
			}
		}

		return Response.status(errorCode).entity(response).build();
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/isl")
	public Response hasRight(@Context HttpServletRequest request) throws SQLException {
		String userName = request.getRemoteUser();
		ApplicationUser user = this.userManager.getUserByKey(userName);
		UpdateSecurityRuleResponse response = new UpdateSecurityRuleResponse();
		int errorCode = 201;
		Collection<IssueSecurityLevel> isl = this.issueSecurityManager.getAllSecurityLevelsForUser(user);
		if (user != null) {
			System.out.println(user.toString());
		} else {
			System.out.println("Utilsiateur Null!");
		}
		System.out.println("Test si permissions pour cet utilisateur : " + userName);
		for (IssueSecurityLevel issueSecurityLevel : isl) {
			System.out.println(issueSecurityLevel.getId() + issueSecurityLevel.getName());
		}

		return Response.status(errorCode).entity(response).build();
	}

	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/isl/{issueKey}")
	public Response canSeeIssue(@PathParam(value = "issueKey") String issueKey, @Context HttpServletRequest request)
			throws SQLException, GenericEntityException, DataAccessException {
		String userName = request.getRemoteUser();
		ApplicationUser user = this.userManager.getUserByKey(userName);
		UpdateSecurityRuleResponse response = new UpdateSecurityRuleResponse();
		int errorCode = 200;
		if (this.issueManager.isExistingIssueKey(issueKey)) {
			MutableIssue mi = this.issueManager.getIssueByCurrentKey(issueKey);
			Long isl = mi.getSecurityLevelId();
			IssueSecurityLevel finalIsl = issueSecurityManager.getSecurityLevel(isl);
			PermissionManager pm = ComponentAccessor.getPermissionManager();

			if (pm.hasPermission(ProjectPermissions.CREATE_ISSUES, mi, user)) {
				System.out.println("Utilisateur habilité !");
			} else {
				System.out.println("Utilisateur non habilité !");
			}
		}
		return Response.status(errorCode).entity(response).build();
	}

	private void checkParameters(AddSecurityRuleRequestBody body, ApplicationUser user) {
		final SearchService.ParseResult parseResult = searchService.parseQuery(user, body.getJql());

		if (body.getActive() == null) {
			throw new ValidationException("Parametre active n'est pas valide");
		}
		if (body.getRuleName() == null || StringUtils.isEmpty(body.getRuleName())) {
			throw new ValidationException("Le nom de la règle est vide");
		}
		if (body.getJql() == null || StringUtils.isEmpty(body.getJql())) {
			throw new ValidationException("Le JQL est vide ou invalide");
		}
		if (body.getEvents() == null || body.getEvents().size() == 0) {
			throw new ValidationException("Au moins un evenement doit être définis");
		}
		if (body.getPriority() == null) {
			throw new ValidationException("Parametre prorité n'est pas valide");
		}
		if (!StringUtils.isEmpty(body.getApplicationDate())) {
			try {
				ZonedDateTime.parse(body.getApplicationDate(), CSLInitializer.getDefaultDateTimeFormatter());
			} catch (DateTimeParseException e) {
				throw new ValidationException("La date d'application n'est pas au format valide");
			}
		}
		try {
			MessageSet msgSet = searchService.validateQuery(user, parseResult.getQuery());
			if (msgSet.hasAnyErrors()) {
				StringBuilder sb = new StringBuilder();
				for (String msg : msgSet.getErrorMessages()) {
					sb.append(msg);
					sb.append("\t\n");
				}
				throw new ValidationException(sb.toString());
			}
		} catch (Exception e) {
			throw new ValidationException(e.getMessage());
		}

	}

	private void checkParameters(UpdateSecurityRuleRequestBody body, ApplicationUser user) {

		if (body.getId() == null) {
			throw new ValidationException("Paramètre ID n'est pas valide");
		}
		if (body.getRuleName() == null || StringUtils.isEmpty(body.getRuleName())) {
			throw new ValidationException("Le nom de la règle est vide");
		}
		if (body.getEvents() == null || body.getEvents().size() == 0) {
			throw new ValidationException("Au moins un evenement doit être définis");
		}

	}

}
