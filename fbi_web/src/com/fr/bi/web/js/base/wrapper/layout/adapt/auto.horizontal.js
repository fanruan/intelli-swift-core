/**
 * 水平方向居中自适应容器
 * @class BI.HorizontalAutoLayout
 * @extends BI.Layout
 */
BI.HorizontalAutoLayout = BI.inherit(BI.Layout, {
    _defaultConfig: function () {
        return BI.extend(BI.HorizontalAutoLayout.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-horizon-auto-layout",
            hgap: 0,
            lgap: 0,
            rgap: 0,
            vgap: 0,
            tgap: 0,
            bgap: 0
        });
    },

    _init: function () {
        BI.HorizontalAutoLayout.superclass._init.apply(this, arguments);
        this.populate(this.options.items);
    },

    _addElement: function (item) {
        var o = this.options, w = BI.createWidget(item);
        w.element.css({
            "position": "relative",
            "margin": "0px auto"
        });
        if (o.hgap + o.lgap + (item.lgap || 0) !== 0) {
            w.element.css({
                "margin-left": o.hgap + o.lgap + (item.lgap || 0) + "px"
            })
        }
        if (o.hgap + o.rgap + (item.rgap || 0) !== 0) {
            w.element.css({
                "margin-right": o.hgap + o.rgap + (item.rgap || 0) + "px"
            })
        }
        if (o.vgap + o.tgap + (item.tgap || 0) !== 0) {
            w.element.css({
                "margin-top": o.vgap + o.tgap + (item.tgap || 0) + "px"
            })
        }
        if (o.vgap + o.bgap + (item.bgap || 0) !== 0) {
            w.element.css({
                "margin-bottom": o.vgap + o.bgap + (item.bgap || 0) + "px"
            })
        }
        this.addWidget(w);
        return w;
    },

    resize: function () {
        console.log("horizontal_adapt布局不需要resize");
    },

    addItem: function (item) {
        BI.HorizontalAutoLayout.superclass.addItem.apply(this, arguments);
        var w = this._addElement(item);
        this.options.items.push(item);
        w.element.appendTo(this.element);
        return w;
    },

    populate: function (items) {
        BI.HorizontalAutoLayout.superclass.populate.apply(this, arguments);
        var self = this;
        BI.each(items, function (i, item) {
            if (!!item) {
                self._addElement(item);
            }
        });
        this.render();
    }
});
$.shortcut('bi.horizontal_auto', BI.HorizontalAutoLayout);