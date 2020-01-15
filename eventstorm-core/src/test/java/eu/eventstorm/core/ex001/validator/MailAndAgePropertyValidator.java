package eu.eventstorm.core.ex001.validator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.core.validation.PropertyValidator;
import eu.eventstorm.util.tuple.Tuple2;

public class MailAndAgePropertyValidator implements PropertyValidator<Tuple2<String,Integer>>{

    @Override
    public void validate(ImmutableList<String> properties, Tuple2<String, Integer> value, Builder<ConstraintViolation> builder) {
        // TODO Auto-generated method stub
        
    }

}
