package fr.nlebec.jira.plugins.customseclvl.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import fr.nlebec.jira.plugins.customseclvl.ao.converters.ItemConverter;
import fr.nlebec.jira.plugins.customseclvl.ao.model.CSLConfigurationAO;
import fr.nlebec.jira.plugins.customseclvl.model.CSLConfiguration;
import fr.nlebec.jira.plugins.customseclvl.service.CSLConfigurationService;

@Named("CSLConfigurationService")
public class DefaultCSLConfiguration implements CSLConfigurationService {

	private final static Logger LOG = Logger.getLogger(DefaultCSLConfiguration.class);

	private ActiveObjects persistenceManager;
	private CSLConfiguration configuration;

	@Inject
	public DefaultCSLConfiguration(@ComponentImport ActiveObjects persistenceManager) {
		this.persistenceManager = checkNotNull(persistenceManager);
	}

	public CSLConfiguration getConfiguration() {
		CSLConfigurationAO[] configs = this.persistenceManager.find(CSLConfigurationAO.class);
		if (configs.length > 0) {
			this.configuration = ItemConverter.convertActiveObjectToPOJO(configs[0]);
		} else {
			LOG.debug("Creating new configuration preferences");
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

	public void updateConfiguration(boolean isActive, String dateFormat, String layout, Boolean silent)
			throws SQLException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Saving configuration preferences");
		}
		CSLConfiguration config = this.getConfiguration();
		config.setActive(isActive);
		config.setDateFormat(dateFormat);
		config.setLayout(layout);
		config.setSilent(silent);
		CSLConfigurationAO configAo = getConfigurationAo();
		ItemConverter.bindPojoToActiveObject(configuration, configAo);
		configAo.save();
	}

}