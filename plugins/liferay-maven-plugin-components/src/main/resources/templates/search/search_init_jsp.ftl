<#include "search_java_macros.ftl">
<@jspCopyrightsComment></@jspCopyrightsComment>
<!-- Liferay Taglibs -->
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>

<!-- Imports -->

<%@ page import="java.util.*"%>

<%@ page import="javax.portlet.*"%>

<!-- Liferay Portal imports -->
<%@ page import="com.liferay.portlet.PortletURLUtil"%>
<%@page import="com.liferay.portal.util.SessionClicks"%>
<%@page import="com.liferay.portal.util.PortalUtil"%>
<%@ page import="com.liferay.portlet.PortletPreferencesFactoryUtil"%>
<%@ page import="com.liferay.portal.kernel.util.*"%>
<%@ page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@ page import="com.liferay.portal.theme.PortletDisplay"%>
<%@ page import="com.liferay.portal.kernel.dao.search.DisplayTerms"%>
<%@ page import="com.liferay.portal.kernel.dao.search.SearchContainer"%>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@ page import="com.liferay.portal.kernel.servlet.SessionErrors"%>
<%@ page import="com.liferay.portal.theme.ThemeDisplay"%>
<!-- Search Container related imports -->
<#list imports as javaImport>
<%@ page import="${javaImport}"%>
</#list>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<%
   
    WindowState windowState = null;
    PortletMode portletMode = null;
    
    PortletURL currentURLObj = null;
    
    if (renderRequest != null) {
		windowState = renderRequest.getWindowState();
		portletMode = renderRequest.getPortletMode();
		
		currentURLObj = PortletURLUtil.getCurrent(renderRequest,
			renderResponse);
    } else if (resourceRequest != null) {
		windowState = resourceRequest.getWindowState();
		portletMode = resourceRequest.getPortletMode();
		
		currentURLObj = PortletURLUtil.getCurrent(resourceRequest,
			resourceResponse);
    }
    
    String currentURL = currentURLObj.toString();  
 
%>