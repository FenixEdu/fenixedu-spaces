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
<c:set var="tab" value="tab=${active}" />

<c:if test="${not empty activePhotos.pageList}">
    <ul class="pagination">
        <li><a href="${pageUrl}?a=f&${tab}">&laquo;</a></li>
        <c:forEach var="page" begin="${activePhotos.firstLinkedPage}" end="${activePhotos.lastLinkedPage}">
            <c:set var="pageNumber" value="${page+1}"/>
            <c:if test="${page == activePhotos.page}">
                <li class="active"><a href="${pageUrl}?a=${pageNumber}&${tab}">${pageNumber}</a></li>
            </c:if>
            <c:if test="${page != activePhotos.page}">
                <li><a href="${pageUrl}?a=${pageNumber}&${tab}">${pageNumber}</a></li>
            </c:if>
        </c:forEach>
        <li><a href="${pageUrl}?a=l&${tab}">&raquo;</a></li>
    </ul>
    <table class="table">
        <thead>
            <th><spring:message code="label.space" /></th>
            <th><spring:message code="label.photo" /></th>
            <th><spring:message code="label.photo.user" /></th>
            <th><spring:message code="label.photo.reviewer" /></th>
            <th><spring:message code="label.photo.date" /></th>
            <th></th>
        </thead>
        <tbody>
            <c:forEach var="photo" items="${activePhotos.pageList}">
            <c:set var="subject" value="${photo.submission.submitor}" />
            <c:set var="reviewer" value="${photo.submission.reviewer}" />
            <c:set var="date" value="${photo.submission.created}" />
            <spring:url var="spacePhotoUrl" value="/spaces-view/photo/${photo.externalId}" />
            <spring:url var="viewUrl" value="/spaces-view/view/${space.externalId}" />
            <spring:url var="formUrl" value="/spaces/photos/${photo.externalId}" />
            <tr>
                <td>
                    <a href="${viewUrl}"><c:out value="${space.presentationName}"/></a>
                </td>
                <td>
                    <img src="${spacePhotoUrl}" class="img-responsive" style="max-height: 450px;"/>
                </td>
                <td>
                    <a href="mailto:${subject.email}"><c:out value="${subject.name}"/></a> (<c:out value="${subject.username}"/>)
                </td>
                <td>
                    <a href="mailto:${reviewer.email}"><c:out value="${reviewer.name}"/></a> (<c:out value="${reviewer.username}"/>)
                </td>
                <td><c:out value="${date.toString('dd/MM/yyyy hh:mm')}"/></td>
                <td>
                    <table>
                        <tr>
                            <td style="padding:5px;">
                                <c:choose>
                                    <c:when test="${photo.visible}">
                                        <form id="form" role="form" action="${formUrl}/hide" method="POST">
                                            ${csrf.field()}
                                        <input type="hidden" name="space" value="${space.externalId}">
                                        <input type="hidden" name="page" value="${page}">
                                        <button type="submit" class="btn btn-xs btn-default"><i class="glyphicon glyphicon-eye-close"></i>  <spring:message code="label.photo.hide" /></button>
                                    </form>
                                    </c:when>
                                    <c:when test="${not photo.visible}">
                                        <form id="form" role="form" action="${formUrl}/show" method="POST">
                                                ${csrf.field()}
                                        <input type="hidden" name="space" value="${space.externalId}">
                                        <input type="hidden" name="page" value="${page}">
                                        <button type="submit" class="btn btn-xs btn-default"><i class="glyphicon glyphicon-eye-open"></i>  <spring:message code="label.photo.show" /></button>
                                    </form>
                                    </c:when>
                                </c:choose> 
                                
                            </td>
                        </tr>
                        <tr>
                            <td style="padding:5px;">
                                <div id="delete${photo.externalId}" class="modal fade" role="dialog">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <a href="#" data-dismiss="modal" aria-hidden="true" class="close">×</a>
                                                 <h3><spring:message code="label.delete" /></h3>
                                            </div>
                                            <div class="modal-body">
                                                <p><spring:message code="label.spaces.delete.message" /></p>
                                            </div>
                                            <div class="modal-footer">
                                                <form id="form" role="form"  action="${formUrl}/delete" method="POST">
                                                        ${csrf.field()}
                                                    <input type="hidden" name="space" value="${space.externalId}">
                                                    <input type="hidden" name="page" value="${page}">
                                                    <button type="submit" class="btn btn-xs btn-danger"><spring:message code="label.yes" /></button>
                                                    <a href="#" data-dismiss="modal" aria-hidden="true" class="btn btn-default" role="button"><spring:message code="label.no" /></a>
                                                </form>
                                            </div> 
                                        </div>
                                    </div>
                                </div>
                                <button data-toggle="modal" data-target="#delete${photo.externalId}" class="btn btn-xs btn-default"><i class="glyphicon glyphicon-trash"></i> <spring:message code="label.delete" /></button>   
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </c:forEach>
    </tbody>
    </table>
</c:if>

<c:if test="${empty activePhotos.pageList}">
    <div id="new-info-panel" class="infoop2">
            <p class="mvert0"><em><spring:message code="space.photo.none"></spring:message></em></p>
    </div>
</c:if>