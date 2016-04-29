/**
 * 具有两个region的popup
 * @class BI.CircleTwoRegionPopup
 * @extends BI.Widget
 */

BI.CircleTwoRegionPopup = BI.inherit(BI.Widget, {

    constants: {
        titleHeight: 30
    },

    _defaultConfig: function() {
        return BI.extend(BI.CircleTwoRegionPopup.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-circle-two-region-popup"
        })
    },

    _init : function() {
        BI.CircleTwoRegionPopup.superclass._init.apply(this, arguments);

        var self = this;

        this.stringRegion = BI.createWidget({
            type: "bi.circle_tab_region",
            title: BI.i18nText("BI-Text_Type")
        });
        this.numberRegion = BI.createWidget({
            type: "bi.circle_tab_region",
            title: BI.i18nText("BI-Numeric_Type")
        });

        this.stringRegion.on(BI.CircleTabRegion.EVENT_CHANGE, function(){
            self.numberRegion.setValue();
            self.fireEvent(BI.CircleTwoRegionPopup.EVENT_CHANGE);
        });

        this.stringRegion.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        this.numberRegion.on(BI.CircleTabRegion.EVENT_CHANGE, function(){
            self.stringRegion.setValue();
            self.fireEvent(BI.CircleTwoRegionPopup.EVENT_CHANGE);
        });

        this.numberRegion.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                el:this.stringRegion,
                tgap: 5
            }, {
                el: this.numberRegion,
                bgap: 10
            }],
            hgap: 5
        });
    },

    populate: function(items){
        this.options.items = items;
        this.stringRegion.populate(items.textItems);
        this.numberRegion.populate(items.numberItems);
        if(BI.isEmpty(items.numberItems)){
            this.numberRegion.setVisible(false);
            return;
        }
        this.numberRegion.setVisible(true);
    },

    setValue: function(v){
        this.stringRegion.setValue(v);
        this.numberRegion.setValue(v);
    },

    getValue: function(){
        var stringValue = this.stringRegion.getValue();
        var numberValue = this.numberRegion.getValue();
        return BI.isEmpty(stringValue) ? numberValue[0] : stringValue[0];
    }
});

BI.CircleTwoRegionPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.circle_two_region_popup", BI.CircleTwoRegionPopup);