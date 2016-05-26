(function ()
{
	'use strict';
	/**
	 * @description
	 * Top bar layout functionality
	 *
	 * @param userService {object} user service
	 * @param configService {object} config service
	 */
	angular
		.module('confApp')
		.controller('homeController', homeController);

	function homeController($location)
	{
		var that = this;

		initController();

		function initController()
		{
		}

		function redirectTo(url)
		{
			$location.path(url);
		}
	}

})();
