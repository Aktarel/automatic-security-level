package fr.nlebec.jira.plugins.customseclvl.ao.model;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Preload
@Table("CSL_EVENT_SR_ASSO")
public interface EventToSecurityRule extends Entity {

	@AutoIncrement
	@NotNull
	@PrimaryKey("ID")
	public int getID();
	
	public void setEvent(EventAO eventAO);
    public EventAO getEvent();

    public void setSecurityRule(SecurityRuleAO securityRuleAo);
    public SecurityRuleAO getSecurityRule();
}
