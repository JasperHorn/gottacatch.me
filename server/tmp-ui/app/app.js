(function ()
{
	'use strict';

	/**
	 * Angular App - Configuration Screens
	 * @author Zoran Trpevski <ztrpevski@wcc-group.com>
	 * @copyright 2015 WCC Development B.V. All rights reserved.
	 * @function run
	 * @config config
	 */
	angular
		.module('confApp',
			[
				'ui.router',
				'ngCookies',
				'pascalprecht.translate',
				'ui.bootstrap',
				'ui.sortable',
				'ui.select'
			]
		)
		.config(config)
		.run(run);

	/**
	 * @ngdoc config
	 * @name config
	 *
	 * @param  $stateProvider {object} from ui-router object injected
	 * @param  $urlRouterProvider {object} from ui-router object injected
	 * @param  $translateProvider {object} translate object injected
	 *
	 * @description
	 *  Configuring angular app
	 */
	function config($stateProvider, $urlRouterProvider, $translateProvider) // jshint ignore:line
	{

		$stateProvider
		//layouts
			.state('sidebar', {
				abstract: true,
				views: {
					layout: {
						templateUrl: 'layouts/sidebar.view.html'
					}
				}
			})
			//views
			.state('dashboard', {
				url: "",
				controller: 'homeController',
				parent: 'sidebar',
				controllerAs: 'dashboard',
				templateUrl: 'modules/home/home.view.html'
			})
			.state('user', {
				url: "/user",
				controller: 'userController',
				parent: 'sidebar',
				controllerAs: 'user',
				templateUrl: 'modules/user/user.view.html'
			})
			.state('admin', {
				url: "/admin",
				controller: 'adminController',
				parent: 'sidebar',
				controllerAs: 'admin',
				templateUrl: 'modules/admin/admin.view.html'
			})
			.state('404', {
				url: "/404",
				parent: 'sidebar',
				templateUrl: 'modules/error/404.view.html'
			})
			.state('500', {
				url: "/500",
				parent: 'sidebar',
				templateUrl: 'modules/error/500.view.html'
			});

		$urlRouterProvider.otherwise('/500');

		$translateProvider.translations('en', localeEN());// jshint ignore:line
		$translateProvider.translations('nl', localeNL());// jshint ignore:line
		$translateProvider.preferredLanguage('en');

	}

	/**
	 * @ngdoc run
	 * @name run
	 *
	 * @param  $rootScope {object} from ui-router object injected
	 * @param  $location {object} from ui-router object injected
	 * @param  $cookieStore {object} translate object injected
	 * @param  $http {object} translate object injected
	 *
	 * @description
	 *  Bootstrapping angular app
	 */

	function run($rootScope, $location, $http, config) // jshint ignore:line
	{
		/**
		 * @ngdoc globals
		 * @name globals
		 * @constant
		 *
		 * @description
		 * constant containing object data available for every controller
		 */
		//logged user always
		$rootScope.globals = {
			currentUser: config.currentUser,
			appBase: config.BASE_URL,
			dashboardLink: config.menuItems[0].link
		};

		//registered event listener on location change
		$rootScope.$on('$locationChangeSuccess', function (event, next, current)
		{
			console.info('$locationChangeSuccess', event, next, current, $rootScope.globals);
		});
	}

})
();
