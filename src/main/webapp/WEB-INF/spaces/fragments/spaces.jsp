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
					<th>Type</th>
					<th>Name</th>
					<th>Number of Sub Spaces</th>
					<th>Operations</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="space" items="${spaces}">
					<spring:url value="/spaces/edit/${space.externalId}" var="editUrl" />
					<spring:url value="/spaces/timeline/${space.externalId}" var="timelineUrl" />
					<spring:url value="/spaces/view/${space.externalId}" var="viewUrl" />
					<spring:url value="/spaces/create/${space.externalId}" var="createSubSpaceUrl" />
					<tr>
						<td>${space.classification.name.content}</td>
						<td>${space.name}</td>
						<td>${fn:length(space.childrenSet)}</td>
						<td>
							<a href="${viewUrl}"  class="btn btn-default" title="View"><span class="glyphicon glyphicon-eye-open"></span></a>
							<a href="${timelineUrl}" class="btn btn-default" title="Timeline"><span class="glyphicon glyphicon-time"></span></a> 
							<!--  <a href="${timelineUrl}" class="btn btn-primary"> <spring:message code="link.space.view" text="Timeline" /></a>--> 
							<!-- <a href="${editUrl}" class="btn btn-warning"><spring:message code="link.space.edit" text="Edit" /></a> -->
							<a href="${editUrl}" class="btn btn-default" title="Edit"><span class="glyphicon glyphicon-pencil"></span></a> 
							<!-- <a href="${createSubSpaceUrl}" class="btn btn-success"><spring:message code="link.space.subspace.create" text="Create Subspace" /></a> -->
							<button data-space-id="${space.externalId}" data-space-name="${space.name}" data-toggle="modal" data-target="#confirmDelete" class="btn btn-default" title="delete"><span class="glyphicon glyphicon-remove"></span></button>
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
        <h4 class="modal-title">Delete Parmanently</h4>
      </div>
      <div class="modal-body">
        <p>Are you sure about this ?</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
        <button type="button" class="btn btn-danger" id="confirm">Delete</button>
      </div>
    </div>
  </div>
</div>