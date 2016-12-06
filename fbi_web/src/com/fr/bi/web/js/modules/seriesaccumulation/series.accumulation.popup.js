/**
 * Created by fay on 2016/12/2.
 */
BI.SeriesAccumulationPanel = BI.inherit(BI.SeriesAccumulationPanel, {
    _defaultConfig: function () {
        return BI.extend(BI.DataLabelPopup.superclass._defaultConfig.apply(this, arguments), {
            width: 600,
            height: 500
        })
    },

    _init: function () {
        BI.DataLabelPopup.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function (north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Data_Label"),
            height: 50,
            textAlign: "left",
            lgap: 10
        });
        return true;
    },

    rebuildCenter: function (center) {
        var o = this.options;
        this.dataLabelPane = BI.createWidget({
            type: "bi.data_label",
            element: center,
            dId: o.dId
        });
        return true;
    },

    populate: function () {
        this.dataLabelPane.populate();
    },

    end: function () {
        this.fireEvent(BI.DataLabelPopup.EVENT_CHANGE, this.dataLabelPane.getValue());
    }
});

BI.SeriesAccumulationPanel.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.series_accumulation_panel", BI.SeriesAccumulationPanel);