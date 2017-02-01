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

<link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">


<script src="${staticUrl}/js/bootbox.js"></script>
<script src="${staticUrl}/js/sprintf.min.js"></script>
<script src="${staticUrl}/js/moment.min.js"></script>
<script src="${staticUrl}/js/dateutils.js"></script>
<script src="${staticUrl}/js/jquery-ui.min.js"></script>


<script type="text/javascript">

	
	var weekdaysLabels = function () {
		var weekdaysNumbers = [1,2,3,4,5,6,7];
		var weekdaysAcronyms = ["mo","tu", "we", "th", "fr", "sa", "su"]
		var weekdaysLabels = [
			"<spring:message code="calendar.daysofweek.mo"/>",
			"<spring:message code="calendar.daysofweek.tu"/>",
			"<spring:message code="calendar.daysofweek.we"/>",
			"<spring:message code="calendar.daysofweek.th"/>",
			"<spring:message code="calendar.daysofweek.fr"/>",
			"<spring:message code="calendar.daysofweek.sa"/>",
			"<spring:message code="calendar.daysofweek.su"/>"
		];
		
		return {
			
			getLabelByNumber : function(number) {
				return weekdaysLabels[number - 1];
			},
			
			getLabelByAcronym : function(acronym) {
				var index = $.inArray(acronym, weekdaysAcronyms);
				return weekdaysLabels[index];
			}
		}
	};
	
	
	var config = ${config};
	var freeSpaces = {};
	var selectedSpaces = [];
	
	<c:forEach var="space" items="${freeSpaces}">
		<c:if test="${not empty space.name}">
			freeSpaces["${space.externalId}"] = {name : "<c:out value='${space.presentationName}'/>", allocatableCapacity : "${space.allocatableCapacity}", examCapacity : "${space.getMetadata('examCapacity').orElse(0)}"};
		</c:if>
	</c:forEach>
	
	var documentReady = function() {
	
		var dateFormat = getMomentDateFormat();
		var timeFormat = getMomentTimeFormat();
		var eventFormatter = dateFormat + " " + timeFormat;
		
		function getStart(config) {
			return moment(config.start, eventFormatter);
		}
		
		function getEnd(config) {
			return moment(config.end, eventFormatter);
		}
			
		var repeatsconfig = {
				"w": {
					label: "<spring:message code="calendar.repeatson.weekly"/>",
					summary: "<spring:message code="calendar.repeats.weekly"/>",
					getSummary: function(config) {
						var label = this['summary'];
						var selectedDays = config.weekdays;
						if (selectedDays.length > 0) {
							selectedDays = $(selectedDays).map(function() {return weekdaysLabels().getLabelByNumber(this);}).get();
							label += " <spring:message code="calendar.on"/> " + selectedDays.join(", ")
						}
						$("#summary").html(label)
					},
				},
				"d": {
					label: "<spring:message code="calendar.repeatson.daily"/>",
					summary: "<spring:message code="calendar.repeats.daily"/>",
					getSummary: function(config) {
						var repeatsevery = config.repeatsevery;
						return sprintf("%s de %s em %s %s", this.summary, repeatsevery , repeatsevery, this.label);
					}
				},
				"m": {
					label: "<spring:message code="calendar.repeatson.monthly"/>",
					summary: "<spring:message code="calendar.repeats.monthly"/>",
					getSummary: function(config) {
						var startdate = getStart(config)
						var value = config.monthlyType;
						if (value == "dayofmonth") {
							$("#summary").html(this["summary"] + "<spring:message code="calendar.repeatson.summary.dayofmonth"/>" + startdate.date());
						}
						if (value == "dayofweek") {
							$("#summary").html(this["summary"] + "<spring:message code="calendar.repeatson.summary.dayofweek"/>" + nthDayOfTheWeekLabel(startdate) + " " + dayOfWeekLabel(startdate));
						}
					},
				},
			
				"n": {
					label: false,
					getSummary: function() {
						return "<spring:message code="calendar.repeats.never"/>";
					}
				},
			
				"y": {
					label: "<spring:message code="calendar.repeatson.yearly"/>",
					getSummary: function() {
						return "<spring:message code="calendar.repeats.yearly"/>"
					}
				}
			};
			
			if (config.isAllDay) {
				$(".time").hide();
			} else {
				$("#starttime").html(getStart(config).format(timeFormat));
				$("#endtime").html(getEnd(config).format(timeFormat));
				$(".time").show();
				$(".allday").hide();
			}
			
			$("#startdate").html(getStart(config).format(dateFormat));
			$("#enddate").html(getEnd(config).format(dateFormat));
			
			var repeatsconfig = repeatsconfig[config['frequency']];
			$("#summary").html(repeatsconfig.getSummary(config));
			
			var events = ${events};

			eventsContent = "<ul>";
			$(events).each(function() {
				var start = moment(this.start, "X").format(eventFormatter)
				var end = moment(this.end, "X").format(eventFormatter)
				var event = sprintf("%s - %s", start, end)
				eventsContent += sprintf("<li>%s</li>", event)
			});
			eventsContent += "</ul>";
			$("#events-number").html(events.length);


			// select spaces

			function reloadBtnRemoveSpace(spaceId) {
				$("#" + spaceId).click(function() {
					var spaceId = $(this).attr('id')
					$(this).parents("tr").remove()
					var index = selectedSpaces.indexOf(spaceId)
					selectedSpaces.splice(index, 1);
					if (selectedSpaces.length == 0) {
						$("#selected-spaces").hide();
					}
				});
			}
			
			$("#selected-spaces").hide();
			
			function selectSpace(spaceId) {
				if (selectedSpaces.indexOf(spaceId) != -1) {
					return;
				}
				selectedSpaces.push(spaceId);
				var template = "<tr class='selected-space' data-id='${space.id}'><td>%s</td><td>%s</td><td>%s</td><td><button class='btn btn-default btn-remove-space' id='%s'><span class='glyphicon glyphicon-remove'></span></button></td></tr>"
				var freeSpace = freeSpaces[spaceId]
				$("#selected-spaces tbody").append(sprintf(template, freeSpace.name, freeSpace.allocatableCapacity, freeSpace.examCapacity, spaceId))
				reloadBtnRemoveSpace(spaceId)
				$("#selected-spaces").show();
			}

			$("#choose-space-form").submit(function(e) {
				var selectedSpace = $("#choose-space :selected").val();
				selectSpace(selectedSpace)
				return false;
			});
			
			$("#create-occupation-form").submit(function(e){
				if (selectedSpaces.length > 0) {
					$("#selected-spaces-input").val(JSON.stringify(selectedSpaces));
					$("#config-input").val(JSON.stringify(config));
					$("#events-input").val(JSON.stringify(events));
				} else {
					bootbox.alert("<spring:message code="error.occupation.no.selected.spaces"/>");
					return false;
				}
			});
			
		};
	
	$(document).ready(documentReady);
	$(document).tooltip({
		items : "#events-number",
		content: function() {
			return eventsContent;
		}
	});
</script>


<c:if test="${not empty errorMessage}">
	<h3 class="bg-danger"><c:out value="${errorMessage}"/></h3>
</c:if>

<div class="page-header">
  <h1><spring:message code="title.space.management"/><small><spring:message code="title.create.occupation.reserve"/></small></h1>
</div>

<h3><spring:message code="title.create.occupation.details"/></h3>

<table class="table">
	<tr class="row">
		<th><spring:message code="label.create.occupation.start.date"/></th>
		<td id="startdate">
		</td>
	</tr>
	<tr class="row">
		<th><spring:message code="label.create.occupation.end.date"/></th>
		<td id="enddate">
		</td>
	</tr>
	<tr class="row time">
		<th><spring:message code="label.create.occupation.start.time"/></th>
		<td id="starttime">
		</td>
	</tr>
	<tr class="row time">
		<th><spring:message code="label.create.occupation.end.time"/></th>
		<td id="endtime">
		</td>
	</tr>
	<tr class="row allday">
		<th><spring:message code="label.create.occupation.allday"/></th>
		<td>
			<span class="glyphicon glyphicon-ok"></span>
		</td>
	</tr>
	<tr class="row">
		<th><spring:message code="label.create.occupation.frequency"/></th>
		<td id="summary">
		</td>
	</tr>
	<tr class="row">
		<th><spring:message code="label.create.occupation.intervals"/></th>
		<td>
			<a href="#" id="events-number"></a>
		</td>
	</tr>
</table>

<h3><spring:message code="title.create.occupation.choose.space"/></h3>

<form role="form" id="choose-space-form">
  <div class="form-group">
	<label for="choose-space"></label>
	<select id="choose-space">
		<c:forEach var="space" items="${freeSpaces}">
			<c:if test="${not empty space.name}">
				<option value="${space.externalId}"><c:out value="${space.presentationName} [${space.allocatableCapacity},${space.getMetadata('examCapacity').orElse(0)}]"/></option>
			</c:if>
		</c:forEach>
	</select>
  </div>
  <input type="submit" class="btn btn-success" value="<spring:message code="label.create.occupation.add.space"/>"></input>
</form>


<table class="table" id="selected-spaces">
	<caption><spring:message code="title.create.occupation.selected.space"/></caption>
	<thead>
		<th><spring:message code="label.create.occupation.selected.space.name"/></th>
		<th><spring:message code="label.create.occupation.selected.space.normal.capacity"/></th>
		<th><spring:message code="label.create.occupation.selected.space.exam.capacity"/></th>
		<th><spring:message code="label.create.occupation.selected.space.operations"/>
	</thead>
	<tbody>
	</tbody>
</table>

<h3><spring:message code="title.create.occupation.reason"/></h3>

<spring:url var="createUrl" value="/spaces/occupations/create" />

<form class="form-horizontal" role="form" id="create-occupation-form" method="POST" action ="${createUrl}">
  ${csrf.field()}
  <div class="form-group">
    <label for="occupation-emails" class="col-sm-2 control-label">
    	<spring:message code="label.create.occupation.reason.emails"></spring:message>
    </label>
    <div class="col-sm-10">
      <input type="email" multiple="multiple" class="form-control" name="emails" id="occupation-emails">
    </div>
  </div>
  <div class="form-group">
    <label for="occupation-subject" class="col-sm-2 control-label">
    	<spring:message code="label.create.occupation.reason.subject"></spring:message>
    </label>
    <div class="col-sm-10">
      <input type="text" class="form-control" name="subject" id="occupation-subject" required>
    </div>
  </div>
  <div class="form-group">
    <label for="occupation-description" class="col-sm-2 control-label">
    	<spring:message code="label.create.occupation.reason.description"></spring:message>
    </label>
    <div class="col-sm-10">
      <textarea cols="50" rows="4" class="form-control" name="description" id="occupation-description" required></textarea>
    </div>
  </div>
  <div class="form-group">
    <div class="col-sm-10">
      <button type="submit" class="btn btn-success"><spring:message code="label.create.occupation.reason.submit"/></button>
    </div>
  </div>
  <input type="hidden" name="selectedSpaces" id="selected-spaces-input"/>
  <input type="hidden" name="config" id="config-input"/>
  <input type="hidden" name="events" id="events-input"/>
  <c:if test="${not empty request}">
		<input type="hidden" name="request" value="${request.externalId}"/>
	</c:if>
</form>