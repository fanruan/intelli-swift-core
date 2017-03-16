/**
 * Created by zcf on 2016/12/20.
 */
TreeListView=BI.inherit(BI.View,{
    _defaultConfig: function () {
        return BI.extend(TreeListView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: ""
        })
    },

    _init: function () {
        TreeListView.superclass._init.apply(this, arguments);
    },

    _render:function (vessel) {
        this.tree=BI.createWidget({
            type:"bi.multi_tree_list",
            itemsCreator:function (op, callback) {
                var data = BI.extend({
                    floors: 4
                }, op);
                debugger;
                BI.requestAsync("bi_demo", "get_tree_node", data, callback)
            }
        });

        BI.createWidget({
            type:"bi.vertical",
            element:vessel,
            items:[this.tree]
        })
    }
});
TreeListModel=BI.inherit(BI.Model,{});