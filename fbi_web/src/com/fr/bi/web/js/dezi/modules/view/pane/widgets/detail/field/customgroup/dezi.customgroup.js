/**
 * Created by roy on 15/10/12.
 */

BIDezi.CustomGroupView = BI.inherit(BI.BarFloatSection, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.CustomGroupView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-group-floatbox"
        })
    },
    _init: function () {
        BIDezi.CustomGroupView.superclass._init.apply(this, arguments);
    },

    end: function () {
        var self = this;
        var value = self.customgroup.getValue();
        value.type = BICst.GROUP.CUSTOM_GROUP;
        this.model.set(value || {});

    },


    rebuildNorth: function (north) {
        var id = this.model.get("id");
        var dimensionName = BI.Utils.getFieldNameByID(BI.Utils.getFieldIDByDimensionID(id));
        var name = BI.i18nText("BI-Custom_Group_Detail", dimensionName);
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: name,
            height: 50,
            textAlign: "left",
            lgap: 10
        });
        return true;
    },

    rebuildCenter: function (center) {
        var id = this.model.get("id");
        this.customgroup = BI.createWidget({
            element: center,
            dId: id,
            type: "bi.custom_group"
        })

    },


    refresh: function () {
        var groupedItems = this.model.get("details");
        var ungroup2Other = this.model.get('ungroup2Other');
        var ungroup2OtherName = this.model.get("ungroup2OtherName")
        this.customgroup.populate(groupedItems, ungroup2Other, ungroup2OtherName)
    }
});