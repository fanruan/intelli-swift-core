ExcelVSettingView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ExcelVSettingView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-excel-view-setting"
        })
    },

    _init: function () {
        ExcelVSettingView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var excelviewsetting = BI.createWidget({
            element: vessel,
            type: "bi.excel_view_setting"
        });
    }
});

ExcelVSettingModel = BI.inherit(BI.Model, {});