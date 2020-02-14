package eu.eventstorm.cqrs.ex001.command;

import static eu.eventstorm.annotation.CqrsCommandType.CREATE;
import static eu.eventstorm.annotation.HttpMethod.POST;

import eu.eventstorm.annotation.CqrsCommand;
import eu.eventstorm.annotation.CqrsCommandRestController;
import eu.eventstorm.annotation.constraint.CustomPropertiesValidator;
import eu.eventstorm.annotation.constraint.CustomPropertyValidator;
import eu.eventstorm.annotation.constraint.NotEmpty;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.ex001.validator.MailAndAgePropertyValidator;
import eu.eventstorm.cqrs.ex001.validator.MailPropertyValidator;

@CqrsCommand(type = CREATE)
@CqrsCommandRestController(name = "UserCommandRestController", javaPackage = "eu.eventstorm.core.ex001.command.rest", method = POST, uri = "command/user/create")
@CustomPropertiesValidator(name = "x25", properties = {"name","age"}, validateBy = MailAndAgePropertyValidator.class)
public interface CreateUserCommand extends Command {

	@NotEmpty
	String getName();

	void setName(String name);

	int getAge();

	void setAge(int age);

	@CustomPropertyValidator(validateBy = MailPropertyValidator.class)
	String getEmail();

	void setEmail(String email);

}