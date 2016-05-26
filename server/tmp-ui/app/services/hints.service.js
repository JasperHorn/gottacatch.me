(function ()
{
	'use strict';

	angular
		.module('confApp')
		.factory('hintsService', hintsService);

	v.$inject = ['$http'];

	function hintsService($http)
	{
		var service = {};

		service.getHints = getHints;
		service.addNextHint = addNextHint;
		service.resetHints = resetHints;

		return service;

		function getHints()
		{
			return $http.get('/rest/hints').then(handleSuccess, handleError('Error getting the hints'));
		}
		
		function addNextHint()
		{
			return $http.post('/rest/nexthint', 'next').then(handleSuccess, handleError('Error asking for the next hints'));
		}
		
		function resetHints()
		{
			return $http.post('/rest/resethints', 'aaa').then(handleSuccess, handleError('Error resetting the next hints'));
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
