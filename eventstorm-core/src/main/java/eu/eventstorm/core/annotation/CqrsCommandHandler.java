package eu.eventstorm.core.annotation;

import eu.eventstorm.core.Command;

public @interface CqrsCommandHandler {

    Class<? extends Command> command();

}
