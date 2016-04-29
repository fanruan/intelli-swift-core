/**
 * @class BI.DimensionFilterPopup
 * @extend BI.BarPopoverSection
 * 过滤
 */
BI.DimensionFilterPopup = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.extend( BI.DimensionFilterPopup.superclass._defaultConfig.apply(this, arguments), {
            width: 600,
            height: 500
        })
    },

    _init: function () {
        BI.DimensionFilterPopup.superclass._init.apply(this, arguments);
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
        this.dimensionFilterPane = BI.createWidget({
            type: "bi.dimension_filter",
            element: center,
            dId: o.dId
        });
        return true;
    },

    populate: function(){
        this.dimensionFilterPane.populate();
    },

    end: function(){
        this.fireEvent(BI.DimensionFilterPopup.EVENT_CHANGE, this.dimensionFilterPane.getValue()[0]);
    }
});

BI.DimensionFilterPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.dimension_filter_popup", BI.DimensionFilterPopup);