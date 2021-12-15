package com.googlecode.s2hibernate.struts2.plugin.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Set the annotated field as a Transaction target at Hibernate Plugin for Struts 2.<br/>
 * @author José Yoshiriro - jyoshiriro@gmail.com
 *
 */
@Target({ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface TransactionTarget {
	
	/** 
	 * Session object name. If do not used, the Full Hibernate Plugin will inject the Transaction from the first annotated Session object found in he same class.
	 */
	public String value() default "";
}
