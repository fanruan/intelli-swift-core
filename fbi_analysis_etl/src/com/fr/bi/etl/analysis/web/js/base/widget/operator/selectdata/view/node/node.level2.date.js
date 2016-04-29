/**
 * Created by GUY on 2015/9/15.
 * @class BI.DetailSelectDataLevel2DateNode
 * @extends BI.NodeButton
 * 相关表中的日期字段
 */
BI.AnalysisDetailSelectDataLevel2DateNode = BI.inherit(BI.NodeButton, {
    _defaultConfig: function () {
        return BI.extend(BI.AnalysisDetailSelectDataLevel2DateNode.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-detail-select-data-level2-date-node bi-select-data-level1-date-node  bi-list-item",
            id: "",
            pId: "",
            open: false,
            height: 25
        })
    },
    _init: function () {
        BI.AnalysisDetailSelectDataLevel2DateNode.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.button = BI.createWidget({
            type: "bi.icon_text_item",
            cls: "select-data-field-date-group-font",
            text: o.text,
            value: o.value,
            height: o.height,
            textLgap: 10,
            textRgap: 5
        });

        this.checkbox = BI.createWidget({
            type: "bi.tree_group_node_checkbox"
        })
        this.checkbox.on(BI.Controller.EVENT_CHANGE, function (type) {
            if(type ===  BI.Events.CLICK) {
                self.setSelected(self.isSelected());
            }
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        this.button.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        })
        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.layout"
                },
                width: 35
            },{
                el: this.button
            }, {
                el: this.checkbox,
                width: 25
            }]
        })
        if(BI.isFunction(o.listener)) {
            o.listener.apply(this);
        }
    },

    doRedMark: function () {
        this.button.doRedMark.apply(this.button, arguments);
    },

    unRedMark: function () {
        this.button.unRedMark.apply(this.button, arguments);
    },

    doClick: function () {
        BI.AnalysisDetailSelectDataLevel2DateNode.superclass.doClick.apply(this, arguments);
        this.checkbox.setSelected(this.isOpened());
    },

    setOpened: function (v) {
        BI.AnalysisDetailSelectDataLevel2DateNode.superclass.setOpened.apply(this, arguments);
        this.checkbox.setSelected(v);
    },

    setValue: function (items) {
        BI.AnalysisDetailSelectDataLevel2DateNode.superclass.setValue.apply(this, arguments);
    }
});

$.shortcut("bi.analysis_detail_select_data_level2_date_node", BI.AnalysisDetailSelectDataLevel2DateNode);