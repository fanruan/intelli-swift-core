/**
 * 组件or控件
 *
 * Created by GUY on 2016/3/8.
 * @class BI.FitWidget
 * @extends BI.BasicButton
 */
BI.FitWidget = BI.inherit(BI.BasicButton, {

    _defaultConfig: function () {
        return BI.extend(BI.FitWidget.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-fit-widget",
            widgetCreator: BI.emptyFn
        });
    },

    _init: function () {
        BI.FitWidget.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.draggable = BI.createWidget({
            type: "bi.layout",
            cls: "fit-widget-draggable",
            height: 12
        });
        this.widget = o.widgetCreator();
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            scrollable: false,
            items: [{
                el: this.widget,
                top: 5,
                left: 5,
                right: 5,
                bottom: 5
            }, {
                el: this.draggable,
                left: 0,
                top: 0,
                right: 0
            }]
        })
    },

    getDraggable: function () {
        return this.draggable;
    }
});
$.shortcut('bi.fit_widget', BI.FitWidget);