(function ()
{
	'use strict';

	angular
		.module('confApp')
		.factory('flashService', flashService);

	function flashService($rootScope, $location) // jshint ignore:line
	{
		var service = {};

		//define public methods
		service.Success = Success;
		service.Warning = Warning;
		service.Error = Error;
		service.Info = Info;

		initService();

		return service;

		/**
		 *  Attaches event on location change to update the flash messages
		 */
		function initService() // jshint ignore:line
		{
			$rootScope.$on('$locationChangeStart', clearFlashMessage);

			/**
			 *  clears the previous flash message if any, and it it doesn't want to be kept.
			 */
			function clearFlashMessage()
			{
				var flash = $rootScope.flash;

				if (flash)
				{
					if (!flash.keepAfterLocationChange && ($location.path() !== flash.path))
					{
						delete $rootScope.flash;
					}
					else
					{
						// only keep for a single location change
						flash.keepAfterLocationChange = false;
					}
				}
			}
		}

		/**
		 * @description
		 *  displays success message with predefined css classes
		 *
		 */
		function Success(message, keepAfterLocationChange) // jshint ignore:line
		{
			flashMessage(message, keepAfterLocationChange, null, 'success');
		}

		function Warning(message, keepAfterLocationChange) // jshint ignore:line
		{
			flashMessage(message, keepAfterLocationChange, null, 'warning');
		}

		function Info(message, keepAfterLocationChange) // jshint ignore:line
		{
			flashMessage(message, keepAfterLocationChange, null, 'info');
		}

		/**
		 * @description
		 *  displays error message with predefined css classes
		 *
		 */
		function Error(message, details, keepAfterLocationChange) // jshint ignore:line
		{
			flashMessage(message, keepAfterLocationChange, details, 'error');
		}

		function flashMessage(message, keepAfterLocationChange, details, type) // jshint ignore:line
		{
			keepAfterLocationChange = keepAfterLocationChange || false;
			console.log(message)

			if (!_.isArray(message))
			{
				message = [message];
			}

			if (details && !_.isUndefined(details) && !_.isArray(details))
			{
				details = [details];
			}

			$rootScope.flash = {
				message: message, //TODO translate check
				details: details,
				type: type,
				keepAfterLocationChange: keepAfterLocationChange,
				expand: false,
				path: $location.path()
			};
		}
	}

})();
