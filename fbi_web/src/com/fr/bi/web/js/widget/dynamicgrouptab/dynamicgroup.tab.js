/**
 * Created by windy on 2016/12/20.
 */
BI.DynamicGroupTab = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.DynamicGroupTab.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-dynamic-group-tab",
            items:[],
            cardCreator: BI.emptyFn,
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
            self.tab.setSelect(v);
            self.fireEvent(BI.DynamicGroupTab.EVENT_ADD_SHEET);
        });

        this.tabButton.on(BI.DynamicGroupTabButtonManager.MERGE_SHEET, function(v){
            self.fireEvent(BI.DynamicGroupTab.EVENT_MERGE_SHEET);
        });

        this.tabButton.on(BI.DynamicGroupTabButtonManager.EVENT_DELETE, function(type, value){
            var deletePos = -1;
            BI.any(self.tabs, function(idx, tab){
                if(tab.getValue() === value){
                    deletePos = idx;
                    tab.destroy();
                    return true;
                }
            })
            var buttons = self.tabButton.getAllButtons();
            if(deletePos < 0 || buttons.length === 0) {
                return;
            }
            var selectIndex = Math.max(0, Math.min(buttonCount - 1, deletePos))
            //self.setSelect();
            //this._refreshButtonStatus(widget, model)
        });

        this.tabButton.on(BI.DynamicGroupTabButtonManager.EVENT_COPY, function(v){
            self.tab.setSelect(v);
            self.fireEvent(BI.DynamicGroupTab.EVENT_ADD_SHEET);
        });

        this.tabButton.on(BI.DynamicGroupTabButtonManager.EVENT_RENAME, function(type, value){
            self.tab.setSelect(value);
        });

        this.tab = BI.createWidget({
            type: "bi.tab",
            cls: "tab-dynamic-center",
            defaultShowIndex:false,
            cardCreator: BI.bind(this._cardCreator, this)
        });
        BI.createWidget({
            type:"bi.vtape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.center",
                    items: [this.tab],
                    hgap: 20
                }
            }, {
                el:this.tabButton,
                height: o.height
            }]
        })
    },

    _cardCreator: function(v){
        var tab = BI.createWidget({
            type: "bi.center",
            items: [{
                type: "bi.label",
                text: v,
                value: v
            }]
        });
        this.tabs.push(tab);
        return tab;
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

    populate: function(items){
        this.tabButton.populate(items);
        this.tab.populate(items);
    }
})
BI.DynamicGroupTab.EVENT_ADD_SHEET = "EVENT_ADD_SHEET";
BI.DynamicGroupTab.EVENT_MERGE_SHEET = "EVENT_MERGE_SHEET";
BI.DynamicGroupTab.EVENT_CHANGE = "EVENT_CHANGE"
$.shortcut("bi.dynamic_group_tab", BI.DynamicGroupTab);