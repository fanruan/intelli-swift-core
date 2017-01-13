/**
 *
 * 表格单元格
 *
 * Created by GUY on 2016/1/12.
 * @class BI.ResizableGridTableCell
 * @extends BI.Widget
 */
BI.ResizableGridTableCell = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ResizableGridTableCell.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-resizable-grid-table-cell",
            el: {},
            start: BI.emptyFn,
            resize: BI.emptyFn,
            stop: BI.emptyFn
        })
    },

    _init: function () {
        BI.ResizableGridTableCell.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var handler = BI.createWidget(BI.extend(o.el, {width: o.width, height: o.height}));

        handler.element.resizable({
            handles: "e",
            minWidth: 15,
            helper: "clone",
            start: o.start,
            resize: o.resize,
            stop: o.stop
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: handler,
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }]
        })
    }
});
$.shortcut("bi.resizable_grid_table_cell", BI.ResizableGridTableCell);