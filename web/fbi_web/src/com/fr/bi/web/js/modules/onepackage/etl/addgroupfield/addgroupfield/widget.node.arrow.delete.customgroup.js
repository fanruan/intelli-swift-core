/**
 * Created by roy on 15/10/16.
 * 带删除按钮的树节点
 */
BI.ArrowNodeDelete = BI.inherit(BI.NodeButton, {
    _defaultConfig: function () {
        return BI.extend(BI.ArrowNodeDelete.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-arrow-group-node bi-list-item",
            id: "",
            pId: "",
            open: false,
            height: 25,
            hoverClass: "search-close-h-font"
        });
    },
    _init: function () {
        var self = this, o = this.options;
        BI.ArrowNodeDelete.superclass._init.apply(this, arguments);
        this.checkbox = BI.createWidget({
            iconWidth: 13,
            iconHeight: 13,
            type: "bi.arrow_tree_group_node_checkbox"
        });

        this.editor = BI.createWidget({
            type: "bi.editor",
            textAlign: "left",
            whiteSpace: "nowrap",
            textHeight: o.height,
            height: o.height,
            hgap: o.hgap,
            validationChecker: o.validationChecker,
            quitChecker: function () {
                return true;
            },
            value: o.value,
            py: o.py
        })


        this.deletebutton = BI.createWidget({
            type: "bi.icon_button",
            stopPropagation: true

        });

        this.checkbox.on(BI.Controller.EVENT_CHANGE, function (type) {
            if (type === BI.Events.CLICK) {
                self.setSelected(self.isSelected());
            }
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });


        this.editor.on(BI.Editor.EVENT_CHANGE, function () {
            self.fireEvent(BI.ArrowNodeDelete.EVENT_CHANGE);
        });

        this.deletebutton.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.ArrowNodeDelete.EVENT_CLICK_DELETE);
        });

        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                width: 23,
                el: this.checkbox
            }, {
                el: this.editor
            }, {
                el: this.deletebutton,
                width: 25
            }]
        });

        this.element.hover(function () {
            self.deletebutton.element.addClass(o.hoverClass);
        }, function () {
            self.deletebutton.element.removeClass(o.hoverClass);
        });
    },


    doRedMark: function () {
        this.text.doRedMark.apply(this.text, arguments);
    },

    unRedMark: function () {
        this.text.unRedMark.apply(this.text, arguments);
    },

    doClick: function () {
        BI.ArrowNodeDelete.superclass.doClick.apply(this, arguments);
        this.checkbox.setSelected(this.isOpened());
    },

    setOpened: function (v) {
        BI.ArrowNodeDelete.superclass.setOpened.apply(this, arguments);
        this.checkbox.setSelected(v);
    },

    setValue: function (v) {
        return this.editor.setValue(v);
    },

    getValue: function () {
        return this.editor.getValue();
    }
})

BI.ArrowNodeDelete.EVENT_CLICK_DELETE = "EVENT_CLICK_DELETE";
BI.ArrowNodeDelete.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.etl_add_group_field_arrow_group_node_delete", BI.ArrowNodeDelete);