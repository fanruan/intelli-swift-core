BI.AnalysisETLOperatorAddColumnPaneModel = BI.inherit(BI.MVCModel, {


    _init : function () {
        BI.AnalysisETLOperatorAddColumnPaneModel.superclass._init.apply(this, arguments);
        this.set(BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY, this.getAddColumns() || [])
    },

    getAddColumns : function () {
        return this.get(BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY)
    },

    addColumn : function (column) {
       var columns = this.get(BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY);
        columns.push(column)
    },

    editColumn : function (column, oldName) {
        var columns = this.get(BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY);
        var oldColumn = BI.find(columns, function (idx, item) {
            return item.field_name === oldName;
        });
        if(BI.isNotNull(oldColumn)) {
            BI.extend(oldColumn, column)
        } else {
            console.log("can't find old column:" + oldName)
        }
    },


    deleteColumnByName : function (name) {
        var columns = this.get(BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY);
        var res = [];
        BI.each(columns, function (idx, item) {
            if(item.field_name !== name) {
                res.push(item)
            }
        })
        this.set(BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY, res);
        return res;
    },


    getColumnByName : function (name) {
        var columns = this.get(BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY);
        return BI.deepClone(BI.find(columns, function (idx, item) {
            return item.field_name === name
        }))
    },

    _createFields : function () {
        var parent = this.get(ETLCst.PARENTS)[0];
        return BI.concat(parent[ETLCst.FIELDS], BI.map(this.getAddColumns(), function (idx, item) {
            return {
                field_name : item.field_name,
                field_type : item.field_type
            }
        }))
    },

    isDefalutValue : function () {
        return this.getAddColumns().length === 0
    },
    
    update : function () {
        var v = BI.AnalysisETLOperatorAddColumnPaneModel.superclass.update.apply(this, arguments);
        v.etlType = ETLCst.ETL_TYPE.ADD_COLUMN;
        v.fields = this._createFields();
        v.operator = {};
        v.operator[BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY] = v[BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY];
        delete v[BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY];
        return v;
    }
})
BI.AnalysisETLOperatorAddColumnPaneModel.COLUMNKEY = "columns"