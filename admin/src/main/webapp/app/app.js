(function () {

    var app = angular.module('admin', ['ui.bootstrap', 'ui.router', 'angularFileUpload', 'admin-catalog', 'admin-login', 'admin-user', 'admin-mail', 'admin-order']);

    app.controller('RestrictedAccessController', function ($state, AuthService) {
        var ctrl = this;

        ctrl.hasAccess = function () {
            if (!AuthService.isAuthenticated()) {
                $state.go('home');
                return false;
            }
            return true;
        };
    });

    app.controller('DatepickerDemoCtrl', function ($scope) {

        $scope.today = function () {
            $scope.dt = new Date();
        };
        $scope.today();

        $scope.open = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();

            $scope.opened = true;
        };

        $scope.dateOptions = {
            startingDay: 1
        };
    });

    app.config(function ($stateProvider, $urlRouterProvider, $locationProvider) {
        // For any unmatched url, redirect to /home
        $urlRouterProvider.otherwise("home");

        $locationProvider.hashPrefix("!");

        $stateProvider
            .state('home', {
                url: "/home",
                templateUrl: "app/home/index.html"
            })
            .state('index', {
                url: "",
                templateUrl: "app/home/index.html"
            })
            .state('catalog', {
                url: "/catalog",
                templateUrl: 'app/catalog/index.html'
            })
            .state('catalog.items', {
                url: "/:resource",
                templateUrl: 'app/catalog/catalog-entries.html'
            })
            .state('catalog.items.detail', {
                url: "/:itemId",
                templateUrl: function ($stateParams) {
                    return 'app/catalog/' + $stateParams.resource + '-form.html';
                }
            })
            .state('user', {
                url: "/user",
                templateUrl: 'app/user/index.html'
            })
            .state('user.users', {
                url: "/users",
                templateUrl: 'app/user/user-entries.html'
            })
            .state('user.users.detail', {
                url: "/:userId",
                templateUrl: 'app/user/user-form.html'
            })
            .state('order', {
                url: "/order",
                templateUrl: 'app/order/index.html'
            })
            .state('order.orders', {
                url: "/orders",
                templateUrl: 'app/order/order-entries.html'
            })
            .state('order.operations', {
                url: "/operations",
                templateUrl: 'app/order/order-operations.html'
            })
            .state('order.orders.detail', {
                url: "/:orderId",
                templateUrl: 'app/order/order-form.html'
            })
            .state('mail', {
                url: "/mail",
                templateUrl: "app/mail/index.html"
            })
            .state('mail.templates', {
                url: "/mail",
                templateUrl: "app/mail/mailtemplate-entries.html"
            })
            .state('mail.operations', {
                url: "/mail",
                templateUrl: "app/mail/mail-operations.html"
            })
            .state('mail.templates.detail', {
                url: "/:mailId",
                templateUrl: "app/mail/mailtemplate-form.html"
            })
            .state('statistic', {
                url: "/statistic",
                templateUrl: 'app/statistic/index.html'
            });

    });
})();


function allLocales() {

    return [
        {displayName: "Albanian (Albania)", name: "sq_AL"},
        {displayName: "Albanian", name: "sq"},
        {displayName: "Arabic (Algeria)", name: "ar_DZ"},
        {displayName: "Arabic (Bahrain)", name: "ar_BH"},
        {displayName: "Arabic (Egypt)", name: "ar_EG"},
        {displayName: "Arabic (Iraq)", name: "ar_IQ"},
        {displayName: "Arabic (Jordan)", name: "ar_JO"},
        {displayName: "Arabic (Kuwait)", name: "ar_KW"},
        {displayName: "Arabic (Lebanon)", name: "ar_LB"},
        {displayName: "Arabic (Libya)", name: "ar_LY"},
        {displayName: "Arabic (Morocco)", name: "ar_MA"},
        {displayName: "Arabic (Oman)", name: "ar_OM"},
        {displayName: "Arabic (Qatar)", name: "ar_QA"},
        {displayName: "Arabic (Saudi Arabia)", name: "ar_SA"},
        {displayName: "Arabic (Sudan)", name: "ar_SD"},
        {displayName: "Arabic (Syria)", name: "ar_SY"},
        {displayName: "Arabic (Tunisia)", name: "ar_TN"},
        {displayName: "Arabic (United Arab Emirates)", name: "ar_AE"},
        {displayName: "Arabic (Yemen)", name: "ar_YE"},
        {displayName: "Arabic", name: "ar"},
        {displayName: "Belarusian (Belarus)", name: "be_BY"},
        {displayName: "Belarusian", name: "be"},
        {displayName: "Bulgarian (Bulgaria)", name: "bg_BG"},
        {displayName: "Bulgarian", name: "bg"},
        {displayName: "Catalan (Spain)", name: "ca_ES"},
        {displayName: "Catalan", name: "ca"},
        {displayName: "Chinese (China)", name: "zh_CN"},
        {displayName: "Chinese (Hong Kong)", name: "zh_HK"},
        {displayName: "Chinese (Singapore)", name: "zh_SG"},
        {displayName: "Chinese (Taiwan)", name: "zh_TW"},
        {displayName: "Chinese", name: "zh"},
        {displayName: "Croatian (Croatia)", name: "hr_HR"},
        {displayName: "Croatian", name: "hr"},
        {displayName: "Czech (Czech Republic)", name: "cs_CZ"},
        {displayName: "Czech", name: "cs"},
        {displayName: "Danish (Denmark)", name: "da_DK"},
        {displayName: "Danish", name: "da"},
        {displayName: "Dutch (Belgium)", name: "nl_BE"},
        {displayName: "Dutch (Netherlands)", name: "nl_NL"},
        {displayName: "Dutch", name: "nl"},
        {displayName: "English (Australia)", name: "en_AU"},
        {displayName: "English (Canada)", name: "en_CA"},
        {displayName: "English (India)", name: "en_IN"},
        {displayName: "English (Ireland)", name: "en_IE"},
        {displayName: "English (Malta)", name: "en_MT"},
        {displayName: "English (New Zealand)", name: "en_NZ"},
        {displayName: "English (Philippines)", name: "en_PH"},
        {displayName: "English (Singapore)", name: "en_SG"},
        {displayName: "English (South Africa)", name: "en_ZA"},
        {displayName: "English (United Kingdom)", name: "en_GB"},
        {displayName: "English (United States)", name: "en_US"},
        {displayName: "English", name: "en"},
        {displayName: "Estonian (Estonia)", name: "et_EE"},
        {displayName: "Estonian", name: "et"},
        {displayName: "Finnish (Finland)", name: "fi_FI"},
        {displayName: "Finnish", name: "fi"},
        {displayName: "French (Belgium)", name: "fr_BE"},
        {displayName: "French (Canada)", name: "fr_CA"},
        {displayName: "French (France)", name: "fr_FR"},
        {displayName: "French (Luxembourg)", name: "fr_LU"},
        {displayName: "French (Switzerland)", name: "fr_CH"},
        {displayName: "French", name: "fr"},
        {displayName: "German (Austria)", name: "de_AT"},
        {displayName: "German (Germany)", name: "de_DE"},
        {displayName: "German (Greece)", name: "de_GR"},
        {displayName: "German (Luxembourg)", name: "de_LU"},
        {displayName: "German (Switzerland)", name: "de_CH"},
        {displayName: "German", name: "de"},
        {displayName: "Greek (Cyprus)", name: "el_CY"},
        {displayName: "Greek (Greece)", name: "el_GR"},
        {displayName: "Greek", name: "el"},
        {displayName: "Hebrew (Israel)", name: "iw_IL"},
        {displayName: "Hebrew", name: "iw"},
        {displayName: "Hindi (India)", name: "hi_IN"},
        {displayName: "Hindi", name: "hi"},
        {displayName: "Hungarian (Hungary)", name: "hu_HU"},
        {displayName: "Hungarian", name: "hu"},
        {displayName: "Icelandic (Iceland)", name: "is_IS"},
        {displayName: "Icelandic", name: "is"},
        {displayName: "Indonesian (Indonesia)", name: "in_ID"},
        {displayName: "Indonesian", name: "in"},
        {displayName: "Irish (Ireland)", name: "ga_IE"},
        {displayName: "Irish", name: "ga"},
        {displayName: "Italian (Italy)", name: "it_IT"},
        {displayName: "Italian (Switzerland)", name: "it_CH"},
        {displayName: "Italian", name: "it"},
        {displayName: "Japanese (Japan)", name: "ja_JP"},
        {displayName: "Japanese (Japan,JP)", name: "ja_JP_JP_#u-ca-japanese"},
        {displayName: "Japanese", name: "ja"},
        {displayName: "Korean (South Korea)", name: "ko_KR"},
        {displayName: "Korean", name: "ko"},
        {displayName: "Latvian (Latvia)", name: "lv_LV"},
        {displayName: "Latvian", name: "lv"},
        {displayName: "Lithuanian (Lithuania)", name: "lt_LT"},
        {displayName: "Lithuanian", name: "lt"},
        {displayName: "Macedonian (Macedonia)", name: "mk_MK"},
        {displayName: "Macedonian", name: "mk"},
        {displayName: "Malay (Malaysia)", name: "ms_MY"},
        {displayName: "Malay", name: "ms"},
        {displayName: "Maltese (Malta)", name: "mt_MT"},
        {displayName: "Maltese", name: "mt"},
        {displayName: "Norwegian (Norway)", name: "no_NO"},
        {displayName: "Norwegian (Norway,Nynorsk)", name: "no_NO_NY"},
        {displayName: "Norwegian", name: "no"},
        {displayName: "Polish (Poland)", name: "pl_PL"},
        {displayName: "Polish", name: "pl"},
        {displayName: "Portuguese (Brazil)", name: "pt_BR"},
        {displayName: "Portuguese (Portugal)", name: "pt_PT"},
        {displayName: "Portuguese", name: "pt"},
        {displayName: "Romanian (Romania)", name: "ro_RO"},
        {displayName: "Romanian", name: "ro"},
        {displayName: "Russian (Russia)", name: "ru_RU"},
        {displayName: "Russian", name: "ru"},
        {displayName: "Serbian (Bosnia and Herzegovina)", name: "sr_BA"},
        {displayName: "Serbian (Latin)", name: "sr__#Latn"},
        {displayName: "Serbian (Latin,Bosnia and Herzegovina)", name: "sr_BA_#Latn"},
        {displayName: "Serbian (Latin,Montenegro)", name: "sr_ME_#Latn"},
        {displayName: "Serbian (Latin,Serbia)", name: "sr_RS_#Latn"},
        {displayName: "Serbian (Montenegro)", name: "sr_ME"},
        {displayName: "Serbian (Serbia and Montenegro)", name: "sr_CS"},
        {displayName: "Serbian (Serbia)", name: "sr_RS"},
        {displayName: "Serbian", name: "sr"},
        {displayName: "Slovak (Slovakia)", name: "sk_SK"},
        {displayName: "Slovak", name: "sk"},
        {displayName: "Slovenian (Slovenia)", name: "sl_SI"},
        {displayName: "Slovenian", name: "sl"},
        {displayName: "Spanish (Argentina)", name: "es_AR"},
        {displayName: "Spanish (Bolivia)", name: "es_BO"},
        {displayName: "Spanish (Chile)", name: "es_CL"},
        {displayName: "Spanish (Colombia)", name: "es_CO"},
        {displayName: "Spanish (Costa Rica)", name: "es_CR"},
        {displayName: "Spanish (Cuba)", name: "es_CU"},
        {displayName: "Spanish (Dominican Republic)", name: "es_DO"},
        {displayName: "Spanish (Ecuador)", name: "es_EC"},
        {displayName: "Spanish (El Salvador)", name: "es_SV"},
        {displayName: "Spanish (Guatemala)", name: "es_GT"},
        {displayName: "Spanish (Honduras)", name: "es_HN"},
        {displayName: "Spanish (Mexico)", name: "es_MX"},
        {displayName: "Spanish (Nicaragua)", name: "es_NI"},
        {displayName: "Spanish (Panama)", name: "es_PA"},
        {displayName: "Spanish (Paraguay)", name: "es_PY"},
        {displayName: "Spanish (Peru)", name: "es_PE"},
        {displayName: "Spanish (Puerto Rico)", name: "es_PR"},
        {displayName: "Spanish (Spain)", name: "es_ES"},
        {displayName: "Spanish (United States)", name: "es_US"},
        {displayName: "Spanish (Uruguay)", name: "es_UY"},
        {displayName: "Spanish (Venezuela)", name: "es_VE"},
        {displayName: "Spanish", name: "es"},
        {displayName: "Swedish (Sweden)", name: "sv_SE"},
        {displayName: "Swedish", name: "sv"},
        {displayName: "Thai (Thailand)", name: "th_TH"},
        {displayName: "Thai (Thailand,TH)", name: "th_TH_TH_#u-nu-thai"},
        {displayName: "Thai", name: "th"},
        {displayName: "Turkish (Turkey)", name: "tr_TR"},
        {displayName: "Turkish", name: "tr"},
        {displayName: "Ukrainian (Ukraine)", name: "uk_UA"},
        {displayName: "Ukrainian", name: "uk"},
        {displayName: "Vietnamese (Vietnam)", name: "vi_VN"}
    ];
}