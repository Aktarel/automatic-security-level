package fr.nlebec.jira.plugins.customseclvl.scheduler;

import javax.inject.Named;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.config.JobRunnerKey;

import fr.nlebec.jira.plugins.customseclvl.scheduler.impl.RemoveSecurityLevelTask;

@Named
public interface RemoveSecurityLevel extends JobRunner
{
    /** Our job runner key */
    JobRunnerKey REMOVE_SL_JOB = JobRunnerKey.of(RemoveSecurityLevelTask.class.getName());

    /** Name of the parameter map entry where the ID is stored */
    String REMOVE_SL_JOB_ID= "RemoveSecurityLevel";
}