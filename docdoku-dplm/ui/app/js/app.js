(function(){

    'use strict';

    process.on('uncaughtException', function (e) {
        console.log(e);
    });

    angular.module('dplm', [

        // Dependencies
        'ngMaterial',
        'ngAnimate',
        'ngRoute',
        'pascalprecht.translate',
        'uuid4',
        'ngDragDrop',
        'ngAnimate',
        'ngAria',

        // Templates
        'dplm.templates',

        // Routes
        'dplm.home',
        'dplm.settings',
        'dplm.workspace',

        'dplm.folder',
        // Components
        'dplm.services.cli',
        'dplm.services.configuration',
        'dplm.services.translations',
        'dplm.services.notification',
        'dplm.services.folders',
        'dplm.services.workspaces',
        'dplm.services.confirm',
        'dplm.services.prompt',
        'dplm.services.output',

        'dplm.services.3d',
        'dplm.directives.filechange',
        'dplm.directives.scrollend',
        'dplm.directives.filedrop',
        'dplm.filters.fileshortname',
        'dplm.filters.timeago',
        'dplm.filters.last',
        'dplm.filters.join',
        'dplm.filters.humanreadablesize',

        'dplm.contextmenu',
        'dplm.menu'

    ])

        .config(function ($routeProvider) {
            $routeProvider.otherwise('/');
        })

        .controller('AppCtrl', function ($scope, $location, $mdSidenav, $filter, NotificationService, ConfigurationService, CliService, WorkspaceService, FolderService) {

            $scope.title = 'DocDoku DPLM';

            $scope.openMenu = function () {
                $mdSidenav('menu').open();
            };

            $scope.configuration = ConfigurationService.configuration;
            $scope.workspaces = WorkspaceService.workspaces;
            $scope.folders = FolderService.folders;

            $scope.addFolder = function ($event, files) {
                if (files && files.length === 1) {
                    FolderService.add(files[0].path);
                }
            };

            $scope.isSelectedWorkspace = function(workspace){
                var currentParts = $location.path().split('/');
                return currentParts[1] === 'workspace'  && workspace === currentParts[2];
            };

            $scope.isSelectedFolder = function(folderUuid) {
                var currentParts = $location.path().split('/');
                return currentParts[1] === 'folder'  && folderUuid === currentParts[2];
            };
        });

})();