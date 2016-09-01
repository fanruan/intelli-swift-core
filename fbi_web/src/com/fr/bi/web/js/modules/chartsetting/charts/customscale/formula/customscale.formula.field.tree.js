/**
 * formula field tree of custom scale
 * Created by AstronautOO7 on 2016/8/24.
 */
BI.CustomScaleFormulaFieldTree = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CustomScaleFormulaFieldTree.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-scale-formula-field-tree",
            chooseType: 0,
            items: {}
        })
    },

    _init: function () {
        BI.CustomScaleFormulaFieldTree.superclass._init.apply(this, arguments);
        this.populate(this.options.items)
    },

    _createOneAxis: function (nodes, targets, axisOne) {
        var self = this;
        BI.each(targets[BICst.REGION.TARGET1], function (idx, item) {
            nodes.push({
                type: "bi.custom_scale_formula_field_tree_mid_leaf",
                id: item,
                pId: nodes[0].id,
                text: axisOne + (idx + 1),
                value: item
            });
            nodes = BI.concat(nodes, self._createLeaves(item))
        });

        return nodes
    },

    _createTwoAxis: function (nodes, targets, axisOne, axisTwo) {
        var self = this;
        BI.each(targets[BICst.REGION.TARGET1], function (idx, item) {
            nodes.push({
                type: "bi.custom_scale_formula_field_tree_mid_leaf",
                id: item,
                pId: nodes[0].id,
                text: axisOne + (idx + 1),
                value: item
            });
            nodes = BI.concat(nodes, self._createLeaves(item))
        });

        BI.each(targets[BICst.REGION.TARGET2], function (idx, item) {
            nodes.push({
                type: "bi.custom_scale_formula_field_tree_mid_leaf",
                id: item,
                pId: nodes[0].id,
                text: axisTwo + (idx + 1),
                value: item
            });
            nodes = BI.concat(nodes, self._createLeaves(item))
        });
        return nodes
    },

    _createThreeAxis: function (nodes, targets, axisOne, axisTwo, axisThree) {
        var self = this;
        BI.each(targets[BICst.REGION.TARGET1], function (idx, item) {
            nodes.push({
                type: "bi.custom_scale_formula_field_tree_mid_leaf",
                id: item,
                pId: nodes[0].id,
                text: axisOne + (idx + 1),
                value: item
            });
            nodes = BI.concat(nodes, self._createLeaves(item))
        });

        BI.each(targets[BICst.REGION.TARGET2], function (idx, item) {
            nodes.push({
                type: "bi.custom_scale_formula_field_tree_mid_leaf",
                id: item,
                pId: nodes[0].id,
                text: axisTwo + (idx + 1),
                value: item
            });
            nodes = BI.concat(nodes, self._createLeaves(item))
        });

        BI.each(targets[BICst.REGION.TARGET3], function (idx, item) {
            nodes.push({
                type: "bi.custom_scale_formula_field_tree_mid_leaf",
                id: item,
                pId: nodes[0].id,
                text: axisThree + (idx + 1),
                value: item
            });
            nodes = BI.concat(nodes, self._createLeaves(item, item))
        });

        return nodes
    },

    _createLeaves: function (pId) {
        var leaves = [];
        var texts = [BI.i18nText("BI-DOT_MAX"), BI.i18nText("BI-DOT_MIN"), BI.i18nText("BI-DOT_AVERAGE")];

        BI.each(texts, function (idx, text) {
            leaves.push({
                type: "bi.tree_text_leaf_item",
                pId: pId,
                id: pId + text,
                text: text,
                value: pId + idx,
                lgap: 48
            })
        });

        return leaves
    },

    populate: function (usedTargets) {
        var self = this;

        this.empty();

        var nodes = [
            {
                type: "bi.triangle_group_node",
                id: BICst.COLUMN.NUMBER,
                text: BI.i18nText("BI-Formula_Numberic_Field"),
                isParent: true,
                value: BICst.COLUMN.NUMBER,
                open: true
            }
        ];

        switch (usedTargets.type) {
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
                nodes = this._createTwoAxis(nodes, usedTargets, BI.i18nText("BI-Left_Value_Axis"), BI.i18nText("BI-Right_Value_Axis"));
                break;
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.FALL_AXIS:
                nodes = this._createOneAxis(nodes, usedTargets, BI.i18nText("BI-Value_Axis"));
                break;
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
                nodes = this._createOneAxis(nodes, usedTargets, BI.i18nText("BI-Target"));
                break;
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.COMPARE_AREA:
                nodes = this._createTwoAxis(nodes, usedTargets, BI.i18nText("BI-Positive_Value_Axis"), BI.i18nText("BI-Negative_Value_Axis"));
                break;
            case BICst.WIDGET.COMPARE_BAR:
                nodes = this._createTwoAxis(nodes, usedTargets, BI.i18nText("BI-Value_Axis_One"), BI.i18nText("BI-Value_Axis_Two"));
                break;
            case BICst.WIDGET.RANGE_AREA:
                nodes = this._createTwoAxis(nodes, usedTargets, BI.i18nText("BI-Low_Value_Axis"), BI.i18nText("BI-High_Value_Axis"));
                break;
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                nodes = this._createThreeAxis(nodes, usedTargets, BI.i18nText("BI-Left_Value_Axis"), BI.i18nText("BI-Right_Value_Axis_One"), BI.i18nText("BI-Right_Value_Axis_Two"));
                break;
            case BICst.WIDGET.BUBBLE:
                nodes = this._createTwoAxis(nodes, usedTargets, BI.i18nText("BI-Y_Value_Axis"), BI.i18nText("BI-X_Value_Axis"));
                break;
        }

        this.fieldTree = BI.createWidget({
            type: "bi.multilayer_single_level_tree",
            element: this.element,
            isDefaultInit: true,
            items: nodes
        });

        this.fieldTree.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        this.fieldTree.on(BI.MultiLayerSingleLevelTree.EVENT_CHANGE, function () {
            self.fireEvent(BI.CustomScaleFormulaFieldTree.EVENT_CHANGE);
        });
    },

    getValue: function () {
        return this.fieldTree.getValue()
    },

    setValue: function (v) {
        this.fieldTree.setValue(v)
    },

    getAllLeaves: function () {
        return this.fieldTree.getAllLeaves()
    }
});
BI.CustomScaleFormulaFieldTree.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.custom_scale_formula_field_tree", BI.CustomScaleFormulaFieldTree);