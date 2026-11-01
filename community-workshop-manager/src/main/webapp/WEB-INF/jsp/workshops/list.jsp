<%@ include file="/WEB-INF/jsp/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Workshops"/>
<c:set var="activeNav" value="workshops"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-heading">
    <h1>Workshops</h1>
    <a class="button" href="<c:url value='/workshops/new'/>">Add workshop</a>
</div>
<c:choose>
    <c:when test="${empty workshops}">
        <div class="empty-state">
            <p>No workshops are scheduled.</p>
            <p><a href="<c:url value='/workshops/new'/>">Add a workshop</a> to start building the programme.</p>
        </div>
    </c:when>
    <c:otherwise>
        <p class="summary">${fn:length(workshops)} workshops, earliest first.</p>
        <div class="table-wrap">
            <table>
                <caption class="visually-hidden">Workshops</caption>
                <thead>
                <tr>
                    <th scope="col">Code</th>
                    <th scope="col">Title</th>
                    <th scope="col">Date</th>
                    <th scope="col" class="num">Duration</th>
                    <th scope="col" class="num">Capacity</th>
                    <th scope="col">Instructor</th>
                    <th scope="col"><span class="visually-hidden">Actions</span></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="workshop" items="${workshops}">
                    <tr>
                        <td data-label="Code"><span class="code"><c:out value="${workshop.code}"/></span></td>
                        <td data-label="Title"><c:out value="${workshop.title}"/></td>
                        <td data-label="Date" class="nowrap"><c:out value="${workshop.scheduledDate}"/></td>
                        <td data-label="Duration" class="num"><c:out value="${workshop.durationMinutes}"/> min</td>
                        <td data-label="Capacity" class="num"><c:out value="${workshop.capacity}"/></td>
                        <td data-label="Instructor"><c:out value="${workshop.instructor.fullName}"/></td>
                        <td class="actions">
                            <a href="<c:url value='/workshops/${workshop.id}/edit'/>">Edit<span class="visually-hidden"> <c:out value="${workshop.code}"/></span></a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
