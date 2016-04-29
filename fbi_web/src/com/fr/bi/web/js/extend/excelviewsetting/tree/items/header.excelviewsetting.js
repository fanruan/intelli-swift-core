/**
 * ExcelViewSettingHeader
 *
 * Created by GUY on 2016/4/7.
 * @class BI.ExcelViewSettingHeader
 * @extends BI.Widget
 */
BI.ExcelViewSettingHeader = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ExcelViewSettingHeader.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-excel-view-setting-header",
            height: 25
        });
    },

    _init: function () {
        BI.ExcelViewSettingHeader.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                type: "bi.label",
                hgap: 10,
                textAlign: BI.HorizontalAlign.Left,
                text: BI.i18nText("BI-Field"),
                height: o.height
            }, {
                type: "bi.label",
                hgap: 10,
                textAlign: BI.HorizontalAlign.Left,
                text: BI.i18nText("BI-Cell"),
                cls: "excel-view-setting-header-cell",
                height: o.height,
                width: 80
            }]
        });
    }
});
$.shortcut('bi.excel_view_setting_header', BI.ExcelViewSettingHeader);