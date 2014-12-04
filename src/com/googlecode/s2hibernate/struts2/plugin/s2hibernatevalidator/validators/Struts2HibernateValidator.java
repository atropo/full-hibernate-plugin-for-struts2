package com.googlecode.s2hibernate.struts2.plugin.s2hibernatevalidator.validators;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import com.opensymphony.xwork2.ActionSupport;

public interface Struts2HibernateValidator {

	Collection<?> validate(ActionSupport actionAs, Locale clientLocale,
			ClassLoader classLoader) throws IOException;

	void addFieldErrors(ActionSupport actionAs, Collection invalidValuesFromRequest);

}
