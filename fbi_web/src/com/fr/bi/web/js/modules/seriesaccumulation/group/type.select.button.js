/**
 * Created by fay on 2016/12/14.
 */
BI.SelectTypeButton = BI.inherit(BI.Widget, {
    _constant: {
        SELECT_ITEMS: [{
            text: BI.i18nText("BI-Stacked_Chart"),
            value: 4
        }, {
            text: BI.i18nText("BI-Accumulate_Area") + "(折线)",
            value: BICst.CHART_SHAPE.NORMAL
        }, {
            text: BI.i18nText("BI-Accumulate_Area") + "(曲线)",
            value: BICst.CHART_SHAPE.CURVE
        }, {
            text: BI.i18nText("BI-Accumulate_Area") + "(直角折线)",
            value: BICst.CHART_SHAPE.RIGHT_ANGLE
        }]
    },
    _defaultConfig: function () {
        return BI.extend(BI.SelectTypeButton.superclass._defaultConfig.apply(this, arguments), {
            cls: "bi-select-type"
        })
    },

    _init: function () {
        BI.SelectTypeButton.superclass._init.apply(this, arguments);
        var self = this;
        var typeIcon = BI.createWidget({
            type: "bi.icon_button",
            cls: "drag-axis-accu-icon",
            iconWidth: 24,
            iconHeight: 24
        })

        var triggerIcon = BI.createWidget({
            type: "bi.trigger_icon_button",
            cls: "trigger-icon"
        });

        var trigger = BI.createWidget({
            type: "bi.horizontal",
            items: [{
                el: typeIcon,
                lgap: 10,
                rgap: 10
            },{
                el: triggerIcon,
                rgap: 5
            }]
        });

        var combo = BI.createWidget({
            type: "bi.combo",
            element: this.element,
            el: trigger,
            popup: {
                el: {
                    items: BI.createItems(this._constant.SELECT_ITEMS, {
                        type: "bi.single_select_item",
                        height: 25,
                        handler: function () {
                            combo.hideView();
                        }
                    }),
                    layouts: [{
                        type: "bi.vertical"
                    }]
                }
            }
        });

        combo.on(BI.Combo.EVENT_CHANGE, function () {
            self._refreshView();
        })
    },

    _createTypeIcons: function () {

    },

    _refreshView: function () {

    }
});
$.shortcut("bi.select_type_button", BI.SelectTypeButton);