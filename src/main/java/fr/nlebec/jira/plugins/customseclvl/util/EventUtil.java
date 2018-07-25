package fr.nlebec.jira.plugins.customseclvl.util;

import java.util.List;

import org.apache.log4j.Logger;

import fr.nlebec.jira.plugins.customseclvl.model.Event;

public class EventUtil {

	private final static Logger log = Logger.getLogger(EventUtil.class);
	
	public static boolean contains(List<Event> events, Long actualEventId){
		boolean ret = false;
		for(Event event : events ){
			if(event.getJiraEventId().equals(actualEventId)){
				log.debug("Event match a security rule parametrization : "+actualEventId);
				ret = true;
			}
		}
		return ret;
	}
	
}
