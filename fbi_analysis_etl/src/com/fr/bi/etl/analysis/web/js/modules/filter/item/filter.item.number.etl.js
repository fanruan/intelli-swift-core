/**
 * Created by 小灰灰 on 2016/2/29.
 */
BI.NumberETLFilterItem = BI.inherit(BI.AbstractETLFilterItem, {
    _defaultConfig: function () {
        return BI.extend(BI.NumberETLFilterItem.superclass._defaultConfig.apply(this, arguments), {
            filterTypes : BICst.ETL_FILTER_NUMBER_COMBO,
            defaultType : BICst.TARGET_FILTER_NUMBER.CONTAINS
        })
    },

    _init: function () {
        BI.NumberETLFilterItem.superclass._init.apply(this, arguments);
    },

    _refreshFilterWidget: function (value) {
        var item;
        switch (this.filter_type) {
            case BICst.TARGET_FILTER_NUMBER.CONTAINS:
                item = this._createMultiChooserPane();
                break;
            case BICst.TARGET_FILTER_NUMBER.BELONG_VALUE:
            case BICst.TARGET_FILTER_NUMBER.NOT_BELONG_VALUE:
                item = this._createNumberIntervalFilter();
                break;
            case BICst.TARGET_FILTER_NUMBER.EQUAL_TO:
            case BICst.TARGET_FILTER_NUMBER.NOT_EQUAL_TO:
                item = this._createNumberInput();
                break;
            case BICst.TARGET_FILTER_NUMBER.LARGE_OR_EQUAL_CAL_LINE:
            case BICst.TARGET_FILTER_NUMBER.SMALL_OR_EQUAL_CAL_LINE:
                item = this._createNumberOneSidePane(this.filter_type);
                break;
            case BICst.TARGET_FILTER_NUMBER.TOP_N:
            case BICst.TARGET_FILTER_NUMBER.BOTTOM_N:
                item = this._createNumberNInput(this.filter_type);
                break;
            case BICst.FILTER_TYPE.FORMULA:
                item = this._createFormular();
                break;
        }
        this.filterValueContainer.empty();
        BI.isNotNull(value) && this.filterWidget.setValue(value);
        if (BI.isFunction(item.populate)){
            item.populate.apply(item, arguments);
        }
        this.filterValueContainer.addItem(item);
    },

    _createNumberIntervalFilter: function(){
        var self = this, o = this.options;
        this.filterWidget = BI.createWidget({
            type: "bi.numerical_interval",
            width: this._constant.INPUT_WIDTH,
            height: this._constant.LINE_SIZE
        });
        this.filterWidget.on(BI.NumericalInterval.EVENT_VALID, function () {
            self.fireEvent(BI.AbstractETLFilterItem.EVENT_VALUE_CHANGED);
        })
        return this.filterWidget;
    },

    _createNumberInput: function(){
        var self = this, o = this.options;
        this.filterWidget = BI.createWidget({
            type: "bi.text_editor",
            validationChecker: function(v){
                return BI.isNumeric(v);
            },
            errorText: BI.i18nText("BI-Numerical_Interval_Input_Data"),
            height: this._constant.LINE_SIZE,
            width: this._constant.INPUT_WIDTH
        });
        this.filterWidget.on(BI.TextEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AbstractETLFilterItem.EVENT_VALUE_CHANGED);
        })
        return this.filterWidget;
    },

    _createNumberOneSidePane: function(filterType){
        var self = this, o = this.options;
        var op ={
            type: "bi.filter_number_oneside_etl",
            filter_type : filterType,
            field_name : o.field_name,
        }
        op[ETLCst.FIELDS] = o[ETLCst.FIELDS];
        this.filterWidget = BI.createWidget(op);
        this.filterWidget.on(BI.ETLNumberFilterOneSidePane.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AbstractETLFilterItem.EVENT_VALUE_CHANGED);
        })
        return this.filterWidget;
    },

    _createNumberNInput: function(filterType){
        var self = this, o = this.options;
        var op ={
            type: 'bi.filter_number_n_etl',
            field_name : o.field_name,
            filterType : filterType
        }
        op[ETLCst.FIELDS] = o[ETLCst.FIELDS];
        this.filterWidget = BI.createWidget(op);
        this.filterWidget.on(BI.ETLNumberNFilterPane.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AbstractETLFilterItem.EVENT_VALUE_CHANGED);
        })
        return this.filterWidget;
    }
});
$.shortcut('bi.number_filter_item_etl', BI.NumberETLFilterItem)

