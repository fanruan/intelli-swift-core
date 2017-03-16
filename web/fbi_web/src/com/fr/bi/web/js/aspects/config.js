(function () {
    var aspectConfig = function (widget, fn) {
        BI.Plugin.registerObject(widget, function (obj) {
            fn.apply(obj, arguments);
        });
    };


    aspectConfig("bi.table_chart_manager", BI.TableChartManagerAspect);
    aspectConfig("bi.detail_table", BI.DetailTableAspect);
})();
