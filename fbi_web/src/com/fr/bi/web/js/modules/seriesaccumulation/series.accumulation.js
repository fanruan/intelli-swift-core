/**
 * Created by fay on 2016/12/2.
 */
BI.SeriesAccumulation = BI.inherit(BI.Widget, {
    _constant: {
        ADD_BUTTON_HEIGHT: 26,
        MAX_LENGTH: 500
    },

    _defaultConfig: function () {
        return BI.extend(BI.SeriesAccumulation.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-series-accumulation",
            dId: ""
        })
    },

    _init: function () {
        BI.SeriesAccumulation.superclass._init.apply(this, arguments);
    },

    _createGroups: function () {
        var self = this, o = this.options;
        this.addButton = BI.createWidget({
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
            dId: o.dId
        });
        BI.createWidget({
            type: "bi.vtape",
            items: [{
                el: {
                    type: "bi.right",
                    items: [this.addButton]
                },
                height: 30
            }, {
                el: this.accumulationGroup
            }],
            element: this.element
        });
    },

    _createMask: function () {
        BI.createWidget({
            type: "bi.flex_center",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Unsupport_Too_Many_Groups")
            }],
            element: this.element
        })
    },

    populate: function () {
        var self = this, o = this.options;
        var id = o.dId;
        var accumulationObj = BI.Utils.getSeriesAccumulationByDimensionID(o.dId);
        var items = accumulationObj.items;
        BI.Utils.getDataByDimensionID(id, function (allData) {
            var seriesGroup = BI.Utils.getDimensionGroupByID(id);
            if (BI.isNotNull(seriesGroup)) {
                var convertData = [];
                BI.each(allData, function (idx, data) {
                    convertData.push(getFormatDateText(seriesGroup.type, data))
                });
                allData = convertData;
            }
            if(allData.length <= self._constant.MAX_LENGTH) {
                self._createGroups();
                BI.each(items, function (idx, item) {
                    items[idx].items = BI.filter(item.items, function (id, value) {
                        return BI.contains(allData, value);
                    })
                })
                self.accumulationGroup.populate(items, allData);
            } else {
                self._createMask();
            }
        })

        function getFormatDateText(type, text){
            switch (type) {
                case BICst.GROUP.S:
                    text = BICst.FULL_QUARTER_NAMES[text];
                    break;
                case BICst.GROUP.M:
                    text = BICst.FULL_MONTH_NAMES[text];
                    break;
                case BICst.GROUP.W:
                    text = BICst.FULL_WEEK_NAMES[text];
                    break;
                case BICst.GROUP.YMD:
                    var date = new Date(BI.parseInt(text));
                    text = date.print("%Y-%X-%d");
                    break;
            }
            return text;
        }
    },

    getValue: function () {
        if(this.accumulationGroup) {
            return {
                type: BICst.SERIES_ACCUMULATION.EXIST,
                items: this.accumulationGroup.getValue()
            };
        } else {
            return {};
        }
    }
});

$.shortcut("bi.series_accumulation", BI.SeriesAccumulation);