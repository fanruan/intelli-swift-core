/**
 * ExcelViewCell
 *
 * Created by GUY on 2016/3/30.
 * @class BI.ExcelViewCell
 * @extends BI.TextButton
 */
BI.ExcelViewCell = BI.inherit(BI.TextButton, {

    _defaultConfig: function () {
        return BI.extend(BI.ExcelViewCell.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-excel-view-cell select-data-level0-item-button",
            trigger: "mousedown",
            text: "",
            drag: {},
            textAlign: "left",
            whiteSpace: "nowrap",
            hgap: 5
        });
    },

    _init: function () {
        BI.ExcelViewCell.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var selected = BI.createWidget({
            type: "bi.layout",
            cls: "selected-border"
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: {
                    type: "bi.layout",
                    cls: "excel-view-cell-triangle",
                    width: 0,
                    height: 0
                },
                right: 0,
                top: 0
            }, {
                el: selected,
                top: 0,
                left: 0,
                bottom: 0,
                right: 0
            }]
        });
        this.element.draggable(o.drag);
        selected.setVisible(BI.Utils.isSrcUsedBySrcID(o.value) === true);
        BI.Broadcasts.on(o.value, function(v){
            selected.setVisible(v === true);
        });
    }
});
$.shortcut('bi.excel_view_cell', BI.ExcelViewCell);