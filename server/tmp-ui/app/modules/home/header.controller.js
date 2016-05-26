(function ()
{
	'use strict';
	/**
	 * @ngdoc controller
	 * @name home.headerController
	 * @description
	 * Top bar layout functionality
	 *
	 * @param userService {object} user service
	 * @param $rootScope {object} angular root scope object
	 */
	angular
		.module('confApp')
		.controller('headerController', headerController);

	function headerController($rootScope)
	{

		/**
		 * @ngdoc method
		 * @name sidebarToggle
		 * @methodOf home.headerController
		 *
		 * @description
		 * toggle the size  of the sidebar
		 *
		 */
		$rootScope.sidebarToggle = function ()
		{
			if ($rootScope.sidebarClass && $rootScope.sidebarClass.length)
			{
				$rootScope.sidebarClass = '';
			}
			else
			{
				$rootScope.sidebarClass = 'sidebar-collapse';
			}
		}

	}

})();
