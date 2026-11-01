<%@ include file="/WEB-INF/jsp/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Instructors"/>
<c:set var="activeNav" value="instructors"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-heading">
    <h1>Instructors</h1>
    <a class="button" href="<c:url value='/instructors/new'/>">Add instructor</a>
</div>
<c:choose>
    <c:when test="${empty instructors}">
        <div class="empty-state">
            <p>No instructors have been added yet.</p>
            <p><a href="<c:url value='/instructors/new'/>">Add the first instructor</a> before scheduling workshops.</p>
        </div>
    </c:when>
    <c:otherwise>
        <p class="summary">${fn:length(instructors)} instructors, sorted by name.</p>
        <div class="table-wrap">
            <table>
                <caption class="visually-hidden">Instructors</caption>
                <thead>
                <tr>
                    <th scope="col">ID</th>
                    <th scope="col">Full name</th>
                    <th scope="col">Email</th>
                    <th scope="col">Speciality</th>
                    <th scope="col"><span class="visually-hidden">Actions</span></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="instructor" items="${instructors}">
                    <tr>
                        <td data-label="ID"><c:out value="${instructor.id}"/></td>
                        <td data-label="Full name"><c:out value="${instructor.fullName}"/></td>
                        <td data-label="Email"><c:out value="${instructor.email}"/></td>
                        <td data-label="Speciality"><c:out value="${instructor.speciality}"/></td>
                        <td class="actions">
                            <a href="<c:url value='/instructors/${instructor.id}/edit'/>">Edit<span class="visually-hidden"> <c:out value="${instructor.fullName}"/></span></a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
