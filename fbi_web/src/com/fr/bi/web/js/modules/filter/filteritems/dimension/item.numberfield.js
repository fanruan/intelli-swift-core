/**
 * @class BI.DimensionNumberFieldFilterItem

 * @extend BI.AbstractFilterItem
 * 维度过滤——数值字段类型过滤条件

 */
BI.DimensionNumberFieldFilterItem = BI.inherit(BI.AbstractFilterItem, {

    _constant: {
        LEFT_ITEMS_H_GAP: 5,
        CONTAINER_HEIGHT: 40,
        BUTTON_HEIGHT: 30,
        COMBO_WIDTH: 130,
        FIELD_NAME_BUTTON_WIDTH: 90,
        TEXT_BUTTON_H_GAP: 10,
        INPUT_WIDTH: 230
    },


    _defaultConfig: function(){
        return BI.extend(BI.DimensionNumberFieldFilterItem.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-dimension-number-field-item",
            afterValueChange: BI.emptyFn
        })
    },

    _init: function(){
        BI.DimensionNumberFieldFilterItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.isDimension = false;
        var wId = BI.Utils.getWidgetIDByDimensionID(this.options.dId);
        if(BI.contains(BI.Utils.getAllDimDimensionIDs(wId), this.options.dId)){
            this.isDimension = true;
        }

        var left = this._buildConditions();

        this.deleteButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-h-font"
        });
        this.deleteButton.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE, BI.Events.DESTROY, o.id, self);
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                el:{
                    type: "bi.left_right_vertical_adapt",
                    height: this._constant.CONTAINER_HEIGHT,
                    items: {

                        left: [left[0], left[1], left[2]],
                        right: [this.deleteButton]

                    },
                    lhgap: this._constant.LEFT_ITEMS_H_GAP,
                    rhgap: this._constant.LEFT_ITEMS_H_GAP
                }
            }]
        });
    },

    populate: function(item){
        this.filterType.setValue(item.filter_type);
        this._refreshFilterWidget(item.filter_type, item.filter_value);
    },

    _buildConditions: function(){
        var self = this, o = this.options;
        o.filter_type = this.isDimension === false ? o.filter_type : BICst.DIMENSION_FILTER_STRING.BELONG_VALUE;
        if(BI.isNull(o.dId)){
            return[];
        }
        var fieldName = BI.Utils.getDimensionNameByID(o.dId);
        this.fieldButton = BI.createWidget({
            type: "bi.text_button",
            text: fieldName,
            title: fieldName,
            width: this._constant.FIELD_NAME_BUTTON_WIDTH,
            height: this._constant.BUTTON_HEIGHT,
            textAlign: "left",
            hgap: this._constant.TEXT_BUTTON_H_GAP
        });
        this.fieldButton.on(BI.Controller.EVENT_CHANGE, function(){

            arguments[2] = self;
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        this.filterWidgetContainer = BI.createWidget({
            type: "bi.left"
        });
        this.id = o.id;
        this.filterType = BI.createWidget({
            type: "bi.text_value_down_list_combo",
            width: this._constant.COMBO_WIDTH,
            height: this._constant.BUTTON_HEIGHT,
            items: this.isDimension ? BICst.DIMENSION_FILTER_STRING_COMBO : BICst.DIMENSION_TAR_FILTER_NUMBER_COMBO
        });
        this.filterType.setValue(o.filter_type);
        this.filterType.on(BI.TextValueDownListCombo.EVENT_CHANGE, function(){
            self._refreshFilterWidget(self.filterType.getValue()[0]);
            self._setNodeData({
                filter_type : self.filterType.getValue()[0]
            });
            o.afterValueChange.apply(self, arguments);
        });
        this._refreshFilterWidget(o.filter_type, this.options.filter_value);
        return [this.fieldButton, this.filterType, this.filterWidgetContainer];
    },

    _refreshFilterWidget: function(filterType, initData){
        var w = BI.createWidget();
        switch (filterType){
            case BICst.DIMENSION_FILTER_STRING.CONTAIN:
            case BICst.DIMENSION_FILTER_STRING.NOT_CONTAIN:
                w = this._createStringInput(initData);
                break;
            case BICst.DIMENSION_FILTER_STRING.IS_NULL:
            case BICst.DIMENSION_FILTER_STRING.NOT_NULL:
                w = this.filterWidget = BI.createWidget();
                break;
            case BICst.DIMENSION_FILTER_STRING.BEGIN_WITH:
            case BICst.DIMENSION_FILTER_STRING.END_WITH:
                w = this._createStringInput(initData);
                break;
            case BICst.DIMENSION_FILTER_STRING.TOP_N:
            case BICst.DIMENSION_FILTER_STRING.BOTTOM_N:
                w = this._createNInput(initData);
                break;
            case BICst.DIMENSION_FILTER_STRING.BELONG_VALUE:
            case BICst.DIMENSION_FILTER_STRING.NOT_BELONG_VALUE:
                w = this._createStringBelongCombo(initData);
                break;
            case BICst.DIMENSION_FILTER_NUMBER.BELONG_VALUE:
            case BICst.DIMENSION_FILTER_NUMBER.NOT_BELONG_VALUE:
                w = this._createNumberIntervalFilter(initData);
                break;
            case BICst.DIMENSION_FILTER_NUMBER.MORE_THAN_AVG:
            case BICst.DIMENSION_FILTER_NUMBER.LESS_THAN_AVG:
            case BICst.DIMENSION_FILTER_NUMBER.IS_NULL:
            case BICst.DIMENSION_FILTER_NUMBER.NOT_NULL:
                w = this.filterWidget = BI.createWidget();
                break;
            case BICst.DIMENSION_FILTER_NUMBER.TOP_N:
            case BICst.DIMENSION_FILTER_NUMBER.BOTTOM_N:
                w = this._createNInput(initData);
                break;
        }
        this.filterWidgetContainer.empty();
        this.filterWidgetContainer.addItem(w);
    },

    _createStringInput: function (initData) {
        var self = this, o = this.options;
        this.filterWidget = BI.createWidget({
            type: "bi.sign_editor",
            cls: "condition-operator-input",
            allowBlank: true,
            height: this._constant.BUTTON_HEIGHT,
            width: this._constant.INPUT_WIDTH
        });
        this.filterWidget.on(BI.SignEditor.EVENT_CONFIRM, function(){
            self._setNodeData({
                filter_value : this.getValue()
            });
            o.afterValueChange.apply(self, arguments);
        });
        BI.isNotNull(initData) && this.filterWidget.setValue(initData);
        return this.filterWidget;
    },

    _createStringBelongCombo: function (initData) {
        var o = this.options, self = this;
        this.filterWidget = BI.createWidget({
            type: "bi.select_dimension_data_combo",
            dId: o.dId,
            width: 200,
            height: this._constant.BUTTON_HEIGHT
        });

        this.filterWidget.on(BI.SelectFieldDataCombo.EVENT_CONFIRM, function () {
            self._setNodeData({
                filter_value : this.getValue()
            });
            o.afterValueChange.apply(self, arguments);
        });
        BI.isNotNull(initData) && this.filterWidget.setValue(initData);
        return this.filterWidget;
    },

    _createNumberIntervalFilter: function(initData){
        var self = this, o = this.options;
        this.filterWidget = BI.createWidget({
            type: "bi.numerical_interval",
            width: this._constant.INPUT_WIDTH,
            height: this._constant.BUTTON_HEIGHT
        });
        BI.isNotNull(initData) && this.filterWidget.setValue(initData);
        this.filterWidget.on(BI.NumericalInterval.EVENT_CHANGE, function(){
            self._setNodeData({
                filter_value : this.getValue()
            });
            o.afterValueChange.apply(self, arguments);
        });
        return this.filterWidget;
    },
    _createNumberInput: function(initData){
        var self = this, o = this.options;
        this.filterWidget = BI.createWidget({
            type: "bi.text_editor",
            validationChecker: function(v){
                return BI.isPositiveInteger(v);
            },
            errorText: BI.i18nText("BI-Please_Input_Integer"),
            allowBlank: true,
            height: this._constant.BUTTON_HEIGHT,
            width: this._constant.INPUT_WIDTH
        });
        this.filterWidget.on(BI.TextEditor.EVENT_CONFIRM, function(){
            self._setNodeData({
                filter_value : this.getValue()
            });
            o.afterValueChange.apply(self, arguments);
        });
        BI.isNotNull(initData) && BI.isNumeric(initData) && this.filterWidget.setValue(initData);
        return this.filterWidget;
    },

    _createNInput: function(initData){
        this._createNumberInput(initData);
        return BI.createWidget({
            type: "bi.inline",
            items: [{
                type: "bi.label",
                text: "N = ",
                height: 30
            }, this.filterWidget]
        });
    },

    _setNodeData: function(v){
        var o = this.options;
        o.node.set("data", BI.extend(o.node.get("data"), v));
    },

    getValue: function(){
        return {
            target_id: this.options.dId,
            filter_type: this.filterType.getValue()[0],
            filter_value: this.filterWidget.getValue()
        }
    }
});
$.shortcut("bi.dimension_number_field_filter_item", BI.DimensionNumberFieldFilterItem);