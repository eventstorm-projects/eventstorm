package eu.eventstorm.core.ex001.command;

import static eu.eventstorm.core.annotation.CqrsCommandType.CREATE;
import static eu.eventstorm.core.annotation.HttpMethod.POST;

import eu.eventstorm.core.Command;
import eu.eventstorm.core.annotation.CqrsCommand;
import eu.eventstorm.core.annotation.CqrsCommandRestController;
import eu.eventstorm.core.annotation.constrain.CustomPropertyValidator;
import eu.eventstorm.core.annotation.constrain.NotEmpty;

@CqrsCommand(type = CREATE)
@CqrsCommandRestController(name = "UserCommandRestController", javaPackage = "eu.eventstorm.core.ex001.command.rest", method = POST, uri = "command/user/create")
public interface CreateUserCommand extends Command {

	@NotEmpty
	String getName();

	void setName(String name);

	int getAge();

	void setAge(int age);

	String getEmail();

	void setEmail(String email);

}