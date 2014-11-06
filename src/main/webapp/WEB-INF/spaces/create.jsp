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

<spring:url var="baseUrl" value="/static/fenix-spaces"/>

<script type="text/javascript" src="${baseUrl}/js/sprintf.min.js">
</script>
<script type="text/javascript">
var thisSpec = {};
 
 <c:if test="${ null != information}">
 	thisSpec = ${information.rawMetadata};
 </c:if>

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
		loadClassification(selectedClassification,false);
		$("#classificationInput").change(function() {
			loadClassification(this.value,true);
		});
		function loadClassification(value, loaded){
			if (value != selectedClassification && loaded) {
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
				var thisValue = thisSpec[this.name];
				var theValue = this.defaultValue;
				if(thisValue != undefined){
					theValue = thisValue;					
				}
				var checked = "";
				if(type == "checkbox" && theValue == true){
					checked = 'checked="checked"';
				}
				var input = $(sprintf("<input type='%s' name=\"metadata['%s']\" class='form-control' value='%s', %s %s/>", type, this.name, theValue, required, checked));
				formGroup.append(label);
				formGroup.append(input);
				$($("form[role=form] .form-group").last()).after(formGroup);
			});
			
		}
		$("#BlueprintFrame").toggle();
	});
	
	function toggleBlueprint(){
		if($("#toggleBluePrint").text() == ' [<spring:message code="label.spaces.blueprint.hide" text="hide"/>]'){
			$("#toggleBluePrint").text(' [<spring:message code="label.spaces.blueprint.show" text="show"/>]');
		}else{
			$("#toggleBluePrint").text(' [<spring:message code="label.spaces.blueprint.hide" text="hide"/>]');
		}
		$("#BlueprintFrame").toggle();
	}
</script>

<div class="page-header">
  <h1><fmt:message key="title.space.management"/><small><spring:message code="title.create.space" text="Criar/Editar Espaço"/></small></h1>
</div>

<%@include file="fragments/parent.jsp" %>

<spring:url var="formActionUrl" value="${action}"/>
<form:form modelAttribute="information" role="form" method="post" action="${formActionUrl}" enctype="multipart/form-data">
  <div class="form-group">
    <form:label for="validFromInput" path="validFrom"><spring:message code="label.spaces.validFrom" text="Valid From"/></form:label>
    <form:input type="date" class="formtitle.create.space-control" id="validFromInput" path="validFrom" placeholder="Valid From" required="required"/>
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
    	<c:set var="classificationName" value="${classification.absoluteCode} - ${classification.name.content}"/>
    	<c:set var="classificationId" value="${classification.externalId}"/>
    		<c:choose>
    			<c:when test="${classificationId == information.classification.externalId}">
    				<form:option value="${classificationId}" selected="selected">${classificationName}</form:option>
    			</c:when>
				<c:otherwise>  
					<form:option value="${classificationId}">${classificationName}</form:option>
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
    <c:if test="${information.blueprint != null}">
	  <div div class="form-group has-warning">
	    <p class="help-block has-warning"><spring:message code="label.spaces.replaceBlueprintWarning" text="This space already has a blueprint. Selecting a new one will replace it"/><span style="color:grey" id="toggleBluePrint" onClick="toggleBlueprint()"> [<spring:message code="label.spaces.blueprint.show" text="show"/>]</span></p>
	  </div>
	  <div id="BlueprintFrame">
	  	<img src="/fenix/spaces-view/blueprint/${space.externalId}?viewDoorNumbers=false&amp;viewBlueprintNumbers=false&amp;viewIdentifications=false&amp;viewOriginalSpaceBlueprint=true&amp;scale=100" usemap="#roomLinksMap">
	  </div>
    </c:if>
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
