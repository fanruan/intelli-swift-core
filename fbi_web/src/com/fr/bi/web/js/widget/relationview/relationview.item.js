/**
 * 关联视图字段Item
 *
 * Created by GUY on 2015/12/23.
 * @class BI.RelationViewItem
 * @extends BI.Widget
 */
BI.RelationViewItem = BI.inherit(BI.BasicButton, {

    _defaultConfig: function () {
        return BI.extend(BI.RelationViewItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-relation-view-item",
            height: 25,
            hoverIn: BI.emptyFn,
            hoverOut: BI.emptyFn
        });
    },

    _init: function () {
        BI.RelationViewItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.element.hover(o.hoverIn, o.hoverOut);
        BI.createWidget({
            type: "bi.label",
            element: this.element,
            text: o.text,
            value: o.value,
            height: o.height
        })
    },

    setSelected: function (b) {
        this.element[b ? "addClass" : "removeClass"]("active");
    }
});
$.shortcut('bi.relation_view_item', BI.RelationViewItem);