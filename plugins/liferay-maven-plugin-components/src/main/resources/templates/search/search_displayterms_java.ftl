<#include "search_java_macros.ftl">

<@copyrightsComment></@copyrightsComment>

<#-- Define the imports-->
<#macro jImport>
	import javax.portlet.PortletRequest;
	import com.liferay.portal.kernel.dao.search.DisplayTerms;
	import com.liferay.portal.kernel.util.ParamUtil;
	<#list imports as javaImport>
  		import ${javaImport};
	</#list>
</#macro>

<@packageDef></@packageDef>

<@jImport></@jImport>

<@typeComment></@typeComment>
public class ${jClassName}DisplayTerms extends DisplayTerms {
        
	<@varDef></@varDef>
	    
   /**
    * @param portletRequest
    * @generated
    */
    public ${jClassName}DisplayTerms(PortletRequest portletRequest) {
	    super(portletRequest);
		<@reqParams false></@reqParams>
    }
	
	<@gettersSetters></@gettersSetters>
        
   /**
    * (non-Javadoc)
    * @see com.liferay.portal.kernel.dao.search.DisplayTerms#isAdvancedSearch()
    * @generated
    */
    @Override
    public boolean isAdvancedSearch() {
		return ${advancedSearch?string};
    }
    
}
