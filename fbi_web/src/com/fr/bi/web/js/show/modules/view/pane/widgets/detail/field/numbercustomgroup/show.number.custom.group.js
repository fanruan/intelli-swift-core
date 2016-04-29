/**
 * @class BIShow.NumberCustomGroupView
 * @extend BI.BarFloatSection
 * 数值自定义分组
 */
BIShow.NumberCustomGroupView = BI.inherit(BI.BarFloatSection, {
    _defaultConfig: function () {
        return BI.extend(BIShow.NumberCustomGroupView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-number-custom-group-view"
        })
    },

    yes: function () {
        this.model.set(this.group.getValue());
        this.notifyParentEnd();
    },

    no: function () {
        this.notifyParentEnd();
    },

    end: function () {

    },

    _init: function () {
        BIShow.NumberCustomGroupView.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function (north) {
        var id = this.model.get("id");
        var value = BI.Utils.getDimensionNumberMaxMinValueByID(o.dId)(id);
        var max = value.max;
        var min = value.min;
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nJoint("BI-Grouping_Setting_Detail", [min, max]),
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
            self.saveButton.setValue(BI.i18nText("BI-Correct_The_Errors_Red"));
            self.saveButton.setEnable(false);
        });

        this.group.on(BI.NumberIntervalCustomGroup.EVENT_VALID, function () {
            self.saveButton.setValue(BI.i18nText("BI-Save"));
            self.saveButton.setEnable(true);
        });

        this.group.on(BI.NumberIntervalCustomGroup.EVENT_EMPTY_GROUP, function () {
            self.saveButton.setEnable(false);
        });

        BI.createWidget({
            type: 'bi.vertical',
            element: center,
            items: [this.group]
        })


        return true;
    },

    rebuildSouth: function (south) {
        var self = this;

        this.saveButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Save"),
            height: 30,
            level: 'common',
            readonly: false,
            handler: function () {
                self.yes();
            }
        });

        this.cancelButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Cancel"),
            height: 30,
            level: 'ignore',
            readonly: false,
            handler: function () {
                self.no()
            }
        });

        BI.createWidget({
            type: "bi.right",
            element: south,
            items: [this.saveButton,
                {
                    el: this.cancelButton,
                    rgap: 10
                }
            ]
        });
        return true;
    },

    refresh: function () {
        this.group.populate(this.model.toJSON());
    }
});
