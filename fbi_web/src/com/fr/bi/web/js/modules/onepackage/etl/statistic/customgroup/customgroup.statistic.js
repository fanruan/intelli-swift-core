/**
 * @class BI.CustomGroupPopup
 * @extend BI.BarPopoverSection
 * 数值自定义分组
 */
BI.CustomGroupPopup = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.extend( BI.CustomGroupPopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-group-floatbox",
            id: "",
            table: {},
            group: {}
        })
    },

    end: function(){
        this.fireEvent(BI.CustomGroupPopup.EVENT_CHANGE, BI.extend(this.customgroup.getValue(), {
            type: BICst.GROUP.CUSTOM_GROUP
        }));
    },

    _init: function () {
        BI.CustomGroupPopup.superclass._init.apply(this, arguments);
        var o = this.options;
    },

    rebuildNorth: function (north) {
        var o = this.options;
        var dimensionName = o.fieldName;
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
        this.customgroup = BI.createWidget({
            element: center,
            model: this.options.model,
            dId: this.options.dId,
            type: "bi.etl_group_custom_group"
        })

    },

    populate: function(group){
        this.customgroup.populate(group);
    }
});

BI.CustomGroupPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.custom_group_popup", BI.CustomGroupPopup);