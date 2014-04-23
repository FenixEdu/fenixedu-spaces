<!DOCTYPE html> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:url var="staticUrl" value="/static/fenix-spaces"/>

<script src="${staticUrl}/js/sprintf.min.js"></script>
<script src="${staticUrl}/js/moment.min.js"></script>
<script src="${staticUrl}/js/dateutils.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		var repeatsconfig = {
			"w": {
				label: "<spring:message code="calendar.repeatson.weekly" text="Semanas"/>",
				summary: "<spring:message code="calendar.repeats.weekly" text="Semanalmente"/>",
				getSummary: function(config) {
					var label = this['summary'];
					if (selectedDays.length > 0) {
						label += " <spring:message code="calendar.on" text="às"/> " + selectedDays.join(", ")
					}
					$("#summary").html(label)
				},
			},
			"d": {
				label: "<spring:message code="calendar.repeatson.daily" text="Dias"/>",
				summary: "<spring:message code="calendar.repeats.daily" text="Diariamente"/>",
				getSummary: function(config) {
					var repeatsevery = config.repeatsevery;
					return sprintf("%s de %s em %s %s", this.summary, repeatsevery , repeatsevery, this.label);
				}
			},
			"m": {
				label: "<spring:message code="calendar.repeatson.daily" text="Meses"/>",
				summary: "<spring:message code="calendar.repeats.monthly" text="Mensalmente"/>",
				getSummary: function(config) {
					var startdate = moment($("#startdate").val(), "DD/MM/YYYY")
					var value = $("input:radio[name=monthly]:checked").val();
					if (value == "dayofmonth") {
						$("#summary").html(this["summary"] + "<spring:message code="calendar.repeatson.summary.dayofmonth" text=" ao dia "/>" + startdate.date());
					}
					if (value == "dayofweek") {
						$("#summary").html(this["summary"] + "<spring:message code="calendar.repeatson.summary.dayofweek" text=" à "/>" + nthDayOfTheWeekLabel(startdate) + " " + dayOfWeekLabel(startdate));
					}
				},
			},
		
			"n": {
				label: false,
				summary: "<spring:message code="calendar.repeats.never" text="Nunca"/>",
			},
		
			"y": {
				label: "<spring:message code="calendar.repeatson.yearly" text="Anos"/>",
				summary: "<spring:message code="calendar.repeats.yearly" text="Anualmente"/>",
			}
		};
		
		var config = ${config};
		var dateFormat = getMomentDateFormat();
		var timeFormat = getMomentTimeFormat();
		var datetimeFormat = dateFormat + " " + timeFormat;
		var formatter = datetimeFormat;
		if (config.isAllDay) {
			$(".time").hide();
			formatter = dateFormat;
		} else {
			$(".time").show();
			$(".allday").hide();
		}
		$("#start").html(moment(config.start).format(formatter));
		$("#end").html(moment(config.end).format(formatter));
		var repeatsconfig = repeatsconfig[config['frequency']];
		var summary = null;
		if (repeatsconfig.getSummary) {
			summary = repeatsconfig.getSummary(config);
		} else {
			summary = repeatsconfig.summary;
		}
		$("#summary").html(summary);
	});
</script>

<div class="page-header">
  <h1><spring:message code="title.space.management" text="Space Management"/><small><spring:message code="title.create.occupation" text="Reservar Espaço"/></small></h1>
</div>

<h2><spring:message code="title.create.occupation.details" text="Detalhes da ocupação"/></h2>
<table class="table">
	<tr class="row">
		<th><spring:message code="label.create.occupation.start.date" text="Data Início"/></th>
		<td id="start">
		</td>
	</tr>
	<tr class="row">
		<th><spring:message code="label.create.occupation.end.date" text="Data Fim"/></th>
		<td id="end">
		</td>
	</tr>
	<tr class="row time">
		<th><spring:message code="label.create.occupation.start.time" text="Hora Início"/></th>
		<td id="start">
		</td>
	</tr>
	<tr class="row time">
		<th><spring:message code="label.create.occupation.end.time" text="Hora Fim"/></th>
		<td id="end">
		</td>
	</tr>
	<tr class="row allday">
		<th><spring:message code="label.create.occupation.allday" text="Todo o dia"/></th>
		<td>
			<span class="glyphicon glyphicon-ok"></span>
		</td>
	</tr>
	<tr class="row">
		<th><spring:message code="label.create.occupation.frequency" text="Frequência"/></th>
		<td id="summary">
		</td>
	</tr>
	<tr class="row">
		<th><spring:message code="label.create.occupation.intervals" text="Intervalos"/></th>
		<td>
		<ul id="intervals">
		</ul>
		</td>
	</tr>
</table>
