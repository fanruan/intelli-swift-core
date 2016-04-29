/**
 * 浮动的居中布局
 */
BI.CenterVerticalAdaptLayout = BI.inherit(BI.Layout, {
    _defaultConfig: function () {
        return BI.extend(BI.CenterVerticalAdaptLayout.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-float-center-layout",
            items: [],
            hgap: 0,
            vgap: 0,
            tgap: 0,
            bgap: 0,
            lgap: 0,
            rgap: 0
        });
    },
    _init: function () {
        BI.CenterVerticalAdaptLayout.superclass._init.apply(this, arguments);
        this.populate(this.options.items);
    },

    resize: function () {
        console.log("float_center_adapt布局不需要resize");
    },

    addItem: function () {
        //do nothing
        throw new Error("不能添加元素")
    },

    populate: function (items) {
        BI.CenterVerticalAdaptLayout.superclass.populate.apply(this, arguments);
        var self = this, o = this.options;
        var left = BI.createWidget({
            type: "bi.vertical",
            items: items,
            hgap: o.hgap,
            vgap: o.vgap,
            tgap: o.tgap,
            bgap: o.bgap,
            lgap: o.lgap,
            rgap: o.rgap
        });

        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [left]
        });

        BI.defer(function () {
            var width = left.element.width(),
                height = left.element.height();
            BI.DOM.hang([left]);
            left.element.width(width).height(height).css("float", "none");
            BI.createWidget({
                type: "bi.center_adapt",
                element: self.element,
                items: [left]
            })
        });
    }
});
$.shortcut('bi.float_center_adapt', BI.CenterVerticalAdaptLayout);