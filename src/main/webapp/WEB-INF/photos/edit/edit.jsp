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

<spring:url var="viewUrl" value="/spaces-view/view/${space.externalId}" />
<div class="page-header">
  <h1>
      <spring:message code="title.photo.edit"/>
      <br><small><a href="${viewUrl}"><c:out value="${space.presentationName}"/></a></small>
  </h1>
</div>

<a href="${viewUrl}">&laquo; <spring:message code="button.back" /></a>	
	
<!-- Nav tabs titles -->
<c:set var="active" value="1" />
<c:set var="archived" value="2" />
<c:set var="pending" value="3" />

<!-- Nav tabs -->
<ul class="nav nav-tabs" id="myTabs">
  	<li><a href="#${active}" data-toggle="tab"><spring:message code="title.photo.manage.active"/></a></li>
  	<li><a href="#${archived}" data-toggle="tab"><spring:message code="title.photo.manage.archived"/></a></li>
  	<li><a href="#${pending}" data-toggle="tab"><spring:message code="title.photo.manage.pending"/></a></li>
</ul>

<spring:url var="pageUrl" value="/spaces/photos/edit/${space.externalId}" />

<div class="tab-content">
	<!-- active Content -->
	<div class="tab-pane fade" id="${active}">
		<%@include file="fragments/active.jsp"%>
	</div>

	<!-- archived Content -->
	<div class="tab-pane fade" id="${archived}">
		<%@include file="fragments/archived.jsp"%>
	</div>

	<!-- pending Content -->
	<div class="tab-pane fade" id="${pending}">
		<%@include file="fragments/pending.jsp"%>
	</div>
</div>

<script type="text/javascript">
  $( document ).ready(function() {
    $("ul.nav-tabs li").eq( '<c:out value="${activeTab-1}"/>' ).addClass("active");
    $(".tab-pane").eq( '<c:out value="${activeTab-1}"/>' ).addClass("active in");
  });
</script>


