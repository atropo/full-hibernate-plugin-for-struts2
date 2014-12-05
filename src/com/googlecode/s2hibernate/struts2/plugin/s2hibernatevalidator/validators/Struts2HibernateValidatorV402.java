package com.googlecode.s2hibernate.struts2.plugin.s2hibernatevalidator.validators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.engine.ResourceBundleMessageInterpolator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class Struts2HibernateValidatorV402 implements Struts2HibernateValidator{
	
	static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	
	static Map<String, MessageInterpolator> localesMap = new TreeMap<String, MessageInterpolator>();
	
	public List<ConstraintViolation> validate(ActionSupport actionAs, Locale clientLocale,
			ClassLoader classLoader, Class<?>[] groups)
			throws IOException {
		List<ConstraintViolation> invalidValuesFromRequest = new ArrayList<ConstraintViolation>();
		
		MessageInterpolator interpolator = null;
		
		if (localesMap.containsKey(clientLocale.toString())) {
			interpolator = localesMap.get(clientLocale.toString());
		} else {
			ResourceBundle clientDefaultMessages = ResourceBundle.getBundle("org/hibernate/validator/ValidationMessages", clientLocale);
			
			try {
				ResourceBundle clientCustomMessages = ResourceBundle.getBundle("ValidationMessages", clientLocale);
				
			} catch (MissingResourceException e) {
				interpolator = new ResourceBundleMessageInterpolator(clientDefaultMessages);
			}
			localesMap.put(clientLocale.toString(), interpolator);
		}

		Validator validator = factory.usingContext().messageInterpolator(interpolator).getValidator();
		
		Set<ConstraintViolation<ActionSupport>> constraintViolations = validator.validate(actionAs, groups);
		
		List<Path> invalidFieldNames = new ArrayList<Path>();
		Map parameters = ActionContext.getContext().getParameters();
		for (ConstraintViolation<ActionSupport> constrantViolation : constraintViolations) {
			Path fieldPath = constrantViolation.getPropertyPath();
			Map<String, Object> attrs = constrantViolation.getConstraintDescriptor().getAttributes();
			if (invalidFieldNames.contains(fieldPath))
				continue;
			if (parameters.containsKey(fieldPath.toString())) {
				invalidValuesFromRequest.add(constrantViolation);
				invalidFieldNames.add(fieldPath);
			}
		}	
		constraintViolations.clear();
		constraintViolations=null;
		invalidFieldNames.clear();
		invalidFieldNames = null;
		validator=null;
		return invalidValuesFromRequest;
	}


	public void addFieldErrors(ActionSupport actionAs,
			Collection invalidValuesFromRequest) {
		for (ConstraintViolation constraintViolation : (List<ConstraintViolation>)invalidValuesFromRequest) {
			StringBuilder sbMessage = new StringBuilder(actionAs.getText(constraintViolation.getPropertyPath().toString(),""));
			if (sbMessage.length()>0)
				sbMessage.append(" - ");
			sbMessage.append(actionAs.getText(constraintViolation.getMessage()));
			actionAs.addFieldError(constraintViolation.getPropertyPath().toString(), sbMessage.toString());
		}
	}

}
