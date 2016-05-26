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

	userController.$inject = ['$location', '$http', '$scope'];

	function userController($location, $http, $scope)
	{
		var that = this;

		that.login = login;
		
		$http.get('rest/scores').then(function(scores)
		{
			$scope.scores = scores.data;
		});

		(function initController()
		{
			
		})();

		function login()
		{
		};
	}

})();
