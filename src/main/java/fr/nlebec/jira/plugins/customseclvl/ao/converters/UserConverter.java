package fr.nlebec.jira.plugins.customseclvl.ao.converters;

import java.util.Optional;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;

public class UserConverter {

	public static Optional<ApplicationUser> convertUsert(Long userId){
		UserManager userManager = ComponentAccessor.getUserManager();
		return userManager.getUserById(userId);
		
	}
	
}
