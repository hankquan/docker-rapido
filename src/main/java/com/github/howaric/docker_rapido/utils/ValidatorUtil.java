package com.github.howaric.docker_rapido.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

public class ValidatorUtil {

	private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	public static <T> Map<String, StringBuffer> validate(T obj) {
		Map<String, StringBuffer> errorMap = null;
		Set<ConstraintViolation<T>> set = validator.validate(obj, Default.class);
		if (set != null && set.size() > 0) {
			errorMap = new HashMap<String, StringBuffer>();
			String property = null;
			for (ConstraintViolation<T> cv : set) {
				property = cv.getPropertyPath().toString();
				if (errorMap.get(property) != null) {
					errorMap.get(property).append("," + cv.getMessage());
				} else {
					StringBuffer sb = new StringBuffer();
					sb.append(cv.getMessage());
					errorMap.put(property, sb);
				}
			}
		}
		return errorMap;
	}

}
