package fr.nlebec.jira.plugins.customseclvl.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.querydsl.core.QueryResults;

import fr.nlebec.jira.plugins.customseclvl.ao.converters.ItemConverter;
import fr.nlebec.jira.plugins.customseclvl.ao.model.CSLConfigurationAO;
import fr.nlebec.jira.plugins.customseclvl.ao.model.EventAO;
import fr.nlebec.jira.plugins.customseclvl.ao.model.EventToSecurityRule;
import fr.nlebec.jira.plugins.customseclvl.ao.model.SecurityRuleAO;
import fr.nlebec.jira.plugins.customseclvl.model.CSLConfiguration;
import fr.nlebec.jira.plugins.customseclvl.model.Event;
import fr.nlebec.jira.plugins.customseclvl.service.EventService;
import net.java.ao.Query;

@Named(value="eventService")
public class DefaultEventService implements EventService {

    private final static Logger LOG = Logger.getLogger(DefaultEventService.class);

    private ActiveObjects persistenceManager;
    private CustomFieldManager customFieldManager;
    private CSLConfiguration configuration;
    

    @Inject
    public DefaultEventService(@ComponentImport ActiveObjects persistenceManager,
                                   @ComponentImport CustomFieldManager customFieldManager
    		)
    {
        this.customFieldManager = customFieldManager;
        this.persistenceManager = checkNotNull(persistenceManager);
    }

	public CSLConfiguration getConfiguration() {
		CSLConfigurationAO[] configs = this.persistenceManager.find(CSLConfigurationAO.class);
		if (configs.length > 0) {
			this.configuration = ItemConverter.convertActiveObjectToPOJO(configs[0]);
		} else {
			this.configuration = new CSLConfiguration();
			this.persistenceManager.create(CSLConfigurationAO.class).save();
		}
		return this.configuration;
	}
    
    public CSLConfigurationAO getConfigurationAo() {
        CSLConfigurationAO[] configs = this.persistenceManager.find(CSLConfigurationAO.class);
        CSLConfigurationAO configAo;
        
        if (configs.length == 0) {
            configAo = this.persistenceManager.create(CSLConfigurationAO.class);
        } else {
            configAo = configs[0];
        }
        return configAo;
    }


    public void addEvent(Event event, SecurityRuleAO srao) throws SQLException {
    	LOG.info("Add new event : "+ event.toString());
    	EventAO eventAO = this.persistenceManager.create(EventAO.class);
    	EventToSecurityRule eventToSecurityRule = this.persistenceManager.create(EventToSecurityRule.class);
    	ItemConverter.bindPojoToActiveObject(eventAO, srao, eventToSecurityRule, event);
        eventAO.save();
        eventToSecurityRule.save();
    }
    
    public void deleteEvent(EventAO eventAo, SecurityRuleAO srao)  {
    	LOG.info("Delete new event : "+ eventAo.toString());
    	this.deleteEventAssociation(eventAo, srao);
    	this.persistenceManager.delete(eventAo);
    	
    }
    private void deleteEventAssociation(EventAO eventAo, SecurityRuleAO srao)  {
    	LOG.info("Delete event association : "+ eventAo.toString());
    	EventToSecurityRule[] associations = persistenceManager.find(EventToSecurityRule.class,Query.select().where("EVENT_ID = ? AND SECURITY_RULE_ID = ?",eventAo.getID(),srao.getID()));
    	for (int i = 0; i < associations.length; i++) {
			this.persistenceManager.delete(associations[i]);
		}
    	
    }
}
