package com.googlecode.s2hibernate.struts2.plugin.interceptors;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.lf5.util.StreamUtils;
import org.apache.struts2.ServletActionContext;

import com.googlecode.s2hibernate.struts2.plugin.actions.HibernateManagementAction;
import com.googlecode.s2hibernate.struts2.plugin.util.Constants;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class HibernateManagementInterceptor extends AbstractInterceptor{
	
	boolean publicAccessEnabled = false;
	String httpAuthRoles;
	String validIpsHosts = "127.0.0.1";
	static boolean jspFileRecreated = false;
	
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		createTempJSPFile(invocation);
		
		String errormessage = null;
		HibernateManagementAction action = ((HibernateManagementAction)invocation.getAction());
		if (publicAccessEnabled) {
			action.setPublicAccessEnabled(true);
			return invocation.invoke();
		}
		else if ((httpAuthRoles!=null) || (validIpsHosts!=null)) {
			if (httpAuthRoles!=null) {
				HttpServletRequest request = ServletActionContext.getRequest();
				String roles[] = httpAuthRoles.split(",");
				Boolean isValidUser = false;
				for (String role : roles) {
					if (request.isUserInRole(role)) {
						isValidUser = true;
						break;
					}
				}
				if (!isValidUser)
					errormessage = action.getText("hibernateplugin.httpauth_error");
			}
			if (validIpsHosts!=null) {
				String userIp = ServletActionContext.getRequest().getRemoteAddr();
				if (ServletActionContext.getRequest().getHeader("X-Forwarded-For")!=null) 
					userIp = ServletActionContext.getRequest().getHeader("X-Forwarded-For");
				String userHost = ServletActionContext.getRequest().getRemoteHost();
				if (userHost.equals(userIp))
					userHost = InetAddress.getByAddress(new byte[]{127,0,0,1}).getHostName();
				String ipshosts[] = validIpsHosts.split(",");
				Boolean isValidIp = ArrayUtils.contains(ipshosts, userIp);
				Boolean isValidHost = ArrayUtils.contains(ipshosts, userHost);
				if ( (!isValidIp) && (!isValidHost) )
					errormessage = action.getText("hibernateplugin.iphost_error");
			}
		} 
		else {
			errormessage = action.getText("hibernateplugin.public_access_disabled");
		}
		
		if (errormessage!=null) {
			throw new SecurityException(errormessage);
		} else {
			return invocation.invoke();
		}
	}


	private void createTempJSPFile(ActionInvocation invocation) throws IOException {
		try {
			if (!jspFileRecreated) {
				InputStream is = getClass().getResourceAsStream("/com/googlecode/s2hibernate/struts2/plugin/pages/management.jsp");
				byte[] bytes = StreamUtils.getBytes(is);
				String jspPath = ServletActionContext.getRequest().getSession().getServletContext().getRealPath("/");
				jspPath+="/WEB-INF/temp/hibernatePlugin/management.jsp";
				FileUtils.writeByteArrayToFile(new File(jspPath), bytes);
				jspFileRecreated=true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("Unable to create Plugin Management JSP file. "+e.getMessage());
		}
	}


	@Inject(value=Constants.HIBERNATEPLUGIN_MANAGER_PUBLICACCESSENABLED,required=false)
	public void setPublicAccessEnabled(String publicAccessEnabled) {
		this.publicAccessEnabled = new Boolean(publicAccessEnabled);
	}


	public String getHttpAuthRoles() {
		return httpAuthRoles;
	}

	@Inject(value=Constants.HIBERNATEPLUGIN_MANAGER_HTTPAUTHROLES,required=false)
	public void setHttpAuthRoles(String httpAuthRoles) {
		this.httpAuthRoles = httpAuthRoles;
	}


	public String getValidIpsHosts() {
		return validIpsHosts;
	}

	@Inject(value=Constants.HIBERNATEPLUGIN_MANAGER_VALIDIPSHOSTS,required=false)
	public void setValidIpsHosts(String validIpsHosts) {
		this.validIpsHosts = validIpsHosts;
	}

}
