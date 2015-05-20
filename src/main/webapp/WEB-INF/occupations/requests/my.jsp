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

<spring:url var="requestUrl" value="/spaces/occupations/requests/my"/>
<spring:url var="createUrl" value="/spaces/occupations/requests/my/create"/>


<div class="page-header">
  	<h1><spring:message code="title.view.my.occupations"/></h1>
	
	<p><a class="btn btn-primary" href="${createUrl}"><spring:message code="link.occupations.create.request"></spring:message></a></p>
	
	
  	<c:if test="${not empty requests.pageList}">
  	   	<c:set var="searchPageUrl" value="${searchUrl}${searchId}"/>
  	   	<h3><spring:message code="title.view.my.occupations.list"/></h3>
  		<ul class="pagination">
	  		<li><a href="${searchPageUrl}?p=f">&laquo;</a></li>
  			<c:forEach var="page" begin="${requests.firstLinkedPage}" end="${requests.lastLinkedPage}">
  				<c:set var="pageNumber" value="${page+1}"/>
  				<c:if test="${page == requests.page}">
  					<li class="active"><a href="${searchPageUrl}?p=${pageNumber}">${pageNumber}</a></li>
  				</c:if>
  				<c:if test="${page != requests.page}">
  					<li><a href="${searchPageUrl}?p=${pageNumber}">${pageNumber}</a></li>
  				</c:if>
  			</c:forEach>
	  		<li><a href="${searchPageUrl}?p=l">&raquo;</a></li>
		</ul>
	  	<table class="table">
	  		<thead>
	  			<th><spring:message code="label.occupation.request.instant" /></th>
	  			<th><spring:message code="label.occupation.request.subject" /></th>
	  			<th><spring:message code="label.occupation.request.state" /></th>
	  			<th><spring:message code="label.occupation.request.occupations" /></th>
	  			<th><spring:message code="label.occupation.request.new.comments" /></th>
	  		</thead>
	  		<tbody>
	  			<c:forEach var="occupationRequest" items="${requests.pageList}">
					<c:set var="instant" value="${occupationRequest.presentationInstant}" />
					<c:set var="subject" value="${occupationRequest.subject}" />
					<c:set var="state" value="${occupationRequest.currentState}" />
					<c:set var="occupations" value="${occupationRequest.occupationSet}"/>
					<c:set var="numberOfUnreadComments" value="${occupationRequest.getNumberOfNewComments(requestor)}" />
					<tr>
						<td>
							<c:out value="${instant}"/>
						</td>
						<td>
							<a href="${requestUrl}/${occupationRequest.externalId}">
								<c:out value="${subject}"/>
							</a>
						</td>
						<td><spring:message code="OccupationRequestState.${state}"/></td>
						<td>
							<c:if test="${empty occupations}">
								<spring:message code="label.no"/>
							</c:if>
							<c:if test="${not empty occupations}">
								<spring:message code="label.yes"/>
							</c:if>
						</td>
	 					<td><c:out value="${numberOfUnreadComments}"/></td>
					</tr>
				</c:forEach>
			</tbody>
	   	</table>
  	</c:if>
  	
  	<c:if test="${empty requests.pageList}">
  		<em><spring:message code="space.occupations.no.requests"></spring:message></em>
  	</c:if>
</div>

