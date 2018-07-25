//package fr.nlebec.jira.plugins.customseclvl.scheduler;
//
//import java.time.ZonedDateTime;
//
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
//import com.atlassian.sal.api.lifecycle.LifecycleAware;
//import com.atlassian.scheduler.SchedulerServiceException;
//import com.atlassian.scheduler.config.JobId;
//
//@Service
//public interface CSLDefaultJobRunner extends LifecycleAware {
//
//	public void addSecurityLevelJob(Integer idEntity, ZonedDateTime zonedDateTime) throws SchedulerServiceException;
//
//	public void removeSecurityLevelJob(Integer idSecurityRule, ZonedDateTime applicationDate) throws SchedulerServiceException ;
//	
//	public void disableSecurityLevelJob(Integer idSecurityRule, ZonedDateTime applicationDate) throws SchedulerServiceException ;
//
//	public void activateSecurityLevelJob(Integer idSecurityRule, ZonedDateTime applicationDate) throws SchedulerServiceException ;
//	
//	public void removeTaskFromScheduler(JobId jobid);
//}
