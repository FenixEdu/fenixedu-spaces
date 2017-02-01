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

<spring:url var="listUrl" value="/spaces/occupations/requests/my"/>

<script type="text/javascript">
$(document).ready(function() {
	$("#state${occupationRequest.currentState}").prop("checked", "checked");
});
</script>
<div class="page-header">
  	<h1><spring:message code="title.space.management"/><small><spring:message code="title.view.occupations"/></small></h1>
</div>
<main>
	<em><a href="${listUrl}"><spring:message code="label.back"/></a></em>
	<h3><spring:message code="occupation.request.details"/></h3>
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
					<th class="row"><spring:message code="label.occupation.request.identification" /></th>
					<td><c:out value="${id}"/></td>
				</tr>
				<tr>
					<th class="row"><spring:message code="label.occupation.request.subject" /></th>
					<td><c:out value="${subject}"/></td>
				</tr>
				<tr>
					<th class="row"><spring:message code="label.occupation.request.requestor" /></th>
					<td><c:out value="${requestor.profile.displayName} (${requestor.username})"/></td>
				</tr>
				<tr>
					<th class="row"><spring:message code="label.occupation.request.instant" /></th>
					<td><c:out value="${instant}"/></td>
				</tr>
				<tr>
					<th class="row"><spring:message code="label.occupation.request.state" /></th>
					<td><b><spring:message code="${stateBundleKey}"></spring:message></b></td>
				</tr>
				<tr>
					<th class="row"><spring:message code="label.occupations" /></th>
					<td>
						<c:forEach var="occupation" items="${occupationRequest.occupationSet}">
							<p>
									<c:out value="${occupation.summary}"/>&nbsp;&mdash;&nbsp;
									<c:forEach var="space" items="${occupation.spaces}">
										<c:out value="${space.name}"/>
									</c:forEach>
							</p>
						</c:forEach>
					</td>
				</tr>
				<tr>
					<th class="row"><spring:message code="label.campus" /></th>
					<td><c:out value="${campusName}"/></td>
				</tr>
				<tr>
					<th class="row"><spring:message code="label.occupations.description" /></th>
					<td>
						<pre class="comment"><c:out value="${occupationRequest.description}"/></pre>
					</td>
					
				</tr>
			</tbody>
	   	</table>
	   	
	   	<!--  Comments -->
	   	<h3><spring:message code="occupation.request.comments"/></h3>
	   	<c:set var="comments" value="${occupationRequest.commentsWithoutFirstCommentOrderByDate}"/>
	   	<c:if test="${ empty comments }">
			<em><spring:message code="occupation.no.comments"/></em>
	   	</c:if>
	   	<c:if test="${not empty comments}">
	   		<c:forEach var="comment" items="${comments}">
	   			<c:set var="date" value="${comment.instant.toDate()}"/>
	   			
	   			<div class="panel panel-default">
	  				<div class="panel-heading"><strong><c:out value="${comment.owner.profile.displayName} (${comment.owner.username})"/></strong> (<fmt:formatDate value="${date}" pattern="dd-MM-yyyy HH:mm"/>) </div>
	  				<div class="panel-body">
	    				<pre class="comment"><c:out value="${comment.description}"/></pre>
	  				</div>
				</div>
	   		</c:forEach>
	   	</c:if>
	   	
	   	<!-- New Comment -->
	   	<spring:url var="commentUrl" value="/spaces/occupations/requests/my/${occupationRequest.externalId}/comments"/>
	   	<h3><spring:message code="occupation.request.comments.add"/></h3>
	   	<form class="form" role="form" action="${commentUrl}" method="post">
			${csrf.field()}
	  		<div class="form-group">
	   		  <label class="sr-only" for="descriptionInput"><spring:message code="occupation.request.comments.add"/></label>
	    	  <textarea rows="10" cols="60" class="form-control" id="descriptionInput" name="description" required="required"></textarea>
	  		</div>
	  		<div class="form-group">
	  			<label class="radio-inline">
	    			<input type="radio" name="state" id="stateNEW" value="NEW" disabled>
	    			<spring:message code="OccupationRequestState.NEW"/>
	    		</label>
	    		<label class="radio-inline">
	    			<input type="radio" name="state" id="stateOPEN" value="OPEN">
	    			<spring:message code="OccupationRequestState.OPEN"/>
	    		</label>
	    		<label class="radio-inline">
	    			<input type="radio" name="state" id="stateRESOLVED" value="RESOLVED">
	    			<spring:message code="OccupationRequestState.RESOLVED"/>
	  			</label>
	  		</div>
	  		 <button type="submit" class="btn btn-default"><spring:message code="label.submit"/></button>
	  	</form>
	</c:if>
	<c:if test="${empty occupationRequest}">
		<h3><spring:message code="occupation.request.not.found"></spring:message></h3>
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
