<#assign jClassName=searchName?cap_first>
<#assign jSearchClass=searchName?cap_first+"Search">
<#assign jSearchDisplayTermsClass=searchName?cap_first+"DisplayTerms">
<#assign jSearchSearchTermsClass=searchName?cap_first+"SearchTerms">
<#assign searchInitJsp=searchName?lower_case+"_search_init.jsp">

<#-- Extract the request params-->
<#macro reqParams isJSP>
	<#list searchParameters as searchParameter>
	  <#assign  fieldName    = searchParameter.getFieldName()>
	  <#assign  fieldType    = searchParameter.getFieldType()>
	  <#assign  defaultValue = searchParameter.getDefaultValue()>
	   <#-- TODO: handle date type and corresponding methods -->
	  <#if (fieldType?lower_case) = "date">
	${fieldName?uncap_first} = ParamUtil.getDate(portletRequest, ${fieldName?upper_case}, DateFormat.getDateInstance( DateFormat.MEDIUM, Locale.getDefault()));
	  <#else>
	${fieldName?uncap_first} = ParamUtil.get${fieldType}(portletRequest, ${fieldName?upper_case});
	  </#if>
    <#if searchParameter_has_next>
     
    </#if>   
	</#list>  
</#macro>

<#-- Set Request params-->
<#macro createPortletURL>
	//Note to Developers: Add other paramters as required
	PortletURL portletURL = renderResponse.createRenderURL();
	<#list searchParameters as searchParameter>
	  <#assign  fieldName    = searchParameter.getFieldName()>
	  <#assign  fieldType    = searchParameter.getFieldType()>
	  <#assign  defaultValue = searchParameter.getDefaultValue()>
    portletURL.setParameter("${fieldName?uncap_first}", String.valueOf(${fieldName?uncap_first}));
    </#list>
    portletURL.setParameter("redirect", currentURL);
	${"\n"}
</#macro>

<#-- Define the variables-->
<#macro varDef>
	<#list searchParameters as searchParameter>
	    <#assign  fieldName    = searchParameter.getFieldName()>
	    <#assign  fieldType    = searchParameter.getFieldType()>
	    <#assign  defaultValue = searchParameter.getDefaultValue()>
		 public static String ${fieldName?upper_case} = "${fieldName}";
		  
		 protected ${fieldType} ${fieldName};
	</#list>  
</#macro>

<#-- Define the getters/setters-->
<#macro gettersSetters>
	<#list searchParameters as searchParameter>
	   <#assign  fieldName    = searchParameter.getFieldName()>	
	   <#assign jMethodName=fieldName?cap_first>
	   <#assign  fieldType    = searchParameter.getFieldType()>
       <#compress>
		   /**
			* @return the ${fieldName}
			* @generated
            */
			public ${fieldType} get${jMethodName}() {
			    return ${fieldName};
			}
			
		   /**
			* set the ${fieldName}
			* @generated
            */
			public void set${jMethodName}(${fieldType} ${fieldName}) {
			   this.${fieldName}=${fieldName};
			}
		 </#compress>	
	</#list>  
</#macro>

<#-- Package Def -->
<#macro packageDef>
 <#if packageName?exists>
  package ${packageName?lower_case};
 </#if>
</#macro> 

<#-- type comment -->
<#macro typeComment>
/**
 *
 * @author ${statics["java.lang.System"].getProperty("user.name")}
 *
 * @generated
 */
</#macro>

<#-- copyrightsComment -->
<#macro copyrightsComment>
/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
</#macro>

<#macro jspCopyrightsComment>
<%--
   * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
   *
   * This library is free software; you can redistribute it and/or modify it under
   * the terms of the GNU Lesser General Public License as published by the Free
   * Software Foundation; either version 2.1 of the License, or (at your option)
   * any later version.
   *
   * This library is distributed in the hope that it will be useful, but WITHOUT
   * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
   * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
   * details.
--%>
</#macro>