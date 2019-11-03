package eu.eventstorm.core.ex001.command;

import static eu.eventstorm.core.annotation.CqrsCommandType.UPDATE;

import eu.eventstorm.core.Command;
import eu.eventstorm.core.annotation.CqrsCommand;

@CqrsCommand(type = UPDATE)
public interface UpdateUserCommand extends Command {

    String getName();

    void setName(String name);

    int getAge();

    void setAge(int age);

    String getEmail();

    void setEmail(String email);

}