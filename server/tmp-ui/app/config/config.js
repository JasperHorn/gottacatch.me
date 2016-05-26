(function ()
{
	'use strict';

	/**
	 * @ngdoc config
	 * @name config
	 * @description
	 * Config service that will be available for the entire app
	 */

	angular
		.module('confApp')
		.factory('config', config);// jshint ignore:line

	function config()// jshint ignore:line
	{
		var service = {};

		service.BASE_URL = window.location.pathname;

		service.menuItems = [
 			{
				name: 'Home',
				icon: 'search',
				breadcrumb: [0],
				link: service.BASE_URL + '',
				visible: true,
				active: false
			},
			{
				name: 'User page',
				icon: 'search',
				breadcrumb: [0],
				link: service.BASE_URL + '#/user',
				visible: true,
				active: false
			},
			{
				name: 'Management',
				icon: 'dashboard',
				breadcrumb: [0],
				link: service.BASE_URL + '#/admin',
				visible: true,
				active: false
			}
		];

		return service;
	}

})();
