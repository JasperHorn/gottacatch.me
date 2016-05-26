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

	userController.$inject = ['$location', 'authService'];

	function userController($location, authService)
	{
		var that = this;

		that.login = login;

		(function initController()
		{
			
		})();

		function login()
		{
		};
	}

})();
