
/**
 * @class BI.MatchingRelationShipTab
 * @extend BI.Widget
 */

BI.MatchingRelationShipTab = BI.inherit(BI.Widget,{

    constants:{
        CIRCLE_GAP_TEN: 10,
        conditionHeight: 30
    },

    _defaultConfig:function(){
        return BI.extend(BI.MatchingRelationShipTab.superclass._defaultConfig.apply(this,arguments),{
            baseCls:"bi-matching-relation-tab"
        });
    },

    _init:function(){
        BI.MatchingRelationShipTab.superclass._init.apply(this,arguments);

        this.tab = BI.createWidget({
            direction: "custom",
            type: "bi.tab",
            element: this.element,
            cardCreator: BI.bind(this._createTabs, this)
        });
    },

    _createTabs : function(v){
        var self = this, o = this.options;
        switch (v){
            case BI.MatchingRelationShipTab.INFO_PANE:
                this.infoPane = BI.createWidget({
                    type: "bi.relation_info_pane",
                    dId: o.dId
                });
                self.infoPane.on(BI.RelationInfoPane.EVENT_CLICK_RELATION_BUTTON, function(v){
                    self.setSelect(BI.MatchingRelationShipTab.SETTING_PANE);
                    self.populate(BI.keys(v));
                    self.setValue(BI.values(v)[0]);
                    self.fireEvent(BI.MatchingRelationShipTab.EVENT_CHANGE);
                });
                return this.infoPane;
            case BI.MatchingRelationShipTab.SETTING_PANE:
                this.setPane = BI.createWidget({
                    type: "bi.set_relation_pane",
                    dimensionId: o.dId
                });
                return this.setPane;
        }
    },

    populate: function(items){
        this.tab.getSelectedTab().populate(items);
    },

    setSelect: function(v){
        this.tab.setSelect(v);
    },

    getSelect: function(){
        return this.tab.getSelect();
    },

    setValue: function(v){
        this.tab.setValue(v);
    },

    getValue: function(){
        return this.tab.getValue();
    }
});

BI.extend(BI.MatchingRelationShipTab, {
    INFO_PANE: 0,
    SETTING_PANE: 1
});

BI.MatchingRelationShipTab.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.matching_relationship_tab",BI.MatchingRelationShipTab);