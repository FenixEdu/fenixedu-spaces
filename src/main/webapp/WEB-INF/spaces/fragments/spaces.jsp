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
<spring:url value='/spaces/' var="deleteBaseUrl" />

<script type="text/javascript">
	$(document).ready(function() {
		var deleteUrl = "${deleteBaseUrl}";
		$('#confirmDelete').on('show.bs.modal', function (e) {
			      var $spaceName = $(e.relatedTarget).attr('data-space-name');
			      var $message = "Are you sure you want to delete '" + $spaceName + "' ?";
			      $(this).find('.modal-body p').text($message);
			      var $title = "Delete '" + $spaceName + "'";
			      var $spaceId = $(e.relatedTarget).attr('data-space-id');
			      $(this).find('.modal-title').text($title);

			      $('#confirmDelete').find('.modal-footer #confirm').on('click', function(){
			    	  $.ajax({
						    url: deleteUrl + $spaceId,
						    type: 'DELETE',
						    success: function(result) {
						    	location.reload();
						    }
						});
				  });
			  });
		});
</script>

<c:choose>
	<c:when test="${not empty spaces}">
		<table class="table">
			<thead>
				<tr>
					<th><spring:message code="label.spaces.type" text="Type"/></th>
					<th><spring:message code="label.spaces.name" text="Name"/></th>
					<th><spring:message code="label.spaces.blueprint.number" text="Número da Planta"/></th>
					<th><spring:message code="label.spaces.number.sub.spaces" text="Number of Sub Spaces"/></th>
					<th><spring:message code="label.spaces.operations" text="Operations"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="space" items="${spaces}">
					<spring:url value="/spaces/edit/${space.externalId}" var="editUrl" />
					<spring:url value="/spaces/timeline/${space.externalId}" var="timelineUrl" />
					<spring:url value="/spaces/view/${space.externalId}" var="viewUrl" />
					<spring:url value="/spaces/schedule/${space.externalId}" var="scheduleUrl" />
					<spring:url value="/spaces/create/${space.externalId}" var="createSubSpaceUrl" />
					<spring:url value="/spaces/access/${space.externalId}" var="manageAccessUrl" />
					
					<tr>
						<td>${space.classification.name.content}</td>
						<td>${space.name}</td>
						<td>${space.getBlueprintNumber().orElse("-")}</td>
						<td>${fn:length(space.children)}</td>
						<td>
							<a href="${viewUrl}"  class="btn btn-default" title="View"><span class="glyphicon glyphicon-eye-open"></span></a>
							<c:if test="${space.isSpaceManagementMember(currentUser)}">
								<a href="${timelineUrl}" class="btn btn-default" title="Timeline"><span class="glyphicon glyphicon-time"></span></a>
								<a href="${editUrl}" class="btn btn-default" title="Edit"><span class="glyphicon glyphicon-pencil"></span></a>
								<button data-space-id="${space.externalId}" data-space-name="${space.name}" data-toggle="modal" data-target="#confirmDelete" class="btn btn-default" title="delete"><span class="glyphicon glyphicon-remove"></span></button>
							</c:if>
							<c:if test="${not empty space.occupationSet}">
								<a href="${scheduleUrl}" class="btn btn-default" title="<spring:message code="title.space.schedule" text="Horário do Espaço"/>"><span class="glyphicon glyphicon-dashboard"></span></a>
							</c:if>
							<%-- <a href="${manageAccessUrl}" class="btn btn-default" title="Access"><span class="glyphicon glyphicon-user"></span></a> --%>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<spring:message code="label.empty.spaces" text="No available spaces." />
	</c:otherwise>
</c:choose>
<!-- Modal Dialog -->
<div class="modal fade" id="confirmDelete" role="dialog" aria-labelledby="confirmDeleteLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title"><spring:message code="label.spaces.delete.title" text="Delete Permanently"/></h4>
      </div>
      <div class="modal-body">
        <p><spring:message code="label.spaces.delete.message" text="Are you sure you want to delete this ?"/></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="label.cancel" text="Cancel"/></button>
        <button type="button" class="btn btn-danger" id="confirm"><spring:message code="label.delete" text="Delete"/></button>
      </div>
    </div>
  </div>
</div>