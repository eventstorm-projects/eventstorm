package eu.eventstorm.core.ex001.command;

import eu.eventstorm.core.Command;
import eu.eventstorm.core.annotation.CqrsCommand;

@CqrsCommand
public interface CreateUserCommand extends Command {

    String getName();

    void setName(String name);

    int getAge();

    void setAge(int age);

    String getEmail();

    void setEmail(String email);

}