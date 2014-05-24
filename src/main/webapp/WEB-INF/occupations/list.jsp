<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="page-header">
	<h1>
		<spring:message code="title.space.management" text="Space Management" />
		<small><spring:message code="title.list.occupation" text="Listagem de ocupações" /></small>
	</h1>
</div>


<form role="form" action="" method="GET">
  <div class="form-group">
    <label for="year"><spring:message code="label.year" text="Ano"/></label>
    <select name="year">
    	<c:forEach var="year" items="${years}">
    		<c:choose>
    			<c:when test="${ year == currentYear}">
    				<option value="${year}" selected>${year}</option>
    			</c:when>
    			<c:otherwise>
    				<option value="${year}">${year}</option>
    			</c:otherwise>
    		</c:choose>
    	</c:forEach>
    </select>
  </div>
  <div class="form-group">
    <label for="month"><spring:message code="label.month" text="Mês"/></label>
     <select name="month">
    	<c:forEach var="month" items="${months}">
    		<c:set var="monthValue" value="${month.toString('M')}"/>
    		<c:choose>
    			<c:when test="${monthValue == currentMonth}">
    				<option value="${monthValue}" selected>${month.toString("MMM")}</option>
    			</c:when>
    			<c:otherwise>
    				<option value="${monthValue}">${month.toString("MMM")}</option>
    			</c:otherwise>
			</c:choose>
    	</c:forEach>
    </select>
  </div>
  <button type="submit" class="btn btn-default"><spring:message code="label.select" text="Seleccionar"/></button>
</form>

<h2><spring:message code="title.list.occupation.details" text="Detalhes das ocupações" /></h2>


<spring:url var="viewUrl" value="/spaces/occupations/view"/>

<c:if test="${not empty occupations}">
	<table class="table">
		<thead>
			<th><spring:message code="label.occupation.subject" text="Assunto" /></th>
			<th><spring:message code="label.occupation.intervals" text="Intervalo" /></th>
			<th><spring:message code="label.occupation.rooms" text="Salas" /></th>
		</thead>
		<tbody>
			<c:forEach var="occupation" items="${occupations}">
				<c:set var="id" value="${occupation.externalId}" />
				<c:set var="subject" value="${occupation.subject}" />
				<c:set var="summary" value="${occupation.summary}" />
				<c:set var="extendedSummary" value="${occupation.extendedSummary}" />
				<c:set var="rooms" value="${occupation.spaces}" />
				<tr>
					<td class="col-md-5"><a href="${viewUrl}/${id}">${subject}</a></td>
					<td class="col-md-3"><p title="${extendedSummary}">${summary}</p></td>
					<td class="col-md-4">
						<c:forEach var="room" items="${rooms}">
							${room.name}
						</c:forEach>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:if>
<c:if test="${empty occupations}">
	<h4><spring:message code="label.occupations.empty" text="Não existem ocupações para o período seleccionado."/></h4>
</c:if>