/**
 * Created by GUY on 2015/9/15.
 * @class BI.SelectDataLevel1DateNode
 * @extends BI.NodeButton
 */
BI.AnalysisETLSelectDataLevel1DateNode = BI.inherit(BI.NodeButton, {
    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLSelectDataLevel1DateNode.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-select-data-level1-date-node bi-list-item",
            id: "",
            pId: "",
            open: false,
            height: 25
        })
    },
    _init: function () {
        BI.AnalysisETLSelectDataLevel1DateNode.superclass._init.apply(this, arguments);
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
                width: 10
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
        BI.AnalysisETLSelectDataLevel1DateNode.superclass.doClick.apply(this, arguments);
        this.checkbox.setSelected(this.isOpened());
    },

    setOpened: function (v) {
        BI.AnalysisETLSelectDataLevel1DateNode.superclass.setOpened.apply(this, arguments);
        this.checkbox.setSelected(v);
    },

    setValue: function (items) {
        BI.AnalysisETLSelectDataLevel1DateNode.superclass.setValue.apply(this, arguments);
    }
});

$.shortcut("bi.analysis_etl_select_data_level1_date_node", BI.AnalysisETLSelectDataLevel1DateNode);