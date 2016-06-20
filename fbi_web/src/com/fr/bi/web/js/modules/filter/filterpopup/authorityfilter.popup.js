/**
 * Created by Young's on 2016/5/19.
 */
BI.AuthorityFilterPopup = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function(){
        return BI.extend( BI.AuthorityFilterPopup.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-authority-filter"
        })
    },

    _init: function () {
        BI.AuthorityFilterPopup.superclass._init.apply(this, arguments);
    },

    rebuildNorth : function(north) {
        var o = this.options;
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Sen_Add_Filter_wei", o.name),
            height: 50,
            textAlign: "left",
            lgap: 10
        });
        return true;
    },

    rebuildCenter : function(center) {
        this.targetFilterPane = BI.createWidget({
            type: "bi.authority_filter",
            element: center
        });
        return true;
    },

    populate: function(filter){
        this.targetFilterPane.populate(filter);
    },

    end: function(){
        this.fireEvent(BI.AuthorityFilterPopup.EVENT_CHANGE, this.targetFilterPane.getValue()[0]);
    }
});

BI.AuthorityFilterPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.authority_filter_popup", BI.AuthorityFilterPopup);