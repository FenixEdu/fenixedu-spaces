<!DOCTYPE html> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="page-header">
  <h1><spring:message code="title.space.management" text="Space Management"/><small><spring:message code="title.spaces" text="Spaces"/></small></h1>
</div>
<%@include file="spaces/fragments/spaces.jsp" %>

<spring:url var="createUrl" value="/spaces/create"/>
<a href="${createUrl}" class="btn btn-success"><spring:message code="link.space.create" text="Create Space"></spring:message></a>