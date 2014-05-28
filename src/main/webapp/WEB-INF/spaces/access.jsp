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
  <h1><spring:message code="title.space.management" text="Space Management"/><small><spring:message code="title.space.access.management" text="Gestão de Grupos de Acesso"/></small></h1>
</div>

<h3>
	<spring:message code="label.space.access.occupations.group" text="Grupo de Ocupações"/>
</h3>

<c:if test="${empty localOccupationsGroup and not empty chainOccupationsGroup}">
		
	<p>
		<strong>
			<spring:message code="label.space.access.occupations.group.description" text="Descrição do Grupo"/>
		</strong>
		${chainOccupationsGroup.presentationName}
	</p>
	<table class="table">
		<thead>
			<th><spring:message code="label.space.access.occupations.member" text="Nome Utilizador"/></th>
		</thead>
		<tbody>
		<c:forEach var="member" items="${chainOccupationsGroup.members}">
			<tr>
				<td>${member.presentationName}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
</c:if>
<c:if test="${ not empty localOccupationsGroup }">
	<p>${localOccupationsGroup.presentationName}</p>
</c:if>
<h2>
	<spring:message code="label.space.access.management.group" text="Grupo de Gestão de Espaço"/>
</h2>


