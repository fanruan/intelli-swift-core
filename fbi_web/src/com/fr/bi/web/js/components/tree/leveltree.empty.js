/**
 * 简单的包装了leveltree控件
 * 当parent节点下没有子节点的时候展示一个空值
 *
 * @class BI.EmptyLevelTree
 * @extends BI.Widget
 */
BI.EmptyLevelTree = BI.inherit(BI.Widget, {


    _defaultConfig: function () {
        return BI.extend(BI.EmptyLevelTree.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-empty-tree",
            el: {
                chooseType: 0
            },
            expander: {},
            items: []
        });
    },

    _init: function () {
        BI.EmptyLevelTree.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.tree = BI.createWidget({
            type: "bi.level_tree",
            el: o.el,
            element: this.element,
            expander: o.expander,
            items: this._formatItems(o.items)
        })
    },

    _formatItems: function(items){
        var pIdMap = {};
        BI.each(items, function(idx, item){
            if(BI.has(item, "isParent") && item.isParent === true){
                pIdMap[item.id] = [];
            }
        });
        BI.each(items, function(idx, item){
            if(BI.has(pIdMap, item.pId)){
                delete pIdMap[item.pId];
            }
        });
        BI.each(pIdMap, function(key){
            items.push({
                value: BI.i18nText("BI-(Empty)"),
                text: BI.i18nText("BI-(Empty)"),
                pId: key,
                id: BI.UUID(),
                disabled: true
            });
        });
        return items;
    },

    //构造树结构，
    initTree: function (nodes) {
        this.tree.initTree(this._formatItems(nodes));
    },

    //生成树方法
    stroke: function (nodes) {
        this.tree.stroke(nodes);
    },

    populate: function (items) {
        this.tree.populate(this._formatItems(items));
    },

    doBehavior: function () {
        this.tree.doBehavior.apply(this.tree, arguments);
    },

    setValue: function (v) {
        this.tree.setValue(v);
    },

    getValue: function () {
        return this.tree.getValue();
    },

    getAllLeaves: function () {
        return this.tree.getAllLeaves();
    },

    getNodeById: function (id) {
        return this.tree.getNodeById(id);
    },

    getNodeByValue: function (id) {
        return this.tree.getNodeByValue(id);
    }
});
BI.EmptyLevelTree.EVENT_CONFIRM = "EmptyLevelTree.EVENT_CONFIRM";
$.shortcut('bi.empty_level_tree', BI.EmptyLevelTree);