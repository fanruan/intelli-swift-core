/**
 * Created by 小灰灰 on 2016/3/11.
 */
BI.ETLNumberNFilterPane = BI.inherit(BI.Widget, {
    _constants: {
        HEIGHT: 30,
        WIDTH: 30,
        BORDER : 1,
        GAP : 5
    },

    _defaultConfig: function () {
        var conf = BI.ETLNumberNFilterPane.superclass._defaultConfig.apply(this, arguments)
        return BI.extend(conf, {
            baseCls: "bi-etl-filter-number-n"
        })
    },

    _init: function () {
        BI.ETLNumberNFilterPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        self.storedValue = BI.deepClone(o.value || {});
        self.combo = BI.createWidget({
            type: "bi.text_value_combo",
            width:'',
            height:self._constants.HEIGHT,
            items :BICst.ETL_FILTER_NUMBER_N_ITEMS
        });
        self.combo.on(BI.TextValueCombo.EVENT_CHANGE, function(){
            self.storedValue.type = self.combo.getValue()[0];
            self.populateGroupContainer();
        });
        self.groupContainer = BI.createWidget({
            type : 'bi.vertical'
        });
        self.editor = BI.createWidget({
            type: "bi.text_editor",
            validationChecker: function(v){
                return BI.isNumeric(v);
            },
            errorText: BI.i18nText("BI-Numerical_Interval_Input_Data"),
            height: self._constants.HEIGHT
        });
        self.editor.on(BI.TextEditor.EVENT_CONFIRM, function () {
            self.storedValue.value =  self.editor.getValue();
            self.fireEvent(BI.ETLNumberNFilterPane.EVENT_CONFIRM);
        });
        BI.createWidget({
            type : 'bi.vertical',
            element : self.element,
            bgap : self._constants.GAP,
            items :[
                {
                    el : self.combo,
                    height : self._constants.HEIGHT
                },
                {
                    type : 'bi.absolute',
                    items: [{
                        el:BI.createWidget({
                            type: "bi.label",
                            height: self._constants.HEIGHT,
                            text: "N = "
                        })
                    },{
                        el :self.editor,
                        top: 0,
                        bottom: 0,
                        right: 0,
                        left: self._constants.WIDTH
                    }],
                    height : self._constants.HEIGHT
                },
                {
                    el : self.groupContainer
                }
            ]
        });
    },

    populateGroupContainer: function () {
        var self = this, o = this.options;
        self.groupContainer.empty();
        if (self.combo.getValue() == BICst.ETL_FILTER_NUMBER_N_TYPE.INNER_GROUP){
            var op ={
                type :'bi.filter_etl_group_setting',
                field_name : o.field_name,
                filterType : o.filterType,
                value : BI.deepClone(self.storedValue.group),
                nValueGetter : function (){
                    return self.storedValue.value
                }
            }
            op[ETLCst.FIELDS] = o[ETLCst.FIELDS];
            self.group = BI.createWidget(op)
            self.group.on(BI.ETLGroupSettingPane.EVENT_VALUE_CHANGED, function () {
                self.storedValue.group = self.group.getValue();
                self.fireEvent(BI.ETLNumberNFilterPane.EVENT_CONFIRM)
            })
            self.group.populate();
            self.groupContainer.addItem(self.group);
        }
    },

    setValue : function (v) {
        this.storedValue = v || {};
    },

    getValue: function () {
        return this.storedValue;
    },
    
    populate : function () {
        this.combo.setValue(this.storedValue.type || BICst.ETL_FILTER_NUMBER_N_TYPE.ALL);
        if (BI.isNotNull(this.storedValue.value)){
            this.editor.setValue(this.storedValue.value);
        }
        this.populateGroupContainer();
    }
});
BI.ETLNumberNFilterPane.EVENT_CONFIRM = 'ETLNumberNFilterPane.EVENT_CONFIRM';
$.shortcut('bi.filter_number_n_etl', BI.ETLNumberNFilterPane);