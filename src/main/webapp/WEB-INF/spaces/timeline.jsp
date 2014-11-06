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

<div class="page-header">
  <h1><spring:message code="title.space.management" text="Space Management"/><small><spring:message code="title.space.timeline" text="Space Timeline"/></small></h1>
</div>

<script type="text/javascript">
	$(document).ready(function() {
		$("ul li").last().addClass("active");
		$(".tab-pane").last().addClass("in active");
	});
</script>

<!-- Nav tabs -->
<ul class="nav nav-tabs">
  <c:forEach var="information" items="${timeline}">
  	<li><a href="#${information.externalId}" data-toggle="tab"><spring:eval expression="information.validFrom"/> - <spring:eval expression="information.validUntil"/></a></li>
  </c:forEach>
</ul>

<div class="tab-content">
	<c:forEach var="information" items="${timeline}">
		<div class="tab-pane fade" id="${information.externalId}">
			<table class="table">
				<tr>
					<th scope="row"><spring:message code="label.spaces.validFrom" text="Valid From"/></th>
					<td><spring:eval expression="information.validFrom"/></td>
				</tr>
				<tr>
					<th scope="row"><spring:message code="label.spaces.validUntil" text="Valid Until"/></th>
					<td><spring:eval expression="information.validUntil"/></td>
				</tr>
				<tr>
					<th scope="row"><spring:message code="label.spaces.name" text="Name"/></th>
					<td>${information.name}</td>
				</tr>
				<tr>
					<th scope="row"><spring:message code="label.spaces.identification" text="Identification"/></th>
					<td>${information.identification}</td>
				</tr>
				<tr>
					<th scope="row"><spring:message code="label.spaces.classification" text="Classification"/></th>
					<td>${information.classification.name.content}</td>
				</tr>
				<tr>
					<th scope="row"><spring:message code="label.spaces.allocatableCapacity" text="Allocatable Capacity"/></th>
					<td>${information.allocatableCapacity}</td>
				</tr>
				<tr>
					<th scope="row"><spring:message code="label.spaces.blueprintNumber" text="Blueprint Number"/></th>
					<td>${information.blueprintNumber}</td>
				</tr>
				<tr>
					<th scope="row"><spring:message code="label.spaces.blueprint" text="Blueprint"/></th>
					<c:url value="/spaces-view/blueprint/${space.externalId}?when=${information.validFrom.toString('yyyy-MM-dd')}" var="blueprintUrl"/>
					<td><img src="${blueprintUrl}"/>
				</tr>
				<tr>
					<th scope="row"><spring:message code="label.spaces.area" text="Area"/></th>
					<td>${information.area}</td>
				</tr>
				<c:forEach var="metadata" items="${information.metadata}">
					<c:set var="field" value="${metadata.key}"/>
					<c:set var="metadataSpec" value="${information.classification.getMetadataSpec(field).get()}"/>
					<c:set var="value" value="${metadata.value}"/>
					<tr>
						<th scope="row">${metadataSpec.description.content}</th>
						<td>
							<c:choose>
								<c:when test="${value == 'true' }">
									<i class="glyphicon glyphicon-ok"></i>
								</c:when>
								<c:when test="${value == 'false' }">
									<i class="glyphicon glyphicon-remove"></i>
								</c:when>
								<c:otherwise>
									${value}
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</c:forEach>