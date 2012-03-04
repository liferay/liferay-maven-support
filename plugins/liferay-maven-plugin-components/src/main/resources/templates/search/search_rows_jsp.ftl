<#include "search_java_macros.ftl">
<@jspCopyrightsComment></@jspCopyrightsComment>
<%
   /**
    * Note to Developers: Please update the rowHREF with a valid URL
    */
   String rowHREF="#";
   String align ="right"
%>
<liferay-ui:search-container-row className="${modelClass}"
		escapedModel="<%= true %>" keyProperty="${modelPK}" modelVar="row">
    <#list searchParameters as searchParameter>
      <#assign  fieldName    = searchParameter.getFieldName()>	
	  <#assign  jMethodName  = "get"+fieldName?cap_first+"()">
	  <#assign  fieldType    = searchParameter.getFieldType()>
	  <#assign  showable     = searchParameter.isShowable()>
	  <#if showable>
       <liferay-ui:search-container-column-text
				href="<%= rowHREF %>"
				name="<%= fieldName%>"
				value="<%= HtmlUtil.escape(row.${jMethodName}) %>"/>
	  </#if>
	  			
	</#list>			
    <liferay-ui:search-container-column-jsp
				align="<%=align%>"
				path="${searchName?lower_case}_row_actions.jsp"/>
</liferay-ui:search-container-row>