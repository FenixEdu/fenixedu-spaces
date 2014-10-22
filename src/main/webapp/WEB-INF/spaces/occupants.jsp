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
<script src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>

	<link rel="stylesheet" type="text/css" media="screen" href="/fenix/themes/ashes/css/general.css">
    <link href="/fenix/themes/ashes/css/style.css" rel="stylesheet">

<spring:url var="toolkit" value="/bennu-toolkit/js/toolkit.js"/>
<script type="text/javascript" src="${toolkit}">
</script>
<spring:url var="toolkitCSS" value="/bennu-toolkit/css/toolkit.css"/>
<link rel="stylesheet" type="text/css" media="screen" href="${toolkitCSS}">
	<spring:url var="staticUrl" value="/static/fenix-spaces"/>
<script src="${staticUrl}/js/sprintf.min.js"></script>
	<script src="${staticUrl}/js/dateutils.js"></script>
	<script src="${staticUrl}/js/moment.min.js"></script>
<spring:url var="formActionUrl" value="${action}"/>


<div class="page-header">
  <h1><spring:message code="title.space.management" text="Space Management"/><small><spring:message code="title.space.access.management" text="Gestão de Ocupantes"/></small></h1>
</div>

<script>
var timeMap =
	<c:if test="${userConfigPairs != null}">
	{<c:forEach var="pair" items="${userConfigPairs}">
		<c:set var="name" value="${pair.user.externalId}"/>
			"${ name}": [
			<c:forEach var="interval" items="${pair.config.intervals}">
				<c:set var="start" value="${interval.start}"/>
				<c:set var="end" value="${interval.end}"/>
				{start: Date.parse("${start}"), end: Date.parse("${end}")}
			</c:forEach>
			],
		</c:forEach>
		}
	</c:if>;
</script>
<c:if test="${userConfigPairs != null}">
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">Ocupantes do Espaço</h3>
	</div>
	<div class="panel-body">
	<div class="container-fluid">
	
	<c:forEach var="pair" items="${userConfigPairs}">
	<c:set var="name" value="${pair.user.name}"/>
		<div class="row">
		  <div class="col-md-2"><b>Name:</b></div> 
    	  <div class="col-md-8" id="theNameInput">${name}</div>
    	  <div class="col-md-2">
    	  <button class="btn btn-default" title="Edit"><span class="glyphicon glyphicon-pencil"></span></button>
		  <button data-classification-id="7421703487688" data-classification-name="Ensino Teórico" data-toggle="modal" data-target="#confirmDelete" class="btn btn-default" title="delete"><span class="glyphicon glyphicon-remove"></span></button>
		</div>
		</div>
	</c:forEach>
	
	</div>
	</div>
	</div>    	
</c:if>

<button type="submit" class="btn btn-success" id="save"><spring:message code="occupators.save" text="Adicionar Ocupante"/></button>

<style>
#myModal {
	text-align: center;
}
.bootstrap-datetimepicker-widget {
	z-index:20000 !important;
}
</style>

<div class="modal fade" id="otherModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  		<div class="modal-dialog">
			<div class="modal-content">
			<form:form modelAttribute="occupantsbean" role="form" method="post" action="${formActionUrl}"> 
				<div class="modal-header">
		        	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		        	<h4 class="modal-title" id="myModalLabel"><spring:message code="calendar.add.event" text="Periodos de Ocupação"/></h4>
		      	</div>
				<div class="modal-body">
					<div> ${name}</div>
		        	<div class="bennu-group-list" style="height:149px; overflow:scroll; overflow-x: hidden; border:1px solid #ddd; margin-top:20px;"><table class="table table-striped"></table></div>
				</div>
			    <div class="modal-footer">
			      <button type="button" id="discard" class="btn btn-danger"><spring:message code="occupators.close" text="Fechar"/></button>
			      <button type="submit" class="btn btn-primary" id="save"><spring:message code="occupators.save" text="Guardar alterações"/></button>
			    </div>
			    </form:form>
    		</div>
	  	</div>
	</div>

<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  		<div class="modal-dialog">
			<div class="modal-content">
			<form:form modelAttribute="occupantsbean" role="form" method="post" action="${formActionUrl}"> 
				<div class="modal-header">
		        	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		        	<h4 class="modal-title" id="myModalLabel"><spring:message code="calendar.add.event" text="Seleccionar Período"/></h4>
		      	</div>
				<div class="modal-body">
		        	<table class="table" id="create-event">
		        		<tr class="row">
							<th class="col-lg-3"><spring:message code="occupation.user" text="Selectionar Utilizador"/></th>
							<td>
								<form:input type="text" class="form-control" id="user" path="user" placeholder="user" required="required"/>
							</td>
						</tr>
						<tr class="row">
							<th class="col-lg-3"><spring:message code="calendar.start" text="Início"/></th>
							<td>
								<span style="display:block;">
									<form:input type="date" class="form-control" id="startDate" path="startDate" placeholder="startDate" required="required"/>
								</span>
							</td>
						</tr>
						<tr class="row">
							<th class="col-lg-3"><spring:message code="calendar.end" text="Fim"/></th>
							<td class="col-lg-9">
								
								<span style="display:block;">
									<form:input type="date" class="form-control" id="endDate" path="endDate" placeholder="endDate" required="required"/>
								</span>
								
							</td>
						</tr>
					</table>
				</div>
			    <div class="modal-footer">
			      <button type="button" id="discard" class="btn btn-danger"><spring:message code="occupators.close" text="Fechar"/></button>
			      <button type="submit" class="btn btn-primary" id="save"><spring:message code="occupators.save" text="Guardar alterações"/></button>
			    </div>
			    </form:form>
    		</div>
	  	</div>
	</div>
