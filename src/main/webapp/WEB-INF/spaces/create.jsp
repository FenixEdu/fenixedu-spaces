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
 ${portal.bennuPortal()}
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
		
		function getLocalString(jsonString){
			var myLocale = Bennu.locale.tag;
			if(jsonString[myLocale]!=undefined){
				return jsonString[myLocale];
			}
			for(var i = 0; i< Bennu.locales.length; i++){
				if(jsonString[Bennu.locales[i].tag]!= undefined){
					return jsonString[Bennu.locales[i].tag];
				}
			}
			for(key in jsonString) if (Object.prototype.hasOwnProperty.call(jsonString,key)) return jsonString[key];
		}
		
		function loadClassification(value, loaded){
			
			if (value != selectedClassification && loaded) {
				$("input[name^=metadata]").parent().remove();
				selectedClassification = value;
			}
			var spec = specs[value];
			$(spec).each(function() {
				var formGroup = $("<div class='form-group'>");
				var label = $(sprintf("<label for='%s'>%s</label>", this.name, getLocalString(this.description)));
				var type = "text";
				var required = this.required ? "required" : "";
				if (this.type === "java.lang.Boolean") {
					type = "checkbox";
					required = "";
				}
				if (this.type === "java.lang.Integer") {
					type = "number";
				}
				if (this.type === "org.joda.time.DateTime") {
					type = "date";
				}
				var thisValue = thisSpec[this.name];
				var theValue = this.defaultValue;
				if(this.invalid === true && thisValue === undefined){
					return;
				}
				if(thisValue != undefined){
					theValue = thisValue;					
				}
				var checked = "";
				if(type === "checkbox" && theValue === true){
					checked = 'checked="checked"';
				}

				var input = $(sprintf("<input type='%s' name=\"metadata['%s']\" class='form-control' value='%s', %s %s/>", type, this.name, theValue, required, checked));
				formGroup.append(label);
				formGroup.append(input);
				$($("form[role=form] .form-group").last()).after(formGroup);
			});
			
		}
		$("#blueprintFrame").toggle();
		
		$("#toggleBluePrint").click(function(e) {
			e.preventDefault();
			if($("#blueprintFrame:visible").size()){
				$("#toggleBluePrint").text('<spring:message code="label.spaces.blueprint.show"/>');
			}else{
				$("#toggleBluePrint").text('<spring:message code="label.spaces.blueprint.hide"/>');
			}
			
			$("#blueprintFrame").toggle();
		});
	});
	
</script>

<div class="page-header">
  <h1><fmt:message key="title.space.management"/><small><spring:message code="title.create.space"/></small></h1>
</div>

<%@include file="fragments/parent.jsp" %>

<spring:url var="formActionUrl" value="${action}"/>
<form:form modelAttribute="information" role="form" method="post" action="${formActionUrl}" enctype="multipart/form-data">
    ${csrf.field()}
  <div class="form-group">
    <form:label for="validFromInput" path="validFrom"><spring:message code="label.spaces.validFrom"/></form:label>
    <form:input type="date" class="form-control" id="validFromInput" path="validFrom" placeholder="Valid From" required="required"/>
  </div>
  <div class="form-group">
    <form:label for="validUntilInput" path="validUntil"><spring:message code="label.spaces.validUntil"/></form:label>
    <form:input type="date" class="form-control" id="validUntilInput" path="validUntil" placeholder="Valid Until"/>
  </div>
  <div class="form-group">
    <form:label for="nameInput" path="name"><spring:message code="label.spaces.name"/></form:label>
    <form:input type="text" class="form-control" id="nameInput" path="name" placeholder="Name" required="required"/>
  </div>
  <div class="form-group">
    <form:label for="identificationInput" path="identification"><spring:message code="label.spaces.identification"/></form:label>
    <form:input type="text" class="form-control" id="identificationInput" path="identification" placeholder="identification"/>
  </div>
  <div class="form-group">
    <form:label for="classificationInput" path="classification"><spring:message code="label.spaces.classification"/></form:label>
    <form:select class="form-control" id="classificationInput" path="classification">
    	<c:forEach var="classification" items="${classifications}">
    	<c:set var="classificationName" value="${classification.absoluteCode} - ${classification.name.content}"/>
    	<c:if test="${empty classification.absoluteCode}">
    		<c:set var="classificationName" value="${classification.name.content}"/>
    	</c:if>
    	<c:set var="classificationId" value="${classification.externalId}"/>
    		<c:choose>
    			<c:when test="${classificationId == information.classification.externalId}">
    				<form:option value="${classificationId}" selected="selected"><c:out value="${classificationName}"/></form:option>
    			</c:when>
				<c:otherwise>  
					<form:option value="${classificationId}"><c:out value="${classificationName}"/></form:option>
				</c:otherwise>
			</c:choose>
    	</c:forEach>
    </form:select>
  </div>
  <div class="form-group">
    <form:label for="allocatableCapacityInput" path="allocatableCapacity"><spring:message code="label.spaces.allocatableCapacity"/></form:label>
    <form:input type="number" class="form-control" id="allocatableCapacityInput" path="allocatableCapacity" min="0" placeholder="Allocatable Capacity"/>
  </div>
  <div class="form-group">
    <form:label for="blueprintFileInput" path="blueprintMultipartFile"><spring:message code="label.spaces.blueprint"/></form:label>
    <form:input type="file" class="form-control" id="blueprintFileInput" path="blueprintMultipartFile"/>
    <c:if test="${information.blueprint != null}">
	  <div div class="form-group has-warning">
	    <p class="help-block has-warning">
	    	<spring:message code="label.spaces.replaceBlueprintWarning"/>
	    	<a href="#" class="btn btn-link" id="toggleBluePrint"><spring:message code="label.spaces.blueprint.show"/></a>
	    </p>
	  </div>
	  <spring:url var="blueprintUrl" value="/spaces-view/blueprint/${space.externalId}?viewDoorNumbers=false&amp;viewBlueprintNumbers=false&amp;viewIdentifications=false&amp;viewOriginalSpaceBlueprint=true&amp;scale=100"/>
	  <div id="blueprintFrame">
	  	<img src="${blueprintUrl}" usemap="#roomLinksMap">
	  </div>
    </c:if>
  </div>
  <div class="form-group">
    <form:label for="blueprintNumberInput" path="blueprintNumber"><spring:message code="label.spaces.blueprintNumber"/></form:label>
    <form:input class="form-control" id="blueprintNumberInput" path="blueprintNumber" min="0" placeholder="Blueprint Number"/>
  </div>
  <div class="form-group">
    <form:label for="areaInput" path="area"><spring:message code="label.spaces.area"/></form:label>
    <form:input type="number" class="form-control" id="areaInput" path="area" min="0" step="any" placeholder="Physical Area"/>
  </div>
  <button type="submit" class="btn btn-default"><spring:message code="label.submit"/></button>
</form:form>
