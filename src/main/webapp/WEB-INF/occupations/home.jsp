<!DOCTYPE html> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="page-header">
  <h1><spring:message code="title.space.management" text="Space Management"/></h1>
</div>

<spring:url var="createRequestUrl" value="/spaces/occupations/requests/create"/>
<spring:url var="listRequestsUrl" value="/spaces/occupations/requests"/>
<spring:url var="listRequestsUrl" value="/spaces/occupations/create"/>

<a href="${createRequestUrl}" class="btn btn-success"><spring:message code="link.occupations.create.request" text="Create Occupation Request"></spring:message></a>
<a href="${listRequestsUrl}" class="btn btn-success"><spring:message code="link.occupations.list.request" text="List Occupation Request"></spring:message></a>
<a href="${listRequestsUrl}" class="btn btn-success"><spring:message code="link.occupations.create" text="Criar Ocupação de Salas"></spring:message></a>