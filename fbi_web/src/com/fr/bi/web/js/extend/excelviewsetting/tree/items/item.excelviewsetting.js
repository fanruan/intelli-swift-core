/**
 * ExcelViewSettingItem
 *
 * Created by GUY on 2016/4/7.
 * @class BI.ExcelViewSettingItem
 * @extends BI.BasicButton
 */
BI.ExcelViewSettingItem = BI.inherit(BI.BasicButton, {

    _defaultConfig: function () {
        return BI.extend(BI.ExcelViewSettingItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-excel-view-setting-item",
            height: 25,
            field: "",
            value: ""
        });
    },

    _init: function () {
        BI.ExcelViewSettingItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.row = o.row, this.col = o.col;
        this.field = BI.createWidget({
            type: "bi.label",
            textAlign: BI.HorizontalAlign.Left,
            hgap: 10,
            text: o.field,
            height: o.height
        });
        this.cell = BI.createWidget({
            type: "bi.label",
            textAlign: BI.HorizontalAlign.Left,
            cls: "excel-view-setting-item-cell",
            hgap: 10,
            text: this._getShowText(),
            height: o.height
        });
        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: this.field
            }, {
                el: this.cell,
                width: 80
            }]
        })
    },

    _getShowText: function () {
        if (BI.isNotNull(this.row) && BI.isNotNull(this.col)) {
            return String.fromCharCode("A".charCodeAt(0) + this.col) + (this.row + 1);
        }
        return "";
    },

    getValue: function(){
        return this.options.value;
    },

    getPosition: function(){
        if(BI.isNotNull(this.row) && BI.isNotNull(this.col)) {
            return {
                row: this.row,
                col: this.col
            }
        }
    },

    populate: function (op) {
        this.row = op.row;
        this.col = op.col;
        this.field.setText(op.field);
        this.cell.setText(this._getShowText(op.row, op.col));
    }
});
$.shortcut('bi.excel_view_setting_item', BI.ExcelViewSettingItem);