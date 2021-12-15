package com.googlecode.s2hibernate.struts2.plugin.actions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import com.googlecode.s2hibernate.struts2.plugin.util.Constants;
import com.googlecode.s2hibernate.struts2.plugin.util.HibernateConfiguration;
import com.googlecode.s2hibernate.struts2.plugin.util.HibernateSessionFactory;
import com.googlecode.s2hibernate.struts2.plugin.util.SessionInfo;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.inject.Inject;

public class HibernateManagementAction extends ActionSupport {
	
	Boolean viewOpenSessions = false;
	
	Set<SessionInfo> sessions = new LinkedHashSet<SessionInfo>();
	
	Integer[] sessionids;
	                                           
	String closeAll;          
	
	private String customSessionFactoryClass;

	private String rebuildSessionFactoryMethod;

	private boolean staticRebuildSessionFactoryMethod = true;
	
	private String configurationFiles=HibernateSessionFactory.DEFAULT_HIBERATE_CONFIGFILE;
	
	private boolean publicAccessEnabled = false;
	
	private String configurationType = "xml";
	
	private List<HibernateConfiguration> configurations = new ArrayList<HibernateConfiguration>();
	
	private String configFileName;
	
	public String index() throws Exception {
		return super.execute();
	}
	
	public List<HibernateConfiguration> getConfigurations() {
		try {
			if (customSessionFactoryClass!=null)
				return new ArrayList<HibernateConfiguration>();
			for (String file:getConfigurationFiles()) {
				Configuration configuration = null;
				if (configurationType.equalsIgnoreCase("xml")) {
					configuration = new Configuration();
				} 
				if (configurationType.equalsIgnoreCase("annotation")) {
					configuration = new AnnotationConfiguration();
				}
				if (configuration==null) {
					String errorMessage = getText("hibernateplugin.configurationType_error");
					throw new Exception(errorMessage);
				}
				configuration.configure(file);
				Properties propertiesAll = configuration.getProperties();
				Properties properties = new Properties();
				for (Object key:propertiesAll.keySet()) {
					if (key.toString().startsWith("hibernate"))
						properties.put(key, propertiesAll.get(key));
				}
				HibernateConfiguration hConfiguration = new HibernateConfiguration(file,properties);
				configurations.add(hConfiguration);
			}
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return configurations; 
	}
	
	public String reloadConfiguration() {
		try {

			if (isSessionFactoryFromPlugin()) {
				HibernateSessionFactory.destroyFactory();
				HibernateSessionFactory.setConfigFiles(configurationFiles);
				HibernateSessionFactory.rebuildSessionFactory();
			}
			else {
				if ( (rebuildSessionFactoryMethod==null) || (rebuildSessionFactoryMethod.equals("")) ) {
					throw new Exception(getText("hibernateplugin.no_rebuild_session_factory_method"));
				}
	
				Object sessionFactory = getSessionFactory();
				
				Method method = Class.forName(customSessionFactoryClass).getDeclaredMethod(rebuildSessionFactoryMethod, null);
				method.invoke(sessionFactory, null);
			}
			
			addActionMessage(getText("hibernateplugin.rebuild_successful"));
			return SUCCESS;
		} catch (Exception e) {
			String message = e.getMessage();
			while (message==null) {
				message = e.getCause().getMessage();
			}
			addActionError(getText("hibernateplugin.error_rebuild_hibernate",new String[]{message}));
			e.printStackTrace();
			return SUCCESS;
		}
	}
	
	public String viewOpenSessions() {
		viewOpenSessions=true;
		return SUCCESS;
	}
	
	public String closeHibernateSessions() throws HibernateException, SecurityException, IllegalArgumentException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		List<SessionInfo> openInfos = new ArrayList<SessionInfo>();
		openInfos.addAll(getSessions());
		if (closeAll!=null) {
			for (SessionInfo info:getSessions()) {
				info.getHibernateSession().close();
				openInfos.remove(info);
			}
		} 
		else {
			for (Integer id : sessionids) {
				for (SessionInfo info:getSessions()) {
					if (info.getHibernateSession().hashCode()==id) {
						info.getHibernateSession().close();
						openInfos.remove(info);
					}
				}
			}
		}
		addActionMessage( getText("hibernateplugin.closed_sessions", new String[]{(getSessions().size()-openInfos.size())+""}) );
		ActionContext.getContext().getApplication().put("hibernateSessions", openInfos);
		return SUCCESS;
	}
	
	public String testConnection() {
		try {
			HibernateSessionFactory.testConnection(configFileName);
		} catch (Exception e) {
			e.printStackTrace();
			addActionError(getText("hibernateplugin.connection.error",new String[]{e.getMessage()}));
			return SUCCESS;
		}  
		//Successful established 
		addActionMessage(getText("hibernateplugin.connection.success"));
		return SUCCESS;
	}
	
	private Object getSessionFactory() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Object sessionFactory;
		if (staticRebuildSessionFactoryMethod)
			sessionFactory = Class.forName(customSessionFactoryClass, false, this.getClass().getClassLoader());
		else
			sessionFactory = Class.forName(customSessionFactoryClass).newInstance();
		return sessionFactory;
	}

	public Set<SessionInfo> getSessions() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
		sessions = (Set<SessionInfo>) ActionContext.getContext().getApplication().get("struts2HibernatePlugin_Sessions");
		if (sessions==null)
			sessions = new LinkedHashSet<SessionInfo>();
		return sessions;
	}

	public String getRebuildSessionFactoryMethod() {
		return rebuildSessionFactoryMethod;
	}

	@Inject(value=Constants.HIBERNATEPLUGIN_REBUILDSESSIONFACTORYMETHOD,required=false)
	public void setRebuildSessionFactoryMethod(String rebuildSessionFactoryMethod) {
		this.rebuildSessionFactoryMethod = rebuildSessionFactoryMethod;
	}

	public String getCustomSessionFactoryClass() {
		return customSessionFactoryClass;
	}

	@Inject(value=Constants.HIBERNATEPLUGIN_CUSTOMSESSIONFACTORYCLASS,required=false)
	public void setCustomSessionFactoryClass(String customSessionFactoryClass) {
		this.customSessionFactoryClass = customSessionFactoryClass;
	}
	

	@Inject(value=Constants.HIBERNATEPLUGIN_STATICREBUILDSESSIONFACTORYMETHOD,required=false)
	public void setStaticRebuildSessionFactoryMethod(
			String staticRebuildSessionFactoryMethod) {
		this.staticRebuildSessionFactoryMethod = new Boolean(staticRebuildSessionFactoryMethod);
	}

	public Boolean getViewOpenSessions() {
		return viewOpenSessions;
	}

	public void setViewOpenSessions(Boolean viewOpenSessions) {
		this.viewOpenSessions = viewOpenSessions;
	}

	public Integer[] getSessionids() {
		return sessionids;
	}

	public void setSessionids(Integer[] sessionids) {
		this.sessionids = sessionids;
	}

	public String getCloseAll() {
		return closeAll;
	}

	public void setCloseAll(String closeAll) {
		this.closeAll = closeAll;
	}

	public String[] getConfigurationFiles() {
		if (configurationFiles==null)
			configurationFiles="";
		return configurationFiles.split(",");
	}

	@Inject(value=Constants.HIBERNATEPLUGIN_CONFIGURATIONFILES, required=false)
	public void setConfigurationFiles(String configurationFiles) {
		this.configurationFiles = configurationFiles;
	}

	public Boolean getPublicAccessEnabled() {
		return publicAccessEnabled;
	}

	public void setPublicAccessEnabled(Boolean publicAccessEnabled) {
		this.publicAccessEnabled = publicAccessEnabled;
	}

	public Boolean isSessionFactoryFromPlugin() {
		return (customSessionFactoryClass==null);
	}

	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}

	public String getConfigFileName() {
		return configFileName;
	}

}
