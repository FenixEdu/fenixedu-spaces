<!DOCTYPE html> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:url var="listUrl" value="/spaces/occupations/requests"/>

<script type="text/javascript">
$(document).ready(function() {
	$("#state${occupationRequest.currentState}").prop("checked", "checked");
});
</script>
<div class="page-header">
  	<h1><spring:message code="title.space.management" text="Space Management"/><small><spring:message code="title.view.occupations" text="Occupation Requests"/></small></h1>
</div>
<main>
	<em><a href="${listUrl}"><spring:message code="label.back" text="Voltar"/></a></em>
	<h3><spring:message code="occupation.request.details" text="Detalhes do Pedido"/></h3>
  	<c:if test="${not empty occupationRequest}">
	  	<table class="table">
	  		<tbody>
				<c:set var="id" value="${occupationRequest.identification}" />
				<c:set var="instant" value="${occupationRequest.presentationInstant}" />
				<c:set var="subject" value="${occupationRequest.subject}" />
				<c:set var="requestor" value="${occupationRequest.requestor}" />
				<c:set var="stateBundleKey" value="OccupationRequestState.${occupationRequest.currentState}"/>
				<c:set var="campus" value="${occupationRequest.campus}"/>
				<c:if test="${empty campus}">
					<c:set var="campusName" value="---"/>
				</c:if>
				<c:if test="${not empty campus}">
					<c:set var="campusName" value="${campus.name}"/>
				</c:if>
				<tr>
					<th class="row"><spring:message code="label.occupation.request.identification" text="Identification" /></th>
					<td>${id}</td>
				</tr>
				<tr>
					<th class="row"><spring:message code="label.occupation.request.subject" text="Subject" /></th>
					<td>${subject}</td>
				</tr>
				<tr>
					<th class="row"><spring:message code="label.occupation.request.requestor" text="Requestor" /></th>
					<td>${requestor.presentationName} (${requestor.username})</td>
				</tr>
				<tr>
					<th class="row"><spring:message code="label.occupation.request.instant" text="Instant" /></th>
					<td>${instant}</td>
				</tr>
				<tr>
					<th class="row"><spring:message code="label.occupation.request.state" text="State" /></th>
					<td><b><spring:message code="${stateBundleKey}"></spring:message></b></td>
				</tr>
				<tr>
					<th class="row"><spring:message code="label.occupations" text="Occupations" /></th>
					<td>
						<spring:url var="viewOccupationUrl" value="/spaces/occupations/view"/>
						<c:forEach var="occupation" items="${occupationRequest.occupationSet}">
							<p>
								<a href="${viewOccupationUrl}/${occupation.externalId}" title="${occupation.extendedSummary}">
									${occupation.summary}&nbsp;&mdash;&nbsp;
									<c:forEach var="space" items="${occupation.spaces}">
										${space.name}
									</c:forEach>
								</a>
							</p>
						</c:forEach>
					</td>
				</tr>
				<tr>
					<th class="row"><spring:message code="label.campus" text="Campus" /></th>
					<td>${campusName}</td>
				</tr>
				<tr>
					<th class="row"><spring:message code="label.occupations.description" text="Description" /></th>
					<td>
						<pre class="comment">${occupationRequest.description}</pre>
					</td>
					
				</tr>
			</tbody>
	   	</table>
	   	
	   	<spring:url var="createOccupationUrl" value="/spaces/occupations/create?request=${occupationRequest.externalId}"/>
	   	<c:if test="${occupationRequest.currentState.name == 'OPEN'}">
	   		<p><a href="${createOccupationUrl}"><spring:message code="occupation.request.create.occupation" text="Fazer Marcação"/></a></p>
	   	</c:if>
	   	
	   	<!--  Comments -->
	   	<h3><spring:message code="occupation.request.comments" text="Comments"/></h3>
	   	<c:set var="comments" value="${occupationRequest.commentsWithoutFirstCommentOrderByDate}"/>
	   	<c:if test="${ empty comments }">
			<em><spring:message code="occupation.no.comments" text="No comments."/></em>
	   	</c:if>
	   	<c:if test="${not empty comments}">
	   		<c:forEach var="comment" items="${comments}">
	   			<c:set var="date" value="${comment.instant.toDate()}"/>
	   			
	   			<div class="panel panel-default">
	  				<div class="panel-heading"><strong>${comment.owner.presentationName} (${comment.owner.username})</strong> (<fmt:formatDate value="${date}" pattern="dd-MM-yyyy HH:mm"/>) </div>
	  				<div class="panel-body">
	    				<pre class="comment">${comment.description}</pre>
	  				</div>
				</div>
	   		</c:forEach>
	   	</c:if>
	   	
	   	<!-- New Comment -->
	   	<spring:url var="commentUrl" value="/spaces/occupations/requests/${occupationRequest.externalId}/comments"/>
	   	<h3><spring:message code="occupation.request.comments.add" text="Add comment"/></h3>
	   	<form class="form" role="form" action="${commentUrl}" method="post">
	  		<div class="form-group">
	   		  <label class="sr-only" for="descriptionInput"><spring:message code="occupation.request.comments.add" text="Add comment"/></label>
	    	  <textarea rows="10" cols="60" class="form-control" id="descriptionInput" name="description" required="required"></textarea>
	  		</div>
	  		<div class="form-group">
	  			<label class="radio-inline">
	    			<input type="radio" name="state" id="stateNEW" value="NEW" disabled>
	    			<spring:message code="OccupationRequestState.NEW" text="New"/>
	    		</label>
	    		<label class="radio-inline">
	    			<input type="radio" name="state" id="stateOPEN" value="OPEN">
	    			<spring:message code="OccupationRequestState.OPEN" text="Open"/>
	    		</label>
	    		<label class="radio-inline">
	    			<input type="radio" name="state" id="stateRESOLVED" value="RESOLVED">
	    			<spring:message code="OccupationRequestState.RESOLVED" text="Resolved"/>
	  			</label>
	  		</div>
	  		 <button type="submit" class="btn btn-default"><spring:message code="label.submit" text="Submit"/></button>
	  	</form>
	</c:if>
	<c:if test="${empty occupationRequest}">
		<h3><spring:message code="occupation.request.not.found" text="Occupation Request not found."></spring:message></h3>
	</c:if>
</main>

<style type="text/css">
.comment {
	padding: 0px;
	margin: 0px;
	background-color: #ffffff;
	border: 0px;
}
th.row {
	background-color: #fafafa;
}
</style>
