/**
 *自适应水平和垂直方向都居中容器
 * Created by GUY on 2016/12/2.
 *
 * @class BI.FlexVerticalCenter
 * @extends BI.Layout
 */
BI.FlexVerticalCenter = BI.inherit(BI.Layout, {
    _defaultConfig: function () {
        return BI.extend(BI.FlexVerticalCenter.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-flex-vertical-center",
            columnSize: [],
            hgap: 0,
            vgap: 0,
            lgap: 0,
            rgap: 0,
            tgap: 0,
            bgap: 0
        });
    },
    _init: function () {
        BI.FlexVerticalCenter.superclass._init.apply(this, arguments);
        var o = this.options;
        this.wrapper = $("<div>").addClass("flex-vertical-center-wrapper clearfix").appendTo(this.element);
        this.populate(this.options.items);
    },

    _addElement: function (i, item) {
        var o = this.options, w = BI.createWidget(item);
        w.element.css({"position": "relative"}).appendTo(this.wrapper);
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

    addItem: function (item) {
        BI.FlexVerticalCenter.superclass.addItem.apply(this, arguments);
        var w = this._addElement(this.options.items.length, item);
        this.options.items.push(item);
        w.element.appendTo(this.wrapper);
        return w;
    },

    resize: function () {
        console.log("flex_vertical_center布局不需要resize");
    },

    populate: function (items) {
        BI.FlexVerticalCenter.superclass.populate.apply(this, arguments);
        var self = this;
        this.wrapper.empty();
        BI.each(items, function (i, item) {
            if (!!item) {
                self._addElement(i, item);
            }
        });
    }
});
$.shortcut('bi.flex_vertical_center', BI.FlexVerticalCenter);