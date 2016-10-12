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
    _getMinAndMaxFromData: function (jsonData) {
        var dataArray = jsonData.data.c;
        var minString = dataArray[0].n;
        var maxString = dataArray[dataArray.length - 1].n;
        var minArray = minString.split("-");
        var maxArray = maxString.split("-");
        var min = BI.parseFloat(minArray[0]);
        var max = BI.parseFloat(maxArray[1]);
        return [min, max];
    },
    getValue: function () {
        var value = this.widget.getValue();
        return {
            closemax: false,
            closemin: false,
            max: value,
            min: ""
        }
    },
    setValue: function () {
        var o = this.options;
        var widgetValue = BI.Utils.getWidgetValueByID(o.wId) || {};
        this.widget.setValue(widgetValue.max);
    },
    populate: function () {
        var self = this, o = this.options;
        var dimensions = BI.Utils.getAllDimDimensionIDs(o.wId);
        var widgetValue = BI.Utils.getWidgetValueByID(o.wId) || {};
        var value = widgetValue.max;
        if (dimensions.length == 0) {
            this.widget.reset()
        } else {
            BI.Utils.getWidgetDataByID(o.wId, function (jsonData) {
                var minAndMax = self._getMinAndMaxFromData(jsonData);
                self.widget.populate(minAndMax[0], minAndMax[1], value);
            })
        }
    }
});
BI.SelectDataSingleSlider.ENENT_CHANGE = "SelectDataSingleSlider.EVENT_CHANGE";
$.shortcut("bi.select_data_single_slider", BI.SelectDataSingleSlider);