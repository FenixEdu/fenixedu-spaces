<!DOCTYPE html> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="page-header">
  <h1><spring:message code="title.space.management.occupations" text="Space Occupation Management"/><small><spring:message code="title.create.occupation" text="Create Occupation Request"/></small></h1>
</div>

<spring:url var="formActionUrl" value="/spaces/occupations/requests/create"/>

<form:form modelAttribute="occupation" role="form" method="post" action="${formActionUrl}">
	<div class="form-group">
	    <form:label for="campusInput" path="campus"><spring:message code="label.occupations.campus" text="Campus"/></form:label>
	    <form:select class="form-control" id="campusInput" path="campus">
			<option value="">---</option>	    
	    	<c:forEach var="campi" items="${campus}">
		    	<c:set var="campusName" value="${campi.name}"/>
		    	<c:set var="campusId" value="${campi.externalId}"/>
				<form:option value="${campusId}">${campusName}</form:option>
	    	</c:forEach>
	    </form:select>
  </div>
	<div class="form-group">
		<form:label for="subjectInput" path="subject"><spring:message code="label.occupations.subject" text="Subject"/></form:label>
		<form:input type="text" class="form-control" id="subjectInput" path="subject" placeholder="Subject" required="required"/>
	</div>
	<div class="form-group">
		<form:label for="descriptionInput" path="description"><spring:message code="label.occupations.description" text="Description"/></form:label>
		<form:textarea rows="10" cols="60" class="form-control" id="descriptionInput" path="description" placeholder="Description" required="required"/>
	</div>
	<button type="submit" class="btn btn-success"><spring:message code="label.submit" text="Submit"/></button>
</form:form>