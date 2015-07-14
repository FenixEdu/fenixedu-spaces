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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:url var="staticUrl" value="/static/fenix-spaces"/>

<link href="${staticUrl}/css/fullcalendar.css" rel="stylesheet">
<link href="${staticUrl}/css/fullcalendar.print.css" rel="stylesheet" media="print">
<link rel="stylesheet" href="${staticUrl}/css/jquery.datetimepicker.css">
	
<script src="${staticUrl}/js/jquery-ui.min.js"></script>
<script src="${staticUrl}/js/fullcalendar.min.js"></script>
<script src="${staticUrl}/js/moment.min.js"></script>
<script src="${staticUrl}/js/dateutils.js"></script>
<script src="${staticUrl}/js/jquery.datetimepicker.js"></script>
<script src="${staticUrl}/js/sprintf.min.js"></script>

<spring:url var="eventsUrl" value="/spaces-view/schedule/${space.externalId}/events"/>

<script type="text/javascript">
	$(document).ready(function() {
	    var colors = ["#1F505E", "#75B08A", "#F0E797", "#FF9D84", "#FF5460"];
	
		var calendar = {
			header: {
				left: 'prev,next today',
				center: 'title',
				right: 'agendaWeek,month,year'
			},
			monthNames: ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'],
			monthNamesShort: ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'],
			dayNames: dayNames,
			dayNamesShort: dayNamesShort,
			buttonText: {
	   			today:    'Hoje',
	   			month:    'Mês',
	   			week:     'Semana',
	   			day:      'Ano'
			},
			timeFormat: { month: 'H:mm{ - H:mm}', 'agendaWeek' : "H:mm{ - H:mm}" } ,
			columnFormat : {
   					month: 'ddd',    // Mon
   					week: 'ddd d/M', // Mon 9/7
   					day: 'dddd d/M'  // Monday 9/7
			},
			minTime : "08:00",
			maxTime : "24:00",
			axisFormat: 'H:mm',
			allDaySlot : true,
			defaultView: "agendaWeek",
			firstDay: 1,
			editable: false,
			events : "${eventsUrl}",
			eventDataTransform: function(event) {
				event.textColor = "black";
				event.backgroundColor = colors[event.id % colors.length];
				return event;
    		},
    	    eventClick: function(event) {
    	        if (event.url) {
    	            window.open(event.url);
    	            return false;
    	        }
    	    },
    		eventMouseover: function(event){
    			if(event.info){
    				this.title = event.info;
    			}
    		}

		};
		$('#calendar').fullCalendar(calendar);
	});
	
</script>

<div class="page-header">
  <h1><spring:message code="title.space.management"/><small><spring:message code="title.space.schedule"/></small></h1>
</div>

<h3><c:out value="${space.presentationName}"/></h3>
<div id="calendar"></div>
