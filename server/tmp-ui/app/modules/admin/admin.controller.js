(function ()
{
	'use strict';
	angular
		.module('confApp')
		.controller('adminController', adminController);

	adminController.$inject = ['thingystatusService', 'hintsService', '$scope', '$interval', '$http'];

	function adminController(thingystatusService, hintsService, $scope, $interval, $http)
	{
		var that = this;

//		$scope.thingyStatus = thingystatusService.getStatus();
		$scope.thingyStatus = {};
		thingystatusService.getStatus().then(function(data) {$scope.thingyStatus = data;});
		
		$interval(function(){thingystatusService.getStatus().then(function(data) {$scope.thingyStatus = data;})}, 1000);
		$scope.hintcount = 0;
		
		$scope.nextHint = function() {$scope.hintcount++; hintsService.addNextHint();};
		$scope.resetHints = function()
		{
			$scope.hintcount = 0;
			hintsService.resetHints();
			$http.delete('rest/scores');
		};
	}

})();
