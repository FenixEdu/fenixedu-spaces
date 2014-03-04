<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title"><spring:message code="title.parent.spaces" text="Parent Spaces" /></h3>
	</div>
	<div class="panel-body">
		<c:if test="${not empty parentSpace }">
			<c:forEach var="space" items="${parentSpace.path}">
				<c:url var="viewUrl" value="/spaces/view/${space.externalId}" />
				<a href="${viewUrl}">${space.name}</a>&nbsp;&raquo;
		  	</c:forEach>
		</c:if>
		<c:if test="${empty parentSpace }">
			<spring:message code="label.empty.parent.spaces" text="No parent spaces."/>
		</c:if>
	</div>
</div>
