/**
 * 简单的包装了multilayersingleleveltree控件
 * 当parent节点下没有子节点的时候展示一个空值
 *
 * @class BI.MultiLayerSingleEmptyLevelTree
 * @extends BI.Widget
 */
BI.MultiLayerSingleEmptyLevelTree = BI.inherit(BI.Widget, {


    _defaultConfig: function () {
        return BI.extend(BI.MultiLayerSingleEmptyLevelTree.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-multi-layer-single-empty-tree",
            isDefaultInit: false,
            items: [],
            itemsCreator: BI.emptyFn
        });
    },

    _init: function () {
        BI.MultiLayerSingleEmptyLevelTree.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.tree = BI.createWidget({
            type: "bi.multilayer_single_level_tree",
            isDefaultInit: o.isDefaultInit,
            element: this.element,
            items: this._formatItems(o.items),
            itemsCreator: o.itemsCreator
        });
        this.tree.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        this.tree.on(BI.MultiLayerSingleLevelTree.EVENT_CHANGE, function(){
           self.fireEvent(BI.MultiLayerSingleEmptyLevelTree.EVENT_CHANGE, arguments);
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

    populate: function (nodes) {
        this.tree.populate(this._formatItems(nodes));
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
BI.MultiLayerSingleEmptyLevelTree.EVENT_CHANGE = "MultiLayerSingleEmptyLevelTree.EVENT_CONFIRM";
$.shortcut('bi.multi_layer_single_empty_level_tree', BI.MultiLayerSingleEmptyLevelTree);