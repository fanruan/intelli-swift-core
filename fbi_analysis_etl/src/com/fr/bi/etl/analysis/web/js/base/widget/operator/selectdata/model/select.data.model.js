BI.AnalysisETLOperatorSelectDataModel = BI.inherit(BI.MVCModel, {



    _init : function () {
        BI.AnalysisETLOperatorSelectDataModel.superclass._init.apply(this, arguments);
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.KEY)||[];
        this.set(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY, tempFields);
        this.save();
    },


    save : function () {
        this.set(BI.AnalysisETLOperatorSelectDataModel.KEY, this.getTempFields())
    },
    
    addField : function (field) {
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        tempFields.push(field);
        this.set(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY, tempFields)
    },
    
    cancel : function () {
        this.set(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY, this.getValue(BI.AnalysisETLOperatorSelectDataModel.KEY))
    },

    getTempFields: function () {
        return this.getValue(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
    },

    createDistinctName : function (name) {
        var oldNames  = [];
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        BI.each(tempFields, function(i, item){
            if(!BI.isNull(item.field_name)){
                oldNames.push(item.field_name);
            }
        });
        return BI.Utils.createDistinctName(oldNames, name);
    },

    sort : function (oldIndex, newIndex) {
        if(newIndex > oldIndex) {
            newIndex --;
        }
        if(oldIndex === newIndex) {
            //还是原来位置 do nothing
            return
        }
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        var field = tempFields[oldIndex];
        BI.removeAt(tempFields, oldIndex);
        tempFields.splice(newIndex, 0, field);
        this.set(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY, tempFields)
    },

    removeAt : function (v) {
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        BI.removeAt(tempFields, v);
        this.set(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY, tempFields)
    },

    rename : function (index, name) {
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        tempFields[index].field_name = name;
        this.set(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY, tempFields)
    },

    needCancel : function () {
        return !BI.isEqual(this.get(BI.AnalysisETLOperatorSelectDataModel.KEY), this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY))
    },

    checkNameValid : function (index, name) {
        var self = this, valid = true;
        var tempFields = this.get(BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY);
        BI.some(tempFields, function (i, item) {
            if (i != index && item.field_name == name){
                valid =  false;
                return true;
            }
        });
        return valid;
    },

    update : function () {
        var v  = BI.extend(BI.AnalysisETLOperatorSelectDataModel.superclass.update.apply(this, arguments), {
            etlType:ETLCst.ETL_TYPE.SELECT_DATA
        })
        delete v[BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY]
        return v;
    }

})
BI.AnalysisETLOperatorSelectDataModel.KEY = ETLCst.FIELDS;
BI.AnalysisETLOperatorSelectDataModel.TEMP_KEY = "temp_" + BI.AnalysisETLOperatorSelectDataModel.KEY;