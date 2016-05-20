/**
 * Created by 小灰灰 on 2016/3/31.
 */
BI.AnalysisETLOperatorAddColumnValueConvertModel = BI.inherit(BI.AnalysisETLOperatorAddColumnAllFieldsModel, {

    _getTypePrefix: function (type) {
        switch (type){
            case BICst.COLUMN.NUMBER :
                return BI.i18nText('BI-Number');
            case BICst.COLUMN.DATE :
                return BI.i18nText('BI-Time');
            case BICst.COLUMN.STRING :
                return BI.i18nText('BI-Text');
        }
    },

    createFieldsItems : function () {
        var self = this, f = [];
        BI.each(this.get(ETLCst.FIELDS) || [], function (i, field) {
            f.push({
                text : self._getTypePrefix(field.fieldType) + ' ' + field.value,
                value : field.value
            })
        });
        return f;
    },

    update : function () {
        var v = BI.deepClone(this.toJSON());
        delete v[ETLCst.FIELDS];
        return v;
    }

})