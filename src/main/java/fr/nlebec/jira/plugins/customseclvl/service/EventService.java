package fr.nlebec.jira.plugins.customseclvl.service;

import java.sql.SQLException;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;

import fr.nlebec.jira.plugins.customseclvl.ao.model.EventAO;
import fr.nlebec.jira.plugins.customseclvl.ao.model.SecurityRuleAO;
import fr.nlebec.jira.plugins.customseclvl.model.CSLConfiguration;
import fr.nlebec.jira.plugins.customseclvl.model.Event;

@Scanned
public interface EventService {


	public CSLConfiguration getConfiguration();
    public void addEvent(Event event, SecurityRuleAO srao) throws SQLException;    
    public void deleteEvent(EventAO eventAo, SecurityRuleAO srao);
    
}
