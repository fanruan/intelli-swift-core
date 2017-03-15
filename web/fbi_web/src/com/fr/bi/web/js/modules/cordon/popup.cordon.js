/**
 * @class BI.CordonPopup
 * @extend BI.BarPopoverSection
 * 警戒线
 */
BI.CordonPopup = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.extend( BI.CordonPopup.superclass._defaultConfig.apply(this, arguments), {
            width: 600,
            height: 500
        })
    },

    _init: function () {
        BI.CordonPopup.superclass._init.apply(this, arguments);
    },

    rebuildNorth : function(north) {
        var o = this.options;
        var name = BI.Utils.getDimensionNameByID(o.dId);
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: name + BI.i18nText("BI-De") + BI.i18nText("BI-Cordon"),
            height: 50,
            textAlign: "left",
            lgap: 10
        });
        return true;
    },

    rebuildCenter : function(center) {
        var o = this.options;
        this.cordonPane = BI.createWidget({
            type: "bi.cordon_pane",
            element: center,
            dId: o.dId
        });
        return true;
    },

    populate: function(){
        this.cordonPane.populate();
    },

    end: function(){
        this.fireEvent(BI.CordonPopup.EVENT_CHANGE, this.cordonPane.getValue());
    }
});

BI.CordonPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.cordon_popup", BI.CordonPopup);