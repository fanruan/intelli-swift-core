/**
 * Created by 小灰灰 on 2016/3/31.
 */
/**
 * Created by 小灰灰 on 2016/3/4.
 */
BI.AnalysisETLOperatorAddColumnValueConvertPane = BI.inherit(BI.MVCWidget, {
    _constants: {
        HEIGHT: 30,
        LABEL_WIDTH: 70,
        WIDTH : 300,
        GAP : 10
    },

    _initView: function () {
        var self = this, o = this.options;
        self.segment  = BI.createWidget({
            type: "bi.segment",
            items: [
                {
                    type: "bi.text_button",
                    text: BI.i18nText("BI-Numeric_Type"),
                    cls: "bi-segment-button",
                    value: BICst.COLUMN.NUMBER
                }, {
                    type: "bi.text_button",
                    text: BI.i18nText("BI-Text_Type"),
                    cls: "bi-segment-button",
                    value: BICst.COLUMN.STRING
                }, {
                    type: "bi.text_button",
                    text: BI.i18nText("BI-Time_Type"),
                    cls: "bi-segment-button",
                    value: BICst.COLUMN.DATE
                }
            ]
        });
        self.segment.on(BI.Segment.EVENT_CHANGE, function (v) {
            self.controller.setType(v);
        });
        self.combo = BI.createWidget({
            type: "bi.text_icon_combo",
            height : self._constants.HEIGHT,
            items : []
        });
        self.combo.on(BI.TextIconCombo.EVENT_CHANGE, function(v){
            self.controller.setField(v);
        })
        BI.createWidget({
            type : 'bi.vertical',
            element : self.element,
            tgap : self._constants.GAP,
            lgap : self._constants.GAP,
            width : self._constants.WIDTH,
            items :[
                {
                    type : 'bi.htape',
                    items : [
                        {
                            type : 'bi.label',
                            cls : 'label-name',
                            text : BI.i18nText('BI-Field') + BI.i18nText('BI-Choose'),
                            width : self._constants.LABEL_WIDTH,
                            textAlign : 'left'
                        },
                        {
                            el : self.combo
                        }
                    ],
                    height : self._constants.HEIGHT
                },
                {
                    type : 'bi.htape',
                    items : [
                        {
                            type : 'bi.label',
                            cls : 'label-name',
                            width : self._constants.LABEL_WIDTH,
                            text : BI.i18nText('BI-Type') + BI.i18nText('BI-Choose'),
                            textAlign : 'left'
                        },
                        {
                            el : self.segment
                        }
                    ],
                    height : self._constants.HEIGHT
                }
            ]
        });
    },
    _initModel : function() {
        return BI.AnalysisETLOperatorAddColumnValueConvertModel;
    },

    _initController : function() {
        return BI.AnalysisETLOperatorAddColumnValueConvertController;
    }

});
$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BI.ANALYSIS_ETL_ADD_COLUMN_TYPE.VALUE_CONVERT, BI.AnalysisETLOperatorAddColumnValueConvertPane);