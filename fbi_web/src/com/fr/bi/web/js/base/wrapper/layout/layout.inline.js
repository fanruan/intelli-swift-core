/**
 * 内联布局
 * @class BI.InlineLayout
 * @extends BI.Layout
 *
 * @cfg {JSON} options 配置属性
 * @cfg {Number} [hgap=0] 水平间隙
 * @cfg {Number} [vgap=0] 垂直间隙
 */
BI.InlineLayout = BI.inherit(BI.Layout, {
    _defaultConfig: function () {
        return BI.extend(BI.InlineLayout.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-inline-layout",
            hgap: 0,
            vgap: 0,
            lgap: 0,
            rgap: 0,
            tgap: 0,
            bgap: 0
        });
    },
    _init: function () {
        BI.InlineLayout.superclass._init.apply(this, arguments);
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
        w.element.css({"position": "relative", display: "inline-block", "*display": "inline", "*zoom": 1});
        if (o.hgap + o.lgap + (item.lgap || 0) > 0) {
            w.element.css({
                "margin-left": o.hgap + o.lgap + (item.lgap || 0) + "px"
            })
        }
        if (o.hgap + o.rgap + (item.rgap || 0) > 0) {
            w.element.css({
                "margin-right": o.hgap + o.rgap + (item.rgap || 0) + "px"
            })
        }
        if (o.vgap + o.tgap + (item.tgap || 0) > 0) {
            w.element.css({
                "margin-top": o.vgap + o.tgap + (item.tgap || 0) + "px"
            })
        }
        if (o.vgap + o.bgap + (item.bgap || 0) > 0) {
            w.element.css({
                "margin-bottom": o.vgap + o.bgap + (item.bgap || 0) + "px"
            })
        }
        return w;
    },

    resize: function(){
        this.stroke(this.options.items);
    },

    addItem: function (item) {
        BI.InlineLayout.superclass.addItem.apply(this, arguments);
        var w = this._addElement(this.options.items.length, item);
        this.options.items.push(item);
        w.element.appendTo(this.element);
        return w;
    },

    stroke: function(items){
        var self = this;
        BI.each(items, function (i, item) {
            if (!!item) {
                self._addElement(i, item);
            }
        });
    },

    populate: function (items) {
        BI.InlineLayout.superclass.populate.apply(this, arguments);
        this.stroke(items);
        this.render();
    }
});
$.shortcut('bi.inline', BI.InlineLayout);