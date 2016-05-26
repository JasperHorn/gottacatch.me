(function ()
{
	'use strict';
	angular
		.module('confApp')
		.controller('adminController', adminController);

	adminController.$inject = ['thingystatusService', '$scope'];

	function adminController(thingystatusService, $scope)
	{
		var that = this;

		$scope.thingyStatus = {status: "undefined"};
		thingystatusService.getStatus().then(function(data) {$scope.thingyStatus = data;});
		
		(function initController()
		{
			
		})();
	}

})();
