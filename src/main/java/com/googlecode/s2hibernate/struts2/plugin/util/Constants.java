package com.googlecode.s2hibernate.struts2.plugin.util;


public class Constants {
	
	/**
	 * Full qualified name of the custom class used as a Hibernate Session Factory. <br/>
	 * <br/><b>Optional</b>. 
	 * <br/>If not used, the Hibernate Plugin will use an internal Session Factory Class
	 * (<b><link {@link com.googlecode.s2hibernate.struts2.plugin.util.HibernateSessionFactory}</b>).
	 */
	public static final String
	HIBERNATEPLUGIN_CUSTOMSESSIONFACTORYCLASS = 
		"hibernatePlugin.customSessionFactoryClass";
	
	/**
	 * Public method from the Hibernate Session Factory class for rebuild configurations. 
	 * <br/><b>Optional</b>, but <b>Required</b> if <b>hibernatePlugin.customSessionFactoryClass</b> property is used.
	 */
	public static final String
		HIBERNATEPLUGIN_REBUILDSESSIONFACTORYMETHOD = 
		"hibernatePlugin.rebuildSessionFactoryMethod";
	
	/**
	 * Set the access mode of the rebuild Session Factory method of the Session Factory class configured at 
	 * <b>hibernatePlugin.customSessionFactoryClass</b>.<br/>
	 * If <b>true</b> the method if used in static mode (ie. <code>MySessionFactoryClass.rebuildMySessionFactory()</code>).<br/> 
	 * If <b>false</b> the Hibernate Plugin will instantiate the Session Factory class.
	 * <br/><b>Optional</b>, but <b>Required</b> if <b>hibernatePlugin.customSessionFactoryClass</b> property is used.
	 * <br/>Default <b>true</b>.
	 */
	public static final String
		HIBERNATEPLUGIN_STATICREBUILDSESSIONFACTORYMETHOD =
		"hibernatePlugin.staticRebuildSessionFactoryMethod";
	
	/**
	 * Comma-separated Hibernate Configuration file names.
	 * Note that if <b>hibernatePlugin.customSessionFactoryClass</b> property is used that is not guarantee about the using of the files named here. 
	 * <br/><b>Optional</b>.
	 * <br/>Default <b>/hibernate.cfg.xml</b>.
	 */
	public static final String
		HIBERNATEPLUGIN_CONFIGURATIONFILES =
		"hibernatePlugin.configurationFiles";
	
	/**
	 * Hibernate Session Target in the Action ValueStack. This property can be used in 2 ways:
	 * <br/> 1. Simple values. Examples: <code>myHibSession</code>; <code>hibSession</code>; <code>myDao.session</code>
	 * <br/> 2. Values using Wildcard. Examples: <code>*Dao.session</code>; <code>*Facade.hibernateSession</code>
	 * <br/><b>Required</b> if not using the Annotation @SessionTarget
	 */
	public static final String
		HIBERNATEPLUGIN_SESSIONTARGET =
		"hibernatePlugin.sessionTarget";
	
	/**
	 * Method used to get the Hibernate Session from the Session Factory class configured at 
	 * <b>hibernatePlugin.customSessionFactoryClass</b>.
	 * <br/>Examples: <code>getSession</code>; <code>getHibernateSession</code>
	 * <br/><b>Optional</b>, but <b>Required</b> if <b>hibernatePlugin.customSessionFactoryClass</b> property is used.
	 */
	public static final String
		HIBERNATEPLUGIN_GETSESSIONMETHOD =
		"hibernatePlugin.getSessionMethod";
	
	/**
	 * Is the method used to get the Hibernate Session static?
	 * If <b>true</b> the method if used in static mode (ie. <code>MySessionFactoryClass.getSession()</code>).<br/> 
	 * If <b>false</b> the Hibernate Plugin will instantiate the Session Factory class.
	 * <br/><b>Optional</b>, but <b>Required</b> if <b>hibernatePlugin.customSessionFactoryClass</b> property is used.
	 * <br/>Default <b>true</b>.
	 */
	public static final String
	HIBERNATEPLUGIN_STATICGETSESSIONMETHOD =
		"hibernatePlugin.staticGetSessionMethod";
	
	/**
	 * If <b>true</b>, the Hibernate Session of the request will be closed after the response.
	 * <br/>If <b>false</b>, the Hibernate Session of the request WILL NOT be closed after the response.
	 * <br/><b>Optional</b>
	 * <br/>Default <b>true</b>.
	 */
	public static final String
		HIBERNATEPLUGIN_CLOSESESSIONAFTERINVOKE =
		"hibernatePlugin.closeSessionAfterInvoke";

	/**
	 * Method used to CLOSE the Hibernate Session from the Session Factory class configured at 
	 * <b>hibernatePlugin.customSessionFactoryClass</b>.
	 * <br/>Example: <code>closeSession</code>; 
	 * <br/><b>Optional</b>, but <b>Required</b> if <b>hibernatePlugin.customSessionFactoryClass</b> property is used.
	 */
	public static final String
		HIBERNATEPLUGIN_CLOSESESSIONMETHOD =
		"hibernatePlugin.closeSessionMethod";
	
	/**
	 * Indicates the use of the Hibernate Session Object in close session method at the Session Factory class configured at 
	 * <b>hibernatePlugin.customSessionFactoryClass</b>.
	 * <br/>Use <code>true</code> or <code>false</code>; 
	 * <br/><b>Optional</b>.
	 * <br/>Default <b>true</b>.
	 */
	public static final String HIBERNATEPLUGIN_USESESSIONOBJECTINCLOSESESSIONMETHOD = 
		"hibernatePlugin.useSessionObjectInCloseSessionMethod";
	
	/**
	 * Hibernate Transaction Target in the Action ValueStack. This property can be used in 2 ways:
	 * <br/> 1. Simple values. Examples: <code>myHibTransaction</code>; <code>hibernateTransaction</code>; <code>myDao.transaction</code>
	 * <br/> 2. Values using Wildcard. Examples: <code>*Dao.transaction</code>; <code>*Facade.hibernateTransaction</code>
	 * <br/><b>Required</b>  if not using the Annotation @TransactionTarget.
	 */
	public static final String
		HIBERNATEPLUGIN_TRANSACTIONTARGET =
		"hibernatePlugin.transactionTarget";
	
	@Deprecated
	/**
	 * Hibernate Configuration Type. Values: <b>xml</b> or <b>annotation</b>.
	 * <br/>Note that if <b>hibernatePlugin.customSessionFactoryClass</b> property is used that is not guarantee about the using of the configuration type named here.	  
	 * <br/><b>Optional</b>
	 * <br/>Default <b>xml</b>. <br/> 
	 * <b>Deprecated since 1.2: Configuration type is detected automatically.
	 */
	public static final String
		HIBERNATEPLUGIN_CONFIGURATIONTYPE =
		"hibernatePlugin.configurationType";
	
	
	/**
	 * Define public access for the Hibernate Manager tool.
	 * Values: <b>true</b> or <b>false</b>.
	 * If <b>true</b>, the plugin will ignore values defined at 
	 * <b>hibernatePlugin.manager.publicAccessEnabled</b> and 
	 * <b>hibernatePlugin.manager.httpAuthRoles</b> properties.
	 * <br/><b>Optional</b>
	 * <br/>Default <b>false</b>.
	 */
	public static final String
		HIBERNATEPLUGIN_MANAGER_PUBLICACCESSENABLED =
		"hibernatePlugin.manager.publicAccessEnabled";
	
	/**
	 * Comma-separated roles that can access the Hibernate Manager tool. 
	 * <br/>Example: manager, admin
	 * <br/><b>Optional</b>
	 */
	public static final String
		HIBERNATEPLUGIN_MANAGER_HTTPAUTHROLES =
		"hibernatePlugin.manager.httpAuthRoles";
	
	/**
	 * Comma-separated IP numbers or host names that can access the Hibernate Manager tool.
	 * <br/>If you use this property, use <b>false</b> at <b>hibernatePlugin.manager.publicAccessEnabled</b> 
	 * property.
	 * <br/>Example: 10.1.0.3, localhost, admim.my.domain, pc1.my.domain
	 * <br/><b>Optional</b>
	 * <br/>Default <b>127.0.0.1</b>.
	 */
	public static final String
		HIBERNATEPLUGIN_MANAGER_VALIDIPSHOSTS =
		"hibernatePlugin.manager.validIpsHosts";

	/**
	 * Name of the SessionFactory to be used   
	 * <br/><b>Optional</b>
	 */
	public static final String HIBERNATEPLUGIN_SOURCESESSIONFACTORY = 
		"hibernatePlugin.sourceSessionFactory";

	/**
	 * Define if the Full Hibernate Plugin's Session Factory will rebuild all configured SessionFactories when an
	 * {@link org.hibernate.exception.JDBCConnectionException} occurs.
	 * <br/><b>Optional</b>
	 * <br/>Default <b>true</b>.
	 */
	public static final String HIBERNATEPLUGIN_AUTOREBUILD = 
		"hibernatePlugin.autoRebuild";

	/**
	 * Define if there are mapped Actions inside Jar Files
	 * <br/><b>Optional</b>
	 * <br/>Default <b>false</b> 
	 */
	public static final String HIBERNATEPLUGIN_SEARCHMAPPINGINSIDEJARS = 
		"hibernatePlugin.searchMappingsInsideJars";

	

}
