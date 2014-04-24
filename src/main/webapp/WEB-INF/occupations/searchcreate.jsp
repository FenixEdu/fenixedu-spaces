<!DOCTYPE html> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:url var="staticUrl" value="/static/fenix-spaces"/>

<link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">


<script src="${staticUrl}/js/sprintf.min.js"></script>
<script src="${staticUrl}/js/moment.min.js"></script>
<script src="${staticUrl}/js/dateutils.js"></script>
<script src="${staticUrl}/js/jquery-ui.min.js"></script>


<script type="text/javascript">
	var config = ${config};
	var freeSpaces = {};
	var selectedSpaces = [];
	
	<c:forEach var="space" items="${freeSpaces}">
		<c:if test="${not empty space.name}">
			freeSpaces["${space.externalId}"] = {name : "${space.nameWithParents}", allocatableCapacity : "0"};
		</c:if>
	</c:forEach>
	
	var documentReady = function() {
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
			var dateFormat = getMomentDateFormat();
			var timeFormat = getMomentTimeFormat();
			var eventFormatter = dateFormat + " " + timeFormat;
			if (config.isAllDay) {
				eventFormatter = dateFormat;
				$(".time").hide();
			} else {
				$("#starttime").html(moment(config.start).format(timeFormat));
				$("#endtime").html(moment(config.end).format(timeFormat));
				$(".time").show();
				$(".allday").hide();
			}
			
			$("#startdate").html(moment(config.start).format(dateFormat));
			$("#enddate").html(moment(config.end).format(dateFormat));
			
			var repeatsconfig = repeatsconfig[config['frequency']];
			var summary = null;
			if (repeatsconfig.getSummary) {
				summary = repeatsconfig.getSummary(config);
			} else {
				summary = repeatsconfig.summary;
			}
			$("#summary").html(summary);
			
			var events = ${events};

			eventsContent = "<ul>";
			$(events).each(function() {
				var start = moment(this.start, "X").format(eventFormatter)
				var end = moment(this.end, "X").format(eventFormatter)
				var event = sprintf("%s - %s", start, end)
				content += sprintf("<li>%s</li>", event)
			});
			content += "</ul>";
			$("#events-number").html(events.length);


			// select spaces

			function reloadBtnRemoveSpace(spaceId) {
				$("#" + spaceId).click(function() {
					var spaceId = $(this).attr('id')
					$(this).parents("tr").remove()
					var index = selectedSpaces.indexOf(spaceId)
					selectedSpaces.splice(index, 1);
				});
			}
			function selectSpace(spaceId) {
				if (selectedSpaces.indexOf(spaceId) != -1) {
					return;
				}
				selectedSpaces.push(spaceId);
				var template = "<tr class='selected-space' data-id='${space.id}'><td>%s</td><td>%s</td><td><button class='btn btn-default btn-remove-space' id='%s'><span class='glyphicon glyphicon-remove'></span></button></td></tr>"
				var freeSpace = freeSpaces[spaceId]
				$("#selected-spaces tbody").append(sprintf(template, freeSpace.name, freeSpace.allocatableCapacity, spaceId))
				reloadBtnRemoveSpace(spaceId)
			}

			$("#choose-space-form").submit(function(e) {
				e.preventDefault();
				var selectedSpace = $("#choose-space :selected").val();
				selectSpace(selectedSpace)
			});
			
		};
	
	$(document).ready(documentReady);
	$(document).tooltip({
		items : "#events-number",
		content: function() {
			var element = $(this);
			return content;
		}
	});
</script>

<div class="page-header">
  <h1><spring:message code="title.space.management" text="Space Management"/><small><spring:message code="title.create.occupation" text="Reservar Espaço"/></small></h1>
</div>

<h2><spring:message code="title.create.occupation.details" text="Detalhes da ocupação"/></h2>

<table class="table">
	<tr class="row">
		<th><spring:message code="label.create.occupation.start.date" text="Data Início"/></th>
		<td id="startdate">
		</td>
	</tr>
	<tr class="row">
		<th><spring:message code="label.create.occupation.end.date" text="Data Fim"/></th>
		<td id="enddate">
		</td>
	</tr>
	<tr class="row time">
		<th><spring:message code="label.create.occupation.start.time" text="Hora Início"/></th>
		<td id="starttime">
		</td>
	</tr>
	<tr class="row time">
		<th><spring:message code="label.create.occupation.end.time" text="Hora Fim"/></th>
		<td id="endtime">
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
			<a href="#" id="events-number"></a>
		</td>
	</tr>
</table>

<h2><spring:message code="title.create.occupation.choose.space" text="Escolher Espaço"/></h2>

<form role="form" id="choose-space-form">
  <div class="form-group">
	<label for="choose-space"></label>
	<select id="choose-space">
		<c:forEach var="space" items="${freeSpaces}">
			<c:if test="${not empty space.name}">
				<option value="${space.externalId}">${space.nameWithParents}</option>
			</c:if>
		</c:forEach>
	</select>
  </div>
  <button type="submit" class="btn btn-success"><spring:message code="label.create.occupation.add.space" text="Adicionar Espaço"/></button>
</form>

<table class="table" id="selected-spaces">
	<thead>
		<th><spring:message code="label.create.occupation.selected.space.name" text="Nome"></spring:message></th>
		<th><spring:message code="label.create.occupation.selected.space.normal.capacity" text="Capacidade Normal"></spring:message></th>
		<th><spring:message code="label.create.occupation.selected.space.operations" text="Ações"></spring:message>
	</thead>
	<tbody>
	</tbody>
</table>


