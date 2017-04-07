/**
 * Created by windy on 2017/4/6.
 */
BI.AnalysisETLSelectDataLevelNode = BI.inherit(BI.NodeButton, {
    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLSelectDataLevelNode.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-select-data-level0-node bi-list-item",
            id: "",
            pId: "",
            open: false,
            layer: 0,
            height: 25
        })
    },
    _init: function () {
        BI.AnalysisETLSelectDataLevelNode.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.checkbox = BI.createWidget({
            type: "bi.tree_group_node_checkbox"
        })
        this.text = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            whiteSpace: "nowrap",
            textHeight: o.height,
            height: o.height,
            hgap: o.hgap,
            text: o.text,
            value: o.value,
            keyword: o.keyword,
            py: o.py
        });
        this.checkbox.on(BI.Controller.EVENT_CHANGE, function (type) {
            if(type ===  BI.Events.CLICK) {
                self.setSelected(self.isSelected());
            }
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        })
        BI.createWidget({
            type: "bi.htape",
            element: this,
            items: [{
                el: {
                    type: "bi.layout"
                },
                width: o.layer * 20
            }, {
                width: 23,
                el: this.checkbox
            }, {
                el: this.text
            }]
        })
        if(BI.isFunction(o.listener)) {
            o.listener.apply(this);
        }
    },

    setEnable : function (v) {
        BI.AnalysisETLSelectDataLevelNode.superclass.setEnable.apply(this, arguments);
        this.checkbox.setEnable(v);
    },

    doRedMark: function () {
        this.text.doRedMark.apply(this.text, arguments);
    },

    unRedMark: function () {
        this.text.unRedMark.apply(this.text, arguments);
    },

    doClick: function () {
        BI.AnalysisETLSelectDataLevelNode.superclass.doClick.apply(this, arguments);
        this.checkbox.setSelected(this.isOpened());
    },

    setOpened: function (v) {
        BI.AnalysisETLSelectDataLevelNode.superclass.setOpened.apply(this, arguments);
        this.checkbox.setSelected(v);
    }

});

BI.shortcut("bi.analysis_etl_select_data_level_node", BI.AnalysisETLSelectDataLevelNode);