/**
 * Created by windy on 2017/4/5.
 */
BI.AnalysisETLOperatorFilterPane = BI.inherit(BI.Widget, {

    _constant : {
        nullCard : "null_card",
        filterCard : "filter_card"
    },

    props: {
        extraCls:"bi-analysis-etl-operator-filter-pane",
        value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.FILTER
    },

    _createTabs: function(v){
        var self = this;
        switch (v) {
            case this._constant.nullCard:
                return BI.createWidget({
                    type:"bi.center_adapt",
                    items:[{
                        type:"bi.label",
                        cls:"null-label",
                        text: BI.i18nText('BI-Add_Filter_ON_Table_Title')
                    }]
                });
            case this._constant.filterCard:
                return BI.createWidget({
                    type:"bi.border",
                    items :{
                        west :{
                            el : {
                                type:"bi.layout"
                            },
                            width:10
                        },
                        east : {
                            el : {
                                type:"bi.layout"
                            },
                            width:10
                        },
                        center: {
                            el:{
                                type:"bi.vtape",
                                items:[{
                                    type:"bi.layout",
                                    height:10
                                },{
                                    el:{
                                        type:"bi.center_adapt",
                                        cls:"operator-choose",
                                        items:[{
                                            type:"bi.vertical_adapt",
                                            items:[{
                                                type:"bi.label",
                                                cls:'bi-label-msg',
                                                text: BI.i18nText('BI-Show_Filter')
                                            }, {
                                                type:"bi.layout",
                                                width:10
                                            }, {
                                                type:'bi.text_value_combo',
                                                width: 250,
                                                height: 30,
                                                ref: function(_ref){
                                                    self.operatorCombo = _ref;
                                                },
                                                items: [{
                                                    text: BI.i18nText('BI-Show_Operator_Together_AND'),
                                                    value: BICst.FILTER_TYPE.AND
                                                }, {
                                                    text: BI.i18nText('BI-Show_Operator_Together_OR'),
                                                    value: BICst.FILTER_TYPE.OR
                                                }],
                                                listeners: [{
                                                    eventName: BI.TextValueCombo.EVENT_CHANGE,
                                                    action: function(){
                                                        self.operatorChange(self.operatorCombo.getValue()[0]);
                                                    }
                                                }]
                                            }]
                                        }]
                                    },
                                    height:30
                                },  {
                                    type:"bi.layout",
                                    height:10
                                }, {
                                    type:ETLCst.ANALYSIS_ETL_PAGES.FILTER + "_table",
                                    ref: function(_ref){
                                        self.content = _ref;
                                    }
                                }]
                            }
                        }
                    }
                })
        }
    },

    render: function(){
        var self = this, o = this.options;
        this.card = this.operatorCombo = this.content = null;
        this.model = new BI.AnalysisETLOperatorFilterPaneModel({
        });
        this.operatorCombo = this.content = this.card = null;
        return {
            type:"bi.tab",
            ref: function(_ref){
                self.card = _ref;
            },
            cardCreator: BI.bind(this._createTabs, this)
        };
    },

    filterChange : function(filter){
        var operator = this.model.get('operator');
        var items = operator.items || [];
        if (BI.isNull(filter.value) || BI.isEmptyArray(filter.value)){
            BI.remove(items, function (idx, item) {
                return item.fieldName === filter.fieldName;
            });
        } else {
            var item = BI.find(items, function (idx, item) {
                return item.fieldName === filter.fieldName;
            })
            if (BI.isNull(item)){
                items.push(filter)
            } else {
                BI.extend(item, filter);
            }
        }
        this._populate();
    },

    getFilterValue : function (field) {
        var operator = this.model.get('operator');
        return BI.find(operator.items, function (idx, item) {
            return item.fieldName === field;
        })
    },

    //todo fireEvent把个方法传出去了
    operatorChange : function (v) {
        var operator = this.model.get("operator");
        this.model.set('operator', BI.extend(operator, {type: v}));
        this.fireEvent(BI.AnalysisOperatorAbstarctPane.PREVIEW_CHANGE, {
            isDefaultValue: BI.bind(this.isDefaultValue, this)
        }, this.options.value.operatorType);
    },

    doCheck : function () {
        var operator = this.model.get('operator');
        var items = operator.items;
        this.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, BI.isNotNull(items) && items.length !== 0, BI.i18nText('BI-Value_Cannot_Be_Null'))
    },

    _check : function () {
        var invalid = this.model.check();
        if(invalid[0] === true) {
            this.fireEvent(BI.TopPointerSavePane.EVENT_INVALID, invalid[1])
        } else {
            var parent = this.model.get(ETLCst.PARENTS)[0];
            this.fireEvent(BI.TopPointerSavePane.EVENT_FIELD_VALID, BI.deepClone(parent[ETLCst.FIELDS]))
        }
        this.fireEvent(BI.AnalysisOperatorAbstarctPane.VALID_CHANGE, !invalid[0]);
    },

    isDefaultValue : function () {
        var operator = this.model.get('operator');
        var items = operator.items;
        return BI.isNull(items) || items.length === 0
    },


    update : function () {
        var v =  this.model.update();
        v.etlType = ETLCst.ETL_TYPE.FILTER;
        var parent = this.model.get(ETLCst.PARENTS)[0];
        v[ETLCst.FIELDS] = BI.deepClone(parent[ETLCst.FIELDS])
        return v;
    },

    _populate: function(){
        var operator = this.model.get('operator');
        if (BI.isNull(operator)){
            this.model.set('operator', {type : BICst.FILTER_TYPE.AND});
            operator = this.model.get('operator');
        }
        this._check();
        var items = operator.items;
        if(BI.isNull(items) || items.length === 0) {
            this.card.setSelect(this._constant.nullCard)
        } else {
            this.card.setSelect(this._constant.filterCard)
            var type = operator.type || BICst.FILTER_TYPE.AND
            this.operatorCombo.setValue(type)
            var fieldItems = [];
            BI.each(this.model.get(ETLCst.PARENTS)[0][ETLCst.FIELDS], function (i, item) {
                fieldItems.push({
                    text : item.fieldName,
                    value : item.fieldName,
                    fieldType : item.fieldType
                })
            })
            this.content.populate(items, fieldItems);
        }
        this.doCheck()
        this.fireEvent(BI.AnalysisOperatorAbstarctPane.PREVIEW_CHANGE, {
            update: BI.bind(this.update, this),
            isDefaultValue: BI.bind(this.isDefaultValue, this)
        }, this.options.value.operatorType)
    },

    //todo 外界调用populate居然还会传options.func进来以拓展自身的controller，现在放widget里，之后删掉
    populate: function (m, options) {
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
    }
})

BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.FILTER + ETLCst.ANALYSIS_TABLE_PANE, BI.AnalysisETLOperatorFilterPane);