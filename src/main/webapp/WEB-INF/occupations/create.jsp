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
  <h1><spring:message code="title.space.management"/><small><spring:message code="title.create.occupation.reserve"/></small></h1>
</div>

<h2><spring:message code="title.create.occupation.select.period"/></h2>

<button class="btn btn-success" id="search-spaces"><spring:message code="title.create.occupation.search.periods"/></button>

<%@include file="calendar.jsp" %>


<form action="${searchUrl}" id="form-search-spaces" method="post">
	${csrf.field()}
	<input type="hidden" name="events" id="events"/>
	<input type="hidden" name="config" id="config"/>
	<c:if test="${not empty request}">
		<input type="hidden" name="request" value="${request.externalId}"/>
	</c:if>
</form>
