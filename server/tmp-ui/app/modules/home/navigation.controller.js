(function ()
{
	'use strict';
	/**
	 * @description
	 * Top bar layout functionality
	 *
	 * @param $scope {object} angular scope object
	 * @param $rootScope {object} angular root scope object
	 * @param config {object} global config
	 * @param configService {object} config service
	 * @param $location {object} angular location object
	 */

	angular
		.module('confApp')
		.controller('navigationController', navigationController);

	function navigationController($scope, $rootScope, config, $location)
	{
		$scope.menuItems = config.menuItems;
		$rootScope.breadcrumb = getRecursiveItems(config.menuItems);

		$scope.selected = function (element)
		{
			return element.active ? 'active' : '';
		}

		$scope.treeview = function (element)
		{
			return element.children ? 'treeview' : '';
		}

		/**
		 * @param  element {object}
		 *
		 * @description
		 * checks if the passed element has active class and toggles the status
		 * @used for DOM purpose for Bootstrap Classes
		 *
		 * @returns {object} changed element
		 */
		$scope.select = function (element)
		{
			if (element.active)
			{
				element.active = false;
			}
			else
			{
				element.active = true;
			}

			if (!_.isEmpty(element.link))
			{
				$scope.updateMenu(element);
			}

			return element;
		}

		/**
		 * @param  element {object}
		 *
		 * @description
		 * checks if the user is logged in
		 * @used for DOM purpose for Bootstrap Classes
		 *
		 * @returns {boolean}
		 */
		$scope.logged = function ()
		{
			return $rootScope.globals.currentUser ? true : false;
		}

		/**
		 * @param element {object} of menuItems structure (optional)
		 * @description
		 * updates the current menu item
		 *
		 * @returns {object} - menu item
		 */
		$scope.updateMenu = function (element)
		{
			$rootScope.globals.currentMenuItem = element || $scope.currentMenuItem();
			return $rootScope.globals.currentMenuItem;
		}

		/**
		 * @description
		 * searching the current menu item
		 *
		 * @returns {object} - menu item
		 */
		$scope.currentMenuItem = function ()
		{

			var location = $location.path().split('/'), result = [];
			result = getRecursiveItems(config.menuItems);
			
			if (location.length < 2) {
				return _(result).find({link: $rootScope.globals.appBase + '#/'}) || $rootScope.globals.currentMenuItem;
			}

			var pageLength = Math.min(location.length, 5);
			for (var i = pageLength; i >= 2; i--)
			{
				var pageLocation = _.take(location, i);
				var pagePath = pageLocation.join('/');
				var menuPage = _(result).find({link: $rootScope.globals.appBase + '#' + pagePath});
				if (!_.isUndefined(menuPage))
				{
					return menuPage;
				}
			}
			return $rootScope.globals.currentMenuItem;
		};

		function getRecursiveItems(menuItems)
		{
			var result = [];

			_.each(menuItems, function (item)
			{
				result.push(item);
				item.children && (result = _.union(result, getRecursiveItems(item.children)))
			});

			return result;
		}



		$scope.updateMenu();

	}

})();
