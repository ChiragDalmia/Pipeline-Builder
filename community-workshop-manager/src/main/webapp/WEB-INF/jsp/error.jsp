<%@ include file="/WEB-INF/jsp/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Something went wrong"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<c:choose>
    <c:when test="${status == 404}">
        <h1>Page not found</h1>
        <p>The page you asked for does not exist.</p>
    </c:when>
    <c:otherwise>
        <h1>Something went wrong</h1>
        <p>The request could not be completed<c:if test="${not empty status}"> (status <c:out value="${status}"/>)</c:if>. Please try again.</p>
    </c:otherwise>
</c:choose>
<p><a href="<c:url value='/overview'/>">Go to the schedule overview</a></p>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
