/**
 * Created by 小灰灰 on 2016/4/7.
 */
BI.AnalysisETLMainModel = BI.inherit(BI.MVCModel, {
    _init : function () {
        BI.AnalysisETLMainModel.superclass._init.apply(this, arguments);
        this.set(BI.AnalysisETLMainModel.TAB, new BI.DynamictabModel({
            items:this.getValue(BI.AnalysisETLMainModel.TAB)
        }));
    },

    getSheetLength : function () {
        return this.get(BI.AnalysisETLMainModel.TAB).get(ETLCst.ITEMS).length;
    },

    getTableDefaultName : function () {
        var id = this.get(BI.AnalysisETLMainModel.TAB).get(ETLCst.ITEMS)[0];
        return this.get('name') || this.get(BI.AnalysisETLMainModel.TAB).get(id).get('table_name');
    },
    
    update : function () {
        var value = {
            id : this.get('id'),
            name : this.get('name')
        }
        value[BI.AnalysisETLMainModel.TAB] = this.get(BI.AnalysisETLMainModel.TAB).update();
        return value;
    }
});
BI.AnalysisETLMainModel.TAB = 'table';