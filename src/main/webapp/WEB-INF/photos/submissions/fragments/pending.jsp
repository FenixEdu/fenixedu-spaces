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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!-- Current Tab -->
<c:set var="tab" value="tab=${pending}" />

<c:if test="${not empty pendingSubmissions.pageList}">
    <ul class="pagination">
        <li><a href="${searchPageUrl}?p=f&${tab}">&laquo;</a></li>
        <c:forEach var="page" begin="${pendingSubmissions.firstLinkedPage}" end="${pendingSubmissions.lastLinkedPage}">
            <c:set var="pageNumber" value="${page+1}"/>
            <c:if test="${page == pendingSubmissions.page}">
                <li class="active"><a href="${searchPageUrl}?p=${pageNumber}&${tab}">${pageNumber}</a></li>
            </c:if>
            <c:if test="${page != pendingSubmissions.page}">
                <li><a href="${searchPageUrl}?p=${pageNumber}&${tab}">${pageNumber}</a></li>
            </c:if>
        </c:forEach>
        <li><a href="${searchPageUrl}?p=l&${tab}">&raquo;</a></li>
    </ul>
    <table class="table">
        <thead>
            <th><spring:message code="label.space" /></th>
            <th><spring:message code="label.photo" /></th>
            <th><spring:message code="label.photo.date" /></th>
            <th></th>
        </thead>
        <tbody>
            <c:forEach var="photoSubmission" items="${pendingSubmissions.pageList}">
                <c:set var="photo" value="${photoSubmission.photo}" />
                <c:set var="date" value="${photoSubmission.created}" />
                <c:set var="space" value="${photoSubmission.spacePending}" />
                <spring:url var="spacePhotoUrl" value="/spaces-view/photo/${photo.externalId}" />
                <spring:url var="viewUrl" value="/spaces-view/view/${space.externalId}" />
                <spring:url var="formUrl" value="/spaces/photos/submissions/my/${photoSubmission.externalId}" />
                <tr>
                    <td>
                        <a href="${viewUrl}"><c:out value="${space.presentationName}"/></a>
                    </td>
                    <td>
                        <img src="${spacePhotoUrl}" class="img-responsive" style="max-height: 450px;"/>
                    </td>
                    <td><c:out value="${date.toString('dd/MM/yyyy hh:mm')}"/></td>
                    <td>
                        <form id="form" role="form" class="cancel" action="${formUrl}/cancel" method="POST">
                                ${csrf.field()}
                            <input type="hidden" name="space" value="${space.externalId}">
                            <input type="hidden" name="page" value="${page}">
                            <button type="submit" class="btn btn-xs btn-default"><i class="glyphicon glyphicon-remove"></i>  <spring:message code="label.photo.submission.cancel" /></button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>

<c:if test="${empty pendingSubmissions.pageList}">
    <div id="new-info-panel" class="infoop2">
        <p class="mvert0"><em><spring:message code="space.photo.none"></spring:message></em></p>
    </div>
</c:if>