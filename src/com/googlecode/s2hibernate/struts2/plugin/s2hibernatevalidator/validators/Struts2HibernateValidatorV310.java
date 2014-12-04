package com.googlecode.s2hibernate.struts2.plugin.s2hibernatevalidator.validators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class Struts2HibernateValidatorV310 implements Struts2HibernateValidator{
	
	static Map<String, ResourceBundle> localesMap = new TreeMap<String, ResourceBundle>();

	public List<InvalidValue> validate(ActionSupport actionAs, Locale clientLocale,
			ClassLoader classLoader) throws IOException {
		List<InvalidValue> invalidValuesFromRequest = new ArrayList<InvalidValue>();
		
		ClassValidator actionValidator = null;
		if (localesMap.containsKey(clientLocale.toString())) {
			actionValidator = new ClassValidator(actionAs.getClass(),localesMap.get(clientLocale.toString()));
		} else {
			ResourceBundle clientDefaultMessages = ResourceBundle.getBundle("org.hibernate.validator.resources.DefaultValidatorMessages", clientLocale, this.getClass().getClassLoader());
			
			try {
				ResourceBundle clientCustomMessages = ResourceBundle.getBundle("ValidatorMessages", clientLocale);
				actionValidator = new ClassValidator(actionAs.getClass(),clientCustomMessages);
				localesMap.put(clientLocale.toString(), clientCustomMessages);
			} catch (MissingResourceException e) {
				actionValidator = new ClassValidator(actionAs.getClass(),clientDefaultMessages);
				localesMap.put(clientLocale.toString(), clientDefaultMessages);
			}
		}
		
		// take all errors but discard when the field do not came from the request
		// Only the first validation error by field is used.
		InvalidValue[] invalidValues = actionValidator.getInvalidValues(actionAs);
		List<String> invalidFieldNames = new ArrayList<String>();
		Map parameters = ActionContext.getContext().getParameters();
		for (InvalidValue invalidValue : invalidValues) {
			String fieldFullName = invalidValue.getPropertyPath();
			if (invalidFieldNames.contains(fieldFullName))
				continue;
			if (parameters.containsKey(fieldFullName)) {
				invalidValuesFromRequest.add(invalidValue);
				invalidFieldNames.add(fieldFullName);
			}
		}
		invalidValues=null;
		invalidFieldNames.clear();
		invalidFieldNames=null;
		actionValidator=null;
		return invalidValuesFromRequest;
	}

	public void addFieldErrors(ActionSupport actionAs,
			Collection invalidValuesFromRequest) {
		for (InvalidValue invalidValue : (List<InvalidValue>)invalidValuesFromRequest) {
			StringBuilder sbMessage = new StringBuilder(actionAs.getText(invalidValue.getPropertyPath(),""));
			if (sbMessage.length()>0)
				sbMessage.append(" - ");
			sbMessage.append(actionAs.getText(invalidValue.getMessage()));
			actionAs.addFieldError(invalidValue.getPropertyPath(), sbMessage.toString());
		}
	}


}
