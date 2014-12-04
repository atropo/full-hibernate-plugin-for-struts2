<%@ page contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="/struts-tags" prefix="s" %>

<html>
	<head>
		<s:head/>
		<meta name="author" content="José Yoshiriro - jyoshiriro@gmail.com" />
		<title><s:text name="hibernateplugin.plugin_name"/></title>
		
		<style>
				.visible{
					display: inherit;
				}
				.hidden{
					display: none;;
				}
				.hlink:link{
				text-decoration: none;
				color:navy;
			}
		</style>
		<script type="text/javascript">
			function showHideDetais(id) {
				visual = document.getElementById('div'+id).className;
				if (visual=='visible') {
					document.getElementById('div'+id).className='hidden';
				} else {
					document.getElementById('div'+id).className='visible';
				}
			}
		</script>
	</head>

<body style="font-family: arial">

<p><font color="#002d59">
<strong><em>
<a href="http://code.google.com/p/full-hibernate-plugin-for-struts2/" target="_blank"><s:text name="hibernateplugin.plugin_name"/></a><br>
</em></strong>
</font></p>

<h2>
	<font color="#643200">
	<s:text name="hibernateplugin.management_title"/>
	</font>
	<s:if test="publicAccessEnabled">
		<br/><i style="color:maroon;font-size:13px;"><s:text name="hibernateplugin.alert_manager_public_access_enabled"/></i>
	</s:if>
</h2>

<s:actionmessage/>
<s:actionerror/>
<s:fielderror/>

<hr/>
<s:text name="hibernateplugin.configuration_files_title"/>:<br/>
<s:url id="urlIndex" action="index"/>
<a href="${urlIndex}" style="font-size:11px;">(<s:text name="hibernateplugin.try_update_configurationfiles_content"/>)</a>

<s:iterator value="configurations" id="conf" status="st">
	<p>
		<b> ${st.index+1}. <a class="hlink" href="javascript:;" onclick="showHideDetais(${st.index})">${conf.fileName}</a></b>
		<s:form action="testConnection" namespace="/hibernateManager" theme="simple">
			<s:hidden name="configFileName" value="%{#conf.fileName}"></s:hidden>
			<s:submit key="hibernateplugin.test_connection_button" cssStyle="margin-bottom: 16px;" theme="simple"></s:submit>
		</s:form>
		<div id="div${st.index}" style="margin-left: 30px;width:70%;font-family:courier-new;background-color:#EEEEEE;padding:7px;" class="hidden">
			<b>${conf.content}</b>
		</div>
	</p>
	<s:if test="#attr.st.last">
		<hr/>
	</s:if>
</s:iterator>



	<p>
		<s:url id="urlReload" action="reloadConfiguration" namespace="/hibernateManager"></s:url>
		&bull; <b><a href="${urlReload}"><s:text name="hibernateplugin.reload_hibernate_configuration_button"/></a></b>
	</p>
	<p>
		<s:url id="urlOpenSessions" action="viewOpenSessions" namespace="/hibernateManager"></s:url>
		&bull; <b><a href="${urlOpenSessions}"><s:text name="hibernateplugin.manage_open_sessions_button"/></a></b>
	</p>
	
	<s:if test="viewOpenSessions">
		<i><b><s:property value="%{sessions.size()}"/></b> <s:text name="hibernateplugin.open_sessions"/></i>
		<s:form action="closeHibernateSessions" namespace="/hibernateManager" theme="simple">
			<s:hidden name="sessionids" value="0"/>
			<table cellpadding="4" cellspacing="4">
				<s:iterator value="sessions" id="info" status="st">
					<s:if test="#st.first">
						<tr><td colspan="4">
						<s:submit key="hibernateplugin.close_all_sessions_button" name="closeAll"></s:submit> &nbsp;&nbsp;
						<s:submit key="hibernateplugin.close_selected_sessions_button" name="closeSelected"></s:submit> <br/>
						</td></tr>
						<tr>
							<td>#</td>
							<td><b><s:text name="hibernateplugin.hibernate_session_creation_time"/></b></td>
							<td><b><s:text name="hibernateplugin.http_session_id"/></b></td>
							<td><b><s:text name="hibernateplugin.hibernate_session"/></b></td>
						</tr>
					</s:if>
					<tr valign="top">
						<td>
							<s:checkbox name="sessionids" fieldValue="%{#attr.info.hibernateSession.hashCode()}"></s:checkbox>
						</td>
						<td>
							<s:date name="#attr.info.creationTime"/>
							<br/> <i>(<s:date name="#attr.info.creationTime" nice="true"/>)</i>
						</td>
						<td>
							${info.httpSession.id}
						</td>
						<td>
							${info.hibernateSession}
						</td>
					</tr>
				</s:iterator>
			</table>
		</s:form>
	</s:if>

<p>&nbsp;</p>

<hr/>
<div style="font-size: 11">
By <a href="mailto:jyoshiriro@gmail.com">José Yoshiriro</a> &copy; 2009
</div>
</body>
</html>