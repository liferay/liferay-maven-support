<#include "search_java_macros.ftl">
<@jspCopyrightsComment></@jspCopyrightsComment>
<%
 /**
   * Note to developers : Please
   */
  results = <%=Collections.EMPTY_LIST%>; // Call to the Service to get the records
  int total = results.getLength(); // The number of records  returned by the search
  searchContainer.setTotal(total);
%>