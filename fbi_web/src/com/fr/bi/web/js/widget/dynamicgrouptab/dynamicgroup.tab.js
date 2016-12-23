/**
 * Created by windy on 2016/12/20.
 */
BI.DynamicGroupTab = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.DynamicGroupTab.superclass._defaultConfig.apply(this, arguments), {
            cls: "bi-dynamic-group-tab",
            cardCreator: function(v){
                return BI.createWidget();
            },
            height:30
        })
    },

    _init: function () {
        BI.DynamicGroupTab.superclass._init.apply(this, arguments);

        var self = this, o = this.options;
        this.tabs = [];
        this.tabButton = BI.createWidget({
            type: "bi.dynamic_group_tab_button_manager"
        });

        this.tabButton.on(BI.DynamicGroupTabButtonManager.EVENT_CHANGE, function(value){
            self.tab.setSelect(value);
        });

        this.tabButton.on(BI.DynamicGroupTabButtonManager.ADD_SHEET, function(v){
            self.setSelect(v);
            self.fireEvent(BI.DynamicGroupTab.EVENT_ADD_SHEET);
        });

        this.tabButton.on(BI.DynamicGroupTabButtonManager.MERGE_SHEET, function(v){
            self.fireEvent(BI.DynamicGroupTab.EVENT_MERGE_SHEET);
        });

        this.tabButton.on(BI.DynamicGroupTabButtonManager.EVENT_DELETE, function(value){
            var deletePos = -1;
            BI.any(self.tabs, function(idx, tab){
                if(tab.getValue()[0] === value){
                    deletePos = idx;
                    return true;
                }
            })
            var tab  = self.tabs.splice(deletePos, 1)[0];
            tab.destroy();
            var buttons = self.tabButton.getAllButtons();
            if(deletePos < 0 || buttons.length === 0) {
                return;
            }
            var selectIndex = Math.max(0, Math.min(buttons.length - 1, deletePos))
            self.setSelect(self.tabs[selectIndex].getValue());
        });

        this.tabButton.on(BI.DynamicGroupTabButtonManager.EVENT_COPY, function(oldId, newId){
            self.setSelect(newId);
            self.fireEvent(BI.DynamicGroupTab.EVENT_ADD_SHEET);
        });

        this.tabButton.on(BI.DynamicGroupTabButtonManager.EVENT_RENAME, function(type, value){

        });

        this.tab = BI.createWidget({
            type: "bi.tab",
            defaultShowIndex:false,
            cardCreator: function(v) {
                var tab = o.cardCreator(v);
                self.tabs.push(tab);
                return tab;
            }
        });
        BI.createWidget({
            type:"bi.vtape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.center",
                    items: [this.tab]
                }
            }, {
                el:this.tabButton,
                height: o.height
            }]
        })
    },

    setSelect: function(v){
        this.tabButton.setValue(v);
        this.tab.setSelect(v);
    },

    getTab: function(v){
        return this.tab.getTab(v)
    },

    getSelect: function () {
        return this.tab.getSelect();
    },

    getSelectedTab: function () {
        return this.tab.getSelectedTab();
    },

    populate: function(){
        this.tab.populate();
    }
})
BI.DynamicGroupTab.EVENT_ADD_SHEET = "EVENT_ADD_SHEET";
BI.DynamicGroupTab.EVENT_MERGE_SHEET = "EVENT_MERGE_SHEET";
BI.DynamicGroupTab.EVENT_CHANGE = "EVENT_CHANGE"
$.shortcut("bi.dynamic_group_tab", BI.DynamicGroupTab);