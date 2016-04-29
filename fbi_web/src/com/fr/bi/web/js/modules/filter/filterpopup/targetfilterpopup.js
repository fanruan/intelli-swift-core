/**
 * @class BI.TargetFilterPopup
 * @extend BI.BarPopoverSection
 * 过滤
 */
BI.TargetFilterPopup = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.extend( BI.TargetFilterPopup.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-target-filter"
        })
    },

    _init: function () {
        BI.TargetFilterPopup.superclass._init.apply(this, arguments);
    },

    rebuildNorth : function(north) {
        var o = this.options;
        var name = BI.Utils.getDimensionNameByID(o.dId);
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Sen_Add_Filter_wei", name),
            height: 50,
            textAlign: "left",
            lgap: 10
        });
        return true;
    },

    rebuildCenter : function(center) {
        var o = this.options;
        this.targetFilterPane = BI.createWidget({
            type: "bi.target_filter",
            element: center,
            dId: o.dId
        });
        return true;
    },

    populate: function(){
        this.targetFilterPane.populate();
    },

    end: function(){
        this.fireEvent(BI.TargetFilterPopup.EVENT_CHANGE, this.targetFilterPane.getValue()[0]);
    }
});

BI.TargetFilterPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.target_filter_popup", BI.TargetFilterPopup);