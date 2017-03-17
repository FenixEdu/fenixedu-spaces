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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!-- <script -->
<!-- 	src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script> -->

<link rel="stylesheet" type="text/css" media="screen"
	href="/fenix/themes/ashes/css/general.css">
<link href="/fenix/themes/ashes/css/style.css" rel="stylesheet">
${portal.toolkit()}
<%-- <spring:url var="toolkit" value="/bennu-toolkit/js/toolkit.js" /> --%>
<%-- <script type="text/javascript" src="${toolkit}"></script> --%>
<%-- <spring:url var="toolkitCSS" value="/bennu-toolkit/css/toolkit.css" /> --%>
<!-- <link rel="stylesheet" type="text/css" media="screen" -->
<%-- 	href="${toolkitCSS}"> --%>
<spring:url var="staticUrl" value="/static/fenix-spaces" />
<script src="${staticUrl}/js/sprintf.min.js"></script>
<script src="${staticUrl}/js/dateutils.js"></script>
<script src="${staticUrl}/js/moment.min.js"></script>

<spring:url var="formActionUrl" value="${action}" />
<script>
$(document).ready(function() {
	$('#confirmDelete').on('show.bs.modal', function (e) {
	      var $spaceName = $(e.relatedTarget).attr('data-space-name');
	      var $userGivenName = $(e.relatedTarget).attr('data-user-name');
	      var $userName = $(e.relatedTarget).attr('data-user-username');
	      var $message = "Are you sure you want to remove " + $userGivenName + " from this space?";
	      $(this).find('.modal-body p').text($message);
	      var $title = "Remove User From Space";
	      $(this).find('.modal-title').text($title);
	      var $datestart = $(e.relatedTarget).attr('data-date-start');
	      var $dateend = $(e.relatedTarget).attr('data-date-end');
	      var $action = $(e.relatedTarget).attr('data-action');
	      $('#confirmDelete').find('#action').val($action);
	      $('#confirmDelete').find('#username').val($userName);
	      if($action == "edit" || $action == "remove"){
	    	  $('#confirmDelete').find('#newInterval').val('[]');
	    	  $('#confirmDelete').find('#oldInterval').val('[{start:'+moment($datestart).valueOf() +',end:'+moment($dateend).valueOf() +'}]');
	      }
	      $('#confirmDelete').find('#occupantsbean').on('submit', function(){
	    	  setNewInterval();
			  return;
		  });
	});
	
	var deleteUrl = "${deleteBaseUrl}";
	$('#otherModal').on('show.bs.modal', function (e) {
		      var $userGivenName = $(e.relatedTarget).attr('data-user-name');
		      var $userName = $(e.relatedTarget).attr('data-user-username');
		      var $datestart = $(e.relatedTarget).attr('data-date-start');
		      var $dateend = $(e.relatedTarget).attr('data-date-end');
		      var $action = $(e.relatedTarget).attr('data-action');
		      if($action == "edit" || $action == "remove"){
		    	  $('#otherModal').find('#oldInterval').val('[{start:'+moment($datestart).valueOf() +',end:'+moment($dateend).valueOf() +'}]');
		      }
		      $(this).find('.modal-title').text($userGivenName);
			  
		      $('#otherModal').find('#action').val($action);
		      $('#otherModal').find('#startTimeInput').val($datestart);
		      if(moment($dateend).isBefore(moment($datestart).add(150,'y'))){
		      	$('#otherModal').find('#endTimeInput').val($dateend);
		      }
		      $('#otherModal').find('#username').val($userName);
		      $('#otherModal').find('#showUsername').attr('hidden','hidden');
		      if($userName==""){
		    	  $('#otherModal').find('#showUsername').removeAttr('hidden');
		      }

		      $('#otherModal').find('.modal-footer #discard').on('click', function(){
		    	  $('#otherModal').modal('hide');
			  });
		      $('#otherModal').find('#occupantsbean').on('submit', function(){
		    	  setNewInterval();
				  return;
			  });
		  });
	var alertmessage = {};
	<c:if test="${not empty message}">
		alertmessage = ${message};
	</c:if>
	setAlertModal(alertmessage);
	
	});

function intervalInterceptsPerson(interval, person){
	return person.map(function(e){return intercepts(e,interval);}).reduce(function(a,b){return a || b;},false);
}

function setAlertModal(container){
	var kind = "";
	var message = "";
	if(container["warning"]!= undefined){
		kind = "warning";
		message = container["warning"];
	}
	if(container["error"]!= undefined){
		kind = "error";
		message = container["error"];
	}
	if(kind == "") return;
	$('#alertModal').find('#alertMessage').text(message);
	$('#alertModal').find('#alertDiv').attr("role","alert");
	$('#alertModal').find('#alertDiv').removeClass();
	if(kind.toLowerCase() == "warning"){
		
		$('#alertModal').find('#alertTitle').text("Warning");
		$('#alertModal').find('#alertDiv').addClass("alert alert-warning");
	}if(kind.toLowerCase() == "error"){
		$('#alertModal').find('#alertTitle').text("Error");
		$('#alertModal').find('#alertDiv').addClass("alert alert-danger");
	}
	$("#alertModal").modal('show');
}

function setNewInterval(){
	  var endTime = $("#endTimeInput").val();
	  var startTime = moment($("#startTimeInput").val());
	  if(endTime == ""){
		  endTime = moment(startTime);
		  endTime.add(200,'y');
	  }else{
		  endTime = moment(endTime);
	  }
	  $('#otherModal').find('#newInterval').val('[{start:'+startTime.valueOf() +',end:'+ endTime.valueOf() +'}]');
}

function activeInterval(interval){
	var now = moment();
	now = moment(now.format("YYYY-MM-DD"));
	if(now.isSame(interval.start)) return true;
	if(now.isAfter(interval.start) && now.isBefore(interval.end)) return true;
	return false;
}


	
function PersonInterceptsPerson(A,B){
	return A.map(function(e){return intervalInterceptsPerson(e,B);}).reduce(function(a,b){return a || b;},false);
}

function intervalInterceptsPersons(interval, persons, intervalPerson){
	var counter = 0;
	for (var p in persons){
		if(p == intervalPerson){
			continue;
		}
		if(intervalInterceptsPerson(interval,persons[p])) {
			counter++;
		}
	}
	return counter;
}

function intercepts(intA, intB){
	if(intA.start >= intB.start && intA.start <= intB.end)
		return true;
	if(intB.start >= intA.start && intB.start <= intA.end)
		return true
	return false;
}

function getSelectedPeriod(){
	return getSelectedPeriod(false);
}

function sameInterval(intA, intB){
	var UnixIntA = getUnixInterval(intA);
	var UnixIntB = getUnixInterval(intB);
	return (UnixIntA.start == UnixIntB.start) && (UnixIntA.end == UnixIntB.end);
	
}

function getUnixInterval(interval){
	var unixInt= {};
	if(typeof (interval.start) == "number"){
		unixInt = interval;
	}else{
		unixInt.start = interval.start.valueOf();
		unixInt.end = interval.end.valueOf();
	}
	return unixInt;
}

function getSelectedPeriod(unixFormat){

	var aux={};
	aux.start=new moment($("#startTimeInput").val());
	aux.end=new moment($("#endTimeInput").val());
	if(!aux.start.isValid() || !aux.end.isValid())
		return;
	if(aux.start.isAfter(aux.end)){
		var swapper = aux.start;
		aux.start=aux.end;
		aux.end=swapper;
	}
	if(unixFormat){
		aux.start=aux.start.valueOf();
		aux.end=aux.end.valueOf();
	}
	return periods;
}
</script>


<div class="page-header">
	<h1>
		<spring:message code="title.space.management" />
		<small><spring:message code="title.space.occupants.management" /></small>
	</h1>
</div>



<c:if test="${not empty activeOccupations}">
	<div class="panel panel-primary" id="currentOccupationsHide">
		<div class="panel-heading">
			<h3 class="panel-title">Actuais Ocupantes do Espaço</h3>
		</div>
		<div class="panel-body">
			<div class="container-fluid" id="currentOccupations">
				<div class="row">
					<div class="col-md-5">
						<b>Nome</b>
					</div>
					<div class="col-md-6">
						<b>Periodo de Ocupação</b>
					</div>
					<div class="col-md-1">
						<b>Operações</b>
					</div>
				</div>
				<c:forEach var="occupation" items="${activeOccupations}">
					<c:forEach var="interval" items="${occupation.activeIntervals}">
						<div class="row">
							<div class="col-md-5" id="theNameInput"><c:out value="${occupation.user.profile.displayName}"/></div>
							<div class="col-md-6">
								<c:set var="endDate" value='${interval.end.toString("dd/MM/yyyy")}' />
								<c:set var="startPlus150Years" value="${interval.start.plusYears(150)}" />
								
								<c:if test="${occupantsbean.isAfter(interval.end,startPlus150Years)}">
									<c:set var="endDate" value="" />
								</c:if>
								
								<c:out value="${interval.start.toString('dd/MM/yyyy')} --- ${endDate}"/>
							</div>
							<div class="col-md-1">
								<button data-backdrop="static" data-user-id="${occupation.user.externalId}"
									data-user-username="<c:out value='${occupation.user.username}'/>"
									data-user-name="<c:out value='${occupation.user.profile.displayName}'/>" data-toggle="modal"
									data-date-start="${interval.start.toString("yyyy-MM-dd")}" data-date-end="${interval.end.toString("yyyy-MM-dd")}"
									data-target="#otherModal" class="btn btn-default" title="edit"
									data-action="edit">
									<span class="glyphicon glyphicon-edit"></span>
								</button>
								<button data-backdrop="static" data-user-id="${occupation.user.externalId}"
									data-user-username="<c:out value='${occupation.user.username}'/>"
									data-user-name="<c:out value='${occupation.user.profile.displayName}'/>" data-toggle="modal"
									data-date-start="${interval.start.toString("yyyy-MM-dd")}" data-date-end="${interval.end.toString("yyyy-MM-dd")}"
									data-toggle="modal" data-target="#confirmDelete"
									class="btn btn-default" title="delete" data-action="remove">
									<span class="glyphicon glyphicon-remove"></span>
								</button>
							</div>
						</div>
					</c:forEach>
				</c:forEach>
			</div>
		</div>
	</div>
</c:if>

<c:if test="${not empty inactiveOccupations}">
	<div class="panel panel-primary" id="otherOccupationsHide">
		<div class="panel-heading">
			<h3 class="panel-title">Ocupações Inactivas</h3>
		</div>
		<div class="panel-body">
			<div class="container-fluid" id="otherOccupations">
				<div class="row">
					<div class="col-md-5">
						<b>Nome</b>
					</div>
					<div class="col-md-6">
						<b>Periodo de Ocupação</b>
					</div>
					<div class="col-md-1">
						<b>Operações</b>
					</div>
				</div>
				<c:forEach var="occupation" items="${inactiveOccupations}">
					<c:forEach var="interval" items="${occupation.inactiveIntervals}">
						<div class="row">
							<div class="col-md-5" id="theNameInput"><c:out value="${occupation.user.profile.displayName}"/></div>
							<div class="col-md-6">
								<c:set var="endDate" value='${interval.end.toString("dd/MM/yyyy")}' />
								<c:set var="startPlus150Years" value="${interval.start.plusYears(150)}" />
								<c:if test="${occupantsbean.isAfter(interval.end,startPlus150Years)}">
									<c:set var="endDate" value="" />
								</c:if>
								<c:out value="${interval.start.toString('dd/MM/yyyy')} --- ${endDate}"/>
							</div>
							<div class="col-md-1">
								<button data-backdrop="static" data-user-id="${occupation.user.externalId}"
									data-user-username="<c:out value='${occupation.user.username}'/>"
									data-user-name="<c:out value='${occupation.user.profile.displayName}'/>" data-toggle="modal"
									data-date-start="${interval.start.toString("yyyy-MM-dd")}" data-date-end="${interval.end.toString("yyyy-MM-dd")}"
									data-target="#otherModal" class="btn btn-default" title="edit"
									data-action="edit">
									<span class="glyphicon glyphicon-edit"></span>
								</button>
								<button data-backdrop="static" data-user-id="${occupation.user.externalId}"
									data-user-username="<c:out value='${occupation.user.username}'/>"
									data-user-name="<c:out value='${occupation.user.profile.displayName}'/>" data-toggle="modal"
									data-date-start="${interval.start.toString("yyyy-MM-dd")}" data-date-end="${interval.end.toString("yyyy-MM-dd")}"
									data-toggle="modal" data-target="#confirmDelete"
									class="btn btn-default" title="delete" data-action="remove">
									<span class="glyphicon glyphicon-remove"></span>
								</button>
							</div>
						</div>
					</c:forEach>
				</c:forEach>
			</div>
		</div>
	</div>
 </c:if>

<button data-backdrop="static" data-user-id="empty"
	data-user-username="" data-user-name="Novo Utilizador" data-toggle="modal"
	data-target="#otherModal" title="edit" type="button" data-action="add"
	class="btn btn-success" id="confirm">
	<spring:message code="label.addUser" />
</button>

<style>
#myModal {
	text-align: center;
}

.bootstrap-datetimepicker-widget {
	z-index: 20000 !important;
}
</style>

<div class="modal fade" id="confirmDelete" role="dialog"
	aria-labelledby="confirmDeleteLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<form:form modelAttribute="occupantsbean" role="form" method="post"
				action="${formActionUrl}">
			${csrf.field()}
			<div class="modal-header">

				<input type="hidden" id="oldInterval" name="oldInterval" value />
				<input type="hidden" id="newInterval" name="newInterval" value />
				<input type="hidden" id="action" name="action" value />
				<input type="hidden" id="username" name="user" value />
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">&times;</button>
				<h4 class="modal-title">
					<spring:message code="label.spaces.delete.title" />
				</h4>
			</div>
			<div class="modal-body">
				<p>
					<spring:message code="label.spaces.delete.message" />
				</p>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.cancel" />
				</button>
				<button type="submit" class="btn btn-danger" id="confirm">
					<spring:message code="label.delete" />
				</button>
			</div>
			</form:form>
		</div>
	</div>
</div>

<div class="modal fade" id="alertModal" role="dialog"
	aria-labelledby="alertModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header" >
				<h4 id="alertTitle" />
			</div>
			<div class="modal-body">
			 <div id="alertDiv">
				<p id="alertMessage">
					LOL
				</p>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.ok" />
				</button>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="otherModal" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<form:form modelAttribute="occupantsbean" role="form" method="post"
				action="${formActionUrl}">
				${csrf.field()}
				<input type="hidden" id="oldInterval" name="oldInterval" value />
				<input type="hidden" id="newInterval" name="newInterval" value />
				<input type="hidden" id="action" name="action" value />
				<!-- WARNINGGGG! -->
				<div class="modal-header">
					
					<!-- WARNINGGGG! -->
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel">
						<spring:message code="occupation.periods" />
					</h4>
				</div>
				<div class="modal-body">
				
				<div class="row" id="showUsername">
						<div class="col-md-3">Utilizador:</div>
						<div class="col-md-8">
							<input bennu-user-autocomplete id="username" class="form-control" name="user" value />
						</div>
					</div>
					<div class="row">
						<div class="col-md-3">Data de Inicio:</div>
						<div class="col-md-8">
							<input required type="date" id="startTimeInput" class="form-control" placeholder="inicio">
						</div>
					</div>
					<div class="row">
						<div class="col-md-3">Data de Fim:</div>
						<div class="col-md-8">
							<input type="date" id="endTimeInput" class="form-control" placeholder="fim">
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<div class="col-xs-1"></div>
					<div class="col-xs-7"></div>
					<div class="col-xs-4">
						<button type="submit" class="btn btn-primary" id="save">Guardar</button>
						<button type="button" id="discard" class="btn btn-danger">Fechar</button>
					</div>
				</div>
			</form:form>
		</div>
	</div>
</div>
