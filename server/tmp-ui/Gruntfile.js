/* jshint ignore:start */

'use strict';

module.exports = function (grunt)
{

	// Time how long tasks take. Can help when optimizing build times
	require('time-grunt')(grunt);

	// Automatically load required Grunt tasks
	require('jit-grunt')(grunt, {
		useminPrepare: 'grunt-usemin',
		ngtemplates: 'grunt-angular-templates'
	});

	grunt.loadNpmTasks('grunt-connect-proxy');

	grunt.loadNpmTasks('grunt-ngdocs');

	// Configurable paths for the application
	var appConfig = {
		app: require('./bower.json').appPath || 'app',
		dist: 'target/dist'
	};
	var serveStatic = require('serve-static');
	var serveIndex = require('serve-index');

	// Define the configuration for all the tasks
	grunt.initConfig({

		// Project settings
		yeoman: appConfig,

		// Watches files for changes and runs tasks based on the changed files
		watch: {
			bower: {
				files: ['bower.json'],
				tasks: ['wiredep']
			},
			js: {
				files: ['<%= yeoman.app %>/{,*/}*.js'],
				tasks: ['newer:jshint:all', 'newer:jscs:all'],
				options: {
					livereload: '<%= connect.options.livereload %>'
				}
			},
			jsTest: {
				files: ['test/spec/{,*/}*.js'],
				tasks: ['newer:jshint:test', 'newer:jscs:test', 'karma']
			},
			gruntfile: {
				files: ['Gruntfile.js']
			},
			livereload: {
				options: {
					livereload: '<%= connect.options.livereload %>'
				},
				files: [
					'<%= yeoman.app %>/{,*/}*.html',
					'.tmp/styles/{,*/}*.css',
					'<%= yeoman.app %>/public/{,*/}*.{png,jpg,jpeg,gif,webp,svg}'
				]
			},
			less: {
				files: ['<%= yeoman.app %>/styles/{,*/}*.less', '<%= yeoman.app %>/components/*/styles/{,*/}*.less'],
				tasks: ['less']
			}

		},

		// The actual grunt server settings
		connect: {
			options: {
				port: 9000,
				// Change this to '0.0.0.0' to access the server from outside.
				hostname: '0.0.0.0',
				livereload: 35729
			},
			proxies: [
				{
					context: ['/rest'],
					host: 'localhost',
					port: 8080,
					https: false,
					changeOrigin: true
				}
			],
			livereload: {
				options: {
					open: true,
					middleware: function (connect, options)
					{
						var middlewares =
							[
								serveStatic('.tmp'),
								connect().use(
									'/bower_components',
									serveStatic('./bower_components')
								),
								connect().use(
									'/app/styles',
									serveStatic('./app/styles')
								),
								serveStatic(appConfig.app)
							];

						var directory = options.directory || options.base[options.base.length - 1];
						if (!Array.isArray(options.base))
						{
							options.base = [options.base];
						}

						// Setup the proxy
						middlewares.push(require('grunt-connect-proxy/lib/utils').proxyRequest);

						options.base.forEach(function (base)
						{
							// Serve static files.
							middlewares.push(serveStatic(base));
						});

						// Make directory browse-able.
						middlewares.push(serveIndex(directory));

						return middlewares;
					}
				}
			},
			test: {
				options: {
					port: 9001,
					middleware: function (connect)
					{
						return [
							serveStatic('.tmp'),
							serveStatic('test'),
							connect().use(
								'/bower_components',
								serveStatic('./bower_components')
							),
							serveStatic(appConfig.app)
						];
					}
				}
			},
			dist: {
				options: {
					open: true,
					base: '<%= yeoman.dist %>',
					middleware: function (connect, options)
					{
						var middlewares =
							[
								serveStatic('.tmp'),
								connect().use(
									'/bower_components',
									serveStatic('./bower_components')
								),
								connect().use(
									'/app/styles',
									serveStatic('./app/styles')
								),
								serveStatic(appConfig.app)
							];

						var directory = options.directory || options.base[options.base.length - 1];
						if (!Array.isArray(options.base))
						{
							options.base = [options.base];
						}

						// Setup the proxy
						middlewares.push(require('grunt-connect-proxy/lib/utils').proxyRequest);

						options.base.forEach(function (base)
						{
							// Serve static files.
							middlewares.push(serveStatic(base));
						});

						// Make directory browse-able.
						middlewares.push(serveIndex(directory));

						return middlewares;
					}
				}
			}
		},

		// Make sure there are no obvious mistakes
		jshint: {
			options: {
				jshintrc: '.jshintrc',
				reporter: require('jshint-stylish')
			},
			all: {
				src: [
					'Gruntfile.js',
					'<%= yeoman.app %>/{,*/}*.js'
				]
			},
			test: {
				options: {
					jshintrc: 'test/.jshintrc'
				},
				src: ['test/spec/{,*/}*.js']
			}
		},

		// Make sure code styles are up to par
		jscs: {
			options: {
				config: '.jscsrc',
				verbose: true
			},
			all: {
				src: [
					'Gruntfile.js',
					'<%= yeoman.app %>/{,*/}*.js'
				]
			},
			test: {
				src: ['test/spec/{,*/}*.js']
			}
		},

		// Empties folders to start fresh
		clean: {
			dist: {
				files: [{
					dot: true,
					src: [
						'.tmp',
						'<%= yeoman.dist %>/{,*/}*',
						'!<%= yeoman.dist %>/.git{,*/}*'
					]
				}]
			},
			server: '.tmp'
		},

		// Add vendor prefixed styles
		postcss: {
			options: {
				processors: [
					require('autoprefixer')({browsers: ['last 1 version']})
				]
			},
			server: {
				options: {
					map: true
				},
				files: [{
					expand: true,
					cwd: '.tmp/styles/',
					src: '{,*/}*.css',
					dest: '.tmp/styles/'
				}]
			},
			dist: {
				files: [{
					expand: true,
					cwd: '.tmp/styles/',
					src: '{,*/}*.css',
					dest: '.tmp/styles/'
				}]
			}
		},

		// Automatically inject Bower components into the app
		wiredep: {
			app: {
				src: ['<%= yeoman.app %>/index.html'],
				ignorePath: /\.\.\//,
				fileTypes: {
			        html: {
			            block: /(([ \t]*)<!--\s*bower:*(\S*)\s*-->)(\n|\r|.)*?(<!--\s*endbower\s*-->)/gi,
			            detect: {
			                js: /<script.*src=['"]([^'"]+)/gi,
			                css: /<link.*href=['"]([^'"]+)/gi
			            },
			            replace: {
			                js: function(filePath) {
			                    var filePathStrArr = filePath.split('.');
			                    var filePathStr = ''; //filePathStrArr[filePathStrArr.length-2];
			                    if (filePathStrArr[filePathStrArr.length - 2] != 'min') {
			                        filePathStrArr.pop();
			                        filePathStr = filePathStrArr.join('.') + '.min.js';
			                        if(!grunt.file.exists(filePathStr)){
			                        	var folderPathStrArr = filePathStr.split('/');
			                        	if (folderPathStrArr[folderPathStrArr.length - 2] != 'dist') {
			                        		var minifiedFilename = folderPathStrArr[folderPathStrArr.length - 1];
			                        		folderPathStrArr.pop();
			                        		filePathStr = folderPathStrArr.join('/') + '/dist/' + minifiedFilename;
			                        		if(!grunt.file.exists(filePathStr)){
					                            console.log('warning - file does not exist:'+filePathStr);
					                            filePathStr = filePath; //if the .min.js file does not exist then revert back to original filename
			                        		}
			                        	}
			                        }
			                    } else {
			                        filePathStr = filePath;
			                    }
			                    return '<script src="' + filePathStr + '"></script>';
			                },
			                css: '<link rel="stylesheet" href="{{filePath}}" />'
			            }
			        }
			    }
			},
			test: {
				devDependencies: true,
				src: '<%= karma.unit.configFile %>',
				ignorePath: /\.\.\//,
				fileTypes: {
					js: {
						block: /(([\s\t]*)\/{2}\s*?bower:\s*?(\S*))(\n|\r|.)*?(\/{2}\s*endbower)/gi,
						detect: {
							js: /'(.*\.js)'/gi
						},
						replace: {
							js: '\'{{filePath}}\','
						}
					}
				}
			}
		},
		//Compile less files
		less: {
			dist: {
				files: {
					'.tmp/styles/main.css': ['<%= yeoman.app %>/styles/app.less']
				},
				options: {
					sourceMap: true,
					sourceMapFilename: '.tmp/styles/main.css.map',
					sourceMapBasepath: '.tmp/',
					sourceMapRootpath: '/'
				}
			}
		},
		// Renames files for browser caching purposes
		filerev: {
			dist: {
				src: [
					'<%= yeoman.dist %>/{,*/}*.js',
					'<%= yeoman.dist %>/styles/{,*/}*.css'
				]
			}
		},

		// Reads HTML for usemin blocks to enable smart builds that automatically
		// concat, minify and revision files. Creates configurations in memory so
		// additional tasks can operate on them
		useminPrepare: {
			html: '<%= yeoman.app %>/index.html',
			options: {
				dest: '<%= yeoman.dist %>',
				flow: {
					html: {
						steps: {
							js: ['concat', 'uglifyjs'],
							css: ['cssmin']
						},
						post: {}
					}
				}
			}
		},

		// Performs rewrites based on filerev and the useminPrepare configuration
		usemin: {
			html: ['<%= yeoman.dist %>/{,*/}*.html'],
			css: ['<%= yeoman.dist %>/styles/{,*/}*.css'],
			js: ['<%= yeoman.dist %>/{,*/}*.js'],
			options: {
				assetsDirs: [
					'<%= yeoman.dist %>',
					'<%= yeoman.dist %>/styles'
				]
			}
		},

		htmlmin: {
			dist: {
				options: {
					collapseWhitespace: true,
					conservativeCollapse: true,
					collapseBooleanAttributes: true,
					removeCommentsFromCDATA: true
				},
				files: [{
					expand: true,
					cwd: '<%= yeoman.dist %>',
					src: ['*.html'],
					dest: '<%= yeoman.dist %>'
				}]
			}
		},
		
		uglify: {
			onlyScripts: {
				files:   [{
					dest: '<%= yeoman.dist %>/scripts/scripts.js',
					src:  ['.tmp/concat/scripts/scripts.js']
				}]
			}
		},
		
		ngtemplates: {
			dist: {
				options: {
					module: 'confApp',
					htmlmin: '<%= htmlmin.dist.options %>',
					usemin: 'scripts/scripts.js'
				},
				cwd: '<%= yeoman.app %>',
				src: ['directives/*.html', 'layouts/*.html', 'modules/*.html',
				      'directives/**/*.html', 'layouts/**/*.html', 'modules/**/*.html'],
				dest: '.tmp/templateCache.js'
			}
		},

		// ng-annotate tries to make the code safe for minification automatically
		// by using the Angular long form for dependency injection.
		ngAnnotate: {
			dist: {
				files: [{
					expand: true,
					cwd: '.tmp/concat/scripts',
					src: '*.js',
					dest: '.tmp/concat/scripts'
				}]
			}
		},

		// Copies remaining files to places other tasks can use
		copy: {
			dist: {
				files: [{
					expand: true,
					dot: true,
					cwd: '<%= yeoman.app %>',
					dest: '<%= yeoman.dist %>',
					src: [
						'**.{ico,png,txt,jpg}',
						'*.html',
						'public/{,*/}*.*',
						'fonts/{,*/}*.*'
					]
				}]
			},
			styles: {
				expand: true,
				cwd: '<%= yeoman.app %>/styles',
				dest: '.tmp/styles/',
				src: '{,*/}*.css'
			},
			vendorJS: {
				expand: true,
				cwd:    '.tmp/concat/scripts/',
				dest:   '<%= yeoman.dist %>/scripts/',
				src:    'vendor.js'
			}
		},

		// Run some tasks in parallel to speed up the build process
		concurrent: {
			server: [
				'copy:styles'
			],
			test: [
				'copy:styles'
			],
			dist: [
				'copy:styles',
			]
		},

		// Test settings
		karma: {
			unit: {
				configFile: 'test/karma.conf.js',
				singleRun: true
			}
		}
	});

	grunt.registerTask('serve', 'Compile then start a connect web server', function (target)
	{
		if (target === 'dist')
		{
			return grunt.task.run(['build', 'configureProxies:server', 'connect:dist:keepalive']);
		}

		grunt.task.run([
			'clean:server',
			'wiredep',
			'less',
			'concurrent:server',
			'configureProxies:server',
			'postcss:server',
			'connect:livereload',
			'watch'
		]);
	});

	grunt.registerTask('server', 'DEPRECATED TASK. Use the "serve" task instead', function (target)
	{
		grunt.log.warn('The `server` task has been deprecated. Use `grunt serve` to start a server.');
		grunt.task.run(['serve:' + target]);
	});

	grunt.registerTask('test', [
		'clean:server',
		'wiredep',
		'concurrent:test',
		'postcss',
		'connect:test',
		'karma'
	]);

	grunt.registerTask('build', [
		'clean:dist',
		'wiredep',
		'less',
		'useminPrepare',
		'concurrent:dist',
		'postcss',
		'ngtemplates',
		'concat',
		'ngAnnotate',
		'copy:dist',
		'cssmin',
		'uglify:onlyScripts',
	    'copy:vendorJS',
		'filerev',
		'usemin',
		'htmlmin'
	]);

	grunt.registerTask('default', [
		'newer:jshint',
		'newer:jscs',
		'test',
		'build'
	]);
};
/* jshint ignore:end */
