/**
 * Created by roy on 16/2/19.
 */
BI.TableFieldItem = BI.inherit(BI.Single, {
    _defaultConfig: function () {
        return BI.extend(BI.TableFieldItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-multi-relation-table-filed-item",
            tableName: "",
            fieldName: ""
        })
    },

    _init: function () {
        BI.TableFieldItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.tableLabel = BI.createWidget({
            type: "bi.label",
            textHeight: 20,
            cls: "multi-relation-table",
            value: o.tableName
        });

        var dotLabel = BI.createWidget({
            type: "bi.label",
            textHeight: 20,
            cls: "multi-relation-dot",
            value: "."
        });

        this.fieldLabel = BI.createWidget({
            type: "bi.label",
            textHeight: 20,
            cls: "multi-relation-field",
            value: o.fieldName
        });

        BI.createWidget({
            type: "bi.vertical_adapt",
            element: this.element,
            items: [
                this.tableLabel,
                dotLabel,
                this.fieldLabel
            ]
        })

    },


    doRedMark: function () {
        this.tableLabel.doRedMark.apply(this.tableLabel, arguments);
        this.fieldLabel.doRedMark.apply(this.fieldLabel, arguments);
    },

    unRedMark: function () {
        this.tableLabel.unRedMark.apply(this.tableLabel, arguments);
        this.fieldLabel.unRedMark.apply(this.fieldLabel, arguments);
    },


    setValue: function (value) {
        this.tableLabel.setValue(value.tableName || "");
        this.fieldLabel.setValue(value.fieldName || "");
    },


    getValue: function () {
        var value = {};
        value.tableName = this.tableLabel.getValue();
        value.fieldName = this.fieldLabel.getValue();
        return value;
    }


});
$.shortcut('bi.multi_relation_table_field_item', BI.TableFieldItem);