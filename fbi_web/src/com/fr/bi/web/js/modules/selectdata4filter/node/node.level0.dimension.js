/**
 * @class BI.DimensionSelectDataLevel0Node
 * @extends BI.NodeButton
 */
BI.DimensionSelectDataLevel0Node = BI.inherit(BI.NodeButton, {

    constants:{
        ICON_WIDTH: 25,
        ICON_HEIGHT: 25
    },

    _defaultConfig: function () {
        return BI.extend(BI.DimensionSelectDataLevel0Node.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-dimension-select-data-level0-node bi-list-item",
            id: "",
            pId: "",
            open: false,
            fontType: 0,
            height: 25
        })
    },
    _init: function () {
        BI.DimensionSelectDataLevel0Node.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        var cssInfo = this._getFontColor(o.fontType);
        this.checkbox = BI.createWidget({
            type: "bi.tree_group_node_checkbox"
        });
        var icon = BI.createWidget({
            type: "bi.center_adapt",
            cls: cssInfo.font,
            items: [{
                type: "bi.icon"
            }]
        });

        this.text = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            cls: cssInfo.textColor,
            whiteSpace: "nowrap",
            textHeight: o.height,
            height: o.height,
            hgap: o.hgap,
            text: o.text,
            value: o.value,
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
            element: this.element,
            items: [{
                width: 23,
                el: this.checkbox
            }, {
                width: 23,
                el: icon
            }, {
                el: this.text
            }]
        })
    },

    _getFontColor: function(type){
        switch(type){
            case BI.DimensionSelectDataLevel0Node.CLASSIFY:
                return {
                    font: "classify-font",
                    textColor: "dimension-classify-label"
                };
            case BI.DimensionSelectDataLevel0Node.SERIES:
                return {
                    font: "series-font",
                    textColor: "dimension-series-label"
                }
        }
    },

    doRedMark: function () {
        this.text.doRedMark.apply(this.text, arguments);
    },

    unRedMark: function () {
        this.text.unRedMark.apply(this.text, arguments);
    },

    doClick: function () {
        BI.DimensionSelectDataLevel0Node.superclass.doClick.apply(this, arguments);
        this.checkbox.setSelected(this.isOpened());
    },

    setOpened: function (v) {
        BI.DimensionSelectDataLevel0Node.superclass.setOpened.apply(this, arguments);
        this.checkbox.setSelected(v);
    },

    setValue: function (items) {
        BI.DimensionSelectDataLevel0Node.superclass.setValue.apply(this, arguments);
    }
});

BI.extend(BI.DimensionSelectDataLevel0Node, {
    CLASSIFY: 0,
    SERIES: 1
});

$.shortcut("bi.dimension_select_data_level0_node", BI.DimensionSelectDataLevel0Node);