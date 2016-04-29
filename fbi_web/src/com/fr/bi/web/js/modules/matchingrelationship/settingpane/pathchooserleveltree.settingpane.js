/**
 * 多对多所用level tree暂时不用
 * @class BI.PathChooserLevelTree
 * @extends BI.Single
 */
BI.PathChooserLevelTree = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.PathChooserLevelTree.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-path-chooser-level-tree",
            items: []
        })
    },

    _init: function () {
        BI.PathChooserLevelTree.superclass._init.apply(this, arguments);

        this.initTree(this.options.items);
    },

    _formatItems: function (nodes) {
        var self = this;
        BI.each(nodes, function (i, node) {
            var extend = {};
            if (!BI.isKey(node.id)) {
                node.id = BI.UUID();
            }
            if (node.isParent === true || BI.isNotEmptyArray(node.children)) {
                extend.type = "bi.plus_group_node";
                BI.defaults(node, extend);
                self._formatItems(node.children);
            } else {
                extend.type = "bi.direction_path_chooser";
                BI.defaults(node, extend);
            }
        });
        return nodes;
    },

    _assertId: function (sNodes) {
        BI.each(sNodes, function (i, node) {
            if (!BI.isKey(node.id)) {
                node.id = BI.UUID();
            }
        });
    },

    //构造树结构，
    initTree: function (nodes) {
        var self = this, o = this.options;
        this.empty();
        this._assertId(nodes);
        this.tree = BI.createWidget({
            type: "bi.custom_tree",
            element: this.element,
            expander: BI.extend({
                el: {},
                popup: {
                    type: "bi.custom_tree"
                }
            }, o.expander),

            items: this._formatItems(BI.Tree.transformToTreeFormat(nodes)),

            el: BI.extend({
                type: "bi.button_tree",
                chooseType: 0,
                layouts: [{
                    type: "bi.vertical"
                }]
            }, o.el)
        });
        this.tree.on(BI.Controller.EVENT_CHANGE, function (type) {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
            if (type === BI.Events.CLICK) {
                self.fireEvent(BI.PathChooserLevelTree.EVENT_CHANGE, arguments);
            }
        })
    },

    //生成树方法
    stroke: function (nodes) {
        this.tree.stroke.apply(this.tree, arguments);
    },

    populate: function (items) {
        items = this._formatItems(BI.Tree.transformToTreeFormat(items));
        this.tree.populate(items);
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
BI.PathChooserLevelTree.EVENT_CHANGE = "EVENT_CHANGE";

$.shortcut("bi.path_chooser_level_tree", BI.PathChooserLevelTree);