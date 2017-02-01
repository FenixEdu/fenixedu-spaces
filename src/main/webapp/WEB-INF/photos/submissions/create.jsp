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

<spring:url var="baseUrl" value="/static/fenix-spaces"/>
 ${portal.bennuPortal()}
<script type="text/javascript" src="${baseUrl}/js/sprintf.min.js">
</script>

<spring:url var="viewUrl" value="/spaces-view/view/${space.externalId}" />

<div class="page-header">
    <h1>
        <spring:message code="title.space.photo.submit"/>
        <br><small><a href="${viewUrl}"><c:out value="${space.presentationName}"/></a></small>
    </h1>
</div>

<a href="${viewUrl}">&laquo; <spring:message code="button.back" /></a>

<div class="alert alert-warning" role="alert"><spring:message code="message.site.image.type.validation"/></div>

<c:if test="${not empty error}">
    <p class="mtop15">
        <span class="error"><!-- Error messages go here -->
            <spring:message code="label.photo.error"/>
        </span>
    </p>
</c:if>

<c:choose>
    <c:when test="${empty submitted}">
        <spring:url var="formActionUrl" value="${action}"/>
        <form:form modelAttribute="photoSubmission" role="form" method="post" action="${formActionUrl}" enctype="multipart/form-data">
            ${csrf.field()} 
          <img id="myimage" style="max-height: 300px;" class="img-thumbnail" src="${pageContext.request.contextPath}/static/image_placeholder.jpg" alt="Your Image" />
            <script type="text/javascript">
              function readURL(input) {
                    if (input.files && input.files[0]) {
                        var reader = new FileReader();

                        reader.onload = function (e) {
                            $('#myimage')
                                .attr('src', e.target.result);
                        };

                        reader.readAsDataURL(input.files[0]);
                    }
                }
            </script>
          <div class="form-group">
            <form:label for="spacePhotoFileInput" path="submissionMultipartFile"><spring:message code="label.spaces.photo"/></form:label>
            <form:input type="file" accept="image/*" required="true" onchange="readURL(this)" class="form-control input" id="spacePhotoFileInput" path="submissionMultipartFile"/>
          </div>
          
          <button type="submit" class="btn btn-default"><spring:message code="label.submit"/></button>
        </form:form>
    </c:when>
    <c:otherwise>
        <spring:url var="submitUrl" value="/spaces/photos/submissions/my/create/${space.externalId}"/>
        <spring:url var="reviewUrl" value="/spaces/photos/submissions/my"/>
        <div id="new-info-panel" class="infoop2">
            <p class="mvert0"><spring:message code="label.photo.success"/></p>
        </div>
        <a class = "btn btn-default" href = "${submitUrl}" role = "button"><spring:message code="label.photo.submit.another"/></a>
        <a class = "btn btn-default" href = "${reviewUrl}" role = "button"><spring:message code="label.photo.view"/></a>
        <a class = "btn btn-default" href = "${viewUrl}" role = "button"><spring:message code="button.back"/></a>
    </c:otherwise>
</c:choose>

