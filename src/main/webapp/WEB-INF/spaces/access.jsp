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

<spring:url var="toolkit" value="/bennu-toolkit/js/toolkit.js"/>
<script type="text/javascript" src="${toolkit}">
</script>
<spring:url var="toolkitCSS" value="/bennu-toolkit/css/toolkit.css"/>
<link rel="stylesheet" type="text/css" media="screen" href="${toolkitCSS}">
	<spring:url var="staticUrl" value="/static/fenix-spaces"/>
<script src="${staticUrl}/js/sprintf.min.js"></script>
	<script src="${staticUrl}/js/dateutils.js"></script>

<div class="page-header">
  <h1><spring:message code="title.space.management"/><small><spring:message code="title.space.access.management"/></small></h1>
</div>
<spring:url var="formActionUrl" value="${action}"/>
<form:form modelAttribute="spacebean" role="form" method="post"  action="${formActionUrl}" enctype="multipart/form-data">
	<div class="form-group">
		<h4>
			<spring:message code="label.space.access.occupations.group" />
		</h4>
		${spacebean.occupationGroup.presentationName}
	</div>
	<input bennu-group allow="public,users,spaceSuperUsers,custom" id="occupationExpression" name="occupationExpression" value="${spacebean.occupationExpression}"/>
	<div class="form-group">
		<h4>
			<spring:message code="label.space.access.management.group" />
		</h4>
		${spacebean.managementGroup.presentationName}
	</div>
	<input bennu-group allow="public,users,spaceSuperUsers,custom" id="managementExpression" name="managementExpression" value="${spacebean.managementExpression}"/>
	<p class="help-block"> </p>
		<button type="submit" class="btn btn-default">
			<spring:message code="label.submit" />
		</button>
</form:form>

<script>
 Bennu.group.allow['spaceSuperUsers'] = {
         name: "Space Managers",
         group: "#spaceSuperUsers",
         icon: "glyphicon glyphicon-globe"
     };
</script>

