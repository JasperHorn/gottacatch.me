(function ()
{
	'use strict';
	angular
		.module('confApp')
		.controller('adminController', adminController);

	adminController.$inject = ['thingystatusService', '$scope', '$interval'];

	function adminController(thingystatusService, $scope, $interval)
	{
		var that = this;

//		$scope.thingyStatus = thingystatusService.getStatus();
		$scope.thingyStatus = {};
		thingystatusService.getStatus().then(function(data) {$scope.thingyStatus = data;});
		
		$interval(function(){thingystatusService.getStatus().then(function(data) {$scope.thingyStatus = data;})}, 1000);
		
		(function initController()
		{
			
		})();
	}

})();
