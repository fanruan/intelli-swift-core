/**
 * Created by fay on 2016/12/2.
 */
BI.SeriesAccumulation = BI.inherit(BI.Widget, {
    _constant: {
        ADD_BUTTON_HEIGHT: 26
    },

    _defaultConfig: function () {
        return BI.extend(BI.SeriesAccumulation.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-series-accumulation",
            dId: ""
        })
    },

    _init: function () {
        BI.SeriesAccumulation.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var addButton = BI.createWidget({
            type: "bi.button",
            cls: "button-ignore",
            text: BI.i18nText("BI-Add_Accumulation_Group"),
            height: this._constant.ADD_BUTTON_HEIGHT,
            handler: function () {
                self.accumulationGroup.createItem();
            }
        });
        this.accumulationGroup = BI.createWidget({
            type: "bi.accumulation_group",
        });
        BI.createWidget({
            type: "bi.vtape",
            items: [{
                el: {
                    type: "bi.right",
                    items: [addButton]
                },
                height: 30
            }, {
                el: this.accumulationGroup
            }],
            element: this.element
        });
    },

    populate: function () {
        var self = this, o = this.options;
        var id = o.dId;
        var items = BI.Utils.getSeriesAccumulationByID(o.dId);
        if (BI.isNotEmptyArray(items)) {
            self.accumulationGroup.populate(items);
        } else {
            BI.Utils.getDataByDimensionID(id, function (allDate) {
                self.accumulationGroup.populate([{
                    index: 0,
                    items: allDate
                }]);
            })
        }
    },

    getValue: function () {
        return this.accumulationGroup.getValue();
    }
});

$.shortcut("bi.series_accumulation", BI.SeriesAccumulation);