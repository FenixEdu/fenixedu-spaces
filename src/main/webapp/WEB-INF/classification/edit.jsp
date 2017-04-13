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

<%-- <spring:url var="portalUrl" value="/bennu-portal"/> --%>

<link type="text/css" rel="stylesheet" href="${baseUrl}/css/dialogs.min.css"></link>

${portal.angularToolkit()}
<script>
    angular.module("bennuToolkit").config(['$httpProvider',function($httpProvider) {
        $httpProvider.defaults.headers.common = $httpProvider.defaults.headers.common || {};
        $httpProvider.defaults.headers.common['${csrf.headerName}'] = '${csrf.token}';
    }]);
</script>
<script type="text/javascript" src="${baseUrl}/js/angular-sanitize.min.js"></script>
<script type="text/javascript" src="${baseUrl}/js/angular-translate.min.js"></script>
<script type="text/javascript" src="${baseUrl}/js/angular-translate-loader-static-files.min.js"></script>

<script type="text/javascript" src="${baseUrl}/js/dialogs.min.js"></script>
<script type="text/javascript" src="${baseUrl}/js/ui-bootstrap.min.js"></script>

<spring:url var="baseUrl" value="/static/fenix-spaces"/>


<script type="text/javascript" src="${baseUrl}/js/editMetadata.js"></script>


<script type="text/javascript">
	window.informationName = ${information.localizedName.json()};
	window.specs = ${information.metadataSpec.toString()};
	window.code = "${information.code}";
	window.isAllocatable = ${information.isAllocatable};
	window.parent = "${information.parent}";
	window.locale = Bennu.locale.lang;
</script>
<div ng-app="metadataEdit" ng-controller="MetadataController">
<!-- Modal Dialog -->
<script type="text/ng-template" id="deleteModal.html">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"
			aria-hidden="true">&times;</button>
		<h4 class="modal-title">Delete Permanently</h4>
	</div>
	<div class="modal-body bg-danger">
		<p style="text-align: center">
			Are you sure you want to delete <b> {{field.name}} </b>?
		</p>
	</div>
	<div class="modal-footer">
		<button type="button" class="btn btn-default" ng-click='cancel()'><spring:message code="label.cancel"/></button>
		<button type="button" class="btn btn-danger" ng-click='confirm()'><spring:message code="label.delete"/></button>
	</div>
</script>
	<div class="page-header">
  <h1><fmt:message key="title.space.management"/><small><spring:message code="title.edit.classification"/></small></h1>
</div>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><spring:message code="label.spaces.classification.details"/></h3>
	</div>
		<div class="panel-body">
			<div class="container-fluid">
				<div class="row">
					<div class="col-md-2">
						<b><spring:message code="label.spaces.name" /></b>
					</div>
					<div class="col-md-10" id="theNameInput">
						<input required-any bennu-localized-string='informationName' type="text" />
					</div>
				</div>
				<c:choose>
					<c:when test="${classification.isRootClassification()}">
						<input type="hidden" id="classificationParentInput" ng-model="parent">
						<input type="hidden" id="classificationCodeInput" ng-model="code" />
					</c:when>
					<c:otherwise>
						<div class="row show-grid">
							<div class="col-md-2">
								<b><spring:message
										code="label.spaces.classification.parentClassification" /></b>
							</div>
							<div class="col-md-10">
								<c:set var="ParentClassId" value="${information.parent}" />
								<select class="form-control" id="classificationParentInput" ng-model="parent">
									<c:forEach var="classItem" items="${classifications}">
										<c:set var="classificationName"
											value="${classItem.absoluteCode} - ${classItem.name.content}" />
										<c:choose>
											<c:when test="${empty classItem.absoluteCode}">
												<c:set var="classificationName"
													value="${classItem.name.content}" />
											</c:when>
											<c:otherwise>
												<c:set var="classificationName"
													value="${classItem.absoluteCode} - ${classItem.name.content}" />
											</c:otherwise>
										</c:choose>
										<c:set var="classificationId" value="${classItem.externalId}" />
										<c:choose>										
											<c:when test="${classificationId == ParentClassId}">
												<option value="${classificationId}" selected="selected"><c:out value="${classificationName}"/></option>
											</c:when>
											<c:otherwise>
												<option value="${classificationId}"><c:out value="${classificationName}"/></option>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</select>
								<p class="help-block"></p>
							</div>
						</div>
						<div class="row show-grid">
							<div class="col-md-2">
								<b><spring:message code="label.spaces.classification.code" /></b>
							</div>
							<div class="col-md-10">
								<input class="form-control" type="text" required="required"
									id="classificationCodeInput" ng-model="code" />
							</div>
							<p />
						</div>
					</c:otherwise>
				</c:choose>
				<div class="row">
					<div class="col-md-2">
						<b><spring:message code="label.spaces.classification.allocatable" /></b>
					</div>
					<div class="col-md-10" id="theIsAllocatableInput">
						<input type="checkbox" ng-model="isAllocatable" />
					</div>
				</div>
			</div>
		</div>
	</div>

<div>
	<div class="panel panel-primary">
		<div class="panel-heading">
			<h3 class="panel-title"><spring:message code="label.spaces.classification.properties"/></h3>
		</div>
		
		<div class="panel-body" >
			<div class="container-fluid" id=fieldtable">
				<div class="row">
				  <div class="col-md-3"><b><spring:message code="label.spaces.name"/></b></div>
				  <div class="col-md-4"><b><spring:message code="label.spaces.classification.description"/></b></div>
				  <div class="col-md-1"><b><spring:message code="label.spaces.classification.type"/></b></div>
				  <div class="col-md-1"><b><spring:message code="label.spaces.classification.required"/></b></div>
				  <div class="col-md-2"><b><spring:message code="label.spaces.classification.defaultValue"/></b></div>
				  <div class="col-md-1"></div>
				</div>
<%-- 				ng-attr-title='{{fieldDef.inherited && "<spring:message code="label.spaces.classification.inherited.message"/>"}}' --%>
				<div ng-repeat="fieldDef in fieldDefs | orderBy:'inherited'">
				<div class="row"  ng-hide="fieldDef.inactive === true">
				  <div class="col-md-3">{{fieldDef.name}}</div>
				  <div class="col-md-4">
				  	<input type="text" bennu-localized-string="fieldDef.description" required-any class='form-control' ng-readonly='fieldDef.inherited'/>
				  </div>
					<div class="col-md-1">
				  	<select ng-model="fieldDef.type" ng-disabled='{{fieldDef.inherited}}' ng-checked='{{fieldDef.inherited}}'>
				  	 	<option ng-repeat="opt in options" value="{{opt.value}}" ng-selected="opt.value==fieldDef.type">{{opt.text}}</option>
				  	</select>
				  </div>
				  <div class="col-md-1">
				  <input type="checkbox" ng-disabled='{{fieldDef.inherited}}' ng-model="fieldDef.required"/>
				  </div>
				  <div class="col-md-2">
				  <input class="form-control" ng-model="fieldDef.defaultValue" ng-readonly='{{fieldDef.inherited}}'/>
				  </div>
				  <div class="col-md-1" ng-hide="fieldDef.inherited === true">
				  	<button class="btn btn-default" ng-click="removeField(fieldDef)"><spring:message code="label.spaces.classification.remove"/></button>
				  </div>
				  <div class="col-md-1" ng-show="fieldDef.inherited === true" ng-attr-title='{{fieldDef.inherited && "<spring:message code="label.spaces.classification.inherited.message"/>"}}'>
				  	<button disabled class="btn btn-warning" ng-click="removeField(fieldDef)"><spring:message code="label.spaces.classification.inherited"/></button>
				  </div>
				</div>
				</div>
			</div>
		</div>
	</div>
	<input type="text" id="newKey" placeholder="<spring:message code="label.newKey"/>" required="required" ng-model="newKey"/>
	<button class="btn btn-default" ng-click='addField()'><spring:message code="label.addfield"/></button>
</div>

<style>		
	.warning-class .modal-header {
		background-color: #c2741b;
	}

	.warning-class .modal-body .text-danger{
		text-color: #c2741b;
	}
</style>

<spring:url var="formActionUrl" value="${action}"/>
<button type="submit" class="btn btn-default" ng-click="submitInfo()"><spring:message code="label.submit"/></button>

</div>
