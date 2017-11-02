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
<script src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>

</script>
	<spring:url var="staticUrl" value="/static/fenix-spaces"/>
<script src="${staticUrl}/js/sprintf.min.js"></script>
<script src="${staticUrl}/js/dateutils.js"></script>
<script src="${staticUrl}/js/groupParse.js"></script>
${portal.angularToolkit()}
<script>
    angular.module("bennuToolkit").config(['$httpProvider',function($httpProvider) {
        $httpProvider.defaults.headers.common = $httpProvider.defaults.headers.common || {};
        $httpProvider.defaults.headers.common['${csrf.headerName}'] = '${csrf.token}';
    }]);
</script>
<script src="${staticUrl}/js/ui-bootstrap-tpls-0.12.0.min.js"></script>
<script src="${staticUrl}/js/groupEdit.js"></script>
<div class="page-header">
  <h1><spring:message code="title.space.management"/><small><spring:message code="title.space.access.management"/></small></h1>
</div>
<div>
	<h3>
		<spring:message code="label.space"/>: <c:out value="${space.presentationName}"/>
	</h3>
</div>
<p>
<div ng-app="groupEdit" ng-controller="editGroupController">
<spring:url var="formActionUrl" value="${action}"/>
<form:form modelAttribute="spacebean" role="form" method="post"  action="${formActionUrl}" enctype="multipart/form-data">
	${csrf.field()}
	<div class="form-group">
		<h4>
			<spring:message code="label.space.access.occupations.group" />
		</h4>
		<c:out value="${spacebean.occupationGroup.presentationName}"/>
	</div>
	
	<input type="hidden" id="occupationExpression" name="occupationExpression" value="${spacebean.occupationExpression}"/>
	<button type="button" class="btn btn-default" ng-click="open('#occupationExpression')">
    	<spring:message code="label.manage"/>
    </button>
	<div class="form-group">
		<h4>
			<spring:message code="label.space.access.management.group" />
		</h4>
		<c:out value="${spacebean.managementGroup.presentationName}"/>
	</div>
	<input type="hidden" id="managementExpression" name="managementExpression" value="${spacebean.managementExpression}"/>
	<button type="button" class="btn btn-default" ng-click="open('#managementExpression')">
        <spring:message code="label.manage"/>
    </button>
	
	<p class="help-block"> </p>
	<button type="submit" class="btn btn-default">
		<spring:message code="label.submit" />
	</button>
</form:form>

<script>
 Bennu.group.allow['spaceSuperUsers'] = {
         name: "Space Managers",
         group: "#spaceSuperUsers",
         icon: "glyphicon glyphicon-globe"
     };
</script>


<script type="text/ng-template" id="myModalContent.html">
	  <style>
		span.step {
          background: #cccccc;
          border-radius: 1.5em;
          -moz-border-radius: 1.5em;
          -webkit-border-radius: 1.5em;
          color: #ffffff;
          display: inline-block;
          font-weight: bold;
          line-height: 2.5em;
          margin-right: 1px;
          text-align: center;
          width: 2.5em; 
        }
	  </style>
      <div class="modal-header">
        <button type="button" class="close" aria-hidden="true" ng-click="cancel()">&times;</button>
        <h4 class="modal-title"><spring:message code="label.groups.manage"/></h4>
      </div>
	  <div class="modal-body" ng-show="groups === undefined">
	  	<div class="alert alert-danger" role="alert">
			<strong><spring:message code="label.error"/>:</strong> 
			<spring:message code="label.groups.complex"/>
	    </div>
	  </div>
      <div class="modal-body" ng-show="groups !== undefined">
				<ul id="myTab" class="nav nav-tabs nav-justified" role="tablist">
					<li role="presentation" class="active"><a href="#home"
						id="home-tab" role="tab" data-toggle="tab" aria-controls="home"
						aria-expanded="true"><spring:message code="label.add.users"/></a></li>
					<li role="presentation" class=""><a href="#profile" role="tab"
						id="profile-tab" data-toggle="tab" aria-controls="profile"
						aria-expanded="false"><spring:message code="label.add.groups"/></a></li>
				</ul>
				<div id="myTabContent" class="tab-content">
				  
			      <div role="tabpanel" class="tab-pane fade active in" id="home" aria-labelledby="home-tab">
					<p>
					<div class="input-group" style="width: 100%">
				    	<input bennu-user-autocomplete="selectedUser" class="form-control" placeholder="utilizador"/>
						<span class="input-group-btn">
							<button class="btn btn-default" ng-click="addUser()"><spring:message code="label.add"/></button>  
						</span>
					</div>
					</p>
				</div>
			      
				  <div role="tabpanel" class="tab-pane fade" id="profile" aria-labelledby="profile-tab">
                  <p>
				  <div class="input-group">
				  <select class="form-control" ng-model="selectedGroup">
				  <c:forEach var="mngmntGroup" items="${managementGroups}">
				  	<option value="${mngmntGroup.expression()}"><c:out value="${mngmntGroup.presentationName}"/></option>
				  </c:forEach>
				  </select>
				  <span class="input-group-btn">
				  <button class="btn btn btn-default" ng-click="addGroup()"><spring:message code="label.add"/></button> 
					</span>
			      </div>
			    </p>
				</div>
				
				<p></p>
				<p>
					<small><b><spring:message code="label.groups.elements"/></b></small> 
				</p>			
				<div class="bennu-group-list"
					style="height: 149px; overflow: auto; border: 1px solid #ddd; margin-top: 20px;">
					<table class="table table-striped" >
						<tr ng-repeat="group in groups">
							<td class="col-md-2"><span class="step">{{group.type | uppercase | limitTo : 1}}</span></td>
							<td class="col-md-8 text-center">{{group.name}}</td>
							<td class="col-md-2">
								<button class="btn btn-danger" ng-click="remove($index)"><spring:message code="label.spaces.classification.remove"/></button>
							</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
		<div class="modal-footer">
            <button class="btn btn-primary" ng-click="ok()" ng-show="groups !== undefined"><spring:message code="label.ok"/></button>
            <button class="btn btn-default" ng-click="cancel()"><spring:message code="label.cancel"/></button>
        </div>
    
</script>
</div>
</p>
