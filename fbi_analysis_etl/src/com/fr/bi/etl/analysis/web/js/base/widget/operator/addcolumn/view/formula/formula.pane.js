/**
 * Created by 小灰灰 on 2016/4/6.
 */
BI.AnalysisETLOperatorAddColumnFormulaPane = BI.inherit(BI.MVCWidget, {
    _constants: {
        HEIGHT: 194,
        LABEL_WIDTH: 35,
        RGAP : 320
    },

    _initController : function () {
        return  BI.AnalysisETLOperatorAddColumnFormulaPaneController;
    },

    _initModel : function () {
        return  BI.AnalysisETLOperatorAddColumnAllFieldsModel;
    },

    _initView: function () {
        var self = this, o = this.options;

        self.formula = BI.createWidget({
            type : 'bi.formula_insert',
            height : self._constants.HEIGHT,
            fieldItems : []
        });
        self.formula.on(BI.FormulaInsert.EVENT_CHANGE, function () {
            var valid = self.formula.checkValidation();
            if(valid === true) {
                self.controller.setFormula( self.formula.getValue())
            }
            self.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, valid);

        })
        BI.createWidget(    {
            type : 'bi.vertical',
            scrolly:false,
            element : self.element,
            rgap : self._constants.RGAP,
            items : [
                {
                    el : self.formula
                }
            ]
        });
    }

});
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.FORMULA, BI.AnalysisETLOperatorAddColumnFormulaPane);