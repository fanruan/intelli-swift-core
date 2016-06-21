/**
 *自适应水平和垂直方向都居中容器
 * Created by GUY on 2016/5/18.
 *
 * @class BI.FlexboxCenterAdaptLayout
 * @extends BI.Layout
 */
BI.FlexboxCenterAdaptLayout = BI.inherit(BI.Layout, {
    _defaultConfig: function () {
        return BI.extend(BI.FlexboxCenterAdaptLayout.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-flexbox-center-adapt-layout"
        });
    },
    _init: function () {
        BI.FlexboxCenterAdaptLayout.superclass._init.apply(this, arguments);
        this.element.css({
            "display": "flex"
        });
        this.populate(this.options.items);
    },

    _addElement: function (i, item) {
        var o = this.options, w = BI.createWidget(item);
        w.element.css({"position": "relative", "margin": "auto"});
        this.addWidget(w);
        return w;
    },

    addItem: function (item) {
        BI.FlexboxCenterAdaptLayout.superclass.addItem.apply(this, arguments);
        var w = this._addElement(this.options.items.length, item);
        this.options.items.push(item);
        w.element.appendTo(this.element);
        return w;
    },

    resize: function () {
        console.log("flexbox_center_adapt布局不需要resize");
    },

    populate: function (items) {
        BI.FlexboxCenterAdaptLayout.superclass.populate.apply(this, arguments);
        var self = this;
        BI.each(items, function (i, item) {
            if (!!item) {
                self._addElement(i, item);
            }
        });
        this.render();
    }
});
$.shortcut('bi.flexbox_center_adapt', BI.FlexboxCenterAdaptLayout);