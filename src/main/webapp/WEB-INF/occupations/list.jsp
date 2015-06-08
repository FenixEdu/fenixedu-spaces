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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="page-header">
	<h1>
		<spring:message code="title.space.management" />
		<small><spring:message code="title.list.occupation" /></small>
	</h1>
</div>

<spring:url var="viewUrl" value="/spaces/occupations/view"/>

<spring:url var="createUrl" value="/spaces/occupations/create"/>
<a href="${createUrl}"><spring:message code="title.create.occupation"/></a>

<h2><spring:message code="label.filters"/></h2>

<form role="form" action="" method="GET">
  <div class="form-group">
    <label for="year"><spring:message code="label.year"/></label>
    <select name="year">
    	<c:forEach var="year" items="${years}">
    		<c:choose>
    			<c:when test="${ year == currentYear}">
    				<option value="${year}" selected>${year}</option>
    			</c:when>
    			<c:otherwise>
    				<option value="${year}">${year}</option>
    			</c:otherwise>
    		</c:choose>
    	</c:forEach>
    </select>
  </div>
  <div class="form-group">
    <label for="month"><spring:message code="label.month"/></label>
     <select name="month">
    	<c:forEach var="month" items="${months}">
    		<c:set var="monthValue" value="${month.toString('M')}"/>
    		<c:choose>
    			<c:when test="${monthValue == currentMonth}">
    				<option value="${monthValue}" selected>${month.toString("MMM")}</option>
    			</c:when>
    			<c:otherwise>
    				<option value="${monthValue}">${month.toString("MMM")}</option>
    			</c:otherwise>
			</c:choose>
    	</c:forEach>
    </select>
  </div>
  <div class="form-group">
    <label for="searchSpace"><spring:message code="label.space"/></label>
    <input name="name" type="text" id="searchSpace" value="<c:out value='${name}'/>" placeholder="<spring:message code="label.space.search.name"/>"></input>
  </div>
  <button type="submit" class="btn btn-default"><spring:message code="label.select"/></button>
</form>

<h2><spring:message code="title.list.occupation.details" /></h2>

<spring:url var="viewUrl" value="/spaces/occupations/view"/>

<c:if test="${not empty occupations}">
	<table class="table">
		<thead>
			<th><spring:message code="label.occupation.subject" /></th>
			<th><spring:message code="label.occupation.intervals" /></th>
			<th><spring:message code="label.occupation.rooms" /></th>
		</thead>
		<tbody>
			<c:forEach var="occupation" items="${occupations}">
				<c:set var="id" value="${occupation.externalId}" />
				<c:set var="subject" value="${occupation.subject}" />
				<c:set var="summary" value="${occupation.summary}" />
				<c:set var="extendedSummary" value="${occupation.extendedSummary}" />
				<c:set var="rooms" value="${occupation.spaces}" />
				<tr>
					<td class="col-md-5"><a href="${viewUrl}/${id}"><c:out value="${subject}"/></a></td>
					<td class="col-md-3"><p title="${extendedSummary}"><c:out value="${summary}"/></p></td>
					<td class="col-md-4">
						<c:forEach var="room" items="${rooms}">
							<c:out value="${room.name}"/>
						</c:forEach>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:if>
<c:if test="${empty occupations}">
	<h4><spring:message code="label.occupations.empty"/></h4>
</c:if>