package com.googlecode.s2hibernate.struts2.plugin.s2hibernatevalidator.exception;

import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;

public class HibernatePluginValidationException extends InvalidStateException {

	String[] fieldNamesInAction;
	
	public HibernatePluginValidationException(InvalidValue[] invalidValues, String[] fieldNamesInAction) {
		super(invalidValues);
		setFieldNamesInAction(fieldNamesInAction);
	}

	public String[] getFieldNamesInAction() {
		return fieldNamesInAction;
	}

	public void setFieldNamesInAction(String[] fieldNameInAction) {
		this.fieldNamesInAction = fieldNameInAction;
	}

}
