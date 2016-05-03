BI.ETLFilterPopupPane = BI.inherit(BI.Widget, {
    _constants :{
        HEIGHT : 257,
        MARGIN_LEFT : 9,
        CONDITIONS_TOP : 5,
        CONDITIONS_WIDTH : 220,
        CONDITIONS_HEIGHT : 195,
        CONDITIONS_GAP : 5,
        CONDITIONS_VGAP : 10,
        BUTTONS_TOP : 222,
        BUTTON_WIDTH : 98,
        BUTTON_HEIGHT : 30,
        BUTTON_HGAP : 22
    },

    _defaultConfig: function () {
        return BI.extend(BI.ETLFilterPopupPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: 'bi-etl-filter-popup-pane',
            originValue : {value : [{}]},
            height: this._constants.HEIGHT
        });
    },

    _init: function () {
        BI.ETLFilterPopupPane.superclass._init.apply(this, arguments);
        var self = this, opts = this.options;
        self.condition =  BI.createWidget({
            type: "bi.vertical",
            height: self._constants.CONDITIONS_HEIGHT,
            width: self._constants.CONDITIONS_WIDTH,
            vgap : self._constants.CONDITIONS_GAP
        }),
        BI.createWidget({
            type: "bi.absolute",
            element : this.element,
            items : [{
                        el :self.condition,
                        left : self._constants.MARGIN_LEFT,
                        top : self._constants.CONDITIONS_TOP
                     },
                    {
                        el :self._createButtons(),
                        left : self._constants.MARGIN_LEFT,
                        top : self._constants.BUTTONS_TOP
                    }]
        });
    },
    _createConditionItems : function (){
        var self = this, o = self.options;
        var value = self.storeValue.value;
        var conditionItems = [];
        if (value.length === 0){
            conditionItems.push(BI.createWidget({
                type : 'bi.label',
                height : 50,
                cls : 'bi-filter-popup-pane-etl-empty-label',
                text : BI.i18nText('BI-Please_Add_Filter'),
                textAlign : 'center'
            }))
            return conditionItems;
        }
        conditionItems.push(self._createConditionItem(0));
        if (value.length === 2){
            if (FR.isNull(self.storeValue.type)){
                self.storeValue.type = BICst.FILTER_TYPE.AND;
            }
            conditionItems.push(self._createAndOr());
            conditionItems.push(self._createConditionItem(1));
        }
        return conditionItems;
    },

    _createConditionItem : function (index){
        var self = this, o = self.options;
        var value = self.storeValue.value;
        var condition =  BI.createWidget({
            type : self._createOneConditionType(),
            fields : o.fields,
            field_name : o.field_name,
            value : value[index],
            fieldValuesCreator : o.fieldValuesCreator
        });
        condition.on(BI.AbstractETLFilterItem.EVENT_VALUE_CHANGED, function(){
            value[index] = condition.getValue();
        })
        condition.on(BI.AbstractETLFilterItem.EVENT_VALUE_DELETED, function(){
            value.splice(index, index + 1);
            delete self.storeValue.type;
            self.populate();
        })
        self.addButton.setEnable(index === 0);
        return condition;
    },

    _createAndOr : function () {
        var self = this;
        if (FR.isNull(self.storeValue.type)){
            self.storeValue.type = BICst.FILTER_TYPE.AND;
        }
        var andOr = BI.createWidget({
            type : 'bi.segment',
            items: BICst.FILTER_CONDITION_TYPE
        });
        andOr.setValue(self.storeValue.type);
        andOr.on(BI.Controller.EVENT_CHANGE, function(){
            self.storeValue.type = andOr.getValue()[0];
        });
        return andOr;
    },
    
    _createOneConditionType : function (){
        var self = this, opts = self.options;
        switch (opts.field_type){
            case BICst.COLUMN.STRING :
                return 'bi.string_filter_item_etl';
            case BICst.COLUMN.DATE :
                return 'bi.date_filter_item_etl';
            case BICst.COLUMN.NUMBER :
                return 'bi.number_filter_item_etl';
            default :
                return 'bi.number_filter_item_etl';
        }
    },
    _createButtons: function () {
        var self = this, opts = self.options;
        this.addButton = BI.createWidget({
            type : "bi.button",
            width: self._constants.BUTTON_WIDTH,
            height: self._constants.BUTTON_HEIGHT,
            level: 'ignore',
            warningTitle : BI.i18nText('BI-ETL_Filter_Item_Already_Full'),
            text : BI.i18nText('BI-Add_Condition')
        });
        this.addButton.on(BI.Button.EVENT_CHANGE, function(){
            var value = self.storeValue.value;
            value.push({});
            if (value.length === 1){
                self.condition.empty();
            }
            if (value.length === 2){
                self.condition.addItem(self._createAndOr());
            }
            self.condition.addItem(self._createConditionItem(value.length - 1));
        });
        this.clearButton = BI.createWidget({
            type : "bi.button",
            width: self._constants.BUTTON_WIDTH,
            height: self._constants.BUTTON_HEIGHT,
            level: 'ignore',
            text : BI.i18nText('BI-Clears') + BI.i18nText('BI-Filter')
        });
        this.clearButton.on(BI.Button.EVENT_CHANGE, function(){
            self._clearValue();
            self.populate();
        });
        return BI.createWidget({
            type: "bi.absolute",
            items: [{
                        el : self.addButton,
                        left : 0
                    },{
                        el : self.clearButton,
                        left : self._constants.BUTTON_WIDTH + self._constants.BUTTON_HGAP
                    }
            ]
        });
    },

    _clearValue : function () {
        var values = this.storeValue.value;
        BI.each(values, function (i, item) {
            values[i] = {
                filter_type : item.filter_type
            }
        })
    },

    initValue : function () {
        var self = this;
        if (BI.isNull(self.storeValue) || !BI.isNotEmptyArray(this.storeValue.value)){
            self.storeValue = BI.deepClone(this.options.originValue);
        }
    },

    setValue: function (v) {
        this.storeValue = v || {};
    },

    getValue: function () {
        return this.storeValue;
    },

    populate: function () {
        var self = this;
        self.condition.empty();
        BI.each(self._createConditionItems(), function(idx, item){
            self.condition.addItem(item)
        })
    },

    resetHeight: function (h) {

    },

    resetWidth: function (w) {

    }
});
$.shortcut("bi.filter_popup_pane_etl", BI.ETLFilterPopupPane);