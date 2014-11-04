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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="page-header">
	<h1>
		<spring:message code="title.space.management" text="Space Management" />
		<small>${information.name}</small>
	</h1>
</div>

<%@include file="fragments/parent.jsp"%>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><spring:message code="title.view.space" text="Space Details" /></h3>
	</div>
	<div class="panel-body">
		<table class="table">
		<tr>
			<th scope="row"><spring:message code="label.spaces.operations" text="Operations" /></th>
				<td>
					<spring:url value="/spaces/edit/${space.externalId}" var="editUrl" />
					<spring:url value="/spaces/timeline/${space.externalId}" var="timelineUrl" />
					<spring:url value="/spaces-view/schedule/${space.externalId}" var="scheduleUrl" />
					<spring:url value="/spaces/create/${space.externalId}" var="createSubSpaceUrl" />
					<spring:url value="/spaces/access/${space.externalId}" var="manageAccessUrl" />
					<spring:url value="/spaces/occupants/${space.externalId}" var="manageOccupantsUrl" />
					<c:if test="${not empty space.occupationSet}">
						<a href="${scheduleUrl}" title="<spring:message code="title.space.schedule" text="Horário do Espaço"/>"><span class="glyphicon glyphicon-dashboard"></span> <spring:message code="label.spaces.operations.viewSchedule" text="View Schedule"/></a> 
					</c:if>
					<c:if test="${space.isSpaceManagementMember(currentUser)}">
					<c:if test="${not empty space.occupationSet}"> | </c:if>
						<a href="${timelineUrl}" title="Timeline"><span class="glyphicon glyphicon-time"></span> <spring:message code="label.spaces.operations.viewTimeline" text="View Timeline"/></a> |
						<a href="${editUrl}" title="Edit"><span class="glyphicon glyphicon-pencil"></span> <spring:message code="label.spaces.operations.edit" text="Edit"/></a> |
						<a href="${manageAccessUrl}" title="Access"><span class="glyphicon glyphicon-ban-circle"></span> <spring:message code="label.spaces.operations.manageAccess" text="Manage Access"/></a> |
						<a href="${manageOccupantsUrl}" title="Occupants"><span class="glyphicon glyphicon-user"></span> <spring:message code="label.spaces.operations.manageOccupants" text="Manage Occupants"/></a>
					</c:if>
				</td>
			</tr>
			<tr>
				<th scope="row"><spring:message code="label.spaces.validFrom" text="Valid From" /></th>
				<td><spring:eval expression="information.validFrom" /></td>
			</tr>
			<tr>
				<th scope="row"><spring:message code="label.spaces.validUntil" text="Valid Until" /></th>
				<td><spring:eval expression="information.validUntil" /></td>
			</tr>
			<tr>
				<th scope="row"><spring:message code="label.spaces.name" text="Name" /></th>
				<td>${information.name}</td>
			</tr>
			<tr>
				<th scope="row"><spring:message code="label.spaces.identification" text="Identification" /></th>
				<td>${information.identification}</td>
			</tr>
			<tr>
				<th scope="row"><spring:message code="label.spaces.classification" text="Classification" /></th>
				<td>${information.classification.name.content}</td>
			</tr>
			<tr>
				<th scope="row"><spring:message code="label.spaces.allocatableCapacity" text="Allocatable Capacity" /></th>
				<td>${information.allocatableCapacity}</td>
			</tr>
			<tr>
				<th scope="row"><spring:message code="label.spaces.blueprintNumber" text="Blueprint Number" /></th>
				<td>${information.blueprintNumber}</td>
			</tr>
			<tr>
				<th scope="row">
					<spring:message code="label.spaces.blueprint" text="Blueprint" />
				</th>
				<spring:url var="viewUrl" value="/spaces-view/view"/>
				<spring:url var="scaleOpts" value=""/>
				<spring:url var="keepScale" value=""/>
				<c:if test="${not empty scale}">
					<c:set var="keepScale" value="&scale=${scale}"/>
				</c:if>
				<c:if test="${(empty viewOriginalSpaceBlueprint) and (empty viewBlueprintNumbers) and (empty viewIdentifications) and (empty viewDoorNumbers)}">
					<c:set var="scaleOpts" value="viewIdentifications=true"/>
				</c:if>
				<c:if test="${not empty viewOriginalSpaceBlueprint}">
					<c:set var="scaleOpts" value="viewOriginalSpaceBlueprint=${viewOriginalSpaceBlueprint}"/>
				</c:if>
				<c:if test="${not empty viewBlueprintNumbers}">
					<c:set var="scaleOpts" value="viewBlueprintNumbers=${viewBlueprintNumbers}"/>
				</c:if>
				<c:if test="${not empty viewIdentifications}">
					<c:set var="scaleOpts" value="viewIdentifications=${viewIdentifications}"/>
				</c:if>
				<c:if test="${not empty viewDoorNumbers}">
					<c:set var="scaleOpts" value="viewDoorNumbers=${viewDoorNumbers}"/>
				</c:if>
				
				<td>
					<a href="${viewUrl}/${space.externalId}?viewIdentifications=true${keepScale}"><spring:message code="label.spaces.viewBlueprintIds" text="Identifications" /></a> |
					<a href="${viewUrl}/${space.externalId}?viewDoorNumbers=true${keepScale}"><spring:message code="label.spaces.viewDoorNumbers" text="Door Numbers" /></a> |
					<a href="${viewUrl}/${space.externalId}?viewBlueprintNumbers=true${keepScale}"><spring:message code="label.spaces.viewBlueprintNumbers" text="Blueprint Numbers" /></a> |
					<a href="${viewUrl}/${space.externalId}?viewOriginalSpaceBlueprint=true${keepScale}"><spring:message code="label.spaces.viewOriginalBlueprint" text="Original Blueprint" /></a> |
					<c:if test="${scale < 100 }">
						<a href="${viewUrl}/${space.externalId}?${scaleOpts}&scale=100"><span class="glyphicon glyphicon-zoom-in"/></a>					
					</c:if>
					<c:if test="${scale >= 100 }">
						<a href="${viewUrl}/${space.externalId}?${scaleOpts}"><span class="glyphicon glyphicon-zoom-out"/></a>					
					</c:if>	
				</td>
				
				<spring:url var="blueprintUrl" value="/spaces-view/blueprint/${space.externalId}" />
				<spring:url var="vDoorNum" value="false" />
				<spring:url var="vBlueprintNum" value="false" />
				<spring:url var="vIds" value="false" />
				<spring:url var="vOrigSpaceBP" value="false" />
				<spring:url var="theScale" value="50" />
				<c:if test="${(empty viewOriginalSpaceBlueprint) and (empty viewBlueprintNumbers) and (empty viewIdentifications) and (empty viewDoorNumbers)}">
					<c:set var="vIds" value="true"/>
				</c:if>
				<c:if test="${not empty viewOriginalSpaceBlueprint}">
					<c:set var="vOrigSpaceBP" value="${viewOriginalSpaceBlueprint}"/>
				</c:if>
				<c:if test="${not empty viewBlueprintNumbers}">
					<c:set var="vBlueprintNum" value="${viewBlueprintNumbers}"/>
				</c:if>
				<c:if test="${not empty viewIdentifications}">
					<c:set var="vIds" value="${viewIdentifications}"/>
				</c:if>
				<c:if test="${not empty viewDoorNumbers}">
					<c:set var="vDoorNum" value="${viewDoorNumbers}"/>
				</c:if>
				<c:if test="${not empty scale}">
					<c:set var="theScale" value="${scale}"/>
				</c:if>
				<c:set var="blueprintUrl" value="${blueprintUrl}?viewDoorNumbers=${vDoorNum}"/>
				<c:set var="blueprintUrl" value="${blueprintUrl}&viewBlueprintNumbers=${vBlueprintNum}"/>
				<c:set var="blueprintUrl" value="${blueprintUrl}&viewIdentifications=${vIds}"/>
				<c:set var="blueprintUrl" value="${blueprintUrl}&viewOriginalSpaceBlueprint=${vOrigSpaceBP}"/>
				<c:set var="blueprintUrl" value="${blueprintUrl}&scale=${theScale}"/>
				</tr>
				<tr>
				<td colspan="100%">
					<img src="${blueprintUrl}" usemap="#roomLinksMap"/>
					<c:if test="${not empty blueprintTextRectangles}">
						<map id ="roomLinksMap" name="roomLinksMap">
							<c:forEach var="blueprintTextRectanglesEntry" items="${blueprintTextRectangles}">
								<c:set var="blueprintSpace" value="${blueprintTextRectanglesEntry.key}"/>
								<c:forEach var="blueprintTextRectangle" items="${blueprintTextRectanglesEntry.value}">
									<c:set var="p1" value="${blueprintTextRectangle.p1}"/>				
									<c:set var="p2" value="${blueprintTextRectangle.p2}" />				
									<c:set var="p3" value="${blueprintTextRectangle.p3}" />				
									<c:set var="p4" value="${blueprintTextRectangle.p4}" />
									<c:set var="coords" value="${p1.x},${p1.y},${p2.x},${p2.y},${p3.x},${p3.y},${p4.x},${p4.y}"/>
									<c:set var="urlToCoords" value="${viewUrl}/${blueprintSpace.externalId}"/>
									<c:if test="${not empty scale}">
										<c:set var="urlToCoords" value="${urlToCoords}?scale=${scale}"/>				
									</c:if>
									<area shape="poly" coords="${coords}" href="${urlToCoords}"/>
								</c:forEach>
							</c:forEach>
						</map>
					</c:if>
					</td>
				</tr>
				<tr>
					<th scope="row"><spring:message code="label.spaces.area" text="Area" /></th>
					<td>${information.area}</td>
				</tr>
				<c:forEach var="metadata" items="${information.metadata}">
					<c:set var="field" value="${metadata.key}" />
					<c:set var="metadataSpec" value="${information.classification.getMetadataSpec(field).get()}" />
					<c:set var="value" value="${metadata.value}" />
					<tr>
						<th scope="row">${metadataSpec.description.content}</th>
						<td>
							<c:if test='${metadataSpec.type.simpleName.equals("Boolean")}'>
								<c:choose>
									<c:when test="${value == true}">
										<i class="glyphicon glyphicon-ok"></i>
									</c:when>
									<c:when test="${value == false }">
										<i class="glyphicon glyphicon-remove"></i>
									</c:when>
								</c:choose>
							</c:if>
							<c:if test='${!metadataSpec.type.simpleName.equals("Boolean")}'>
								${value}
							</c:if>
						</td>
					</tr>
			</c:forEach>
		</table>
	</div>
</div>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><spring:message code="title.sub.spaces" text="Sub Spaces" /></h3>
	</div>
	<div class="panel-body">
		<%@include file="fragments/spaces.jsp"%>
	</div>
</div>
<spring:url var="exportUrl" value="/spaces-view/export/${space.externalId}" />
<spring:url var="excelGif" value="/images/" />
<p><a href="${exportUrl}"><img src="${excelGif}/excel.gif"/><spring:message code="label.spaces.Export" text=" Export to Excel" /></a></p>

