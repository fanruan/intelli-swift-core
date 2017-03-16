/**
 * triangle mid leaf of tree
 * Created by AstronautOO7 on 2016/8/25.
 */
BI.CustomScaleFormulaFieldTreeMidLeaf = BI.inherit(BI.NodeButton, {
    _defaultConfig: function () {
        var conf = BI.CustomScaleFormulaFieldTreeMidLeaf.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            extraCls: "bi-custom-scale-formula-field-tree-mid-leaf bi-list-item",
            layer: 0,//第几层级
            id: "",
            pId: "",
            open: false,
            height: 25
        })
    },
    _init: function () {
        BI.CustomScaleFormulaFieldTreeMidLeaf.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.node = BI.createWidget({
            type: "bi.triangle_group_node",
            logic: {
                dynamic: true
            },
            id: o.id,
            pId: o.pId,
            open: o.open,
            height: o.height,
            hgap: o.hgap,
            text: o.text,
            value: o.value,
            py: o.py
        });
        this.node.on(BI.Controller.EVENT_CHANGE, function (type) {
            if (type === BI.Events.CLICK) {//本身实现click功能
                return;
            }
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        var items = [];
        BI.count(0, o.layer, function () {
            items.push({
                type: "bi.layout",
                width: 23,
                height: o.height
            })
        });
        items.push(this.node);
        BI.createWidget({
            type: "bi.td",
            element: this.element,
            columnSize: BI.makeArray(o.layer, 23),
            items: [items]
        })
    },

    doRedMark: function () {
        this.node.doRedMark.apply(this.node, arguments);
    },

    unRedMark: function () {
        this.node.unRedMark.apply(this.node, arguments);
    },

    doClick: function () {
        BI.MultiLayerSingleTreeMidPlusGroupNode.superclass.doClick.apply(this, arguments);
        this.node.setSelected(this.isSelected());
    },

    setOpened: function (v) {
        BI.MultiLayerSingleTreeMidPlusGroupNode.superclass.setOpened.apply(this, arguments);
        if (BI.isNotNull(this.node)) {
            this.node.setOpened(v);
        }
    }
});

$.shortcut("bi.custom_scale_formula_field_tree_mid_leaf", BI.CustomScaleFormulaFieldTreeMidLeaf);