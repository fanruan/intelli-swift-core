/**
 * ExcelViewSettingCell
 *
 * Created by GUY on 2016/4/8.
 * @class BI.ExcelViewSettingCell
 * @extends BI.TextButton
 */
BI.ExcelViewSettingCell = BI.inherit(BI.TextButton, {

    _defaultConfig: function () {
        return BI.extend(BI.ExcelViewSettingCell.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-excel-view-setting-cell",
            disableSelected: true,
            text: "",
            textAlign: "left",
            whiteSpace: "nowrap",
            hgap: 5
        });
    },

    _init: function () {
        BI.ExcelViewSettingCell.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.triangle = BI.createWidget({
            type: "bi.layout",
            cls: "excel-view-setting-cell-triangle",
            invisible: true,
            width: 0,
            height: 0
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.triangle,
                right: 0,
                top: 0
            }]
        });
    },

    setSelected: function (b) {
        BI.ExcelViewSettingCell.superclass.setSelected.apply(this, arguments);
        this.triangle.setVisible(b);
    },

    doClick: function () {
        BI.ExcelViewSettingCell.superclass.doClick.apply(this, arguments);
        if (this.isValid()) {
            this.fireEvent(BI.ExcelViewSettingCell.EVENT_CHANGE, this.getValue(), this);
        }
    }
});
BI.ExcelViewSettingCell.EVENT_CHANGE = "ExcelViewSettingCell.EVENT_CHANGE";
$.shortcut('bi.excel_view_setting_cell', BI.ExcelViewSettingCell);