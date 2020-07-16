package eu.eventstorm.cqrs.ex001.command;

import eu.eventstorm.annotation.CqrsCommand;
import eu.eventstorm.cqrs.Command;

@CqrsCommand
public interface UpdateUserMailCommand extends Command {

	String getId();
	
	void setId(String id);

    String getEmail();

    void setEmail(String email);

}