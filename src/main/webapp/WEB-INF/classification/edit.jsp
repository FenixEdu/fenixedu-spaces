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

<spring:url var="baseUrl" value="/static/fenix-spaces"/>

<script type="text/javascript" src="${baseUrl}/js/sprintf.min.js">
</script>


<spring:url var="toolkit" value="/bennu-toolkit/js/toolkit.js"/>
<script type="text/javascript" src="${toolkit}">
</script>
<spring:url var="toolkitCSS" value="/bennu-toolkit/css/toolkit.css"/>
<link rel="stylesheet" type="text/css" media="screen" href="${toolkitCSS}">

<script type="text/javascript">
function inputTypes(obj){
	var theTypes = {
		"Text":"java.lang.String",
		"Date":"java.util.Date",
		"Boolean":"java.lang.Boolean",
		"Number":"java.lang.Integer",
	};
	var thetd = document.createElement("td");
	var theSelect = document.createElement("select");
	theSelect.setAttribute("AttrKey","type");
	theSelect.setAttribute("class","form-control");
	$.each(theTypes,function(key,value){
		var theOption = document.createElement("option");
		theOption.value =value;
		theOption.text = key;
		if(obj.type != null && value == obj.type){
			theOption.setAttribute("selected","selected");
		}
		theSelect.appendChild(theOption);
	});
	thetd.appendChild(theSelect);
	return thetd;
}

function inputReqs(obj){
	var thetd = document.createElement("td");
	var theInput = document.createElement("input");
	theInput.setAttribute("type","checkbox");
	//theInput.setAttribute("class","form-control");
	theInput.setAttribute("AttrKey","required");
	if(obj.required != null && obj.required==true){
		theInput.setAttribute("checked","");
	}
	thetd.appendChild(theInput);
	return thetd;
}

function removeRow(button){
	var row = $(button).closest("div.row");
	var keyName = row.find("[AttrKey=name]").val();
	keySet[keyName]=false;
	row.remove();
}

function removeButtons(obj){
	var thetd = document.createElement("td");
	var theButton = document.createElement("button");
	theButton.setAttribute("type","submit");
	theButton.setAttribute("class","btn btn-default");
	theButton.setAttribute("theLabel",obj.name);
	theButton.setAttribute("data-toggle","modal");
	theButton.innerText="<spring:message code='label.addfield' text='Remove'/>";
	theButton.setAttribute("data-target","#confirmDelete")
	thetd.appendChild(theButton);
	return thetd;
}

function noInputKeys(obj){
	var thetd = document.createElement("td");
	var theInput = document.createElement("input");
	theInput.setAttribute("type","text");
	theInput.setAttribute("readonly","");
	theInput.setAttribute("AttrKey","name");
	//form/theInput.setAttribute("class","form-control");
	theInput.setAttribute("size",obj.name.length);
	theInput.setAttribute("style","border: 0px solid #000000;")
	theInput.setAttribute("value",obj.name);
	thetd.appendChild(theInput);
	return thetd;
}

function inputDefaultVal(obj){
	var thetd = document.createElement("td");
	var theInput = document.createElement("input");
	theInput.setAttribute("type","text");
	theInput.setAttribute("AttrKey","defaultValue");
	theInput.setAttribute("class","form-control");
	if(obj.defaultValue != null ){
		theInput.setAttribute("value",obj.defaultValue);
	}else{
		theInput.setAttribute("value","");
	}

	thetd.appendChild(theInput);
	return thetd;
}

function showSelected(selectId,spanId){
	var key = $(selectId).val();
	var inputs = $(spanId).find("input")
	$(inputs).each(function(){
		var theValue = this.getAttribute("attrkey");
		if(theValue.indexOf(key)>=0){
			this.setAttribute("type","text");
		}else{
			this.setAttribute("type","hidden");
		}
	});
}

function inputDescrs(obj){
	var thetd = document.createElement("td");
	var theInput = document.createElement("input");
	var id = new Date().valueOf();
	theInput.setAttribute("id",id);
	
	theInput.setAttribute("bennu-localized-string","");
	var jsonStr = JSON.stringify(obj.description,undefined,0);
	theInput.setAttribute("required-any","");
	theInput.setAttribute("class","form-control");
	theInput.setAttribute("value",jsonStr);
	theInput.setAttribute("type","text");
	thetd.appendChild(theInput);
	return thetd;
	
// 	var theInputPT = document.createElement("input");
// 	var theInputEN = document.createElement("input");
// 	theInputPT.setAttribute("AttrKey","description[pt-PT]");
// 	theInputEN.setAttribute("AttrKey","description[en-GB]");
// 	if(obj.description != null){
// 		theInputPT.setAttribute("value",obj.description["pt-PT"]);
// 		theInputEN.setAttribute("value",obj.description["en-GB"]);
// 	}else{
// 		theInputPT.setAttribute("value","");
// 		theInputEN.setAttribute("value","");
// 	}
	
// 	theInputEN.type = "text";
// 	thespanPT.appendChild(theTextPT);
// 	thespanPT.appendChild(theInputPT);
// 	thespanEN.appendChild(theTextEN);
// 	thespanEN.appendChild(theInputEN);
// 	thetd.appendChild(thespanPT);
// 	thetd.appendChild(thespanEN);
	
}

function withColSpan(colSpan, content){
	var thediv = document.createElement("div");
	thediv.setAttribute("class","col-md-"+colSpan);
	thediv.appendChild(content);
	return thediv;
}

function addRow(spec){
	var noInputKey = withColSpan(3,noInputKeys(spec));
	var inputDescr = withColSpan(4,inputDescrs(spec));
	
	var inputType = withColSpan(1,inputTypes(spec));
	var inputReq = withColSpan(1,inputReqs(spec));
	
	var inputDefault = withColSpan(2,inputDefaultVal(spec));
	var removeButton = withColSpan(1,removeButtons(spec));
	var input = noInputKey.outerHTML + inputDescr.outerHTML + inputType.outerHTML + inputReq.outerHTML + inputDefault.outerHTML + removeButton.outerHTML;
	var row = document.createElement("div");
	row.setAttribute("class","row"); 
	$(input).each(function (){
		row.appendChild(this);
	});
	$("#fieldtable").append(row);
	return;
}

function addField(){
	var theKey = $("#newKey").val();
	if(theKey.length == 0) return;
	if(keySet[theKey]==true) {
		setAlertModal("warning","the Key '"+theKey+"' Already exists!");
		return;
	}
	keySet[theKey]=true;
	var spec = {name:theKey,description:{}};
	addRow(spec);
	
}

function loadClassification(info){
	if(info == null) return;
	var spec = info;
	$(spec).each(function() {
		 addRow(this);
	});
	
}

function getInputValue(input){
	if(input.type == "checkbox")
		return $(input).is(":checked");
	if(input.type == "text" || input.type == "hidden")
		return $(input).val();
}


function jsonFromRow(row){
	var tdtd = $(row).find("div");
	var toJSON = {};
	toJSON.description = {};
	if(tdtd.length == 0) return null; // header row!
	var inputs = $(tdtd).find("input");
	var selects = $(tdtd).find("select");
	var localizedInputs = $(tdtd).find("div.bennu-localized-string-input-group");
	if(inputs.length + selects.length + localizedInputs.length == 0) return null;
	if(inputs.length > 0){
		$(inputs).each(function(){
			var theKey = this.getAttribute("attrkey");
			if(theKey == null) return;
			var theValue = getInputValue(this);
			if(theKey.indexOf("description")>=0){
				var theDescriptKey = theKey.split(/[\[\]]/)[1];
				toJSON.description[theDescriptKey]=theValue;
				if(1==0 && theDescriptKey == "en-GB"){
					var theNameToks = theValue.split(" ");
					theNameToks[0]=theNameToks[0].toLowerCase();
					var theName = "";
					$(theNameToks).each(function(){
						theName+=this;
					})
					toJSON["name"]=theName;
				}
			}else{
				toJSON[theKey]=theValue;
			}
				
		})
	}if(selects.length > 0){
		$(selects).each(function(){
			var theKey = this.getAttribute("attrkey");
			if(theKey != null) {
				var theValue = $(this).val();
				toJSON[theKey]=theValue;
			}
		})
	}if(localizedInputs.length > 0){
		$(localizedInputs).each(function(){
			toJSON.description=jQuery.parseJSON($($(this).data("related")).val());	
		});
	}
	return toJSON;
}

var keySet = {};

$(document).ready(function() {
	var specs = [];
	<c:forEach var="classItem" items="${classifications}">
		<c:set var="spec" value="${information.metadata}"/>
		<c:set var="id" value="${classItem.externalId}"/>
		<c:if test="${information.metadata == null}">
			<c:set var="spec" value="[]"/>	
		</c:if>
		<c:out value="specs['${id}'] = ${spec};" escapeXml="false"/>
	</c:forEach>
	
	loadClassification(${information.metadata});
	setupKeyNames(${information.metadata});
	$('#confirmDelete').on('show.bs.modal', function (e) {
	    var $KeyName = $(e.relatedTarget).attr('theLabel');
	    var $message = "Are you sure you want to delete '" + $KeyName + "' ?";
	    $(this).find('.modal-body p').text($message);
	    var $title = "Delete '" + $KeyName + "'";
	    $(this).find('.modal-title').text($title);
	    
	    $('#confirmDelete').find('.modal-footer #confirm').on('click', function(){
	    	var button = $(e.relatedTarget)[0];
			removeRow(button);
			$('#confirmDelete').modal('hide');
	    });
	});
		var alertmessage = {};
		<c:if test="${not empty message}">
			alertmessage = ${message};
		</c:if>
		setAlertModal(alertmessage);
});     
	
function setupKeyNames(keys){
	for(var i = 0; i < keys.length; i++){
		keySet[keys[i].name]=true;
	}
}

function prepareSubmit(){
	var val = $("[bennu-localized-string],[bennu-html-editor]").map(function (i, xx) {
           xx = $(xx);
           xx.data("input").removeClass("has-error");
           return Bennu.validation.validateInput(xx);
       });
	for(var i = 0;i< val.length; i++) if(val[i]==false) return false;
	var circ = $("#fieldtable div.row").map(function (){ return jsonFromRow(this) });
	var JSONString = "[";
	$(circ).each(function (){ if(this==null) return; JSONString += JSON.stringify(this,undefined,2)+",";})
	
	JSONString = JSONString.substring(0,JSONString.length-1);
	JSONString += "]";
	
	$("#metadataInput")[0].value = JSONString;
	var localizedInput = $("#theNameInput").find("div.bennu-localized-string-input-group");
	$("#nameInput")[0].value = $($(localizedInput).data("related")).val();
	
	$("#parentInput")[0].value = $("#classificationParentInput").val();
	$("#codeInput")[0].value = $("#classificationCodeInput").val();
	return true;
}

function intervalInterceptsPerson(interval, person){
	return person.map(function(e){return intercepts(e,interval);}).reduce(function(a,b){return a || b;},false);
}

function setAlertModal(container, msg){
	var kind = "";
	var message = "";
	if(msg == undefined){
		if(container["warning"]!= undefined){
			kind = "warning";
			message = container["warning"];
		}
		if(container["error"]!= undefined){
			kind = "error";
			message = container["error"];
		}
	}else{
		kind = container;
		message = msg;
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
	
</script>

<!-- Modal Dialog -->
<div class="modal fade" id="confirmDelete" role="dialog" aria-labelledby="confirmDeleteLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title">Delete Permanently</h4>
      </div>
      <div class="modal-body bg-danger">
        <p style="text-align:center">Are you sure you want to delete <b> this </b>?</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
        <button type="button" class="btn btn-danger" id="confirm">Delete</button>
      </div>
    </div>
  </div>
</div>

<div class="page-header">
  <h1><fmt:message key="title.space.management"/><small><spring:message code="title.edit.classification" text="Criar/Editar Classificação"/></small></h1>
</div>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">Space Classification Details</h3>
	</div>
	<div class="panel-body" >
	<div class="container-fluid">
		<div class="row">
		  <div class="col-md-2"><b>Name:</b></div> 
    	  <div class="col-md-10" id="theNameInput"><input bennu-localized-string type="text" required-any  value='${information.name}'/></div> 	
		</div>
		<div class="row show-grid">
		  <div class="col-md-2"><b>Parent Classification:</b></div> 
    	  <div class="col-md-10"> 
    	<c:set var="ParentClassId" value="${information.parent}"/>	
    	<select class="form-control" id="classificationParentInput">
    	<option value="${null}">No Parent</option>
    	<c:forEach var="classItem" items="${classifications}">
    	<c:set var="classificationName" value="${classItem.absoluteCode} - ${classItem.name.content}"/>
    	<c:set var="classificationId" value="${classItem.externalId}"/>
    		<c:choose>
    			<c:when test="${classificationId == ParentClassId}">
    				<option value="${classificationId}" selected="selected">${classificationName}</option>
    			</c:when>
				<c:otherwise>
					<option value="${classificationId}">${classificationName}</option>
				</c:otherwise>
			</c:choose>
    	</c:forEach>
    </select>
    <p class="help-block"> </p>
    </div> 
  </div>
		<div class="row show-grid">
		  <div class="col-md-2"><b>Code:</b></div> 
    	  <div class="col-md-10"><input class="form-control" type="text" required="required" id="classificationCodeInput" value='${information.code}'/></div> 	
    	  <p/>
		</div>
	</div>
	</div>
</div>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">Space Classification Properties</h3>
	</div>
	
	<div class="panel-body" >
	<div class="container-fluid" id=fieldtable>
		<div class="row">
		  <div class="col-md-3"><b>Name</b></div>
		  <div class="col-md-4"><b>Description</b></div>
		  <div class="col-md-1"><b>Type</b></div>
		  <div class="col-md-1"><b>Required</b></div>
		  <div class="col-md-2"><b>Default Value</b></div>
		  <div class="col-md-1"></div>
		</div>
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
				</p>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.ok" text="Ok" />
				</button>
			</div>
		</div>
	</div>
</div>

<form action="javascript:void(0);">
<input type="text" id="newKey" placeholder="New Key" required="required"/>
<button type="submit" class="btn btn-default" onclick="addField();"><spring:message code="label.addfield" text="Add Field"/></button>
</form>

<spring:url var="formActionUrl" value="${action}"/>
<form:form modelAttribute="information" role="form" method="post" onsubmit="return prepareSubmit()" action="${formActionUrl}" enctype="multipart/form-data">
  <div class="form-group">
    <form:input type="hidden" class="form-control" id="nameInput" path="name" placeholder="name"/>
  </div>
  <div class="form-group">
    <form:input type="hidden" class="form-control" id="metadataInput" path="metadata" placeholder="metadata"/>
  </div>
    <div class="form-group">
    <form:input type="hidden" class="form-control" id="parentInput" path="parent" placeholder="parent"/>
  </div>
  <div class="form-group">
    <form:input type="hidden" class="form-control" id="codeInput" path="code" placeholder="code"/>
  </div>
  <button type="submit" class="btn btn-default" ><spring:message code="label.submit"  text="Submit"/></button>
</form:form>
