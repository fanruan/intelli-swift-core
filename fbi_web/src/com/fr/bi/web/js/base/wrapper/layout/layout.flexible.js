/**
 * 自适应布局
 * @class BI.FlexibleLayout
 * @extends BI.Layout
 */
BI.FlexibleLayout = BI.inherit(BI.Layout, {
    _defaultConfig: function () {
        return BI.extend(BI.FlexibleLayout.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-flexible-layout"
        });
    },

    _init: function () {
        BI.FlexibleLayout.superclass._init.apply(this, arguments);
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
        var left = 0, right = 0, top = 0, bottom = 0;
        if (BI.isNotNull(item.left)) {
            w.element.css({"left": item.left});
            left += item.left;
        }
        if (BI.isNotNull(item.right)) {
            w.element.css({"right": item.right});
            right += item.right;
        }
        if (BI.isNotNull(item.top)) {
            w.element.css({"top": item.top});
            top += item.top;
        }
        if (BI.isNotNull(item.bottom)) {
            w.element.css({"bottom": item.bottom});
            bottom += item.bottom;
        }

        if (BI.isNotNull(o.hgap)) {
            left += o.hgap;
            w.element.css({"left": left});
            right += o.hgap;
            w.element.css({"right": right});
        }
        if (BI.isNotNull(o.vgap)) {
            top += o.vgap;
            w.element.css({"top": top});
            bottom += o.vgap;
            w.element.css({"bottom": bottom});
        }

        if (BI.isNotNull(o.lgap)) {
            left += o.lgap;
            w.element.css({"left": left});
        }
        if (BI.isNotNull(o.rgap)) {
            right += o.rgap;
            w.element.css({"right": right});
        }
        if (BI.isNotNull(o.tgap)) {
            top += o.tgap;
            w.element.css({"top": top});
        }
        if (BI.isNotNull(o.bgap)) {
            bottom += o.bgap;
            w.element.css({"bottom": bottom});
        }


        if (BI.isNotNull(item.width)) {
            w.element.css({"width": item.width});
        }
        if (BI.isNotNull(item.height)) {
            w.element.css({"height": item.height});
        }
        return w;
    },

    resize: function () {
        this.stroke(this.options.items);
    },

    addItem: function (item) {
        BI.FlexibleLayout.superclass.addItem.apply(this, arguments);
        var w = this._addElement(this.options.items.length, item);
        this.options.items.push(item);
        w.element.appendTo(this.element);
        return w;
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
        BI.FlexibleLayout.superclass.populate.apply(this, arguments);
        this.stroke(items);
        this.render();
    }
});