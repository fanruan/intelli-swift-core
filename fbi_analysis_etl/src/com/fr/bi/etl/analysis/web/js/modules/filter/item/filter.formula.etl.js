/**
 * Created by 小灰灰 on 2016/3/10.
 */
BI.ETLFormulaSettingPane = BI.inherit(BI.Widget, {
    _constants: {
        BUTTON_HEIGHT : 30,
        BUTTON_WIDTH : 88,
        BUTTON_LEFT : 65
    },

    _defaultConfig: function () {
        var conf = BI.ETLFormulaSettingPane.superclass._defaultConfig.apply(this, arguments)
        return BI.extend(conf, {
            baseCls: "bi-etl-filter-formula-setting"
        })
    },

    _init: function () {
        BI.ETLFormulaSettingPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        self.button = BI.createWidget({
            type :'bi.button',
            height : self._constants.BUTTON_HEIGHT,
            width : self._constants.BUTTON_WIDTH,
            text : BI.i18nText('BI-Edit') + BI.i18nText('BI-Formula')
        });
        self.fieldItems = [];
        BI.each(o.fields, function (i, item) {
            self.fieldItems.push({
                text : item.field_name,
                value : item.field_name,
                fieldType : item.field_type
            })
        })
        self.button.on(BI.Button.EVENT_CHANGE,function(){
            var formulaPopOver = BI.createWidget({
                type: "bi.etl_filter_formula_popup",
                fieldItems: self.fieldItems
            });
            formulaPopOver.on(BI.ETLFilterFormulaPopup.EVENT_CHANGE, function () {
                self.storedValue = formulaPopOver.getValue();
                self.populate();
                self.fireEvent(BI.ETLFormulaSettingPane.EVENT_CONFIRM);
            });
            formulaPopOver.setValue(self.storedValue);
            BI.Popovers.remove("etlFormula");
            BI.Popovers.create("etlFormula", formulaPopOver).open("etlFormula");
        });
        self.label = BI.createWidget({
            type : 'bi.label',
            whiteSpace : 'normal',
            textAlign : 'left',
            textHeight : 20,
            lgap : 10
        });
        BI.createWidget({
            type : 'bi.vertical',
            element : self.element,
            items : [
                BI.createWidget({
                    type : 'bi.absolute',
                    height : self._constants.BUTTON_HEIGHT,
                    items:[{
                        left : self._constants.BUTTON_LEFT,
                        el : self.button
                    }]
                }),
                self.label
            ]
        })
    },

    setValue : function (v){
        this.storedValue = v || {};
    },

    populate : function () {
        if (BI.isNotNull(this.storedValue)){
            this.label.setText(BI.Utils.getTextFromFormulaValue(this.storedValue, this.fieldItems));
        }
    },
    
    getValue: function () {
        return this.storedValue;
    }
});
BI.ETLFormulaSettingPane.EVENT_CONFIRM = 'ETLFormulaSettingPane.EVENT_CONFIRM';
$.shortcut('bi.filter_etl_formula_setting', BI.ETLFormulaSettingPane);