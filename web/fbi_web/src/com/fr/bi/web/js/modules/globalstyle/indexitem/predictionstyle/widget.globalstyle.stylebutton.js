/**
 * Created by zcf on 2016/9/8.
 */
BI.GlobalStyleStyleButton = BI.inherit(BI.Single, {

    _defaultConfig: function () {
        var conf = BI.GlobalStyleStyleButton.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || ""),
            value: {}
        })
    },

    _init: function () {
        BI.GlobalStyleStyleButton.superclass._init.apply(this, arguments);
        var o = this.options;

        var mainBackgroundLayout = this._createLayout(70, 110);
        var widgetBackgroundLayout = this._createLayout(50, 90);
        var titleBackgroundLayout = this._createLayout(10, 90);
        var chartColorOne = this._createLayout(6, 40);
        var chartColorTwo = this._createLayout(6, 70);
        var chartColorThree = this._createLayout(6, 50);

        var mainBackground = (o.value.mainBackground.type === 1) ? o.value.mainBackground.value : "#f3f3f3";
        var widgetBackground = (o.value.widgetBackground.type === 1) ? o.value.widgetBackground.value : "#ffffff";
        var titleBackground = (o.value.titleBackground.type === 1) ? o.value.titleBackground.value : "#ffffff";
        mainBackgroundLayout.element.css("background-color", mainBackground);
        widgetBackgroundLayout.element.css("background-color", widgetBackground);
        titleBackgroundLayout.element.css("background-color", titleBackground);
        chartColorOne.element.css("background-color", o.value.chartColor[0]);
        chartColorTwo.element.css("background-color", o.value.chartColor[1]);
        chartColorThree.element.css("background-color", o.value.chartColor[2]);

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [
                this._createItem(mainBackgroundLayout, 0, 0),
                this._createItem(widgetBackgroundLayout, 10, 10),
                this._createItem(titleBackgroundLayout, 10, 10),
                this._createItem(chartColorOne, 27, 20),
                this._createItem(chartColorTwo, 37, 20),
                this._createItem(chartColorThree, 47, 20)
            ],
            height: 70,
            width: 110
        });
    },
    _createItem: function (el, top, left) {
        return {
            el: el,
            top: top,
            left: left
        }
    },
    _createLayout: function (height, width) {
        return BI.createWidget({
            type: "bi.layout",
            height: height,
            width: width
        })
    }
});
BI.GlobalStyleStyleButton.EVENT_CHANGE = "BI.GlobalStyleStyleButton.EVENT_CHANGE";
$.shortcut("bi.global_style_style_button", BI.GlobalStyleStyleButton);