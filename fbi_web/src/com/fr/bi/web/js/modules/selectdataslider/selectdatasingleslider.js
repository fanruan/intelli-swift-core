/**
 * Created by zcf on 2016/10/10.
 */
BI.SelectDataSingleSlider = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectDataSingleSlider.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-data-single-slider",
            wId: ""
        });
    },

    _init: function () {
        BI.SelectDataSingleSlider.superclass._init.apply(this, arguments);
        var self = this;

        this.widget = BI.createWidget({
            type: "bi.single_slider",
            element: this.element
        });
        this.widget.on(BI.SingleSlider.EVENT_CHANGE, function () {
            self.fireEvent(BI.SelectDataSingleSlider.ENENT_CHANGE)
        })
    },

    getValue: function () {
        var value = this.widget.getValue();
        return {
            closemax: true,
            closemin: true,
            max: value,
            min: ""
        }
    },

    populate: function () {
        var self = this, o = this.options;
        var dimensions = BI.Utils.getAllDimDimensionIDs(o.wId);
        var widgetValue = BI.Utils.getWidgetValueByID(o.wId) || {};
        var value = widgetValue.max;
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
BI.SelectDataSingleSlider.ENENT_CHANGE = "SelectDataSingleSlider.EVENT_CHANGE";
$.shortcut("bi.select_data_single_slider", BI.SelectDataSingleSlider);