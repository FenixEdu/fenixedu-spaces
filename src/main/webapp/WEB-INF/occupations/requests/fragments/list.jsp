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
<table class="table">
	<thead>
		<th><spring:message code="label.occupation.request.identification" /></th>
		<th><spring:message code="label.occupation.request.instant" /></th>
		<th><spring:message code="label.occupation.request.subject" /></th>
		<th><spring:message code="label.occupation.request.requestor" /></th>
		<th><spring:message code="label.occupation.request.actions" /></th>

	</thead>
	<tbody>
		<c:forEach var="occupationRequest" items="${requests}">
			<c:set var="id" value="${occupationRequest.identification}" />
			<c:set var="instant" value="${occupationRequest.presentationInstant}" />
			<c:set var="subject" value="${occupationRequest.subject}" />
			<c:set var="requestor" value="${occupationRequest.requestor}" />
			<tr>
				<td><a href="${requestUrl}/${occupationRequest.externalId}"> <c:out value="${id}"/> </a></td>
				<td><c:out value="${instant}"/></td>
				<td><a href="${requestUrl}/${occupationRequest.externalId}"> <c:out value="${subject}"/> </a></td>
				<td><c:out value="${requestor.profile.displayName}"/></td>
				<td><a href="${requestUrl}/${occupationRequest.externalId}"> <spring:message code="label.occupation.request.deal" />
				</a></td>
			</tr>
		</c:forEach>
	</tbody>
</table>