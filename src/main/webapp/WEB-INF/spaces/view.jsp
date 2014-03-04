<!DOCTYPE html>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="page-header">
	<h1>
		<spring:message code="title.space.management" text="Space Management" />
		<small>${information.name}</small>
	</h1>
</div>

<%@include file="fragments/parent.jsp"%>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><spring:message code="title.sub.spaces" text="Sub Spaces" /></h3>
	</div>
	<div class="panel-body">
		<%@include file="fragments/spaces.jsp"%>
	</div>
</div>


<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><spring:message code="title.view.space" text="Space Details" /></h3>
	</div>
	<div class="panel-body">
		<table class="table">
			<tr>
				<th scope="row"><spring:message code="label.spaces.validFrom" text="Valid From" /></th>
				<td><spring:eval expression="information.validFrom" /></td>
			</tr>
			<tr>
				<th scope="row"><spring:message code="label.spaces.validUntil" text="Valid Until" /></th>
				<td><spring:eval expression="information.validUntil" /></td>
			</tr>
			<tr>
				<th scope="row"><spring:message code="label.spaces.name" text="Name" /></th>
				<td>${information.name}</td>
			</tr>
			<tr>
				<th scope="row"><spring:message code="label.spaces.identification" text="Identification" /></th>
				<td>${information.identification}</td>
			</tr>
			<tr>
				<th scope="row"><spring:message code="label.spaces.classification" text="Classification" /></th>
				<td>${information.classification.name.content}</td>
			</tr>
			<tr>
				<th scope="row"><spring:message code="label.spaces.allocatableCapacity" text="Allocatable Capacity" /></th>
				<td>${information.allocatableCapacity}</td>
			</tr>
			<tr>
				<th scope="row"><spring:message code="label.spaces.blueprintNumber" text="Blueprint Number" /></th>
				<td>${information.blueprintNumber}</td>
			</tr>
			<tr>
				<th scope="row"><spring:message code="label.spaces.blueprint" text="Blueprint" /></th>
				<c:url var="blueprintUrl" value="/spaces/blueprint/${space.externalId}" />
				<td><img src="${blueprintUrl}" /></td>
			</tr>
			<tr>
				<th scope="row"><spring:message code="label.spaces.area" text="Area" /></th>
				<td>${information.area}</td>
			</tr>
			<c:forEach var="metadata" items="${information.metadata}">
				<c:set var="field" value="${metadata.key}" />
				<c:set var="metadataSpec" value="${information.classification.getMetadataSpec(field)}" />
				<c:set var="value" value="${metadata.value}" />
				<tr>
					<th scope="row">${metadataSpec.description.content}</th>
					<td><c:choose>
							<c:when test="${value == 'true' }">
								<i class="glyphicon glyphicon-ok"></i>
							</c:when>
							<c:when test="${value == 'false' }">
								<i class="glyphicon glyphicon-remove"></i>
							</c:when>
							<c:otherwise>
						${value}
					</c:otherwise>
						</c:choose></td>
				</tr>
			</c:forEach>
		</table>
	</div>
</div>
