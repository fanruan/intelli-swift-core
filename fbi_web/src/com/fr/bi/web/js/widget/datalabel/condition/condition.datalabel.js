BI.DataLabelCondition = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        var conf = BI.DataLabelCondition.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {

        });
    },

    _init: function () {
        BI.DataLabelCondition.superclass._init.apply(this, arguments);
        var self = this;
        var addButton = BI.createWidget({
            type: "bi.button",
            text: "添加条件",
            width: 80,
            height: 24,
            handler: function () {
                self.conditions.addItem();
            }
        });
        this.conditions = BI.createWidget({
            type: "bi.data_label_condition_group"
        });
        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                type: "bi.right",
                items: [addButton],
                rgap: 6,
                tgap: 6,
                bgap: 2
            }, this.conditions]
        })
    }
});

$.shortcut("bi.data_label_condition", BI.DataLabelCondition);