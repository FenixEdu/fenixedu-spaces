<%--

    Copyright © 2014 Instituto Superior Técnico

    This file is part of FenixEdu Spaces.

    FenixEdu Spaces is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FenixEdu Spaces is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FenixEdu Spaces.  If not, see <http://www.gnu.org/licenses/>.

--%>
<!DOCTYPE html> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="page-header">
  <h1><spring:message code="title.space.management.occupations"/><small><spring:message code="title.create.occupation"/></small></h1>
</div>

<spring:url var="createUrl" value="/spaces/occupations/requests/my/create"/>

<form:form modelAttribute="occupation" role="form" method="post" action="${createUrl}">
	${csrf.field()}
	<div class="form-group">
	    <form:label for="campusInput" path="campus"><spring:message code="label.occupations.campus"/></form:label>
	    <form:select class="form-control" id="campusInput" path="campus">
			<option value="">---</option>	    
	    	<c:forEach var="campi" items="${campus}">
		    	<c:set var="campusName" value="${campi.name}"/>
		    	<c:set var="campusId" value="${campi.externalId}"/>
				<form:option value="${campusId}"><c:out value="${campusName}"/></form:option>
	    	</c:forEach>
	    </form:select>
  </div>
	<div class="form-group">
		<form:label for="subjectInput" path="subject"><spring:message code="label.occupations.subject"/></form:label>
		<form:input type="text" class="form-control" id="subjectInput" path="subject" placeholder="Subject" required="required"/>
	</div>
	<div class="form-group">
		<form:label for="descriptionInput" path="description"><spring:message code="label.occupations.description"/></form:label>
		<form:textarea rows="10" cols="60" class="form-control" id="descriptionInput" path="description" placeholder="Description" required="required"/>
	</div>
	<button type="submit" class="btn btn-success"><spring:message code="label.submit"/></button>
</form:form>