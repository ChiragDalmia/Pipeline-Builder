<%@ include file="/WEB-INF/jsp/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Record not found"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<h1>Record not found</h1>
<p><c:out value="${message}"/></p>
<p><a href="<c:url value='/instructors'/>">View instructors</a> or <a href="<c:url value='/workshops'/>">view workshops</a>.</p>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
