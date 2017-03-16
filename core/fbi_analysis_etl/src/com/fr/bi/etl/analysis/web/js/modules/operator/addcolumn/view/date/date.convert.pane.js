/**
 * Created by 小灰灰 on 2016/3/30.
 */
BI.AnalysisETLOperatorAddColumnAbstractDateConvertPane = BI.inherit(BI.MVCWidget, {
    _constants : {
        GAP : 10,
        HEIGHT : 30,
        LABEL_WIDTH : 36,
        COMBO_WIDTH : 200
    },

    _initView : function () {
        var self = this, o = this.options;
        self.combo = BI.createWidget({
            type: "bi.text_value_combo",
            height : self._constants.HEIGHT,
            width : self._constants.COMBO_WIDTH,
            items : []
        });
        self.combo.on(BI.TextValueCombo.EVENT_CHANGE, function(v){
            self.controller.setField(v);
        })
        BI.createWidget({
            type : 'bi.horizontal',
            tgap : self._constants.GAP,
            lgap : self._constants.GAP,
            element : self.element,
            items :[{
                type: 'bi.label',
                cls : 'label-name',
                text: BI.i18nText('BI-Select_Field'),
                textAlign: 'left'
            }, {
                el : self.combo
            },{
                el : self._createLabel()
            }]
        })
    },

    _initController : function() {
        return BI.AnalysisETLOperatorAddColumnDateConvertController;
    },

    _createLabel : function () {
        
    }
})