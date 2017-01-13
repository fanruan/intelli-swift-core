/**
 *
 * 表格单元格
 *
 * Created by GUY on 2016/1/12.
 * @class BI.GridTableCell
 * @extends BI.Widget
 */
BI.GridTableCell = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.GridTableCell.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-grid-table-cell",
            width: 0,
            height: 0,
            _rowIndex: 0,
            _columnIndex: 0,
            _left: 0,
            _top: 0,
            el: {}
        })
    },

    _init: function () {
        BI.GridTableCell.superclass._init.apply(this, arguments);
        var o = this.options;
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: BI.extend(o.el, {
                    width: o.width - (o._columnIndex === 0 ? 1 : 0) - 1,
                    height: o.height - (o._rowIndex === 0 ? 1 : 0) - 1
                }),
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }]
        });
    }
});

$.shortcut("bi.grid_table_cell", BI.GridTableCell);