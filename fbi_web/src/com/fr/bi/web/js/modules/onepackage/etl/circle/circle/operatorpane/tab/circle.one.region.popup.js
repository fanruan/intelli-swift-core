/**
 * @class BI.CircleOneRegionPopup
 * @extends BI.Widget
 */

BI.CircleOneRegionPopup = BI.inherit(BI.Widget, {

    _defaultConfig: function() {
        return BI.extend(BI.CircleOneRegionPopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-circle-one-region-popup"
        })
    },

    _init : function() {
        BI.CircleOneRegionPopup.superclass._init.apply(this, arguments);

        var self = this;

        this.stringRegion = BI.createWidget({
            type: "bi.circle_tab_region"
        });

        this.stringRegion.on(BI.CircleTabRegion.EVENT_CHANGE, function(){
            self.fireEvent(BI.CircleOneRegionPopup.EVENT_CHANGE);
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [this.stringRegion],
            hgap: 5
        });
    },

    populate: function(items){
        this.stringRegion.populate(items);
    },

    setValue: function(v){
        this.stringRegion.setValue(v);
    },

    getValue: function(){
        return this.stringRegion.getValue();
    }
});

BI.CircleOneRegionPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.circle_one_region_popup", BI.CircleOneRegionPopup);