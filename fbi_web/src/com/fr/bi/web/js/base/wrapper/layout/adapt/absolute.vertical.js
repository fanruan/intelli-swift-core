/**
 * absolute实现的居中布局
 * @class BI.AbsoluteVerticalLayout
 * @extends BI.Layout
 */
BI.AbsoluteVerticalLayout = BI.inherit(BI.Layout, {
    _defaultConfig: function () {
        return BI.extend(BI.AbsoluteVerticalLayout.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-absolute-vertical-layout",
            hgap: 0,
            lgap: 0,
            rgap: 0,
            vgap: 0,
            tgap: 0,
            bgap: 0
        });
    },

    _init: function () {
        BI.AbsoluteVerticalLayout.superclass._init.apply(this, arguments);
        this.populate(this.options.items);
    },

    _addElement: function (item) {
        var o = this.options, w = BI.createWidget(item);
        w.element.css({
            "position": "absolute",
            "left": item.lgap,
            "right": item.rgap,
            "top": o.vgap + o.tgap + (item.tgap || 0),
            "bottom": o.vgap + o.bgap + (item.bgap || 0),
            "margin": "auto"
        });
        if (o.hgap + o.lgap + (item.lgap || 0) !== 0) {
            w.element.css("left", o.hgap + o.lgap + (item.lgap || 0));
        }
        if (o.hgap + o.rgap + (item.rgap || 0) !== 0) {
            w.element.css("right", o.hgap + o.rgap + (item.rgap || 0));
        }
        this.addWidget(w);
        return w;
    },

    resize: function () {
        console.log("absolute_vertical_adapt布局不需要resize");
    },

    addItem: function (item) {
        BI.AbsoluteVerticalLayout.superclass.addItem.apply(this, arguments);
        var w = this._addElement(item);
        this.options.items.push(item);
        w.element.appendTo(this.element);
        return w;
    },

    populate: function (items) {
        BI.AbsoluteVerticalLayout.superclass.populate.apply(this, arguments);
        var self = this;
        BI.each(items, function (i, item) {
            if (!!item) {
                self._addElement(item);
            }
        });
        this.render();
    }
});
$.shortcut('bi.absolute_vertical_adapt', BI.AbsoluteVerticalLayout);