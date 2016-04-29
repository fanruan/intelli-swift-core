BI.ETLFilterValueChooser = BI.inherit(BI.Widget, {
    _init: function () {
        BI.ETLFilterValueChooser.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.pane = BI.createWidget({
            type: 'bi.multi_value_chooser_pane_etl',
            element: this.element,
            fieldValuesCreator: o.fieldValuesCreator
        });
        this.pane.on(BI.ETLMultiValueChooserPane.EVENT_CONFIRM, function () {
            self.fireEvent(BI.ETLFilterValueChooser.EVENT_CONFIRM);
        })
    },

    populate : function () {
        this.pane.populate();
    },

    setValue: function (v) {
        this.pane.setValue(v);
    },

    getValue: function () {
        return this.pane.getValue();
    }
});
BI.ETLFilterValueChooser.EVENT_CONFIRM = "ETLFilterValueChooser.EVENT_CONFIRM";
$.shortcut('bi.filter_value_chooser_etl', BI.ETLFilterValueChooser);