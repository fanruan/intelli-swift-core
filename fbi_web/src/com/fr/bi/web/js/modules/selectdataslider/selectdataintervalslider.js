/**
 * Created by zcf on 2016/10/11.
 */
BI.SelectDataIntervalSlider = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectDataIntervalSlider.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-data-interval-slider",
            wId: ""
        });
    },
    _init: function () {
        BI.SelectDataIntervalSlider.superclass._init.apply(this, arguments);
        var self = this;

        this.widget = BI.createWidget({
            type: "bi.interval_slider",
            element: this.element
        });
        this.widget.on(BI.IntervalSlider.EVENT_CHANGE, function () {
            self.fireEvent(BI.SelectDataIntervalSlider.ENENT_CHANGE)
        })
    },

    getValue: function () {
        var value = this.widget.getValue();
        return {
            closemax: true,
            closemin: true,
            max: value.max,
            min: value.min
        }
    },

    populate: function () {
        var self = this, o = this.options;
        var dimensions = BI.Utils.getAllDimDimensionIDs(o.wId);
        var widgetValue = BI.Utils.getWidgetValueByID(o.wId) || {};
        var value = {min: widgetValue.min, max: widgetValue.max};
        if (dimensions.length === 0) {
            this.widget.reset()
        } else {
            BI.Utils.getWidgetDataByID(o.wId, {
                success: function (jsonData) {
                    if (BI.isNotEmptyObject(jsonData)) {
                        self.widget.setMinAndMax(jsonData);
                        self.widget.setValue(value);
                        self.widget.populate();
                    }
                }
            })
        }
    }
});
BI.SelectDataIntervalSlider.ENENT_CHANGE = "SelectDataIntervalSlider.EVENT_CHANGE";
$.shortcut("bi.select_data_interval_slider", BI.SelectDataIntervalSlider);