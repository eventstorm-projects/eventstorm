package eu.eventstorm.core.ex001.command;

import static eu.eventstorm.core.annotation.CqrsCommandType.UPDATE;

import eu.eventstorm.core.Command;
import eu.eventstorm.core.annotation.CqrsCommand;
import eu.eventstorm.core.annotation.Domain;

@CqrsCommand(type = UPDATE, domains = @Domain( name = "user"))
public interface UpdateUserMailCommand extends Command {

	String getId();
	
	void setId(String id);

    String getEmail();

    void setEmail(String email);

}