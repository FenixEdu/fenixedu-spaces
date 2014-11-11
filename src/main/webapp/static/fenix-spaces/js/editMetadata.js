var app = angular.module('metadataEdit', ['ui.bootstrap', 'bennuToolkit', 'dialogs.main', 'pascalprecht.translate']);

app.controller('MetadataController', [ '$scope','$timeout', '$modal', '$log','$translate','dialogs','$http', '$window',  function($scope, $timeout, $modal, $log, $translate, dialogs, $http, $window) {
	$scope.options = [ {
		value : "java.lang.String",
		text : "Text"
	}, {
		value : "java.util.Date",
		text : "Date"
	}, {
		value : "java.lang.Boolean",
		text : "Boolean"
	}, {
		value : "java.lang.Integer",
		text : "Number"
	} ];
	
	$scope.parent = window.parent;
	
	$scope.code = window.code;
	
	$scope.fieldDefs = window.specs;
	
	$scope.informationName = window.informationName;

	$scope.newKey = "";
	$scope.setFieldDefs = function(e) {
		$scope.fieldDefs = e;
	};
	
	$scope.removeField = function(field) {
		var modalInstance = $modal.open({
		      templateUrl: 'deleteModal.html',
		      controller: 'ConfirmDeleteInstanceCtrl',
		      resolve: {
		        field: function () {
		          return field;
		        }
		      }
	    });

	    modalInstance.result.then(function (selectedItem) {
	      $scope.fieldDefs = $scope.fieldDefs.filter(function(e) {
	    	 return e != selectedItem;
	      });
	    }, function () {
	      $log.info('Modal dismissed at: ' + new Date());
	    });
	};

	$scope.addField = function() {
		var trimmedKey = $scope.newKey.trim();
		if (trimmedKey == "")
			return;
		for (var i = 0; i < $scope.fieldDefs.length; i++) {
			if ($scope.fieldDefs[i].name === trimmedKey) {
				dialogs.error($translate.instant("DIALOGS_ERROR"), $translate.instant("ERROR_SAME_KEY", {keyName : $scope.newKey}));
				return;
			}
		}
		var topush = {
			name : trimmedKey,
			description : {},
			required : false,
			defaultValue : "",
			type : "java.lang.String"
		};
		$scope.newKey = ""
		$scope.fieldDefs.push(topush);
	};
	
	$scope.submitInfo = function(){
		var val = $("[ng-localized-string]").map(function (i, xx) {
	           xx = $(xx);
	           xx.data("input").removeClass("has-error");
	           return Bennu.validation.validateInput(xx);
	       });
		for(var i = 0; i<val.length;i++){
			if(val[i]==false){
				return false;
			}
		}
		$http({method : 'POST', url : location.href, data : 
					{name : $scope.informationName,
			     metadata : $scope.fieldDefs,
			     parent : $scope.parent,
			     code : $scope.code},
			     headers: {'Content-Type': 'application/json'}}
		).
		  success(function(data, status, headers, config) {
			  if(data["error"]){
				  dialogs.error($translate.instant("DIALOGS_ERROR"),data["error"]);
				  return;
			  }
			  if(data["warning"]){
				  dialogs.error($translate.instant("DIALOGS_WARNING"),data["warning"],{size:'md',keyboard: true,backdrop: false,windowClass: 'warning-class'});
				  return;
			  }
			  
			  dialogs.notify($translate.instant("DIALOGS_NOTIFICATION"),$translate.instant("DIALOGS_CHANGES_SAVED")).result.then(function(){
				  $window.location.href=$window.contextPath + "/classification";
			  });
		}).
		  error(function(data, status, headers, config) {
			  debugger;
		    // called asynchronously if an error occurs
		    // or server returns response with an error status.
		  });
		
	};
	
} ])

.config(['dialogsProvider','$translateProvider',function(dialogsProvider,$translateProvider){
	dialogsProvider.useBackdrop('static');
	dialogsProvider.useEscClose(false);
	dialogsProvider.useCopy(false);
	dialogsProvider.setSize('md');
	
	$translateProvider.useStaticFilesLoader({
        prefix: window.contextPath + "/static/fenix-spaces/i18n/",
        suffix: '.json'
    });
	
	$translateProvider.preferredLanguage(Bennu.locale.lang);
    }]);

app.controller('ConfirmDeleteInstanceCtrl', function ($scope, $modalInstance, field) {
	
	$scope.field = field;
	
	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
	
	$scope.confirm = function () {
	    $modalInstance.close($scope.field);
	}
	
});


