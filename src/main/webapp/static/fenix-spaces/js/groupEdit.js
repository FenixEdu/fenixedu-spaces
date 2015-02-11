var app = angular.module('groupEdit', ['ui.bootstrap', 'bennuToolkit']);

app.controller('editGroupController', [ '$scope','$modal',  function($scope, $modal) {
    
    
    $scope.setFieldDefs = function(e) {
    	$scope.fieldDefs = e;
    };
    
    $scope.removeField = function(field) {
    	$scope.groups.remove(field);
    };

    $scope.addField = function() {
    	$scope.groups.push($scope.newField);
    };

    $scope.items = ['item1', 'item2', 'item3'];

    $scope.open = function (inputId) {

	    var modalInstance = $modal.open({
	      templateUrl: 'myModalContent.html',
	      controller: 'ModalInstanceCtrl',
	      
	      resolve: {
	        items: function () {
	          return $scope.items;
	        },
	        target : function() {
	        	return inputId;
	        },
	    	
	      }
	    });
	
	    modalInstance.result.then(function (selectedItem) {
	      $scope.selected = selectedItem;
	    }, function () {
	     
	    });
	  };
	}]);

 

    	app.controller('ModalInstanceCtrl', function ($scope, $modalInstance, items, target) {
    	  var expression = angular.element(target).val();
    	  $scope.groups = getUserGroupsFromExpression(expression);
    	  $scope.items = items;
    	  $scope.target = target;
    	  $scope.selected = {
    	    item: $scope.items[0]
    	  };
    	  
    	  $scope.selectedUser;
    	  $scope.selectedGroup;

    	  $scope.ok = function () {
    	    $modalInstance.close($scope.selected.item);
    	    angular.element(target).val(groupEncode($scope.groups));
    	  };
    	  
    	  $scope.addUser = function (){
    		  if($scope.selectedUser != undefined){
    			  var newUser = getInfoGroup($scope.selectedUser.username);
    			  if(newUser != undefined) $scope.groups.push(newUser);
    			  $scope.selectedUser = "";
    		  }
    	  };
    	  
    	  $scope.addGroup = function (){
    		  if($scope.selectedGroup.length > 0){
    			  var newGroup = getInfoGroup($scope.selectedGroup);
    			  if(newGroup != undefined) $scope.groups.push(newGroup);   			  
    		  }
    	  };
    	  
    	  $scope.remove = function (idx){
    		  var person = $scope.groups[idx];
    		  if(person == undefined) return;
    		  if(idx > -1){
    			  $scope.groups.splice(idx,1);
    		  }
    	  }

    	  $scope.cancel = function () {
    	    $modalInstance.dismiss('cancel');
    	  };
    	});
    
    
    



