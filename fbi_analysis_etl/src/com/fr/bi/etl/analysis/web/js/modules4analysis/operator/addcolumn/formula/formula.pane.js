/**
 * Created by windy on 2017/4/10.
 */
BI.AnalysisETLOperatorAddColumnFormulaPane = BI.inherit(BI.Widget, {
    _constants: {
        HEIGHT: 194,
        LABEL_WIDTH: 35,
        RGAP : 320
    },

    render: function(){
        this.model = new BI.AnalysisETLOperatorAddColumnAllFieldsModel({});
        var self = this, o = this.options;
        this.formula = null;
        return {
            type: 'bi.vertical',
            scrolly: false,
            rgap: self._constants.RGAP,
            items: [
                {
                    el: {
                        type : 'bi.formula_insert',
                        height : self._constants.HEIGHT,
                        fieldItems : [],
                        ref: function(_ref){
                            self.formula = _ref;
                        },
                        listeners: [{
                            eventName: BI.FormulaInsert.EVENT_CHANGE,
                            action: function(){
                                var valid = self.formula.checkValidation();
                                if(valid === true) {
                                    self.setFormula( self.formula.getValue())
                                }
                            }
                        }]
                    }
                }
            ]
        }
    },

    //原controller
    _checkCanSave : function () {
        var value = this.model.get('formula');
        if (BI.isNull(value) || BI.isEmptyString(value)){
            this.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Formula_Valid'));
        } else {
            this.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true);
        }
    },

    _formatFormulaItems: function(fields){
        var fieldItems = [[], [], []];
        BI.each(fields, function (i, item) {
            var index = 0;
            switch (item.fieldType){
                case BICst.COLUMN.STRING:
                    index = 1;
                    break;
                case BICst.COLUMN.NUMBER:
                case BICst.COLUMN.COUNTER:
                    index = 0;
                    break;
                case BICst.COLUMN.DATE:
                    index = 2;
                    break;
            }
            fieldItems[index].push(item);
        });
        return fieldItems;
    },

    _populate: function(){
        this.formula.populate(this._formatFormulaItems(this.model.get(ETLCst.FIELDS)))
        this.formula.setValue(this.model.get("formula") || "")
        this._checkCanSave();
    },

    populate : function (m, options) {
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
    },

    setFormula : function (v) {
        this.model.set("formula", v);
        this._checkCanSave();
    },

    update: function(){
        return this.model.update();
    }

});
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.FORMULA, BI.AnalysisETLOperatorAddColumnFormulaPane);