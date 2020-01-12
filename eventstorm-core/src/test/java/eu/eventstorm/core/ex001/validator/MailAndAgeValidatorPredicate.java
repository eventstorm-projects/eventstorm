package eu.eventstorm.core.ex001.validator;

import java.util.function.Predicate;

import eu.eventstorm.util.tuple.Tuple2;

public class MailAndAgeValidatorPredicate implements Predicate<Tuple2<String,Integer>>{

	@Override
	public boolean test(Tuple2<String, Integer> t) {
		return true;
	}

}
