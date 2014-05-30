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

<spring:url var="exportExcelUrl" value="/spaces/occupations/requests/export"/>
<spring:url var="filterUrl" value="/spaces/occupations/requests/filter"/>
<spring:url var="withoutFilterUrl" value="/spaces/occupations/requests"/>
<spring:url var="viewRequestsUrl" value="/spaces/occupations/requests/"/>
<spring:url var="searchUrl" value="/spaces/occupations/requests/search/"/>
<spring:url var="requestUrl" value="/spaces/occupations/requests"/>

<script>
$(document).ready(function() {
	$("#filterByCampus").change(function(e) {
		var campus = $("#filterByCampus option:selected").val();
		if (campus === "") {
			location.href="${withoutFilterUrl}/";
		}else {
			location.href="${filterUrl}/" + campus;
		}
	});
	<c:if test="${not empty selectedCampi}">
		$("#filterByCampus option").each(function() {
			var selectedCampi = "${selectedCampi.externalId}";
			if ($(this).val() === selectedCampi) {
				$(this).attr('selected', 'selected'); }
			}
		);
	</c:if>
	
	$("#searchForm").submit(function(e) {
		e.preventDefault();
		location.href="${searchUrl}" + $("#searchRequest").val(); 
	});
});
</script>


<c:if test="${not empty selectedCampi}">
	<c:set var="viewRequestsUrl" value="${filterUrl}/${selectedCampi.externalId}"/>
	<c:set var="exportExcelUrl" value="${exportExcelUrl}/${selectedCampi.externalId}"/>
</c:if>


<div class="page-header">
  	<h1><spring:message code="title.space.management" text="Space Management"/><small><spring:message code="title.view.occupations" text="Occupation Requests"/></small></h1>
  	
  	<h3><spring:message code="space.occupations.search" text="Search Occupation Request"></spring:message></h3>
  	<form id="searchForm" class="form-inline" role="form">
  		<div class="form-group">
   		  <label class="sr-only" for="searchRequest"><spring:message code="occupations.requests.search" text="Procurar Pedido"/></label>
    	  <input name="search" type="text" class="form-control" id="searchRequest" value="${searchId}" placeholder="<spring:message code="occupations.requests.search" text="Nº Pedido"/>"></input>
  		</div>
  		 <button id="searchRequest" class="btn btn-default"><spring:message code="label.search" text="Procurar"/></button>
  	</form>
  	
  	
  	   <c:if test="${not empty userRequestSearchResult.pageList}">
  	   	
  	   	<c:set var="searchPageUrl" value="${searchUrl}${searchId}"/>
  	   	<h3><spring:message code="label.occupation.request.search.result" text="Resultado da pesquisa por"/> ${searchId}</h3>
  		<ul class="pagination">
	  		<li><a href="${searchPageUrl}?p=f">&laquo;</a></li>
  			<c:forEach var="page" begin="${userRequestSearchResult.firstLinkedPage}" end="${userRequestSearchResult.lastLinkedPage}">
  				<c:set var="pageNumber" value="${page+1}"/>
  				<c:if test="${page == userRequestSearchResult.page}">
  					<li class="active"><a href="${searchPageUrl}?p=${pageNumber}">${pageNumber}</a></li>
  				</c:if>
  				<c:if test="${page != userRequestSearchResult.page}">
  					<li><a href="${searchPageUrl}?p=${pageNumber}">${pageNumber}</a></li>
  				</c:if>
  			</c:forEach>
	  		<li><a href="${searchPageUrl}?p=l">&raquo;</a></li>
		</ul>
	  	<table class="table">
	  		<thead>
	  			<th><spring:message code="label.occupation.request.identification" text="identification" /></th>
	  			<th><spring:message code="label.occupation.request.instant" text="instant" /></th>
	  			<th><spring:message code="label.occupation.request.subject" text="subject" /></th>
	  			<th><spring:message code="label.occupation.request.requestor" text="requestor" /></th>
	  		</thead>
	  		<tbody>
	  			<c:forEach var="occupationRequest" items="${userRequestSearchResult.pageList}">
					<c:set var="id" value="${occupationRequest.identification}" />
					<c:set var="instant" value="${occupationRequest.presentationInstant}" />
					<c:set var="subject" value="${occupationRequest.subject}" />
					<c:set var="requestor" value="${occupationRequest.requestor}" />
					<tr>
						<td>
							<a href="${requestUrl}/${occupationRequest.externalId}">
								${id}
							</a>
						</td>
						<td>${instant}</td>
						<td>
							<a href="${requestUrl}/${occupationRequest.externalId}">
								${subject}
							</a>
						</td>
	 					<td>${requestor.presentationName} (${requestor.username})</td>
					</tr>
				</c:forEach>
			</tbody>
	   	</table>
  	</c:if>
  	
  	<h3><spring:message code="space.occupations.filter.campus" text="Filter by Campus"></spring:message></h3>
  	<div class="form-group">
	    <label for="filterByCampus"><spring:message code="label.occupations.campus" text="Campus"/></label>
	    <select class="form-control" id="filterByCampus">
	    	<option value="">---</option>
	    	<c:forEach var="campi" items="${campus}">
				<option value="${campi.externalId}">${campi.name}</option>
	    	</c:forEach>
	    <select>
  	</div>
  	
  	
  	<!--  My Requests -->
  	<h3><spring:message code="space.occupations.requests.my" text="My Requests"></spring:message></h3>
  	<c:if test="${empty myRequests.pageList}">
  		<em><spring:message code="space.occupations.no.requests" text="No requests available."></spring:message></em>
  	</c:if>
  	<c:if test="${not empty myRequests.pageList}">
  		<ul class="pagination">
	  		<li><a href="${viewRequestsUrl}?p=f">&laquo;</a></li>
  			<c:forEach var="page" begin="${myRequests.firstLinkedPage}" end="${myRequests.lastLinkedPage}">
  				<c:set var="pageNumber" value="${page+1}"/>
  				<c:if test="${page == myRequests.page}">
  					<li class="active"><a href="${viewRequestsUrl}?p=${pageNumber}">${pageNumber}</a></li>
  				</c:if>
  				<c:if test="${page != myRequests.page}">
  					<li><a href="${viewRequestsUrl}?p=${pageNumber}">${pageNumber}</a></li>
  				</c:if>
  			</c:forEach>
	  		<li><a href="${viewRequestsUrl}?p=l">&raquo;</a></li>
		</ul>
	  	<table class="table">
	  		<thead>
	  			<th><spring:message code="label.occupation.request.identification" text="identification" /></th>
	  			<th><spring:message code="label.occupation.request.instant" text="instant" /></th>
	  			<th><spring:message code="label.occupation.request.subject" text="subject" /></th>
	  			<th><spring:message code="label.occupation.request.requestor" text="requestor" /></th>
				<th><spring:message code="label.occupation.request.actions" text="actions" /></th>
	  			
	  		</thead>
	  		<tbody>
	  			<c:forEach var="occupationRequest" items="${myRequests.pageList}">
					<c:set var="id" value="${occupationRequest.identification}" />
					<c:set var="instant" value="${occupationRequest.presentationInstant}" />
					<c:set var="subject" value="${occupationRequest.subject}" />
					<c:set var="requestor" value="${occupationRequest.requestor}" />
					<tr>
						<td>
							<a href="${requestUrl}/${occupationRequest.externalId}">
								${id}
							</a>
						</td>
						<td>${instant}</td>
						<td>
							<a href="${requestUrl}/${occupationRequest.externalId}">
								${subject}
							</a>
						</td>
	 					<td>${requestor.presentationName} (${requestor.username})</td>
						<td>
							<a href="${requestUrl}/${occupationRequest.externalId}">
								<spring:message code="label.occupation.request.deal" text="Tratar Pedido"/>
							</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
	   	</table>
	 </c:if>
   	
   	<!--  New Requests -->
   	<h3><spring:message code="space.occupations.requests.new" text="New Requests"></spring:message></h3>
   	<c:if test="${empty newRequests.pageList}">
  		<em><spring:message code="space.occupations.no.requests" text="No requests available."></spring:message></em>
  	</c:if>
  	<c:if test="${not empty newRequests.pageList}">
  		<ul class="pagination">
	  		<li><a href="${viewRequestsUrl}?p=f&state=NEW">&laquo;</a></li>
  			<c:forEach var="page" begin="${newRequests.firstLinkedPage}" end="${newRequests.lastLinkedPage}">
  				<c:set var="pageNumber" value="${page+1}"/>
  				<c:if test="${page == newRequests.page}">
  					<li class="active"><a href="${viewRequestsUrl}?p=${pageNumber}&state=NEW">${pageNumber}</a></li>
  				</c:if>
  				<c:if test="${page != newRequests.page}">
  					<li><a href="${viewRequestsUrl}?p=${pageNumber}&state=NEW">${pageNumber}</a></li>
  				</c:if>
  			</c:forEach>
	  		<li><a href="${viewRequestsUrl}?p=l&state=NEW">&raquo;</a></li>
		</ul>
	   	<table class="table">
	  		<thead>
	  			<th><spring:message code="label.occupation.request.identification" text="identification" /></th>
	  			<th><spring:message code="label.occupation.request.instant" text="instant" /></th>
	  			<th><spring:message code="label.occupation.request.subject" text="subject" /></th>
	  			<th><spring:message code="label.occupation.request.requestor" text="requestor" /></th>
	  			<th><spring:message code="label.occupation.request.actions" text="actions" /></th>
	  		</thead>
	  		<tbody>
	  			<c:forEach var="occupationRequest" items="${newRequests.pageList}">
					<c:set var="id" value="${occupationRequest.identification}" />
					<c:set var="instant" value="${occupationRequest.presentationInstant}" />
					<c:set var="subject" value="${occupationRequest.subject}" />
					<c:set var="requestor" value="${occupationRequest.requestor}" />
					<tr>
						<td>
							<a href="${requestUrl}/${occupationRequest.externalId}">
								${id}
							</a>
						</td>
						<td>${instant}</td>
						<td>
							<a href="${requestUrl}/${occupationRequest.externalId}">
								${subject}
							</a>
						</td>
	 					<td>${requestor.presentationName} (${requestor.username})</td>
	 					<td>
	 						<a href="${requestUrl}/${occupationRequest.externalId}/OPEN">
	 							<spring:message code="label.occupation.request.open" text="Abrir Pedido"/>
	 						</a>
	 					</td>
					</tr>
				</c:forEach>
			</tbody>
	   	</table>
	 </c:if>
   	
   	<!--  Open Requests -->
   	<h3><spring:message code="space.occupations.requests.open" text="Open Requests"></spring:message></h3>
   	<c:if test="${empty openRequests.pageList}">
  		<em><spring:message code="space.occupations.no.requests" text="No requests available."></spring:message></em>
  	</c:if>
  	<c:if test="${not empty openRequests.pageList}">
  		<ul class="pagination">
	  		<li><a href="${viewRequestsUrl}?p=f&state=OPEN">&laquo;</a></li>
  			<c:forEach var="page" begin="${openRequests.firstLinkedPage}" end="${openRequests.lastLinkedPage}">
  				<c:set var="pageNumber" value="${page+1}"/>
  				<c:if test="${page == openRequests.page}">
  					<li class="active"><a href="${viewRequestsUrl}?p=${pageNumber}&state=OPEN">${pageNumber}</a></li>
  				</c:if>
  				<c:if test="${page != openRequests.page}">
  					<li><a href="${viewRequestsUrl}?p=${pageNumber}&state=OPEN">${pageNumber}</a></li>
  				</c:if>
  			</c:forEach>
	  		<li><a href="${viewRequestsUrl}?p=l&state=OPEN">&raquo;</a></li>
		</ul>
	   	<table class="table">
	  		<thead>
	  			<th><spring:message code="label.occupation.request.identification" text="identification" /></th>
	  			<th><spring:message code="label.occupation.request.instant" text="instant" /></th>
	  			<th><spring:message code="label.occupation.request.subject" text="subject" /></th>
	  			<th><spring:message code="label.occupation.request.requestor" text="requestor" /></th>
				<th><spring:message code="label.occupation.request.actions" text="actions" /></th>
	  		</thead>
	  		<tbody>
	  			<c:forEach var="occupationRequest" items="${openRequests.pageList}">
					<c:set var="id" value="${occupationRequest.identification}" />
					<c:set var="instant" value="${occupationRequest.presentationInstant}" />
					<c:set var="subject" value="${occupationRequest.subject}" />
					<c:set var="requestor" value="${occupationRequest.requestor}" />
					<tr>
						<td>
							<a href="${requestUrl}/${occupationRequest.externalId}">
								${id}
							</a>
						</td>
						<td>${instant}</td>
						<td>
							<a href="${requestUrl}/${occupationRequest.externalId}">
								${subject}
							</a>
						</td>
	 					<td>${requestor.presentationName} (${requestor.username})</td>
	 					<td>
	 						<a href="${requestUrl}/${occupationRequest.externalId}">
	 							<spring:message code="label.occupation.request.open" text="Tratar Pedido"/>
	 						</a>
	 					</td>
					</tr>
				</c:forEach>
			</tbody>
	   	</table>
   	</c:if>
   	
	<!--  Closed Requests -->
	<h3><spring:message code="space.occupations.requests.resolved" text="Resolved Requests"></spring:message></h3>
	<c:if test="${empty resolvedRequests.pageList}">
  		<em><spring:message code="space.occupations.no.requests" text="No requests available."></spring:message></em>
  	</c:if>
  	<c:if test="${not empty resolvedRequests.pageList}">
		<a href="${exportExcelUrl}?state=RESOLVED"><spring:message code="export.excel" text="identification" /><</a>
  		<ul class="pagination">
	  		<li><a href="${viewRequestsUrl}?p=f&state=RESOLVED">&laquo;</a></li>
  			<c:forEach var="page" begin="${resolvedRequests.firstLinkedPage}" end="${resolvedRequests.lastLinkedPage}">
  				<c:set var="pageNumber" value="${page+1}"/>
  				<c:if test="${page == resolvedRequests.page}">
  					<li class="active"><a href="${viewRequestsUrl}?p=${pageNumber}&state=RESOLVED">${pageNumber}</a></li>
  				</c:if>
  				<c:if test="${page != resolvedRequests.page}">
  					<li><a href="${viewRequestsUrl}?p=${pageNumber}&state=RESOLVED">${pageNumber}</a></li>
  				</c:if>
  			</c:forEach>
	  		<li><a href="${viewRequestsUrl}?p=l&state=RESOLVED">&raquo;</a></li>
		</ul>
		<table class="table">
	  		<thead>
	  			<th><spring:message code="label.occupation.request.identification" text="identification" /></th>
	  			<th><spring:message code="label.occupation.request.instant" text="instant" /></th>
	  			<th><spring:message code="label.occupation.request.subject" text="subject" /></th>
	  			<th><spring:message code="label.occupation.request.requestor" text="requestor" /></th>
	  			<th><spring:message code="label.occupation.request.owner" text="Dono" /></th>
	  		</thead>
	  		<tbody>
	  			<c:forEach var="occupationRequest" items="${resolvedRequests.pageList}">
					<c:set var="id" value="${occupationRequest.identification}" />
					<c:set var="instant" value="${occupationRequest.presentationInstant}" />
					<c:set var="subject" value="${occupationRequest.subject}" />
					<c:set var="requestor" value="${occupationRequest.requestor}" />
					<c:set var="owner" value="${occupationRequest.owner}" />
					<tr>
						<td>
							<a href="${requestUrl}/${occupationRequest.externalId}">
								${id}
							</a>
						</td>
						<td>${instant}</td>
						<td>
							<a href="${requestUrl}/${occupationRequest.externalId}">
								${subject}
							</a>
						</td>
	 					<td>${requestor.presentationName} (${requestor.username})</td>
	 					<td>${owner.presentationName} (${owner.username})</td>
					</tr>
				</c:forEach>
			</tbody>
	   	</table>
   	</c:if>
   	
</div>

