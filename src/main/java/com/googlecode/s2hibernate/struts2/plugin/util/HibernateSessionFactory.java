package com.googlecode.s2hibernate.struts2.plugin.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.lf5.util.Resource;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.googlecode.s2hibernate.lang.BuildSessionFactoryException;
import com.googlecode.s2hibernate.lang.LoadSessionFromFactoryException;
import com.googlecode.s2hibernate.lang.SessionFactoryNotFoundException;


/**
 * Hibernate Core Session Factory from Full Hibernate Plugin for Struts 2.
 * @author José Yoshiriro - jyoshiriro@gmail.com
 *
 */
public class HibernateSessionFactory {

    /**
     * '/hibernate.cfg.xml' file
     */
    public static final String DEFAULT_HIBERATE_CONFIGFILE = "/hibernate.cfg.xml";
    
    private static String defaultSessionFactory = null;

	
	private static Map<String, SessionFactory> factoriesMap = new LinkedHashMap<String, SessionFactory>();
    private static String configFiles=DEFAULT_HIBERATE_CONFIGFILE;
    
    private static ThreadLocal<Map<String,Session>> sessionsMap = new ThreadLocal<Map<String,Session>>();
    
    private static boolean autoRebuidOnJdbcConnectionError = true;


	protected static Logger log;
    
    static {
    	initLogger();
    }
    
    public static void initLogger() {
    	if (log!=null)
    		return;
    	log = Logger.getLogger(HibernateSessionFactory.class);
		if (!log.getRootLogger().getAllAppenders().hasMoreElements())
			log.addAppender(new ConsoleAppender(new PatternLayout("%d{HH:mm:ss} - %p: %m %n")));
		log.setLevel(Level.WARN);
    }
    
    public static void setLoggerDebugLevel(){
    	if (!log.getLevel().equals(Level.DEBUG))
    		log.setLevel(Level.DEBUG);
    }

    /**
     * Returns a new Hibernate Core Session or the Session of the current thread (Current HTTP request).<br/>
     * If more than One SessionFactory is configured, the default SessionFactory if used as source.
     * @return The Session object
     * @throws LoadSessionFromFactoryException 
     * @throws BuildSessionFactoryException 
     * @throws Exception
     */
    public synchronized static Session getSession() throws BuildSessionFactoryException, LoadSessionFromFactoryException  {
    	return getSession(defaultSessionFactory);
    }
    
    /**
     * Returns a new Hibernate Core Session or the Session from the current thread (Current HTTP request)
     * @param sessionFactoryName Name of the source SessionFactory  (the same from <i>&ltsession-factory&gt</i> tag of a Hibernate configuration file).If <b>null</b>, the default SessionFactory is returned.
     * @return A Hibernate Core Session
     * @throws BuildSessionFactoryException 
     * @throws LoadSessionFromFactoryException 
     * @throws Exception
     */
    public synchronized static Session getSession(String sessionFactoryName) throws BuildSessionFactoryException, LoadSessionFromFactoryException {
    	log.debug("Hibernate Session Required (from current Thread) - SessionFactory required: "+( ( (sessionFactoryName==null) || (sessionFactoryName.equals("") )?"(default)":"\""+sessionFactoryName+"\"")));
    	
    	Session session = null; 
    	Map<String,Session> currentMap = sessionsMap.get();
        
    	// the current sessions map does not exists
        if (currentMap==null) {
        	log.debug("No Hibernate Session in current thread. New Hibernate Session will be created and returned (SessionFactory \""+( ( (sessionFactoryName==null) || (sessionFactoryName.equals("") )?"(default)":"\""+sessionFactoryName+"\""))+"\")");
			
			session = getNewSession(sessionFactoryName);
			
			Map<String,Session> newSessionMap = new LinkedHashMap<String, Session>();

			// at the first call from getNewSession() this test is need
			if (sessionFactoryName==null)
				sessionFactoryName = defaultSessionFactory;
			
			newSessionMap.put(sessionFactoryName, session);
			sessionsMap.set(newSessionMap);
			
        } 
        //the current sessions map exists
        else {
        	if (sessionFactoryName==null)
        		sessionFactoryName="";
        	session = currentMap.get(sessionFactoryName);
        	// no Session from the required SessionFactory
        	if (session==null) {
        		log.debug("Hibernate Session required from current thread but the SessionFactory from the Session of the current thread is not the same of the required SessionFactory (\""+sessionFactoryName+"\")");
        		session = getNewSession(sessionFactoryName);
        		currentMap.put(sessionFactoryName, session);
        	}
        	// there is a Session from the required SessionFactory
        	else {
        		log.debug("Existing Hibernate Session from current thread returned (SessionFactory \""+sessionFactoryName+"\")");        		
        	}
        }
        return session;
    }
    
    
    /**
     * Returns a new Hibernate Core Session, NOT the Session from the current thread (Current HTTP request). <br/>
     * If more than One SessionFactory is configured, the default SessionFactory if used as source to return the New Session.
     * @return The new Session object
     * @throws LoadSessionFromFactoryException 
     * @throws BuildSessionFactoryException 
     * @throws Exception
     */
    public static Session getNewSession() throws BuildSessionFactoryException, LoadSessionFromFactoryException {
    	return getNewSession(defaultSessionFactory);
    }
    /**
     * Returns a new Hibernate Core Session, NOT the Session of the current thread (Current HTTP request).
     * <br/>The plugin <b>does not commit</b> the transactions of this Session instance.
     * @param sessionFactoryName Name of the source SessionFactory (the same from <i>&ltsession-factory&gt</i> tag of a Hibernate configuration file).If <b>null</b>, the default SessionFactory is used to return the Session)
     * @return A Hibernate Core Session
     * @throws BuildSessionFactoryException 
     * @throws LoadSessionFromFactoryException 
     * @throws Exception
     */
    public static Session getNewSession(String sessionFactoryName) throws BuildSessionFactoryException, LoadSessionFromFactoryException  {
    	log.debug("New Hibernate Session required - SessionFactory required: "+( ( (sessionFactoryName==null) || (sessionFactoryName.equals("") )?"(default)":"\""+sessionFactoryName+"\"")));
    	if (factoriesMap.isEmpty()) {
			rebuildSessionFactory();
		}
    	
    	// at the first call from getNewSession() this test is need
		if (sessionFactoryName==null)
			sessionFactoryName = defaultSessionFactory;
		
		if ( (sessionFactoryName.equals("")) && (!factoriesMap.containsKey("")) )
			sessionFactoryName = defaultSessionFactory;
		
		try{	
	    	Session session = getSessionFactory(sessionFactoryName).openSession();
	    	log.debug("New Hibernate Session created and returned (SessionFactory \""+sessionFactoryName+"\")");
	    	
	    	return session;
		} catch (Exception e) {
			System.err.println("Could not load Hibernate Session from Full Hibernate Plugin's Session Factory");
			throw new LoadSessionFromFactoryException(e);
		}
		
    }
    
    /**
	 * Close all Hibernate Sessions from the current thread (Current HTTP request)
	 * @deprecated Use {@link #closeSessions()} instead (since v1.6)
	 */
	public static void closeSession() {
		closeSessions();
	}

	/**
     * Close all Hibernate Sessions from the current thread (Current HTTP request)
     */
    public static void closeSessions() {
    	for (Session session:sessionsMap.get().values()) {
	    	// If using the "<s:action" with the attribute "executeResult='true'"
	    	// the session can be closed before the end of this execution
	    	if ( (session!=null) && (session.isOpen()) ) {
	    		session.close();
	    	}
    	}
    	sessionsMap.set(null);
    	log.debug("Hibernate Sessions closed and discarted from current thread");
    	
    }
    
    /**
     * Close the Hibernate Session from parameter
     * @param session Hibernate Session to be closed
     */
    public static void closeSession(Session session) {
    	session.close();
    	log.debug("Hibernate Session closed");
    }

	/**
	 * Reload the session factories configurations according the configuration files
	 * @throws BuildSessionFactoryException 
	 * @throws Exception
	 */
	public synchronized static void rebuildSessionFactory() throws BuildSessionFactoryException {
		try {
			log.debug("Full Hibernate Plugin's Session Factory build started...");
			finishUnitOfWork();
			if (!factoriesMap.isEmpty())
				destroyFactory();
			String[] files = configFiles.split(",");
			if (files.length==0)
				throw new Exception("No configuration file for Hibernate");
			for (String file:files) {
				
				
				SessionFactory sessionFactory = createAndTestSessionFactory(file);
				
				Field nameField = sessionFactory.getClass().getDeclaredField("name");
				nameField.setAccessible(true);
				String sessionFactoryName = (String)nameField.get(sessionFactory);
				if (sessionFactoryName==null)
					sessionFactoryName = "";
				factoriesMap.put(sessionFactoryName, sessionFactory);
				log.debug("SessionFactory \""+sessionFactoryName+"\" configured from \""+file+"\" file");
				if (defaultSessionFactory==null) {
					setDefaultSessionFactory(sessionFactoryName);
				}
			}
			
			log.debug("Full Hibernate Plugin's Session Factory built successful");
		} catch (Exception e) {
			factoriesMap.clear();
			System.err.println("Could not build Full Hibernate Plugin's Session Factory");
			e.printStackTrace();
			throw new BuildSessionFactoryException(e);
		}
	}

	/**
	 * Create and test a Hibernate Core SessionFactory
	 * @param file - Full qualified file name 
	 * @return A new Hibernate Core SessionFactory
	 * @throws IOException for File not found or corrupted
	 * @throws HibernateException for invalid configuration
	 * @throws SQLException for General SQL Problems (ie. network connection)
	 */
	public static SessionFactory createAndTestSessionFactory(String file) throws IOException, HibernateException, SQLException {
		Configuration configuration = null;
		try {
			configuration = (Configuration) Class.forName("org.hibernate.cfg.AnnotationConfiguration").newInstance();
			log.debug("Full Hibernate Plugin's Session Factory using Hibernate Annotation Configuration");
		} catch (Exception e) {
			configuration = new Configuration();
			log.debug("Full Hibernate Plugin's Session Factory using Hibernate XML Configuration");
		}
		configuration.configure(file);
		log.debug("Full Hibernate Plugin's Session Factory configuration file \""+file+"\" configured");
		
		String fileProperties = file.substring(1,file.length()).replace(".cfg", "").replace("xml", "properties");
		Resource resource = new Resource(fileProperties);
		if (resource.getURL()!=null) {
			String fullpath = resource.getURL().getPath().replace("%20", " ");
			FileInputStream fis = new FileInputStream(fullpath);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();
			configuration.addProperties(properties);
			log.debug("Full Hibernate Plugin's Session Factory property file \"/"+fileProperties+"\" configured");
		}
		
		SessionFactory sessionFactory = configuration.buildSessionFactory();
		
		Session testSession = sessionFactory.openSession();
		testSession.connection().getMetaData().getCatalogs();
		testSession.close();
		return sessionFactory;
	}

	/**
	 * In Web Applications with Hibernate, when the context is reload, it's necessary to destroy the session factories to evict problems with connection pools, caches, and other third part Hibernate tools 
	 * @throws ClassNotFoundException 
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	public synchronized static void destroyFactory() {
		log.debug("Full Hibernate Plugin's Session Factory: destroy factory required...");
		defaultSessionFactory=null;
		closeAllSessionFactoriesAndClearMap();
		try {
			Class utilClass = Class.forName("com.googlecode.s2hibernate.struts2.plugin.util.C3P0Util", false, HibernateSessionFactory.class.getClassLoader());
			Method method = utilClass.getDeclaredMethod("closePolledDataSources");
			method.invoke(utilClass);
			log.debug("Full Hibernate Plugin's Session Factory: C3P0 Datasources Destroyed sucessful");
		} catch (Exception e) {	
			log.debug("Full Hibernate Plugin's Session Factory: C3P0 not found");
		} 		
		log.debug("Full Hibernate Plugin's Session Factory: All SessionFactories Destroyed sucessful");
	}

	public static void setConfigFiles(String configFiles) {
		if ((configFiles!=null) && (!configFiles.equals(""))) {
			HibernateSessionFactory.configFiles = configFiles;			
		}
		destroyFactory();
	}


	private synchronized static void closeAllSessionFactoriesAndClearMap() {
		for (SessionFactory sessionFactory:factoriesMap.values()) {
			if ((sessionFactory!=null) || (!sessionFactory.isClosed()) )  {
				sessionFactory.close();
			}
			sessionFactory = null;
		}
		factoriesMap.clear();
	}

	public static String getConfigFiles() {
		return configFiles;
	}

	/**
	 * Get the first configured SessionFactory found 
	 * @return The new Sessionfactory object
	 * @throws SessionFactoryNotFoundException 
	 * @throws Exception 
	 */
	public static SessionFactory getSessionFactory() throws SessionFactoryNotFoundException {
		return getSessionFactory(defaultSessionFactory);
	}
    
    /**
     * Get one of the configured SessionFactories
     * @param sessionFactoryName The SessionFactory name (the same from <i>&ltsession-factory&gt</i> tag of a Hibernate configuration file) 
     * @return The new Sessionfactory object
     * @throws SessionFactoryNotFoundException 
     * @throws Exception 
     */
    public static SessionFactory getSessionFactory(String sessionFactoryName) throws SessionFactoryNotFoundException {
    	if (factoriesMap.isEmpty())
    		return null;
    	SessionFactory sessionFactory = factoriesMap.get(sessionFactoryName);
    	if (sessionFactory==null) 
    		throw new SessionFactoryNotFoundException("No SessionFactory named \""+sessionFactoryName+"\" found. You should use "+factoriesMap.keySet());
    	return sessionFactory;
    }
    
    
    /**
     * Get the state of the SessionFactories 
     * @return <b>true</b> if there is at leaat One SessionFactory configured
     */
    public static boolean hasSessionFactories() {
    	return (!factoriesMap.isEmpty());
    }
	
	public static String getDefaultSessionFactory() {
		return defaultSessionFactory;
	}

	public static void setDefaultSessionFactory(String defaultSessionFactory) {
		if (defaultSessionFactory==null)
			defaultSessionFactory="";
		HibernateSessionFactory.defaultSessionFactory = defaultSessionFactory;
		log.debug("\""+defaultSessionFactory+"\" configured as the *default* SessionFactory of the Full Hibernate Plugin's Session Factory ");
	}

	public static boolean isAutoRebuidOnJdbcConnectionError() {
		return autoRebuidOnJdbcConnectionError;
	}

	public static void setAutoRebuidOnJdbcConnectionError(
			boolean autoRebuidOnJdbcConnectionError) {
		HibernateSessionFactory.autoRebuidOnJdbcConnectionError = autoRebuidOnJdbcConnectionError;
	}
	
	/**
	 * Finish the current thread's unit of work
	 */
	public static void finishUnitOfWork() {
		sessionsMap.set(null);
	}
	
	/**
	 * Tests the connection for the "/hibernate.cfg.xml" file
	 * @throws HibernateException
	 * @throws BuildSessionFactoryException
	 * @throws LoadSessionFromFactoryException
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void testConnection() throws HibernateException, BuildSessionFactoryException, LoadSessionFromFactoryException, SQLException, IOException {
		testConnection(DEFAULT_HIBERATE_CONFIGFILE);
	}

	/**
	 * Tests the connection for the file name in parameter
	 * @param fileName
	 * @throws HibernateException
	 * @throws BuildSessionFactoryException
	 * @throws LoadSessionFromFactoryException
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void testConnection(String fileName) throws HibernateException, BuildSessionFactoryException, LoadSessionFromFactoryException, SQLException, IOException {
		createAndTestSessionFactory(fileName).openSession().connection().getMetaData().getSchemas();
	}
   
}