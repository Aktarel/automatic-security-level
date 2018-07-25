package it.fr.nlebec.jira.plugins.customseclvl.wired;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.plugins.osgi.test.AtlassianPluginsTestRunner;
import com.atlassian.sal.api.ApplicationProperties;

import fr.nlebec.jira.plugins.customseclvl.service.impl.DefaultSecurityRuleService;

@RunWith(AtlassianPluginsTestRunner.class)
public class SecurityServiceWiredTest
{
    private final ApplicationProperties applicationProperties;
    private DefaultSecurityRuleService securityRuleService ;

    public SecurityServiceWiredTest(ApplicationProperties applicationProperties,DefaultSecurityRuleService myPluginComponent)
    {
        this.applicationProperties = applicationProperties;
        this.securityRuleService = securityRuleService;
    }

    @Ignore
    @Test
    public void testMyName()
    {
        assertEquals(1.0, 1.0);
    }
}