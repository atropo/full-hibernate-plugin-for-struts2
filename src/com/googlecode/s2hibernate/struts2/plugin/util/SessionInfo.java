package com.googlecode.s2hibernate.struts2.plugin.util;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.hibernate.Session;

public class SessionInfo {
	
	Session hibernateSession;
	Date creationTime;
	HttpSession httpSession;
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SessionInfo))
			return false;
		SessionInfo info = (SessionInfo) obj;
		Boolean equals = info.getHibernateSession().hashCode()==hibernateSession.hashCode(); 
		return equals;
	}
	
	/**
	 * @param hibernateSession
	 * @param creationTime
	 * @param httpSession
	 */
	public SessionInfo(Session hibernateSession, Date creationTime,
			HttpSession httpSession) {
		super();
		this.hibernateSession = hibernateSession;
		this.creationTime = creationTime;
		this.httpSession = httpSession;
	}
	public HttpSession getHttpSession() {
		return httpSession;
	}
	public void setHttpSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}
	public Session getHibernateSession() {
		return hibernateSession;
	}
	public void setHibernateSession(Session hibernateSession) {
		this.hibernateSession = hibernateSession;
	}
	public Date getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	
}
