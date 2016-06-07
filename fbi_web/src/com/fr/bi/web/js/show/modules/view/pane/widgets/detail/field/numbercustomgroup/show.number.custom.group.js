/**
 * @class BIShow.NumberCustomGroupView
 * @extend BI.BarFloatSection
 * 数值自定义分组
 */
BIShow.NumberCustomGroupView = BI.inherit(BI.BarFloatSection, {
    _defaultConfig: function () {
        return BI.extend(BIShow.NumberCustomGroupView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-number-custom-group-view",
            btns: [BI.i18nText(BI.i18nText("BI-Save")), BI.i18nText("BI-Cancel")]
        })
    },

    end: function () {
        this.model.set(this.group.getValue());
    },

    _init: function () {
        BIShow.NumberCustomGroupView.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function (north) {
        var id = this.model.get("id");
        var value = BI.Utils.getDimensionNumberMaxMinValueByID(id);
        var max = value.max;
        var min = value.min;
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Grouping_Setting_Detail", min, max),
            height: 50,
            textAlign: "left",
            lgap: 10
        });
        return true;
    },

    rebuildCenter: function (center) {
        var id = this.model.get("id");
        var self = this;
        this.group = BI.createWidget({
            type: "bi.number_custom_group",
            dId: id
        });

        this.group.on(BI.NumberIntervalCustomGroup.EVENT_ERROR, function () {
            self.sure.setValue(BI.i18nText("BI-Correct_The_Errors_Red"));
            self.sure.setEnable(false);
        });

        this.group.on(BI.NumberIntervalCustomGroup.EVENT_VALID, function () {
            self.sure.setValue(BI.i18nText("BI-Save"));
            self.sure.setEnable(true);
        });

        this.group.on(BI.NumberIntervalCustomGroup.EVENT_EMPTY_GROUP, function () {
            self.sure.setValue(BI.i18nText("BI-Save"));
            self.sure.setEnable(false);
        });

        BI.createWidget({
            type: 'bi.vertical',
            element: center,
            items: [this.group]
        });


        return true;
    },

    refresh: function () {
        this.group.populate(this.model.toJSON());
    }
});