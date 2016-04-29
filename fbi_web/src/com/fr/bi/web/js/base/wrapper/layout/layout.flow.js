/**
 * 靠左对齐的自由浮动布局
 * @class BI.FloatLeftLayout
 * @extends BI.Layout
 *
 * @cfg {JSON} options 配置属性
 * @cfg {Number} [hgap=0] 水平间隙
 * @cfg {Number} [vgap=0] 垂直间隙
 */
BI.FloatLeftLayout = BI.inherit(BI.Layout, {
    _defaultConfig: function () {
        return BI.extend(BI.FloatLeftLayout.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-float-left-layout clearfix",
            hgap: 0,
            vgap: 0,
            lgap: 0,
            rgap: 0,
            tgap: 0,
            bgap: 0
        });
    },
    _init: function () {
        BI.FloatLeftLayout.superclass._init.apply(this, arguments);
        this.populate(this.options.items);
    },

    _addElement: function (i, item) {
        var o = this.options;
        if (!this.hasWidget(this.getName() + i)) {
            var w = BI.createWidget(item);
            this.addWidget(this.getName() + i, w);
        } else {
            var w = this.getWidgetByName(this.getName() + i);
        }
        w.element.css({"position": "relative", "float": "left"});
        if (BI.isNotNull(item.left)) {
            w.element.css({"left": item.left});
        }
        if (BI.isNotNull(item.right)) {
            w.element.css({"right": item.right});
        }
        if (BI.isNotNull(item.top)) {
            w.element.css({"top": item.top});
        }
        if ((item.lgap || 0) + o.hgap + o.lgap !== 0) {
            w.element.css("margin-left", (item.lgap || 0) + o.hgap + o.lgap);
        }
        if ((item.rgap || 0) + o.hgap + o.rgap !== 0) {
            w.element.css("margin-right", (item.rgap || 0) + o.hgap + o.rgap);
        }
        if ((item.tgap || 0) + o.vgap + o.tgap !== 0) {
            w.element.css("margin-top", (item.tgap || 0) + o.vgap + o.tgap);
        }
        if ((item.bgap || 0) + o.vgap + o.bgap !== 0) {
            w.element.css("margin-bottom", (item.bgap || 0) + o.vgap + o.bgap);
        }
        return w;
    },

    addItem: function (item) {
        BI.FloatLeftLayout.superclass.addItem.apply(this, arguments);
        var w = this._addElement(this.options.items.length, item);
        this.options.items.push(item);
        w.element.appendTo(this.element);
        return w;
    },

    resize: function () {
        this.stroke(this.options.items);
    },

    stroke: function (items) {
        var self = this;
        BI.each(items, function (i, item) {
            if (!!item) {
                self._addElement(i, item);
            }
        });
    },

    populate: function (items) {
        BI.FloatLeftLayout.superclass.populate.apply(this, arguments);
        this.stroke(items);
        this.render();
    }
});
$.shortcut('bi.left', BI.FloatLeftLayout);

/**
 * 靠右对齐的自由浮动布局
 * @class BI.FloatRightLayout
 * @extends BI.Layout
 *
 * @cfg {JSON} options 配置属性
 * @cfg {Number} [hgap=0] 水平间隙
 * @cfg {Number} [vgap=0] 垂直间隙
 */
BI.FloatRightLayout = BI.inherit(BI.Layout, {
    _defaultConfig: function () {
        return BI.extend(BI.FloatRightLayout.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-float-right-layout clearfix",
            hgap: 0,
            vgap: 0,
            lgap: 0,
            rgap: 0,
            tgap: 0,
            bgap: 0
        });
    },
    _init: function () {
        BI.FloatRightLayout.superclass._init.apply(this, arguments);
        this.populate(this.options.items);
    },

    _addElement: function (i, item) {
        var o = this.options;
        if (!this.hasWidget(this.getName() + i)) {
            var w = BI.createWidget(item);
            this.addWidget(this.getName() + i, w);
        } else {
            var w = this.getWidgetByName(this.getName() + i);
        }
        w.element.css({"position": "relative", "float": "right"});
        if (BI.isNotNull(item.left)) {
            w.element.css({"left": item.left});
        }
        if (BI.isNotNull(item.right)) {
            w.element.css({"right": item.right});
        }
        if (BI.isNotNull(item.top)) {
            w.element.css({"top": item.top});
        }
        if ((item.lgap || 0) + o.hgap + o.lgap !== 0) {
            w.element.css("margin-left", (item.lgap || 0) + o.hgap + o.lgap);
        }
        if ((item.rgap || 0) + o.hgap + o.rgap !== 0) {
            w.element.css("margin-right", (item.rgap || 0) + o.hgap + o.rgap);
        }
        if ((item.tgap || 0) + o.vgap + o.tgap !== 0) {
            w.element.css("margin-top", (item.tgap || 0) + o.vgap + o.tgap);
        }
        if ((item.bgap || 0) + o.vgap + o.bgap !== 0) {
            w.element.css("margin-bottom", (item.bgap || 0) + o.vgap + o.bgap);
        }
        return w;
    },

    resize: function () {
        this.stroke(this.options.items);
    },

    addItem: function (item) {
        BI.FloatRightLayout.superclass.addItem.apply(this, arguments);
        var w = this._addElement(this.options.items.length, item);
        this.options.items.push(item);
        w.element.appendTo(this.element);
    },

    stroke: function (items) {
        var self = this;
        BI.each(items, function (i, item) {
            if (!!item) {
                self._addElement(i, item);
            }
        });
    },

    populate: function (items) {
        BI.FloatRightLayout.superclass.populate.apply(this, arguments);
        this.stroke(items);
        this.render();
    }
});
$.shortcut('bi.right', BI.FloatRightLayout);