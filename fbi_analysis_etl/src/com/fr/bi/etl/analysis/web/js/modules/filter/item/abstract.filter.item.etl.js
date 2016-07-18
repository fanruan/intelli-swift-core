BI.AbstractETLFilterItem = BI.inherit(BI.Widget, {
    _constant: {
        GAP : 5,
        LINE_SIZE: 30,
        COMBO_WIDTH: 190,
        INPUT_WIDTH: 220
    },
    _defaultConfig: function () {
        return BI.extend(BI.AbstractETLFilterItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-etl-filter-item"
        })
    },

    _init: function () {
        BI.AbstractETLFilterItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var value = o.value || {};
        self.filter_type = BI.isNotNull(value.filter_type) ? value.filter_type : o.defaultType;
        self.filter_value = value.filter_value;
        this.deleteButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "delete-font",
            width : self._constant.LINE_SIZE,
            height : self._constant.LINE_SIZE
        });
        this.deleteButton.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.AbstractETLFilterItem.EVENT_VALUE_DELETED);
        });

        BI.createWidget({
            type: "bi.vertical",
            tgap : self._constant.GAP,
            scrolly : false,
            element : this.element,
            items: [{
                el: {
                    type: "bi.absolute",
                    height: self._constant.LINE_SIZE,
                    items: [{
                        el: self._createFilterTypeCombo(),
                        left : 0
                    },
                        {
                            el: this.deleteButton,
                            left : self._constant.COMBO_WIDTH
                        }]
                }
            }, {
                el :self._createFilterValueWidget()
            }]
        });
    },

    _createFilterValueWidget: function () {
        var self = this, o = this.options;
        this.filterValueContainer = BI.createWidget({
            type: "bi.vertical",
            scrolly : false
        });
        this._refreshFilterWidget(self.filter_value);
        return this.filterValueContainer;
    },

    _createFilterTypeCombo: function () {
        var self = this, o = this.options;
        this.filterType = BI.createWidget({
            type: "bi.text_value_down_list_combo",
            width: this._constant.COMBO_WIDTH,
            height: this._constant.LINE_SIZE,
            items: o.filterTypes
        });
        this.filterType.on(BI.TextValueDownListCombo.EVENT_CHANGE, function () {
            self.filter_type = self.filterType.getValue()[0];
            self._refreshFilterWidget();
            self.fireEvent(BI.AbstractETLFilterItem.EVENT_VALUE_CHANGED);
        });
        this.filterType.setValue(BI.isNotNull(o.value.filter_type) ? o.value.filter_type : o.defaultType);
        return this.filterType;
    },

    _createFormular: function(){
        var self = this;
        var o = this.options;
        var op ={
            type : 'bi.filter_etl_formula_setting'
        }
        op[ETLCst.FIELDS] = o[ETLCst.FIELDS];
        this.filterWidget = BI.createWidget(op);
        this.filterWidget.on(BI.ETLFormulaSettingPane.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AbstractETLFilterItem.EVENT_VALUE_CHANGED);
        })
        return this.filterWidget;
    },

    _createMultiChooserPane: function () {
        var o = this.options, self = this;
        this.filterWidget = BI.createWidget({
            type: "bi.filter_value_chooser_etl",
            fieldValuesCreator : o.fieldValuesCreator
        });
        this.filterWidget.on(BI.ETLFilterValueChooser.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AbstractETLFilterItem.EVENT_VALUE_CHANGED);
        })
        return this.filterWidget;
    },

    _createEmptyWidget: function () {
        this.filterWidget = BI.createWidget({
            type: "bi.layout"
        });
        return this.filterWidget;
    },

    getValue: function () {
        return {
            field_name : this.options.field_name,
            filter_type : this.filter_type,
            filter_value : this.filterWidget.getValue()
        }
    }
});
BI.AbstractETLFilterItem.EVENT_VALUE_CHANGED = 'EVENT_VALUE_CHANGED';
BI.AbstractETLFilterItem.EVENT_VALUE_DELETED = 'EVENT_VALUE_DELETED';