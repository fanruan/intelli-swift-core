/**
 * create by young
 * 用户搜索结果面板
 */
BI.UserSearchResultPane = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.UserSearchResultPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-user-search-result-pane"
        })
    },

    _init: function(){
        BI.UserSearchResultPane.superclass._init.apply(this, arguments);
        var self = this;
        this.tree = BI.createWidget({
            type: "bi.simple_tree"
        });
        this.tree.on(BI.SimpleTreeView.EVENT_CHANGE, function(){
            self.fireEvent(BI.UserSearchResultPane.EVENT_CHANGE, arguments);
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.tree,
                top: 0,
                bottom: 0,
                left: 0,
                right: 0
            }]
        })
    },

    getValue: function(){
        return this.tree.getValue();
    },

    setValue: function(v){
        this.tree.setValue(v);
    },

    empty: function(){
        this.tree.populate([]);
    },

    populate: function(items, keyword, value){
        this.tree.populate(items, keyword);
        this.tree.setValue(value || []);
    }

});
BI.UserSearchResultPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.user_search_result_pane", BI.UserSearchResultPane);