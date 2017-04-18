/**
 * Created by windy on 2017/4/7.
 */
BI.AnalysisETLOperatorUsePartPane = BI.inherit(BI.Widget, {

    props: {
        extraCls: "bi-analysis-etl-operator-use-part-pane",
        value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.USE_PART_FIELDS
    },

    render: function(){
        var self = this;
        this.fieldList = null;
        this.model = new BI.AnalysisETLOperatorUsePartPaneModel();
        BI.createWidget({
            type:"bi.vtape",
            element: this,
            items:[{
                height:40,
                el : {
                    type:"bi.htape",
                    cls:"title",
                    height:40,
                    items:[{
                        type:"bi.layout",
                        width:10
                    },{
                        el : {
                            type:"bi.center_adapt",
                            items :[{
                                type:"bi.label",
                                textAlign :"left",
                                text:BI.i18nText("BI-Choose_Use_Fields")
                            }]
                        }
                    }]
                }
            }, {
                el : {
                    type: "bi.select_part_field_list",
                    ref: function(_ref){
                        self.fieldList = _ref;
                    },
                    listeners: [{
                        eventName: BI.SelectPartFieldList.EVENT_CHANGE,
                        action: function(){
                            self.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, {
                                isDefaultValue: BI.bind(self.isDefaultValue, self),
                                update: BI.bind(self.update, self)
                            }, self.options.value.operatorType)
                            self.doCheck();
                        }
                    }]
                }
            }]
        })
    },

    doCheck  : function () {
        this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, this.isValid(), BI.i18nText("BI-Please_Select_Needed_Field"))
    },

    _check : function () {
        this.model.check();
        this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_FIELD_VALID, this.model.get(ETLCst.FIELDS))

    },

    isValid : function () {
        var parent = this.model.get(ETLCst.PARENTS)[0];
        return parent[ETLCst.FIELDS].length !== this.fieldList.getValue().assist.length;
    },

    update : function () {
        var newFields = [];
        var value = this.fieldList.getValue();

        var isAll = value.type === BI.ButtonGroup.CHOOSE_TYPE_ALL;
        var contains = function (name) {
            var index = BI.indexOf(value.value, name);
            return isAll ? index < 0 :index > -1
        }
        BI.each(this.model.get(ETLCst.PARENTS)[0][ETLCst.FIELDS], function (i, item) {
            if (contains(item.fieldName)){
                newFields.push(item);
            }
        })
        var table = this.model.update();
        table.etlType = ETLCst.ETL_TYPE.USE_PART_FIELDS;
        table[ETLCst.FIELDS] = newFields;
        table.operator = value;
        return table;
    },

    isDefaultValue : function () {
        return !this.isValid()
    },

    _populate: function(){
        this._check();
        var parent = this.model.get(ETLCst.PARENTS)[0];
        this.fieldList.populate(parent[ETLCst.FIELDS]);
        var value = this.model.get('operator');
        if (!BI.isNull(value)){
            this.fieldList.setValue(value);
        }
        this.doCheck();
        this.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, {
            isDefaultValue: BI.bind(this.isDefaultValue, this),
            update: BI.bind(this.update, this)
        }, this.options.value.operatorType)
    },

    populate: function(m, options){
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
    },

    getValue: function(){
        return this.model.update();
    }

})

BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.USE_PART_FIELDS  + ETLCst.ANALYSIS_TABLE_PANE, BI.AnalysisETLOperatorUsePartPane);