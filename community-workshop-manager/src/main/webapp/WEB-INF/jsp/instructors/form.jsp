<%@ include file="/WEB-INF/jsp/common/taglibs.jspf" %>
<c:set var="editing" value="${not empty instructorId}"/>
<c:set var="pageTitle" value="${editing ? 'Edit instructor' : 'Add instructor'}"/>
<c:set var="activeNav" value="instructors"/>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<p class="back-link"><a href="<c:url value='/instructors'/>">&larr; Back to instructors</a></p>
<h1><c:out value="${pageTitle}"/></h1>
<c:url var="formAction" value="${editing ? '/instructors/'.concat(instructorId) : '/instructors'}"/>
<form:form modelAttribute="instructorForm" action="${formAction}" method="post" cssClass="record-form" novalidate="novalidate">
    <form:errors element="div" cssClass="alert alert-error" role="alert"/>
    <c:if test="${requestScope['org.springframework.validation.BindingResult.instructorForm'].errorCount > 0}">
        <p class="form-hint-error" role="alert">Please correct the highlighted fields.</p>
    </c:if>
    <div class="field">
        <form:label path="fullName">Full name</form:label>
        <form:input path="fullName" maxlength="100" autocomplete="name" cssErrorClass="invalid"
                    aria-describedby="fullName-error"/>
        <form:errors path="fullName" cssClass="field-error" id="fullName-error" element="p"/>
    </div>
    <div class="field">
        <form:label path="email">Email address</form:label>
        <form:input path="email" type="email" maxlength="120" autocomplete="email" cssErrorClass="invalid"
                    aria-describedby="email-hint email-error"/>
        <p class="hint" id="email-hint">Each instructor needs a different email address.</p>
        <form:errors path="email" cssClass="field-error" id="email-error" element="p"/>
    </div>
    <div class="field">
        <form:label path="speciality">Speciality</form:label>
        <form:input path="speciality" maxlength="80" cssErrorClass="invalid"
                    aria-describedby="speciality-hint speciality-error"/>
        <p class="hint" id="speciality-hint">For example: Home Cooking, First Aid, Digital Skills.</p>
        <form:errors path="speciality" cssClass="field-error" id="speciality-error" element="p"/>
    </div>
    <div class="form-actions">
        <button type="submit" class="button">${editing ? 'Save changes' : 'Add instructor'}</button>
        <a href="<c:url value='/instructors'/>">Cancel</a>
    </div>
</form:form>
<%@ include file="/WEB-INF/jsp/common/footer.jspf" %>
