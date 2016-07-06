/**
 * 多路径设置
 *
 * @class BI.MultiDateParamPane
 * @extends BI.Widget
 */

BI.MultiDateParamPane = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.MultiDateParamPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-multi-date-param-pane"
        });
    },

    _init: function () {
        BI.MultiDateParamPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.stored_value = [];
        this.tree = BI.createWidget({
            type: "bi.select_data_tree",
            el: {
                el: {
                    chooseType: BI.ButtonGroup.CHOOSE_TYPE_SINGLE
                }
            },
            itemsCreator: function(op, populate){
                if (!op.node) {
                    populate(self._getDateWidgetStructure());
                    self.tree.setValue(self.stored_value);
                    return;
                }
                if (BI.isNotNull(op.node.isParent)) {
                    populate(self._getStartEndStructureByWidgetId(op.node.id));
                    self.tree.setValue(self.stored_value);
                }
            }
        });

        this.tree.on(BI.SelectDataTree.EVENT_CHANGE, function () {
            self.stored_value = self.tree.getValue();
            self.fireEvent(BI.MultiDateParamPane.EVENT_CHANGE, self.tree.getValue());
        });

        this.tree.populate();

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [this.tree]
        })
    },

    _getDateWidgetStructure: function(){
        var targetWidgetType = [BICst.WIDGET.DATE, BICst.WIDGET.YMD];
        var targetWidgetIds = BI.filter(BI.Utils.getAllWidgetIDs(), function(i, id){
            return BI.contains(targetWidgetType, BI.Utils.getWidgetTypeByID(id));
        });
        var widgetItems = BI.map(targetWidgetIds, function(idx, w){
            var wType = BI.Utils.getWidgetTypeByID(w);
            return {
                id: w,
                type: wType === BICst.WIDGET.DATE ? "bi.triangle_group_node" : "bi.select_date_widget_level0_item",
                text: BI.Utils.getWidgetNameByID(w),
                title: BI.Utils.getWidgetNameByID(w),
                value: w,
                isParent: wType === BICst.WIDGET.DATE,
                open: false
            }
        });
        return BI.sortBy(widgetItems, "text");
    },

    _getStartEndStructureByWidgetId: function (wid) {
        var fieldStructure = [];
        fieldStructure.push({
            id: BI.UUID(),
            pId: wid,
            type: "bi.select_date_widget_level1_item",
            text: BI.i18nText("BI-Start_Time"),
            title: BI.i18nText("BI-Start_Time"),
            value: {
                wId: wid,
                startOrEnd: BI.MultiDateParamPane.start
            }
        });
        fieldStructure.push({
            id: BI.UUID(),
            pId: wid,
            type: "bi.select_date_widget_level1_item",
            text: BI.i18nText("BI-End_Time"),
            title: BI.i18nText("BI-End_Time"),
            value: {
                wId: wid,
                startOrEnd: BI.MultiDateParamPane.end
            }
        });
        return fieldStructure;
    },

    setValue: function (v) {
        v = BI.isArray(v) ? v : [v];
        this.stored_value = v;
        this.tree.setValue(this.stored_value);
    },

    getValue: function () {
        return this.stored_value;
    }
});
BI.extend(BI.MultiDateParamPane, {
    start: 0,
    end: 1
});
BI.MultiDateParamPane.EVENT_CHANGE = "MultiDateParamPane.EVENT_CHANGE";
$.shortcut('bi.multi_date_param_pane', BI.MultiDateParamPane);