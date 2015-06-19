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
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><spring:message code="title.parent.spaces" /></h3>
	</div>
	<div class="panel-body">
		<c:if test="${not empty parentSpace }">
			<c:forEach var="space" items="${parentSpace.path}">
				<c:url var="viewUrl" value="/spaces-view/view/${space.externalId}" />
				<a href="${viewUrl}"><c:out value="${space.name}"/></a>&nbsp;&raquo;
		  	</c:forEach>
		</c:if>
		<c:if test="${empty parentSpace }">
			<spring:message code="label.empty.parent.spaces"/>
		</c:if>
	</div>
</div>
