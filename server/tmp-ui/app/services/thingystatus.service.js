(function ()
{
	'use strict';

	angular
		.module('confApp')
		.factory('thingystatusService', thingystatusService);

	thingystatusService.$inject = ['$http'];

	function thingystatusService($http)
	{
		var service = {};

		service.getStatus = getStatus;

		return service;

		function getStatus()
		{
			return $http.get('/rest/thingstatus').then(handleSuccess, handleError('Error getting the status'));
		}

		// private functions

		function handleSuccess(res)
		{
			return res.data;
		}

		function handleError(error)
		{
			return function ()
			{
				return {success: false, message: error};
			};
		}
	}

})();
