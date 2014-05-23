<table class="table">
	<thead>
		<th><spring:message code="label.occupation.request.identification" text="identification" /></th>
		<th><spring:message code="label.occupation.request.instant" text="instant" /></th>
		<th><spring:message code="label.occupation.request.subject" text="subject" /></th>
		<th><spring:message code="label.occupation.request.requestor" text="requestor" /></th>
		<th><spring:message code="label.occupation.request.actions" text="actions" /></th>

	</thead>
	<tbody>
		<c:forEach var="occupationRequest" items="${requests}">
			<c:set var="id" value="${occupationRequest.identification}" />
			<c:set var="instant" value="${occupationRequest.presentationInstant}" />
			<c:set var="subject" value="${occupationRequest.subject}" />
			<c:set var="requestor" value="${occupationRequest.requestor}" />
			<tr>
				<td><a href="${requestUrl}/${occupationRequest.externalId}"> ${id} </a></td>
				<td>${instant}</td>
				<td><a href="${requestUrl}/${occupationRequest.externalId}"> ${subject} </a></td>
				<td>${requestor.presentationName}</td>
				<td><a href="${requestUrl}/${occupationRequest.externalId}"> <spring:message code="label.occupation.request.deal"
							text="Tratar Pedido" />
				</a></td>
			</tr>
		</c:forEach>
	</tbody>
</table>