package com.googlecode.s2hibernate.struts2.plugin.s2hibernatevalidator.interceptor;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Locale;

import org.apache.struts2.interceptor.validation.SkipValidation;

import com.googlecode.s2hibernate.struts2.plugin.annotations.Validated;
import com.googlecode.s2hibernate.struts2.plugin.interceptors.GenericInterceptor;
import com.googlecode.s2hibernate.struts2.plugin.s2hibernatevalidator.validators.Struts2HibernateValidator;
import com.googlecode.s2hibernate.struts2.plugin.s2hibernatevalidator.validators.Struts2HibernateValidatorV402;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

@SuppressWarnings("serial")
public class HibernateValidatorInterceptor extends GenericInterceptor{
	
	private String excludeMethods = "";
	private static Struts2HibernateValidator validator;
	
	static{
		configValidator();
	}
	
	/**
	 * Configure the appropriate Hibernate Validator version
	 * @return
	 */
	private static Struts2HibernateValidator configValidator() {
		if (validator!=null)
			return validator;
		
		try {
			Class.forName("org.hibernate.validator.HibernateValidator");
			validator = new Struts2HibernateValidatorV402();
			log.info("Full Hibernate Plugin Validation using Hibernate Validator 4.x");
			return validator;
		} catch (ClassNotFoundException e) {
			log.info("Full Hibernate Plugin Validation could not detect Hibernate Validator 4.x");
		}
		throw new RuntimeException("No Hibernate Validator class found (neither 3.x nor 4.x)");
	}
	
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		String[] excludeMethodsA = excludeMethods.split(",");
		for (String method : excludeMethodsA) {
			if (invocation.getProxy().getMethod().equals(method))
				return invocation.invoke();
		}
		Object action = invocation.getAction();
		Method method = null;
		try {
			method = action.getClass().getDeclaredMethod(invocation.getProxy().getMethod(), null);
		} catch (NoSuchMethodException e) {
			return invocation.invoke();
		}
		
		if (method.isAnnotationPresent(SkipValidation.class))
			return invocation.invoke();
		
		if (!(action instanceof ActionSupport)) {
			log.warn("Full Hibernate Plugin Validation Allowed only in Actions that 'ISA' ActionSupport");
			return invocation.invoke();
		}
		
		Class<?>[] groups = {};
		Validated groupAnnotation = method.getAnnotation(Validated.class);
		if (groupAnnotation != null) {
			groups = groupAnnotation.value();
		}
		ActionSupport actionAs = (ActionSupport) action;
		log.debug("Full Hibernate Plugin Validation in "+actionAs.getClass());
		
		Locale clientLocale = actionAs.getLocale();
		
//		List<InvalidValue> invalidValuesFromRequest = new ArrayList<InvalidValue>();
		
		Collection invalidValuesFromRequest = validator.validate(actionAs, clientLocale,getClass().getClassLoader(), groups);
		/*
		ResourceBundle clientResourceBundle = ResourceBundle.getBundle("org.hibernate.validator.resources.DefaultValidatorMessages", clientLocale, this.getClass().getClassLoader());
		InputStream stream = getClass().getResourceAsStream("/ValidatorMessages.properties");
		ClassValidator actionValidator = null;
		if (stream!=null) {
			PluginValidatorMessages validatorMessages = new PluginValidatorMessages(stream);
			validatorMessages.setParent(clientResourceBundle);
			actionValidator = new ClassValidator(action.getClass(),validatorMessages);
		} else {
			actionValidator = new ClassValidator(action.getClass(),clientResourceBundle);
		}
		
		// take all errors but discard when the field do not came from the request
		// Only the first validation error by field is used.
		InvalidValue[] invalidValues = actionValidator.getInvalidValues(action);
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
		*/
		if (invalidValuesFromRequest.isEmpty()) {
			log.debug("Full Hibernate Plugin Validation found no erros.");
			actionAs.validate();
			if (actionAs.hasActionErrors() || actionAs.hasFieldErrors()) {
				log.debug("Full Hibernate Plugin found custom validation errors: "+actionAs.getFieldErrors()+" "+actionAs.getActionErrors());
				return actionAs.input();
			}
			else {
				return invocation.invoke();
			}
		} else {
			validator.addFieldErrors(actionAs, invalidValuesFromRequest);
			/*for (InvalidValue invalidValue : invalidValuesFromRequest) {
				StringBuilder sbMessage = new StringBuilder(actionAs.getText(invalidValue.getPropertyPath(),""));
				if (sbMessage.length()>0)
					sbMessage.append(" - ");
				sbMessage.append(actionAs.getText(invalidValue.getMessage()));
				actionAs.addFieldError(invalidValue.getPropertyPath(), sbMessage.toString());
			}*/
			log.debug("Full Hibernate Plugin Validation found "+actionAs.getFieldErrors().size()+" validation Errors.");
			actionAs.validate();
			if (action instanceof Preparable) {
				Method methodPrepare = Preparable.class.getDeclaredMethod("prepare");
				methodPrepare.invoke(action);
			}
			return actionAs.input();
		}

	}

	public String getExcludeMethods() {
		return excludeMethods;
	}

	public void setExcludeMethods(String excludeMethods) {
		this.excludeMethods = excludeMethods;
	}

}