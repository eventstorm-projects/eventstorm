package eu.eventstorm.core.ex001.command;

import static eu.eventstorm.core.annotation.CqrsCommandType.CREATE;

import eu.eventstorm.core.Command;
import eu.eventstorm.core.annotation.CqrsCommand;
import eu.eventstorm.core.annotation.Domain;

@CqrsCommand(type = CREATE, domains = @Domain( name = "user"))
public interface CreateUserCommand extends Command {

    String getName();

    void setName(String name);

    int getAge();

    void setAge(int age);

    String getEmail();

    void setEmail(String email);

}