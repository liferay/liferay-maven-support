<#include "search_java_macros.ftl">
<@jspCopyrightsComment></@jspCopyrightsComment>
<%@ include file="${searchInitJsp}"%>
<%--
  * Note to developers, the portlet URLS mentioned below are just place holders
--%>
<%
  <@reqParams true></@reqParams>
  
  <@createPortletURL></@createPortletURL>
%>
<liferay-ui:header	title='${searchName?lower_case}-search'/>
<div class="${searchName?lower_case}-search">
  <liferay-portlet:renderURL varImpl="searchURL">
	<portlet:param name="redirect" value="<%= currentURL%>" />
  </liferay-portlet:renderURL>
  <aui:form action="<%= searchURL %>" method="post" name="searchFm">
     <%
         ${jSearchClass} searchContainer = new ${jSearchClass}(renderRequest, searchURL);
      %>
      <liferay-ui:search-form
	      page="${searchName?lower_case}_search_form.jsp"
	      searchContainer="<%= searchContainer %>"
	      servletContext="<%=this.getServletContext()%>">
      </liferay-ui:search-form> 
      <%
		${jSearchSearchTermsClass} searchTerms = (${jSearchSearchTermsClass})searchContainer.getSearchTerms();
      %>
      <%@ include file="${searchName}_search_results.jsp" %>
	  <div class="separator"><!-- --></div>
	    <liferay-ui:search-container-row
			className="${modelClass}"
			escapedModel="<%= true %>" keyProperty="${modelPK}" modelVar="rowObj">
		     <%@ include file="${searchName}_search_rows.jsp" %>
	    </liferay-ui:search-container-row>
      <liferay-ui:search-iterator searchContainer="<%= searchContainer %>" /> 
   </aui:form>
</div>  