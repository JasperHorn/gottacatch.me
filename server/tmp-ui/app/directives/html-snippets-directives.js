(function ()
{
	'use strict';

	var confApp = angular.module('confApp');

	confApp.directive("panel", function ()
	{
		return {
			restrict: "EM",
			transclude: {
				'tools': '?panelTools',
				'body': 'panelBody',
				'footer': '?panelFooter'

			},
			scope: {
				title: "@",
				icon: "@",
				row: '@'
			},
			link: function (scope, element, attrs)
			{
				element.removeAttr('title');
			},
			templateUrl: 'directives/html/panel.html'
		};
	});

	confApp.directive('formButton', function ()
	{
		return {
			restrict: 'E',

			replace: true,
			scope: {
				title: '@',
				icon: '@',
			},
			link: function (scope, element, attrs)
			{
				element.removeAttr('title').removeAttr('icon');
			},
			templateUrl: 'directives/html/form-button.html'
		};
	});

	confApp.directive('formField', function ()
	{
		return {
			restrict: 'EM',
			replace: true,
			transclude: true,
			scope: {
				label: '@',
				tooltip: '@'
			},
			link: function (scope, element, attrs)
			{
				element.removeAttr('label').removeAttr('tooltip');
			},
			templateUrl: 'directives/html/form-field.html'
		};
	});

})();
