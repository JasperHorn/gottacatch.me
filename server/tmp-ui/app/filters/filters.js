(function ()
{
	'use strict';

	var confApp = angular.module('confApp');

	/**
	 * @description
	 * rounding float numbers
	 *
	 * @param  input {float}
	 * @param  places {integer}
	 */
	confApp.filter('round', function ($filter)
	{
		return function (input, places)
		{
			return _.round(input, places);
		};
	});

	/**
	 * @description
	 * rounding digit places for numbers
	 *
	 * @param  input {float}
	 * @param  places {integer}
	 */
	confApp.filter('digits', function ()
	{
		return function (input)
		{
			if (input < 10)
			{
				input = '0' + input;
			}

			return input;
		};
	});

	confApp.filter({
		unique: ['$parse', uniqFilter],
		uniq: ['$parse', uniqFilter]
	});

	confApp.filter('capitalize', function ()
	{
		return function (input, all)
		{
			var reg = (all) ? /([^\W_]+[^\s-]*) */g : /([^\W_]+[^\s-]*)/;
			return (!!input) ? input.replace(reg, function (txt)
			{
				return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
			}) : '';
		}
	});

	confApp.filter('percentage', ['$filter', function ($filter)
	{
		return function (input, decimals)
		{
			if (_.isEmpty(decimals))
			{
				decimals = 0;
			}
			return $filter('number')(input * 100, decimals) + '%';
		};
	}]);

	confApp.filter('facetDisplay', ['$filter', function ($filter)
	{
		return _.memoize(function (input)
		{
			var result = '';

			if (!!input.description)
			{
				result += input.description;
			}
			if (!!input.code)
			{
				result += ' <' + input.code + '> ';
			}
			if (!input.fillrate)
			{
				input.fillrate = 0;
			}

			result += ' (' + $filter('number')(input.fillrate * 100, 0) + '%) ';

			return (result);
		});
	}]);

	confApp.filter('detectDisplayFormat', ['$filter', 'config', function ($filter, config)
	{
		return function (input)
		{

			if (isDate(input))
			{
				input = $filter('date')(input.split(' ')[0], config.dateFormat)
			}

			return input;
		};
	}]);

	function isDate(date)
	{
		var date = date.split(' ')[0];
		if (new Date(date) instanceof Date)
		{
			var date = new Date(date);
			return !isNaN(date.valueOf()) ? true : false;
		}
		return false;
	}

	//private functions

	function uniqFilter($parse)
	{
		return function (collection, property)
		{

			collection = _.isObject(collection) ? _.toArray(collection) : collection;

			if (!_.isArray(collection))
			{
				return collection;
			}

			//store all unique identifiers
			var uniqueItems = [],
				get = $parse(property);

			return (_.isUndefined(property)) ? collection.filter(function (elm, pos, self)
			{
				return self.indexOf(elm) === pos;
			}) : collection.filter(function (elm)
			{
				var prop = get(elm);

				if (some(uniqueItems, prop))
				{
					return false;
				}

				uniqueItems.push(prop);

				return true;
			});

			//checked if the unique identifier is already exist
			function some(array, member)
			{
				if (_.isUndefined(member))
				{
					return false;
				}
				return array.some(function (el)
				{
					return _.eq(el, member);
				});
			}
		}
	};

})();
