<!DOCTYPE html> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:url var="staticUrl" value="/static/fenix-spaces"/>
<spring:url var="searchUrl" value="/spaces/occupations/search-create"/>

<script type="text/javascript"> 

	$(document).ready(function() {
		$("#form-search-spaces").hide();
		$("#search-spaces").click(function() {
			events = $("#calendar").fullCalendar("clientEvents");
			if (events.length == 0) {
				alert("Por favor seleccione um periodo!")
			}
			jsonEvents = []
			$(events).each(function() {
				var jsonEvent = { 
						start : moment(this.start).format("X"),
						end : moment(this.end).format("X")
				}
				jsonEvents.push(jsonEvent)
			});
			
			var exportConfig = function(config) {
				var exportConfig = {
					"w" : function(config) {
							config.weekdays = config.weekdays();
							return JSON.stringify(config);
					},
				};
				
				var frequency = config['frequency'];
				var exportConfiguration = exportConfig[frequency];
				
				var datetimeFormatter = getMomentDateFormat() + " " + getMomentTimeFormat()
				config.start = config.start.format(datetimeFormatter);
				config.end = config.end.format(datetimeFormatter);
				
				if (exportConfiguration === undefined) {
					exportConfiguration = JSON.stringify;
				}
				
				return exportConfiguration(config);
			}
			
			$("#config").val(exportConfig(occupationEvents[1]));
			$("#events").val(JSON.stringify(jsonEvents));
			$("#form-search-spaces").submit();
		});
	})
</script>


<div class="page-header">
  <h1><spring:message code="title.space.management" text="Space Management"/><small><spring:message code="title.create.occupation" text="Reservar Espaço"/></small></h1>
</div>

<h2><spring:message code="title.create.occupation.select.period" text="Seleccionar Periodo"/></h2>

<%@include file="calendar.jsp" %>

<button class="btn btn-success" id="search-spaces"><spring:message code="title.create.occupation.search.periods" text="Procurar Espaços"/></button>

<form action="${searchUrl}" id="form-search-spaces" method="post">
	<input type="hidden" name="events" id="events"/>
	<input type="hidden" name="config" id="config"/>
	<c:if test="${not empty request}">
		<input type="hidden" name="request" value="${request.externalId}"/>
	</c:if>
</form>
