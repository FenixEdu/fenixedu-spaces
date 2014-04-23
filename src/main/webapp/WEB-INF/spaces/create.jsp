<!DOCTYPE html> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:url var="baseUrl" value="/static/fenix-spaces"/>

<script type="text/javascript" src="${baseUrl}/js/sprintf.min.js">
</script>

<script type="text/javascript">
	$(document).ready(function() {
		var specs = [];
		var selectedClassification = $("#classificationInput").val();
		<c:forEach var="classification" items="${classifications}">
			<c:set var="spec" value="${classification.metadataSpec}"/>
			<c:set var="id" value="${classification.externalId}"/>
			<c:if test="${classification.metadataSpec == null}">
				<c:set var="spec" value="[]"/>	
			</c:if>
			<c:out value="specs['${id}'] = ${spec};" escapeXml="false"/>
		</c:forEach>
		$("#classificationInput").change(function() {
			var value = this.value;
			if (value != selectedClassification) {
				$("input[name^=metadata]").parent().remove();
				selectedClassification = value;
			}
			var spec = specs[value];
			$(spec).each(function() {
				var formGroup = $("<div class='form-group'>");
				var label = $(sprintf("<label for='%s'>%s</label>", this.name, this.description["pt-PT"]));
				var type = "text";
				var required = this.required ? "required" : "";
				if (this.type === "java.lang.Boolean") {
					type = "checkbox";
					required = "";
				}
				if (this.type === "java.lang.Integer") {
					type = "number";
				}
				
				var input = $(sprintf("<input type='%s' name=\"metadata['%s']\" class='form-control' value='%s', %s/>", type, this.name, this.defaultValue, required));
				formGroup.append(label);
				formGroup.append(input);
				$($("form[role=form] .form-group").last()).after(formGroup);
			});
		});
	});
</script>

<div class="page-header">
  <h1><fmt:message key="title.space.management"/><small><fmt:message key="title.create.space"/></small></h1>
</div>

<%@include file="fragments/parent.jsp" %>

<spring:url var="formActionUrl" value="${action}"/>
<form:form modelAttribute="information" role="form" method="post" action="${formActionUrl}" enctype="multipart/form-data">
  <div class="form-group">
    <form:label for="validFromInput" path="validFrom"><spring:message code="label.spaces.validFrom" text="Valid From"/></form:label>
    <form:input type="date" class="form-control" id="validFromInput" path="validFrom" placeholder="Valid From" required="required"/>
  </div>
  <div class="form-group">
    <form:label for="validUntilInput" path="validUntil"><spring:message code="label.spaces.validUntil" text="Valid Until"/></form:label>
    <form:input type="date" class="form-control" id="validUntilInput" path="validUntil" placeholder="Valid Until"/>
  </div>
  <div class="form-group">
    <form:label for="nameInput" path="name"><spring:message code="label.spaces.name" text="Name"/></form:label>
    <form:input type="text" class="form-control" id="nameInput" path="name" placeholder="Name" required="required"/>
  </div>
  <div class="form-group">
    <form:label for="identificationInput" path="identification"><spring:message code="label.spaces.identification" text="Identification"/></form:label>
    <form:input type="text" class="form-control" id="identificationInput" path="identification" placeholder="identification"/>
  </div>
  <div class="form-group">
    <form:label for="classificationInput" path="classification"><spring:message code="label.spaces.classification" text="Classification"/></form:label>
    <form:select class="form-control" id="classificationInput" path="classification">
    	<c:forEach var="classification" items="${classifications}">
    	<c:set var="classificationName" value="${classification.name.content}"/>
    	<c:set var="classificationId" value="${classification.externalId}"/>
    		<c:choose>
    			<c:when test="${classificationId == information.classification}">
    				<form:option value="${classificationId}">${classificationName}</form:option>
    			</c:when>
				<c:otherwise>
					<form:option value="${classificationId}" selected="selected">${classificationName}</form:option>
				</c:otherwise>
			</c:choose>
    	</c:forEach>
    </form:select>
  </div>
  <div class="form-group">
    <form:label for="allocatableCapacityInput" path="allocatableCapacity"><spring:message code="label.spaces.allocatableCapacity" text="Allocatable Capacity"/></form:label>
    <form:input type="number" class="form-control" id="allocatableCapacityInput" path="allocatableCapacity" min="0" placeholder="Allocatable Capacity"/>
  </div>
  <div class="form-group">
    <form:label for="blueprintFileInput" path="blueprintMultipartFile"><spring:message code="label.spaces.blueprint" text="Blueprint"/></form:label>
    <form:input type="file" class="form-control" id="blueprintFileInput" path="blueprintMultipartFile"/>
  </div>
  <div class="form-group">
    <form:label for="blueprintNumberInput" path="blueprintNumber"><spring:message code="label.spaces.blueprintNumber" text="Blueprint Number"/></form:label>
    <form:input type="number" class="form-control" id="blueprintNumberInput" path="blueprintNumber" min="0" placeholder="Blueprint Number"/>
  </div>
  <div class="form-group">
    <form:label for="areaInput" path="area"><spring:message code="label.spaces.area" text="Area"/></form:label>
    <form:input type="number" class="form-control" id="areaInput" path="area" min="0" step="any" placeholder="Physical Area"/>
  </div>
  <button type="submit" class="btn btn-default"><spring:message code="label.submit" text="Submit"/></button>
</form:form>
