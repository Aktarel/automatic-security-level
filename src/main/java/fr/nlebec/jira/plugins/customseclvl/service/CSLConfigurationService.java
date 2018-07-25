package fr.nlebec.jira.plugins.customseclvl.service;

import java.sql.SQLException;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;

import fr.nlebec.jira.plugins.customseclvl.ao.model.CSLConfigurationAO;
import fr.nlebec.jira.plugins.customseclvl.model.CSLConfiguration;

@Scanned
public interface CSLConfigurationService  {


	public CSLConfiguration getConfiguration();

	public CSLConfigurationAO getConfigurationAo();

	public void updateConfiguration(boolean isActive, String dateFormat, String layout, Boolean silent) throws SQLException;

}