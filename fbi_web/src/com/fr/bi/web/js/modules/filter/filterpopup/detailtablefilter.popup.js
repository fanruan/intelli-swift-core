/**
 * Created by Young's on 2016/4/15.
 * 明细表过滤弹窗
 */
BI.DetailTableFilterPopup = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.extend( BI.DetailTableFilterPopup.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-detail-table-filter"
        })
    },

    _init: function () {
        BI.DetailTableFilterPopup.superclass._init.apply(this, arguments);
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
            type: "bi.detail_table_filter",
            element: center,
            dId: o.dId
        });
        return true;
    },

    populate: function(){
        this.targetFilterPane.populate();
    },

    end: function(){
        this.fireEvent(BI.DetailTableFilterPopup.EVENT_CHANGE, this.targetFilterPane.getValue()[0]);
    }
});

BI.DetailTableFilterPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_table_filter_popup", BI.DetailTableFilterPopup);