/**
 *
 * @class BI.GroupSelectFieldsItem
 * @extends BI.Single
 */
BI.GroupSelectFieldsItem = BI.inherit(BI.Single, {
    _defaultConfig: function () {
        return BI.extend(BI.GroupSelectFieldsItem.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-group-select-fields-item",
            height: 25,
            hgap: 0,
            fieldType: BICst.COLUMN.STRING,
            lgap: 0,
            rgap: 25
        })
    },

    _getFieldClass: function (type) {
        switch (type) {
            case BICst.COLUMN.STRING:
                return "select-group-field-string-font";
            case BICst.COLUMN.NUMBER:
                return "select-group-field-number-font";
            case BICst.COLUMN.DATE:
                return "select-group-field-date-font";
        }
    },

    _init: function () {
        BI.GroupSelectFieldsItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.button = BI.createWidget({
            type: "bi.blank_icon_text_item",
            trigger: "mousedown",
            cls: "group-select-fields-item-button " + this._getFieldClass(o.fieldType),
            blankWidth: 10,
            text: o.text,
            value: o.value,
            height: 25,
            textLgap: 10,
            textRgap: 5
        });
        this.button.on(BI.Controller.EVENT_CHANGE, function (type) {
            if (type === BI.Events.CLICK) {
                self.setSelected(self.isSelected());
            }
            self.fireEvent(BI.Controller.EVENT_CHANGE, BI.Events.CLICK, self.getValue(), self);
        });
        this.button.element.draggable(o.drag);

        BI.createWidget({
            type: "bi.default",
            element: this.element,
            items: [this.button]
        });

    },

    isSelected: function () {
        return this.button.isSelected();
    },

    setSelected: function (b) {
        this.button.setSelected(b);
    },

    doRedMark: function () {
        this.button.doRedMark.apply(this.button, arguments);
    },

    unRedMark: function () {
        this.button.unRedMark.apply(this.button, arguments);
    },

    doHighLight: function () {
        this.button.doHighLight.apply(this.button, arguments);
    },

    unHighLight: function () {
        this.button.unHighLight.apply(this.button, arguments);
    }
});

$.shortcut("bi.group_select_fields_item", BI.GroupSelectFieldsItem);