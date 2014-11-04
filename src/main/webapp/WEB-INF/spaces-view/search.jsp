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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:url var="searchUrl" value="/spaces-view/search/"/>

<div class="page-header">
  <h1><spring:message code="title.space.management" text="Space Management"/><small><spring:message code="title.spaces.search" text="Pesquisa de Espaços"/></small></h1>
</div>

<form id="searchForm" class="form-inline" role="form" action="${searchUrl}" method="GET">
  		<div class="form-group">
   		  <label class="sr-only" for="searchSpace"><spring:message code="label.search" text="Nome"/></label>
    	  <input name="name" type="text" class="form-control" id="searchSpace" value="${name}" placeholder="<spring:message code="label.space.search.name" text="Nome do espaço"/>"></input>
  		</div>
  		 <button id="searchRequest" class="btn btn-default"><spring:message code="label.search" text="Procurar"/></button>
  	</form>

<c:choose>
	<c:when test="${not empty foundSpaces}">
			<table class="table">
				<thead>
					<tr>
						<th><spring:message code="label.spaces.type" text="Type"/></th>
						<th><spring:message code="label.spaces.name" text="Name"/></th>
						<th><spring:message code="label.spaces.number.sub.spaces" text="Number of Sub Spaces"/></th>
						<th><spring:message code="label.spaces.operations" text="Operations"/></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="space" items="${foundSpaces}">
						<spring:url value="/spaces-view/view/${space.externalId}" var="viewUrl" />
						<spring:url value="/spaces-view/schedule/${space.externalId}" var="scheduleUrl" />
						<spring:url value="/spaces/edit/${space.externalId}" var="editUrl" />
						<spring:url value="/spaces/timeline/${space.externalId}" var="timelineUrl" />
						<spring:url value="/spaces-view/view/${space.externalId}" var="viewUrl" />
						<spring:url value="/spaces/create/${space.externalId}" var="createSubSpaceUrl" />
						<spring:url value="/spaces/access/${space.externalId}" var="manageAccessUrl" />
						<spring:url value="/spaces/occupants/${space.externalId}" var="manageOccupantsUrl" />
						<tr>
							<td>${space.classification.name.content}</td>
							<td>${space.fullName}</td>
							<td>${fn:length(space.children)}</td>
							<td>
								<a href="${viewUrl}" class="btn btn-default" title="View"><span class="glyphicon glyphicon-eye-open"></span></a>
								<c:if test="${not empty space.occupationSet}">
									<a href="${scheduleUrl}" class="btn btn-default" title="<spring:message code="title.space.schedule" text="Horário do Espaço"/>"><span class="glyphicon glyphicon-dashboard"></span></a>
								</c:if>
								<c:if test="${space.isSpaceManagementMember(currentUser)}">
									<a href="${timelineUrl}" class="btn btn-default" title="Timeline"><span class="glyphicon glyphicon-time"></span></a>
									<a href="${editUrl}" class="btn btn-default" title="Edit"><span class="glyphicon glyphicon-pencil"></span></a>
									<a href="${createSubSpaceUrl}" class="btn btn-default" title="Create"><span class="glyphicon glyphicon-plus-sign"></span></a>
									<a href="${manageAccessUrl}" class="btn btn-default" title="Access"><span class="glyphicon glyphicon-lock"></span></a>
									<a href="${manageOccupantsUrl}" class="btn btn-default" title="Occupants"><span class="glyphicon glyphicon-user"></span></a>
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:when>
	<c:otherwise>
			<h3><spring:message code="label.empty.spaces" text="No available spaces." /></h3>
	</c:otherwise>
</c:choose>