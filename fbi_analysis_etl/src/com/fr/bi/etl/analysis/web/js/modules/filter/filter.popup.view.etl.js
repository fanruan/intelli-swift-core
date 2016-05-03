BI.ETLFilterPopupView = BI.inherit(BI.Widget, {
    _constants  :{
        WIDTH : 240,
        MAXWIDTH : 240,
        HEIGHT : 290
    },
    _defaultConfig: function () {
        return BI.extend(BI.ETLFilterPopupView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: 'bi-filter-popup-view-etl'
        });
    },

    _init: function () {
        BI.ETLFilterPopupView.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.loader = BI.createWidget({
            type: "bi.filter_popup_pane_etl",
            field_type : o.field_type,
            fields : o.fields,
            field_name : o.field_name,
            fieldValuesCreator : o.fieldValuesCreator
        });
        this.popupView = BI.createWidget({
            type: "bi.multi_popup_view",
            width : self._constants.WIDTH,
            maxWidth : self._constants.MAXWIDTH,
            height : self._constants.HEIGHT,
            element: this.element,
            buttons: [BI.i18nText('BI-Cancel'), BI.i18nText('BI-Sure')],
            el: this.loader
        });
        this.popupView.on(BI.MultiPopupView.EVENT_CLICK_TOOLBAR_BUTTON, function (index) {
            switch (index) {
                case 0:
                    self.fireEvent(BI.ETLFilterPopupView.EVENT_CLICK_CANCEL);
                    break;
                case 1:
                    self.fireEvent(BI.ETLFilterPopupView.EVENT_CLICK_CONFIRM);
                    break;
            }
        });
    },

    setValue: function (v) {
        this.popupView.setValue(v);
    },

    getValue: function () {
        return this.popupView.getValue();
    },

    populate: function (items) {
        this.loader.initValue();
        this.popupView.populate.apply(this.popupView, arguments);
    },

    setEnable: function (arg) {
        this.popupView.setEnable(arg);
    },

    resetHeight: function (h) {
    },

    resetWidth: function (w) {
    }
});
BI.ETLFilterPopupView.EVENT_CLICK_CONFIRM = "EVENT_CLICK_CONFIRM";
BI.ETLFilterPopupView.EVENT_CLICK_CANCEL = "EVENT_CLICK_CANCEL";
$.shortcut("bi.filter_popup_etl", BI.ETLFilterPopupView);
