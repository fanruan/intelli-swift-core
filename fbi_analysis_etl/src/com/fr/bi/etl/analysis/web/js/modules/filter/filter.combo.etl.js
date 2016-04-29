BI.ETLFilterCombo = BI.inherit(BI.Single, {
    _constant: {
        BUTTON_HEIGHT: 30,
        BUTTON_WIDTH : 30
    },
    _defaultConfig : function () {
        return BI.extend(BI.ETLFilterCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: 'bi-etl-filter-combo',
            itemsCreator: BI.emptyFn
        });
    },

    _init: function () {
        BI.ETLFilterCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.trigger = BI.createWidget({
            type: "bi.icon_trigger",
            el:{extraCls: "filter-font"},
            width: self._constant.BUTTON_WIDTH,
            height: self._constant.BUTTON_WIDTH
        });
        this.popup = BI.createWidget({
            type: "bi.filter_popup_etl",
            field_type : o.field_type,
            field_name : o.field_name,
            fields : o.fields,
            fieldValuesCreator : o.fieldValuesCreator
        });
        this.combo = BI.createWidget({
            type: "bi.combo",
            element: this.element,
            toggle: false,
            el: this.trigger,
            popup: this.popup,
            hideChecker : function (e) {
                return $(e.target).parents('.bi-float-box').length === 0
            }
        });
        this.popup.on(BI.ETLFilterPopupView.EVENT_CLICK_CONFIRM, function () {
            self.storedValue = BI.deepClone(self.combo.getValue());
            self.combo.hideView();
            self.fireEvent(BI.ETLFilterCombo.EVENT_VALUE_CHANGED);
        });
        this.popup.on(BI.ETLFilterPopupView.EVENT_CLICK_CANCEL, function () {
            self.setValue(self.storedValue);
            self.combo.hideView();
        });
        this.combo.on(BI.Combo.EVENT_BEFORE_POPUPVIEW, function(){
            self.popup.populate();
        })
    },

    setValue: function(v){
        self.storedValue = v;
        this.combo.setValue(BI.deepClone(v));
    },

    getValue : function (){
        return BI.extend(this.storedValue, {field_name : this.options.field_name});
    },

    populate : function (items){
        this.combo.populate.apply(this.combo, arguments);
    }
});
BI.ETLFilterCombo.EVENT_VALUE_CHANGED = 'EVENT_VALUE_CHANGED';
$.shortcut("bi.filter_combo_etl", BI.ETLFilterCombo);
