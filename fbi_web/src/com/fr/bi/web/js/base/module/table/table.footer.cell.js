/**
 *
 * 表格
 *
 * Created by GUY on 2015/9/22.
 * @class BI.TableFooterCell
 * @extends BI.Single
 */
BI.TableFooterCell = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.TableFooterCell.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-table-footer-cell",
            text: ""
        })
    },

    _init: function () {
        BI.TableFooterCell.superclass._init.apply(this, arguments);
        BI.createWidget({
            type: "bi.label",
            element: this.element,
            textAlign: "left",
            text: this.options.text,
            value: this.options.value
        })
    }
});

$.shortcut("bi.table_footer_cell", BI.TableFooterCell);