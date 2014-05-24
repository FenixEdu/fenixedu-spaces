<!DOCTYPE html> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:url var="filterUrl" value="/spaces/occupations/requests/filter"/>
<spring:url var="withoutFilterUrl" value="/spaces/occupations/requests"/>

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
	
	<spring:url var="searchUrl" value="/spaces/occupations/requests/search/"/>
	
	$("#searchForm").submit(function(e) {
		e.preventDefault();
		location.href="${searchUrl}" + $("#searchRequest").val(); 
	});
});
</script>

<div class="page-header">
  	<h1><spring:message code="title.space.management" text="Space Management"/><small><spring:message code="title.view.occupations" text="Occupation Requests"/></small></h1>
  	
  	<h3><spring:message code="space.occupations.search" text="Search Occupation Request"></spring:message></h3>
  	<form id="searchForm" class="form-inline" role="form">
  		<div class="form-group">
   		  <label class="sr-only" for="searchRequest"><spring:message code="occupations.requests.search" text="Procurar Pedido"/></label>
    	  <input name="search" type="number" min="1" class="form-control" id="searchRequest" placeholder="<spring:message code="occupations.requests.search" text="Procurar Pedido"/>"></input>
  		</div>
  		 <button id="searchRequest" class="btn btn-default"><spring:message code="label.search" text="Procurar"/></button>
  	</form>
  	
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
  	
  	<spring:url var="requestUrl" value="/spaces/occupations/requests"/>
  	
  	
  	<!--  My Requests -->
  	<h3><spring:message code="space.occupations.requests.my" text="My Requests"></spring:message></h3>
  	<c:if test="${empty myRequests}">
  		<em><spring:message code="space.occupations.no.requests" text="No requests available."></spring:message></em>
  	</c:if>
  	<c:if test="${not empty myRequests}">
	  	<table class="table">
	  		<thead>
	  			<th><spring:message code="label.occupation.request.identification" text="identification" /></th>
	  			<th><spring:message code="label.occupation.request.instant" text="instant" /></th>
	  			<th><spring:message code="label.occupation.request.subject" text="subject" /></th>
	  			<th><spring:message code="label.occupation.request.requestor" text="requestor" /></th>
				<th><spring:message code="label.occupation.request.actions" text="actions" /></th>
	  			
	  		</thead>
	  		<tbody>
	  			<c:forEach var="occupationRequest" items="${myRequests}">
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
	 					<td>${requestor.presentationName}</td>
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
   	<c:if test="${empty newRequests}">
  		<em><spring:message code="space.occupations.no.requests" text="No requests available."></spring:message></em>
  	</c:if>
  	<c:if test="${not empty newRequests}">
	   	<table class="table">
	  		<thead>
	  			<th><spring:message code="label.occupation.request.identification" text="identification" /></th>
	  			<th><spring:message code="label.occupation.request.instant" text="instant" /></th>
	  			<th><spring:message code="label.occupation.request.subject" text="subject" /></th>
	  			<th><spring:message code="label.occupation.request.requestor" text="requestor" /></th>
	  			<th><spring:message code="label.occupation.request.actions" text="actions" /></th>
	  		</thead>
	  		<tbody>
	  			<c:forEach var="occupationRequest" items="${newRequests}">
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
	 					<td>${requestor.presentationName}</td>
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
   	<c:if test="${empty openRequests}">
  		<em><spring:message code="space.occupations.no.requests" text="No requests available."></spring:message></em>
  	</c:if>
  	<c:if test="${not empty openRequests}">
	   	<table class="table">
	  		<thead>
	  			<th><spring:message code="label.occupation.request.identification" text="identification" /></th>
	  			<th><spring:message code="label.occupation.request.instant" text="instant" /></th>
	  			<th><spring:message code="label.occupation.request.subject" text="subject" /></th>
	  			<th><spring:message code="label.occupation.request.requestor" text="requestor" /></th>
				<th><spring:message code="label.occupation.request.actions" text="actions" /></th>
	  		</thead>
	  		<tbody>
	  			<c:forEach var="occupationRequest" items="${openRequests}">
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
	 					<td>${requestor.presentationName}</td>
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
	<c:if test="${empty resolvedRequests}">
  		<em><spring:message code="space.occupations.no.requests" text="No requests available."></spring:message></em>
  	</c:if>
  	<c:if test="${not empty resolvedRequests}">
		<table class="table">
	  		<thead>
	  			<th><spring:message code="label.occupation.request.identification" text="identification" /></th>
	  			<th><spring:message code="label.occupation.request.instant" text="instant" /></th>
	  			<th><spring:message code="label.occupation.request.subject" text="subject" /></th>
	  			<th><spring:message code="label.occupation.request.requestor" text="requestor" /></th>
	  			<th><spring:message code="label.occupation.request.owner" text="Dono" /></th>
	  		</thead>
	  		<tbody>
	  			<c:forEach var="occupationRequest" items="${resolvedRequests}">
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

