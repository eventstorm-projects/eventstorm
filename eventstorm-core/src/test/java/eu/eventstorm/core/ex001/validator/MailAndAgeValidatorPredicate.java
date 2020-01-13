package eu.eventstorm.core.ex001.validator;

import com.google.common.collect.ImmutableList.Builder;

import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.core.validation.PropertyValidator;
import eu.eventstorm.util.tuple.Tuple2;

public class MailAndAgeValidatorPredicate implements PropertyValidator<Tuple2<String,Integer>>{

	@Override
	public void validate(String property, Tuple2<String, Integer> value, Builder<ConstraintViolation> builder) {
		// TODO Auto-generated method stub
		
	}

}
