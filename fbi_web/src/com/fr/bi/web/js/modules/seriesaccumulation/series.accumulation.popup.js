/**
 * Created by fay on 2016/12/2.
 */
BI.SeriesAccumulationPopup = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function () {
        return BI.extend(BI.SeriesAccumulationPopup.superclass._defaultConfig.apply(this, arguments), {
        })
    },

    _init: function () {
        BI.SeriesAccumulationPopup.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function (north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Series_Accumulation_Setting"),
            height: 50,
            textAlign: "left",
            lgap: 10
        });
        return true;
    },

    rebuildCenter: function (center) {
        var o = this.options;
        this.seriesAccumulationPane = BI.createWidget({
            type: "bi.series_accumulation",
            element: center,
            dId: o.dId
        });
        return true;
    },

    populate: function () {
        this.seriesAccumulationPane.populate();
    },

    end: function () {
        this.fireEvent(BI.SeriesAccumulationPopup.EVENT_CHANGE, this.seriesAccumulationPane.getValue());
    }
});

BI.SeriesAccumulationPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.series_accumulation_popup", BI.SeriesAccumulationPopup);