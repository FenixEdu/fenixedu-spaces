<!DOCTYPE html> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<script type="text/javascript">
/*! sprintf.js | Copyright (c) 2007-2013 Alexandru Marasteanu <hello at alexei dot ro> | 3 clause BSD license */(function(e){function r(e){return Object.prototype.toString.call(e).slice(8,-1).toLowerCase()}function i(e,t){for(var n=[];t>0;n[--t]=e);return n.join("")}var t=function(){return t.cache.hasOwnProperty(arguments[0])||(t.cache[arguments[0]]=t.parse(arguments[0])),t.format.call(null,t.cache[arguments[0]],arguments)};t.format=function(e,n){var s=1,o=e.length,u="",a,f=[],l,c,h,p,d,v;for(l=0;l<o;l++){u=r(e[l]);if(u==="string")f.push(e[l]);else if(u==="array"){h=e[l];if(h[2]){a=n[s];for(c=0;c<h[2].length;c++){if(!a.hasOwnProperty(h[2][c]))throw t('[sprintf] property "%s" does not exist',h[2][c]);a=a[h[2][c]]}}else h[1]?a=n[h[1]]:a=n[s++];if(/[^s]/.test(h[8])&&r(a)!="number")throw t("[sprintf] expecting number but found %s",r(a));switch(h[8]){case"b":a=a.toString(2);break;case"c":a=String.fromCharCode(a);break;case"d":a=parseInt(a,10);break;case"e":a=h[7]?a.toExponential(h[7]):a.toExponential();break;case"f":a=h[7]?parseFloat(a).toFixed(h[7]):parseFloat(a);break;case"o":a=a.toString(8);break;case"s":a=(a=String(a))&&h[7]?a.substring(0,h[7]):a;break;case"u":a>>>=0;break;case"x":a=a.toString(16);break;case"X":a=a.toString(16).toUpperCase()}a=/[def]/.test(h[8])&&h[3]&&a>=0?"+"+a:a,d=h[4]?h[4]=="0"?"0":h[4].charAt(1):" ",v=h[6]-String(a).length,p=h[6]?i(d,v):"",f.push(h[5]?a+p:p+a)}}return f.join("")},t.cache={},t.parse=function(e){var t=e,n=[],r=[],i=0;while(t){if((n=/^[^\x25]+/.exec(t))!==null)r.push(n[0]);else if((n=/^\x25{2}/.exec(t))!==null)r.push("%");else{if((n=/^\x25(?:([1-9]\d*)\$|\(([^\)]+)\))?(\+)?(0|'[^$])?(-)?(\d+)?(?:\.(\d+))?([b-fosuxX])/.exec(t))===null)throw"[sprintf] huh?";if(n[2]){i|=1;var s=[],o=n[2],u=[];if((u=/^([a-z_][a-z_\d]*)/i.exec(o))===null)throw"[sprintf] huh?";s.push(u[1]);while((o=o.substring(u[0].length))!=="")if((u=/^\.([a-z_][a-z_\d]*)/i.exec(o))!==null)s.push(u[1]);else{if((u=/^\[(\d+)\]/.exec(o))===null)throw"[sprintf] huh?";s.push(u[1])}n[2]=s}else i|=2;if(i===3)throw"[sprintf] mixing positional and named placeholders is not (yet) supported";r.push(n)}t=t.substring(n[0].length)}return r};var n=function(e,n,r){return r=n.slice(0),r.splice(0,0,e),t.apply(null,r)};e.sprintf=t,e.vsprintf=n})(typeof exports!="undefined"?exports:window);
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

<spring:url var="formActionUrl" value="${action}"/>
<form:form modelAttribute="information" role="form" method="post" action="${formActionUrl}" enctype="multipart/form-data">
  <div class="form-group">
    <form:label for="validFromInput" path="validFrom" >Valid From</form:label>
    <form:input type="date" class="form-control" id="validFromInput" path="validFrom" placeholder="Valid From" required="required"/>
  </div>
  <div class="form-group">
    <form:label for="validUntilInput" path="validUntil">Valid Until</form:label>
    <form:input type="date" class="form-control" id="validUntilInput" path="validUntil" placeholder="Valid Until"/>
  </div>
  <div class="form-group">
    <form:label for="nameInput" path="name">Name</form:label>
    <form:input type="text" class="form-control" id="nameInput" path="name" placeholder="Name" required="required"/>
  </div>
  <div class="form-group">
    <form:label for="identificationInput" path="identification">Identification</form:label>
    <form:input type="text" class="form-control" id="identificationInput" path="identification" placeholder="identification"/>
  </div>
  <div class="form-group">
    <form:label for="classificationInput" path="classification">Classification</form:label>
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
    <form:label for="allocatableCapacityInput" path="allocatableCapacity">Allocatable Capacity</form:label>
    <form:input type="number" class="form-control" id="allocatableCapacityInput" path="allocatableCapacity" min="0" placeholder="Allocatable Capacity"/>
  </div>
  <div class="form-group">
    <form:label for="blueprintFileInput" path="blueprintMultipartFile">Blueprint Number</form:label>
    <form:input type="file" class="form-control" id="blueprintFileInput" path="blueprintMultipartFile"/>
  </div>
  <div class="form-group">
    <form:label for="blueprintNumberInput" path="blueprintNumber">Blueprint Number</form:label>
    <form:input type="number" class="form-control" id="blueprintNumberInput" path="blueprintNumber" min="0" placeholder="Blueprint Number"/>
  </div>
  <div class="form-group">
    <form:label for="areaInput" path="area">Area</form:label>
    <form:input type="number" class="form-control" id="areaInput" path="area" min="0" step="any" placeholder="Physical Area"/>
  </div>
  <button type="submit" class="btn btn-default">Submit</button>
</form:form>
