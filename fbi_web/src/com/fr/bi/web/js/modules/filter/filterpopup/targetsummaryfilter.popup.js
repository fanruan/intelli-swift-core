/**
 * Created by Young's on 2016/3/22.
 * 汇总表的表头上的过滤弹出层
 */
BI.TargetSummaryFilterPopup = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.extend( BI.TargetSummaryFilterPopup.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-target-summary-filter"
        })
    },

    _init: function () {
        BI.TargetSummaryFilterPopup.superclass._init.apply(this, arguments);
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
            type: "bi.target_summary_filter",
            element: center,
            dId: o.dId
        });
        return true;
    },

    populate: function(){
        this.targetFilterPane.populate();
    },

    end: function(){
        this.fireEvent(BI.TargetSummaryFilterPopup.EVENT_CHANGE, this.targetFilterPane.getValue()[0]);
    }
});

BI.TargetSummaryFilterPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.target_summary_filter_popup", BI.TargetSummaryFilterPopup);