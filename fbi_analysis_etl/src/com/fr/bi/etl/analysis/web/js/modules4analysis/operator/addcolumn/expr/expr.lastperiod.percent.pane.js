/**
 * Created by 小灰灰 on 2016/4/6.
 */
BI.AnalysisETLOperatorAddColumnExprLastPeriodPercentPane  = BI.inherit(BI.AnalysisETLOperatorAddColumnExprLastPeriodPane, {

    _createDetail: function () {
        var self = this;
        this.firstDetail = this.secondDetail = thi.third = null;
        return {
            type : 'bi.vertical',
            width : self._constants.DETAIL_WIDTH,
            height : self._constants.LIST_HEIGHT,
            cls :'group-detail',
            scrolly : false,
            items : [
                {
                    el : BI.createWidget({
                        type : 'bi.label',
                        cls : 'label-name',
                        text : BI.i18nText('BI-Group_Detail_Short'),
                        textAlign : 'left',
                        height : self._constants.LABEL_HEIGHT
                    })
                },
                {
                    el : {
                        type : 'bi.button_group',
                        cls : 'detail-view',
                        height : self._constants.FIRST_DETAIL_HEIGHT,
                        ref: function(_ref){
                            self.firstDetail = _ref;
                        },
                        layouts: [{
                            type: "bi.vertical",
                            lgap : self._constants.GAP
                        }]
                    }
                },
                {
                    el : BI.createWidget({
                        type : 'bi.label',
                        cls : 'label-name',
                        text : BI.i18nText('BI-Divide_By'),
                        textAlign : 'center',
                        height : self._constants.LABEL_HEIGHT
                    })
                },
                {
                    el : {
                        type : 'bi.button_group',
                        cls : 'detail-view',
                        height : self._constants.SECOND_DETAIL_HEIGHT,
                        ref: function(_ref){
                            self.secondDetail = _ref;
                        },
                        layouts: [{
                            type: "bi.vertical",
                            lgap : self._constants.GAP,
                            tgap : self._constants.GAP
                        }]
                    }
                },
                {
                    el : BI.createWidget({
                        type : 'bi.label',
                        cls : 'label-name',
                        text : BI.i18nText('BI-Basic_Minus'),
                        textAlign : 'center',
                        height : self._constants.LABEL_HEIGHT
                    })
                },
                {
                    el : {
                        type : 'bi.button_group',
                        cls : 'detail-view',
                        height : self._constants.FIRST_DETAIL_HEIGHT,
                        items : [ BI.createWidget({
                            type : 'bi.label',
                            cls : 'detail-label',
                            textAlign : 'center',
                            height : 25,
                            text : 1,
                            title : 1
                        })],
                        ref: function(_ref){
                            self.third = _ref;
                        },
                        layouts: [{
                            type: "bi.vertical",
                            lgap : self._constants.GAP
                        }]
                    }
                }
            ]
        };
    },

    _populateLabel : function () {
        var text = BI.i18nText('BI-Calculate_Target_Each_Value', this.model.get('period') || '', this.model.get('field')||'');
        this.firstDetail.populate([{
            type : 'bi.label',
            cls : 'detail-label',
            textAlign : 'left',
            height : 25,
            text : text,
            title : text
        }]);
        this._populateDownLabel();
    },

    _populateDownLabel : function () {
        var self = this;
        var group = this.model.get('group') || [];
        var labels = []
        BI.each(group, function (i, item) {
            var text = i === group.length -1 ? BI.i18nText('BI-Calculate_Target_Include_In_Same_Last', item, self.model.get('period') || ''): BI.i18nText('BI-Calculate_Target_Include_In_Same', item);
            labels.push({
                type : 'bi.label',
                cls : 'detail-label',
                textAlign : 'left',
                text : text,
                title : text
            })
        });
        if (BI.isEmptyArray(group)){
            var text = BI.i18nText('BI-Calculate_Target_Last', this.model.get('period') || '')
            labels.push({
                type : 'bi.label',
                cls : 'detail-label',
                textAlign : 'left',
                text : text,
                title : text
            })
        }
        var text = BI.i18nText('BI-Brackets_Value', this.model.get('field')||'');
        labels.push({
            type : 'bi.label',
            cls : 'detail-label',
            textAlign : 'left',
            text : text,
            title : text
        });
        this.secondDetail.populate(labels);
    }

});
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.EXPR_LP_PERCENT, BI.AnalysisETLOperatorAddColumnExprLastPeriodPercentPane);