/**
 * Created by fay on 2016/12/14.
 */
BI.SelectAccumulateTypeButton = BI.inherit(BI.Widget, {
    _constant: {
        SELECT_ITEMS: [{
            text: BI.i18nText("BI-Stacked_Chart"),
            title: BI.i18nText("BI-Stacked_Chart"),
            value: BICst.ACCUMULATE_TYPE.COLUMN
        }, {
            text: BI.i18nText("BI-Accumulate_Area") + "(" + BI.i18nText("BI-Polyline") + ")",
            title: BI.i18nText("BI-Accumulate_Area") + "(" + BI.i18nText("BI-Polyline") + ")",
            value: BICst.ACCUMULATE_TYPE.AREA_NORMAL
        }, {
            text: BI.i18nText("BI-Accumulate_Area") + "(" + BI.i18nText("BI-Curve") + ")",
            title: BI.i18nText("BI-Accumulate_Area") + "(" + BI.i18nText("BI-Curve") + ")",
            value: BICst.ACCUMULATE_TYPE.AREA_CURVE
        }, {
            text: BI.i18nText("BI-Accumulate_Area") + "(" + BI.i18nText("BI-Right_Angled_Polyline") + ")",
            title: BI.i18nText("BI-Accumulate_Area") + "(" + BI.i18nText("BI-Right_Angled_Polyline") + ")",
            value: BICst.ACCUMULATE_TYPE.AREA_RIGHT_ANGLE
        }]
    },
    _defaultConfig: function () {
        return BI.extend(BI.SelectAccumulateTypeButton.superclass._defaultConfig.apply(this, arguments), {
            cls: "bi-select-type"
        })
    },

    _init: function () {
        BI.SelectAccumulateTypeButton.superclass._init.apply(this, arguments);
        var self = this;
        this.typeIcon = BI.createWidget({
            type: "bi.button_group",
            width: 24,
            height: 24
        })

        var triggerIcon = BI.createWidget({
            type: "bi.trigger_icon_button",
            cls: "trigger-icon"
        });

        var trigger = BI.createWidget({
            type: "bi.horizontal",
            items: [{
                el: this.typeIcon,
                lgap: 10,
                rgap: 10
            }, {
                el: triggerIcon,
                rgap: 5
            }]
        });

        this.combo = BI.createWidget({
            type: "bi.combo",
            element: this.element,
            el: trigger,
            popup: {
                el: {
                    items: BI.createItems(this._constant.SELECT_ITEMS, {
                        type: "bi.single_select_item",
                        height: 25,
                        handler: function () {
                            self.combo.hideView();
                        }
                    }),
                    layouts: [{
                        type: "bi.vertical"
                    }]
                }
            }
        });
        this.typeIcon.populate([this._createTypeIcons()]);
        this.combo.on(BI.Combo.EVENT_CHANGE, function () {
            self._refreshView();
        });
    },

    _createTypeIcons: function () {
        var className = "";
        switch (this.combo.getValue()[0]) {
            case BICst.ACCUMULATE_TYPE.AREA_NORMAL:
                className = "area-chart-style-broken-icon";
                break;
            case BICst.ACCUMULATE_TYPE.AREA_CURVE:
                className = "area-chart-style-curve-icon";
                break;
            case BICst.ACCUMULATE_TYPE.AREA_RIGHT_ANGLE:
                className = "area-chart-style-vertical-icon";
                break;
            default:
                className = "drag-axis-accu-icon"
        }
        return BI.createWidget({
            type: "bi.icon_button",
            cls: className,
            iconWidth: 24,
            iconHeight: 24
        })
    },

    _refreshView: function () {
        this.typeIcon.populate([this._createTypeIcons()]);
    },

    setValue: function (v) {
        this.combo.setValue(v);
        this._refreshView();
    },

    getValue: function () {
        var v = this.combo.getValue();
        return v[0] ? v[0] : BICst.ACCUMULATE_TYPE.COLUMN;
    }
});

$.shortcut("bi.select_accumulate_type_button", BI.SelectAccumulateTypeButton);