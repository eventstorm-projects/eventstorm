package eu.eventstorm.cqrs.ex001.validator;

import eu.eventstorm.core.validation.PropertyValidator;
import eu.eventstorm.core.validation.ValidatorContext;
import eu.eventstorm.util.tuple.Tuple2;

public class MailAndAgePropertyValidator implements PropertyValidator<Tuple2<String,Integer>>{

    @Override
    public void validate(String property, Tuple2<String, Integer> value, ValidatorContext context) {
    }

}
