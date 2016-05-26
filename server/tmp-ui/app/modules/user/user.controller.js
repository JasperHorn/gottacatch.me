(function ()
{
	'use strict';
	/**
	 * @ngdoc controller
	 * @name home.loginController
	 * @description
	 * login functionality
	 *
	 * @param $location {object} angular location object
	 * @param authService {object} authentication service
	 * @param flashService {object} config service
	 */
	angular
		.module('confApp')
		.controller('userController', userController);

	userController.$inject = ['hintsService', '$scope'];

	function userController(hintsService, $scope)
	{
		var that = this;

		that.login = login;
		
		$scope.hints = {};
		hintsService.getHints().then(function(data){$scope.hints = data});

		(function initController()
		{
			
		})();

		function login()
		{
		};
	}

})();
