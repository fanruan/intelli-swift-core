/**
 * Created by GUY on 2015/9/6.
 * @class BI.SelectDataLevel2Node
 * @extends BI.NodeButton
 */
BI.SelectDataLevel2Node = BI.inherit(BI.NodeButton, {
    _defaultConfig: function () {
        return BI.extend(BI.SelectDataLevel2Node.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-select-data-level2-node bi-list-item",
            id: "",
            pId: "",
            open: false,
            height: 25
        })
    },
    _init: function () {
        var title = this.options.title;
        this.options.title = "";
        BI.SelectDataLevel2Node.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.checkbox = BI.createWidget({
            type: "bi.tree_group_node_checkbox"
        });
        this.text = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            whiteSpace: "nowrap",
            textHeight: o.height,
            height: o.height,
            hgap: o.hgap,
            text: o.text,
            value: o.value,
            title: title,
            py: o.py
        });
        this.tip = BI.createWidget({
            type: "bi.label",
            cls: "select-data-selected-count-label",
            whiteSpace: "nowrap",
            width: 25,
            height: o.height
        });
        this.checkbox.on(BI.Controller.EVENT_CHANGE, function (type) {
            if (type === BI.Events.CLICK) {
                self.setSelected(self.isSelected());
            }
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.layout"
                },
                width: 40
            }, {
                width: 23,
                el: this.checkbox
            }, {
                el: this.text
            }, {
                width: 25,
                el: this.tip
            }]
        })
    },

    doRedMark: function () {
        this.text.doRedMark.apply(this.text, arguments);
    },

    unRedMark: function () {
        this.text.unRedMark.apply(this.text, arguments);
    },

    doClick: function () {
        BI.SelectDataLevel2Node.superclass.doClick.apply(this, arguments);
        this.checkbox.setSelected(this.isOpened());
    },

    setOpened: function (v) {
        BI.SelectDataLevel2Node.superclass.setOpened.apply(this, arguments);
        this.checkbox.setSelected(v);
    },

    setValue: function (items) {
        BI.SelectDataLevel2Node.superclass.setValue.apply(this, arguments);
        if (BI.isEmpty(items)) {
            this.tip.setText("");
        } else {
            this.tip.setText("(" + items.length + ")");
        }
        this.tip.setTitle(items.toString());
    },

    setEnable: function (b) {
        BI.SelectDataLevel2Node.superclass.setEnable.apply(this, arguments);
        this.checkbox.setEnable(b);
    }
});

$.shortcut("bi.select_data_level2_node", BI.SelectDataLevel2Node);