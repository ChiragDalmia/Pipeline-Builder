<%@ include file="/WEB-INF/jsp/common/taglibs.jspf" %>
<c:set var="editing" value="${not empty workshopId}"/>
<c:set var="pageTitle" value="${editing ? 'Edit workshop' : 'Add workshop'}"/>
<c:set var="activeNav" value="workshops"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<p class="back-link"><a href="<c:url value='/workshops'/>">&larr; Back to workshops</a></p>
<h1><c:out value="${pageTitle}"/></h1>
<c:choose>
<c:when test="${empty instructors}">
    <div class="empty-state">
        <p>A workshop needs an instructor, and none have been added yet.</p>
        <p><a href="<c:url value='/instructors/new'/>">Add an instructor</a> first.</p>
    </div>
</c:when>
<c:otherwise>
<c:url var="formAction" value="${editing ? '/workshops/'.concat(workshopId) : '/workshops'}"/>
<form:form modelAttribute="workshopForm" action="${formAction}" method="post" cssClass="record-form" novalidate="novalidate">
    <form:errors element="div" cssClass="alert alert-error" role="alert"/>
    <c:if test="${requestScope['org.springframework.validation.BindingResult.workshopForm'].errorCount > 0}">
        <p class="form-hint-error" role="alert">Please correct the highlighted fields.</p>
    </c:if>
    <div class="field-row">
        <div class="field">
            <form:label path="code">Workshop code</form:label>
            <form:input path="code" maxlength="20" cssErrorClass="invalid" cssClass="short"
                        aria-describedby="code-hint code-error"/>
            <p class="hint" id="code-hint">Letters, numbers and hyphens, for example CK-101. Stored in capitals.</p>
            <form:errors path="code" cssClass="field-error" id="code-error" element="p"/>
        </div>
        <div class="field">
            <form:label path="scheduledDate">Date</form:label>
            <form:input path="scheduledDate" type="date" cssErrorClass="invalid" aria-describedby="scheduledDate-error"/>
            <form:errors path="scheduledDate" cssClass="field-error" id="scheduledDate-error" element="p"/>
        </div>
    </div>
    <div class="field">
        <form:label path="title">Title</form:label>
        <form:input path="title" maxlength="120" cssErrorClass="invalid" aria-describedby="title-error"/>
        <form:errors path="title" cssClass="field-error" id="title-error" element="p"/>
    </div>
    <div class="field-row">
        <div class="field">
            <form:label path="durationMinutes">Duration (minutes)</form:label>
            <form:input path="durationMinutes" type="number" min="15" max="480" step="5" cssErrorClass="invalid"
                        cssClass="short" aria-describedby="durationMinutes-hint durationMinutes-error"/>
            <p class="hint" id="durationMinutes-hint">Between 15 and 480.</p>
            <form:errors path="durationMinutes" cssClass="field-error" id="durationMinutes-error" element="p"/>
        </div>
        <div class="field">
            <form:label path="capacity">Participant capacity</form:label>
            <form:input path="capacity" type="number" min="1" max="200" cssErrorClass="invalid"
                        cssClass="short" aria-describedby="capacity-hint capacity-error"/>
            <p class="hint" id="capacity-hint">Between 1 and 200.</p>
            <form:errors path="capacity" cssClass="field-error" id="capacity-error" element="p"/>
        </div>
    </div>
    <div class="field">
        <form:label path="instructorId">Instructor</form:label>
        <form:select path="instructorId" cssErrorClass="invalid" aria-describedby="instructorId-error">
            <form:option value="">Choose an instructor</form:option>
            <c:forEach var="instructor" items="${instructors}">
                <form:option value="${instructor.id}"><c:out value="${instructor.fullName} (${instructor.speciality})"/></form:option>
            </c:forEach>
        </form:select>
        <form:errors path="instructorId" cssClass="field-error" id="instructorId-error" element="p"/>
    </div>
    <div class="form-actions">
        <button type="submit" class="button">${editing ? 'Save changes' : 'Add workshop'}</button>
        <a href="<c:url value='/workshops'/>">Cancel</a>
    </div>
</form:form>
</c:otherwise>
</c:choose>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
