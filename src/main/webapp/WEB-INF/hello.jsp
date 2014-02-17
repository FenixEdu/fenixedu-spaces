<!DOCTYPE html> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="page-header">
  <h1><spring:message code="title.space.management" text="Space Management"/><small><spring:message code="title.spaces" text="Spaces"/></small></h1>
</div>

<spring:url var="createUrl" value="/spaces/create"/>
<a href="${createUrl}" class="btn btn-success"><spring:message code="link.space.create" text="Create Space"></spring:message></a>
<table class="table">
	<thead>
		<tr>
			<th>Type</th>
			<th>Name</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="space" items="${spaces}">
			<spring:url value="/spaces/edit/${space.externalId}" var="editUrl"/>
			<tr>
				<td>${space.classification.code} - ${space.classification.name.content}</td>
				<td>${space.name}</td>
				<td><a href="${editUrl}" class="btn btn-primary"><spring:message code="link.space.edit" text="Edit Space"/></a></td>
			</tr>
		</c:forEach>
	</tbody>
</table>
