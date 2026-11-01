<%@ include file="/WEB-INF/jsp/common/taglibs.jspf" %>
<c:set var="pageTitle" value="Schedule overview"/>
<c:set var="activeNav" value="overview"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<h1>Schedule overview</h1>
<p class="intro">Each row joins a workshop with the instructor who teaches it. Instructors without a scheduled
    workshop do not appear here; they are listed on the <a href="<c:url value='/instructors'/>">Instructors</a> page.</p>
<c:choose>
    <c:when test="${empty rows}">
        <div class="empty-state">
            <p>No workshops are scheduled, so there is nothing to show yet.</p>
            <p><a href="<c:url value='/workshops/new'/>">Add a workshop</a> to see it here.</p>
        </div>
    </c:when>
    <c:otherwise>
        <p class="summary">${fn:length(rows)} scheduled workshops.</p>
        <div class="table-wrap">
            <table>
                <caption class="visually-hidden">Workshops with their instructors</caption>
                <thead>
                <tr>
                    <th scope="col">Date</th>
                    <th scope="col">Code</th>
                    <th scope="col">Workshop</th>
                    <th scope="col" class="num">Duration</th>
                    <th scope="col" class="num">Capacity</th>
                    <th scope="col">Instructor</th>
                    <th scope="col">Speciality</th>
                    <th scope="col">Contact</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="row" items="${rows}">
                    <tr>
                        <td data-label="Date" class="nowrap"><c:out value="${row.scheduledDate()}"/></td>
                        <td data-label="Code"><span class="code"><c:out value="${row.workshopCode()}"/></span></td>
                        <td data-label="Workshop"><c:out value="${row.workshopTitle()}"/></td>
                        <td data-label="Duration" class="num"><c:out value="${row.durationMinutes()}"/> min</td>
                        <td data-label="Capacity" class="num"><c:out value="${row.capacity()}"/></td>
                        <td data-label="Instructor"><c:out value="${row.instructorName()}"/></td>
                        <td data-label="Speciality"><c:out value="${row.speciality()}"/></td>
                        <td data-label="Contact"><c:out value="${row.instructorEmail()}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
