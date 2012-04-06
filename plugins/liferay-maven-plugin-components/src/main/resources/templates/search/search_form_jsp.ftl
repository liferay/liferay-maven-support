<#include "search_java_macros.ftl">
<@jspCopyrightsComment></@jspCopyrightsComment>
<%@ include file="${searchName}_search_init.jsp"%>
<%--
   * Note to Developers
   * 
   *
--%>
<%
${jSearchClass} searchContainer = (${jSearchClass})request.getAttribute("liferay-ui:search:searchContainer");

${jSearchDisplayTermsClass} displayTerms = (${jSearchDisplayTermsClass})searchContainer.getDisplayTerms();
%>

<liferay-ui:search-toggle
	id="toggle_id_asset_search"
	displayTerms="<%= displayTerms %>"
	buttonLabel="search">
	<aui:fieldset>
	<#list searchParameters as searchParameter>
	  <#assign  fieldName    = searchParameter.getFieldName()>
	  <#assign  fieldType    = searchParameter.getFieldType()>
	  <#assign  defaultValue = searchParameter.getDefaultValue()>
	  <#-- TODO: handle date type and corresponding methods -->
	  <aui:input name="<%= displayTerms.${fieldName?upper_case} %>" size="20" type="text" value="<%= displayTerms.get${fieldName?cap_first}() %>" />
	 </#list>  	
	</aui:fieldset>
</liferay-ui:search-toggle>