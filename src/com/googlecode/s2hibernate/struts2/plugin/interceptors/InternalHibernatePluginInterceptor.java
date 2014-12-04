package com.googlecode.s2hibernate.struts2.plugin.interceptors;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.LocalizedTextUtil;

/**
 * Interceptor for I18N messages config. 
 * <br/>
 * Interceptor para configuração das mensagens I18N do Hibernate Plugin. 
 * @author Yoshiriro
 *
 */
@SuppressWarnings("serial")
public class InternalHibernatePluginInterceptor extends AbstractInterceptor{

	static String messageFiles;
	
	static Boolean messagesConfigured = false;
	
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		configMessages();
		return invocation.invoke();
	}
	
	private void configMessages() {
		if (messagesConfigured)
			return;
		LocalizedTextUtil.addDefaultResourceBundle("hibernateplugin-messages");
		for (String messageFile:getMessageFiles()) {
			LocalizedTextUtil.addDefaultResourceBundle(messageFile);			
		}
		messagesConfigured = true;
	}

	private static String[] getMessageFiles() {
		if (messageFiles==null)
			messageFiles="";
		return messageFiles.split(",");
	}

	@Inject(value="struts.custom.i18n.resources",required=false)
	public void setMessageFiles(String messageFiles) {
		InternalHibernatePluginInterceptor.messageFiles = messageFiles;
	}


	
	
}
