package fr.nlebec.jira.plugins.customseclvl.scheduler;

import javax.inject.Named;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.config.JobRunnerKey;

@Named
public interface ActiveSecurityLevel extends JobRunner
{
    /** Our job runner key */
    JobRunnerKey ACTIVE_SL_JOB = JobRunnerKey.of(ActiveSecurityLevel.class.getName());

    /** Name of the parameter map entry where the ID is stored */
    String ACTIVE_SL_JOB_ID= "ActiveSecurityLevel";
}