package fr.nlebec.jira.plugins.customseclvl.ao.model;

import java.util.Date;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Table;

@Preload
@Table("AUDIT_LOG")
public interface AuditLogAO extends Entity  {

	
	public String getAuthor();
	
	public void setAuthor(String author);
	
	public Date getEventDate();

	public void setEventDate(Date eventDate);
	
	public String getMessage();

	public void setMessage(String message);
	
	public Integer getIdSecurityRule();

	public void setIdSecurityRule(Integer idSecurityRule);

}
