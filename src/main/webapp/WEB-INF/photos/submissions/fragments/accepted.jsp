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
<c:set var="tab" value="tab=${accepted}" />

<c:if test="${not empty acceptedSubmissions.pageList}">
    <ul class="pagination">
        <li><a href="${searchPageUrl}?a=f&${tab}">&laquo;</a></li>
        <c:forEach var="page" begin="${acceptedSubmissions.firstLinkedPage}" end="${acceptedSubmissions.lastLinkedPage}">
            <c:set var="pageNumber" value="${page+1}"/>
            <c:if test="${page == acceptedSubmissions.page}">
                <li class="active"><a href="${searchPageUrl}?a=${pageNumber}&${tab}">${pageNumber}</a></li>
            </c:if>
            <c:if test="${page != acceptedSubmissions.page}">
                <li><a href="${searchPageUrl}?a=${pageNumber}&${tab}">${pageNumber}</a></li>
            </c:if>
        </c:forEach>
        <li><a href="${searchPageUrl}?a=l&${tab}">&raquo;</a></li>
    </ul>
    <table class="table">
        <thead>
            <th><spring:message code="label.space" /></th>
            <th><spring:message code="label.photo" /></th>
            <th><spring:message code="label.photo.date" /></th>
            <th></th>
        </thead>
        <tbody>
            <c:forEach var="photoSubmission" items="${acceptedSubmissions.pageList}">
                <c:set var="photo" value="${photoSubmission.photo}" />
                <c:set var="date" value="${photoSubmission.created}" />
                <c:set var="space" value="${photoSubmission.space}" />
                <spring:url var="spacePhotoUrl" value="/spaces-view/photo/${photo.externalId}" />
                <spring:url var="viewUrl" value="/spaces-view/view/${space.externalId}" />
                <spring:url var="formUrl" value="/spaces/photos/my/${photoSubmission.externalId}" />
                <tr>
                    <td>
                        <a href="${viewUrl}"><c:out value="${space.presentationName}"/></a>
                    </td>
                    <td>
                        <img src="${spacePhotoUrl}" class="img-responsive" style="max-height: 450px;"/>
                    </td>
                    <td><c:out value="${date.toString('dd/MM/yyyy hh:mm')}"/></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>

<c:if test="${empty acceptedSubmissions.pageList}">
    <div id="new-info-panel" class="infoop2">
        <p class="mvert0"><em><spring:message code="space.photo.none"></spring:message></em></p>
    </div>
</c:if>