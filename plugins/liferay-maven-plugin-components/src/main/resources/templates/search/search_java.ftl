<#include "search_java_macros.ftl">
<@copyrightsComment></@copyrightsComment>

<#-- Define the imports-->
<#macro jImport>
	import java.util.*;
	import javax.portlet.PortletRequest;
	import javax.portlet.PortletURL;	
	import com.liferay.portal.kernel.util.ParamUtil;
	import com.liferay.portal.kernel.dao.search.SearchContainer;
	<#list imports as javaImport>
  		import ${javaImport};
	</#list>
</#macro>

<@packageDef></@packageDef>

<@jImport></@jImport>

<#if modelClass??>
   <#assign packageFragments = modelClass?split(".")>
<#else>
  <#assign packageFragments = "java.lang.String"?split(".")>
</#if>

<@typeComment></@typeComment>
public class ${jClassName}Search extends SearchContainer<${packageFragments?last}> {
	
	private static String EMPTY_RESULTS_MESSAGE = "${emptyResultsMessage}";
	
    /**
     * @param portletRequest
     * @generated
     */
    public ${jClassName}Search(PortletRequest portletRequest,
	    PortletURL iteratorURL,List<String> columnNames) {
	    super(portletRequest, new ${jClassName}DisplayTerms(portletRequest),
		new ${jClassName}SearchTerms(portletRequest), DEFAULT_CUR_PARAM,
		${delta}, iteratorURL, columnNames, EMPTY_RESULTS_MESSAGE);
		
		${jClassName}DisplayTerms displayTerms = (${jClassName}DisplayTerms) getDisplayTerms();
		
		<#list searchParameters as searchParameter>
		    <#assign  fieldName    = searchParameter.getFieldName()>
		    <#assign  fieldType    = searchParameter.getFieldType()>
		    <#assign  defaultValue = searchParameter.getDefaultValue()>
			iteratorURL.setParameter(${jClassName}DisplayTerms.${fieldName?upper_case},
				String.valueOf(displayTerms.get${fieldName?cap_first}()));
		</#list>  		
    }	
}
