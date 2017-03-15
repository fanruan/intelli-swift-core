/**
 * @class BI.HyperLinkPopup
 * @extend BI.BarPopoverSection
 * 过滤
 */
BI.HyperLinkPopup = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.extend( BI.HyperLinkPopup.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-hyper-link-filter"
        })
    },

    _init: function () {
        BI.HyperLinkPopup.superclass._init.apply(this, arguments);
    },

    rebuildNorth : function(north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Hyperlink"),
            height: 50,
            textAlign: "left",
            lgap: 10
        });
        return true;
    },

    rebuildCenter : function(center) {
        var o = this.options;
        this.hyperLinkInsert = BI.createWidget({
            type: "bi.hyper_link_insert",
            element: center,
            dId: o.dId
        });
        return true;
    },

    populate: function(){
        this.hyperLinkInsert.populate();
    },

    end: function(){
        this.fireEvent(BI.HyperLinkPopup.EVENT_CHANGE, this.hyperLinkInsert.getValue());
    }
});

BI.HyperLinkPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.hyper_link_popup", BI.HyperLinkPopup);