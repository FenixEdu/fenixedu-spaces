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
<script src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>

<div class="page-header">
  	<h1><spring:message code="title.photo.my.submissions"/></h1>
  	<spring:url var="submitUrl" value="/spaces-view" />
  	<p><a class="btn btn-primary" href="${submitUrl}"><spring:message code="label.photo.suggest"/></a></p>
</div>	
	
<!-- Nav tabs titles -->
<c:set var="pending" value="1" />
<c:set var="accepted" value="2" />
<c:set var="rejected" value="3" />

<!-- Nav tabs -->
<ul class="nav nav-tabs" id="myTabs">
  	<li><a href="#${pending}" data-toggle="tab"><spring:message code="title.photo.my.pending"/></a></li>
  	<li><a href="#${accepted}" data-toggle="tab"><spring:message code="title.photo.my.accepted"/></a></li>
  	<li><a href="#${rejected}" data-toggle="tab"><spring:message code="title.photo.my.rejected"/></a></li>
</ul>

<spring:url var="searchPageUrl" value="/spaces/photos/submissions/my" />

<div class="tab-content">
	<!-- Pending Content -->
	<div class="tab-pane fade" id="${pending}">
		<%@include file="fragments/pending.jsp"%>
	</div>

	<!-- Accepted Content -->
	<div class="tab-pane fade" id="${accepted}">
		<%@include file="fragments/accepted.jsp"%>
	</div>

	<!-- Rejected Content -->
	<div class="tab-pane fade" id="${rejected}">
		<%@include file="fragments/rejected.jsp"%>
	</div>
</div>

<script type="text/javascript">
  $( document ).ready(function() {
    $("ul.nav-tabs li").eq( '<c:out value="${activeTab-1}"/>' ).addClass("active");
    $(".tab-pane").eq( '<c:out value="${activeTab-1}"/>' ).addClass("active in");
  });
</script>