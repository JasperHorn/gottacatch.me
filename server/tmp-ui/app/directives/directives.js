(function ()
{
	'use strict';

	var confApp = angular.module('confApp');
	/**
	 * @description
	 * Setting page title on the browser
	 *
	 * @param  scope {object}
	 * @param  element {object} - (angular or jquery object) field that will contain the title
	 */
	confApp.directive('pageTitle', function ()
	{
		return {
			restrict: 'EA',
			link: function ($scope, $element)
			{
				var el = $element.find('h1');

				var text = function ()
				{

					return el.text();
				};
				var setTitle = function (title)
				{
					document.title = title;
				};
				$scope.$watch(text, setTitle);
			}
		};
	});

	/**
	 *
	 * @description
	 * calling browser back button
	 *
	 * @param  scope {object}
	 * @param  element {object} - (angular or jquery object) field that will contain the title
	 */
	confApp.directive('backButton', ['$window', function ($window)
	{
		return {
			restrict: 'A',
			link: function (scope, element)
			{
				element.bind('click', function ()
				{
					$window.history.back();
				});
			}
		};
	}]);

	/**
	 * @description
	 * detecting whenever ajax requests are in progress
	 *
	 * @param  $http {object} angular http service
	 * @param  scope {object}
	 * @param  element {object} - (angular or jquery object) field that will contain the title
	 */
	confApp.directive('preloader', ['$http', function ($http)
	{
		return {
			restrict: 'A',
			link: function (scope, element)
			{
				//var content = angular.element(element).parent().find('.preloader-content');

				scope.isLoading = function ()
				{
					return $http.pendingRequests.length > 0;
				};

				scope.$watch(scope.isLoading, function (v)
				{

					if (v)
					{
						element.css('visibility', 'visible');
						//content.css('visibility', 'hidden');
					}
					else
					{
						element.css('visibility', 'hidden');
						//content.css('visibility', 'visible');
					}

				});
			}
		};
	}]);

	confApp.directive('confirmOnExit', function ()
	{
		return {
			link: function ($scope, elem, attrs)
			{
				window.onbeforeunload = function ()
				{

					angular.forEach($scope[attrs.name], function (value, key)
					{
						if (key[0] == '$')
						{
							return;
						}
					});

					if ($scope[attrs.name].$dirty)
					{
						return "If you continue your changes will not be saved.";
					}

				}
				$scope.$on('$locationChangeStart', function (event, next, current)
				{

					var tmpName = attrs.name.split('.'), concated;

					if (_.isArray(tmpName) && (tmpName.length > 1))
					{
						concated = $scope[tmpName[0]][tmpName[1]];
					}
					else
					{
						concated = $scope[attrs.name];
					}

					angular.forEach(concated, function (value, key)
					{
						if (key[0] == '$')
						{
							return;
						}
					});

					if (concated.$dirty)
					{
						if (!confirm("If you continue your changes will not be saved."))
						{
							event.preventDefault();
						}
					}
				});
			}
		};
	});

	confApp.directive('stringToNumber', function ()
	{
		return {
			require: 'ngModel',
			link: function (scope, element, attrs, ngModel)
			{
				ngModel.$parsers.push(function (value)
				{
					return '' + value;
				});
				ngModel.$formatters.push(function (value)
				{
					return parseFloat(value, 10);
				});
			}
		};
	});

})();



