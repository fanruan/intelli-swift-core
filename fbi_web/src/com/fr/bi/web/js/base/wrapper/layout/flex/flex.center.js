/**
 *自适应水平和垂直方向都居中容器
 * Created by GUY on 2016/12/2.
 *
 * @class BI.FlexCenterLayout
 * @extends BI.Layout
 */
BI.FlexCenterLayout = BI.inherit(BI.Layout, {
    _defaultConfig: function () {
        return BI.extend(BI.FlexCenterLayout.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-flex-center-layout"
        });
    },
    _init: function () {
        BI.FlexCenterLayout.superclass._init.apply(this, arguments);
        this.wrapper = $("<div>").addClass("flex-center-layout-wrapper clearfix").appendTo(this.element);
        this.populate(this.options.items);
    },

    _addElement: function (i, item) {
        var o = this.options, w = BI.createWidget(item);
        w.element.css({"position": "relative", "margin": "auto"}).appendTo(this.wrapper);
        return w;
    },

    addItem: function (item) {
        BI.FlexCenterLayout.superclass.addItem.apply(this, arguments);
        var w = this._addElement(this.options.items.length, item);
        this.options.items.push(item);
        w.element.appendTo(this.wrapper);
        return w;
    },

    resize: function () {
        console.log("flex_center布局不需要resize");
    },

    populate: function (items) {
        BI.FlexCenterLayout.superclass.populate.apply(this, arguments);
        var self = this;
        BI.each(items, function (i, item) {
            if (!!item) {
                self._addElement(i, item);
            }
        });
    }
});
$.shortcut('bi.flex_center', BI.FlexCenterLayout);