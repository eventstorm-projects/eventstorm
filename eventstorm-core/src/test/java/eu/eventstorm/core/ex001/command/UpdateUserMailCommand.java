package eu.eventstorm.core.ex001.command;

import static eu.eventstorm.core.annotation.CqrsCommandType.UPDATE;

import eu.eventstorm.core.Command;
import eu.eventstorm.core.annotation.CqrsCommand;

@CqrsCommand(type = UPDATE)
public interface UpdateUserMailCommand extends Command {

	String getId();
	
	void setId(String id);

    String getEmail();

    void setEmail(String email);

}