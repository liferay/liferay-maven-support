<#include "search_java_macros.ftl">
<%@ include file="${searchInitJsp}"%>
<@jspCopyrightsComment></@jspCopyrightsComment>
<%
  /**
   * Note for Developers : 
   * Add the URLS to the actions below and add any other actions
   * that might me required   
   */
   boolean view =false; //indicates whether you are in VIEW mode of the portlet 
%>
<liferay-ui:icon-menu showExpanded="<%= view %>" showWhenSingleIcon="<%= view %>">
	<liferay-ui:icon image="edit" url="#"/>
	<liferay-ui:icon-delete url="#" />		
</liferay-ui:icon-menu>