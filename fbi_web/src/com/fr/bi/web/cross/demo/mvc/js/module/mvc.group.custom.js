/**
 * Created by roy on 15/10/12.
 */

CustomGroupView = BI.inherit(BI.View, {

    _defaultConfig: function () {
        return BI.extend(CustomGroupView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-group-floatbox"
        })
    },

    _init: function () {
        var self = this, o = this.options;
        CustomGroupView.superclass._init.apply(this, arguments);
        BI.Popovers.create("ddd", BI.createWidget({
            type: "bi.custom_group_floatbox"
        })).open("ddd")
    },

    _render: function (vessel) {
        var self = this, o = this.options;
        this.fieldButton = BI.createWidget({
            type: "bi.custom_group_field_button",
            valueLeft: "this is button value",
            valueRight: "(this is button right text)"
        });


        this.expander = BI.createWidget({
            type: "bi.custom_group_group_expander"
        });

        this.popupButton = BI.createWidget({
            type: "bi.button",
            cls: "float-box-button",
            text: "click",
            width: 30,
            height: 30,
            handler: function () {
                FloatBoxes.open("ddd", "test", {}, self);
            }
        });

        var wIds = BI.Utils.getAllWidgetIDs();
        var dId = BI.Utils.getAllDimDimensionIDs(wIds[0])[0];

        this.customgroup = BI.createWidget({
            type: "bi.custom_group",
            dId: dId
        });
        this.customgroup.populate();

        BI.createWidget({
            type: "bi.vertical",
            hgap: 20,
            vgap: 20,
            element: vessel,
            items: [
                self.fieldButton,
                self.expander,
                self.customgroup,
                self.popupButton
            ]
        });
    }
});


CustomGroupModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(CustomGroupModel.superclass._defaultConfig.apply(this, arguments), {})
    },
    _init: function () {
        CustomGroupModel.superclass._init.apply(this, arguments);
    }
});


CustomGroupFloatBoxChildView = BI.inherit(BI.BarFloatSection, {
    _defaultConfig: function () {
        return BI.extend(CustomGroupFloatBoxChildView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-group-floatbox"
        })
    },
    _init: function () {
        CustomGroupFloatBoxChildView.superclass._init.apply(this, arguments);
    },

    yes: function () {
        var self = this;
        var value = self.customgroup.getValue();
    },


    rebuildCenter: function (center) {
        var self = this;
        this.customgroup = BI.createWidget({
            element: center,
            type: "bi.custom_group",
            items: [{
                text: "groupName1",
                value: "groupName1",
                title: "groupName1",
                content: [{value: "first"}, {value: "second"}, {value: "third"}]
            }, {
                text: "groupName2",
                value: "groupName2",
                title: "groupName2",
                content: [{value: "first"}, {value: "second"}]
            }],
            unGroupedItems: [
                {
                    text: BI.i18nText("BI-Ungrouped_China"),
                    value: BI.i18nText("BI-Ungrouped_China"),
                    title: BI.i18nText("BI-Ungrouped_China"),
                    content: [{value: "first"}, {value: "second"}, {value: "third"}]
                }
            ]
        })
    }
});

CustomGroupFloatBoxChildModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(CustomGroupFloatBoxChildModel.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        CustomGroupFloatBoxChildModel.superclass._init.apply(this, arguments);
    }
});