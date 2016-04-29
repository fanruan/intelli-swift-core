/**
 * absolute实现的居中布局
 * @class BI.AbsoluteCenterLayout
 * @extends BI.Layout
 */
BI.AbsoluteCenterLayout = BI.inherit(BI.Layout, {
    _defaultConfig: function () {
        return BI.extend(BI.AbsoluteCenterLayout.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-absolute-center-layout",
            hgap: 0,
            lgap: 0,
            rgap: 0,
            vgap: 0,
            tgap: 0,
            bgap: 0
        });
    },

    _init: function () {
        BI.AbsoluteCenterLayout.superclass._init.apply(this, arguments);
        this.populate(this.options.items);
    },

    _addElement: function (item) {
        var o = this.options, w = BI.createWidget(item);
        w.element.css({
            "position": "absolute",
            "left": o.hgap + o.lgap + (item.lgap || 0),
            "right": o.hgap + o.rgap + (item.rgap || 0),
            "top": o.vgap + o.tgap + (item.tgap || 0),
            "bottom": o.vgap + o.bgap + (item.bgap || 0),
            "margin": "auto"
        });
        this.addWidget(w);
        return w;
    },

    resize: function () {
        console.log("absolute_center_adapt布局不需要resize");
    },

    addItem: function (item) {
        BI.AbsoluteCenterLayout.superclass.addItem.apply(this, arguments);
        var w = this._addElement(item);
        this.options.items.push(item);
        w.element.appendTo(this.element);
        return w;
    },

    populate: function (items) {
        BI.AbsoluteCenterLayout.superclass.populate.apply(this, arguments);
        var self = this;
        BI.each(items, function (i, item) {
            if (!!item) {
                self._addElement(item);
            }
        });
        this.render();
    }
});
$.shortcut('bi.absolute_center_adapt', BI.AbsoluteCenterLayout);