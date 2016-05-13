BI.Utils = BI.Utils || {};

BI.extend(BI.Utils, {
    afterSaveTable : function(res){
        BI.each(res, function(i, item){
            BI.extend(Pool[i], item);
        })
    },

    afterReNameTable : function (id, name) {
        Pool["translations"][id] = name;
    },

    afterDeleteTable : function (id) {
        delete Pool["tables"][id];
    },

    getTableTypeByID :function (tableId){
        var source = Pool.tables;
        var table = source[tableId];
        if(!table){
            return BICst.BUSINESS_TABLE_TYPE.NORMAL;
        }
        var key = BICst.JSON_KEYS.TABLE_TYPE;
        if(table[key] === undefined || table[key] === null){
            return ETLCst.BUSINESS_TABLE_TYPE.ANALYSIS_TYPE;
        }
        return table[key];
    },

    getFieldClass: function (type) {
        switch (type) {
            case BICst.COLUMN.STRING:
                return "select-data-field-string-font";
            case BICst.COLUMN.NUMBER:
                return "select-data-field-number-font";
            case BICst.COLUMN.DATE:
                return "select-data-field-date-font";
            default :
                return BI.Utils.getFieldClass(BICst.COLUMN.STRING)
        }
    },

    createDistinctName : function (array, name) {
        var res = name;
        var index = 1;
        while(BI.indexOf(array, res) > -1){
            res = name + index++;
        }
        return res;
    },

    getFieldArrayFromTable : function (table) {
        var fields = [];
        BI.each(table[ETLCst.FIELDS], function (idx, item) {
            fields = BI.concat(fields, item);
        })
        return fields;
    },

    /**
     * 返回数组对象
     * @param tableIds 数组
     * @returns 数组
     */
    getProbablySinglePathTables: function (tableIds) {
        if(BI.isNull(tableIds) || tableIds.length === 0) {
            //不禁用
            return [];
        }
        var fTable = tableIds[0];
        BI.each(tableIds, function (idx, item) {
            var relation = BI.Utils.getPathsFromTableAToTableB(item, fTable);
            if(relation.length === 0) {
                fTable = item;
            }
        });
        var pTables = Pool.foreignRelations[fTable]
        var result = {};
        BI.each(pTables, function (idx, item) {
            if(item.length === 1) {
                result[idx] = true;
            }
        })
        var fTables = Pool.relations[fTable]
        BI.each(fTables, function (idx, item) {
            if(item.length === 1) {
                result[idx] = true;
            }
        })
        result[fTable] = true;
        return BI.map(result, function (idx, item) {
            return idx;
        })
    },

    getTextFromFormulaValue: function (formulaValue, fieldItems) {
        if (BI.isNull(formulaValue) || BI.isNull(fieldItems)){
            return '';
        }
        var formulaString = "";
        var regx = /\$[\{][^\}]*[\}]|\w*\w|\$\{[^\$\(\)\+\-\*\/)\$,]*\w\}|\$\{[^\$\(\)\+\-\*\/]*\w\}|\$\{[^\$\(\)\+\-\*\/]*[\u4e00-\u9fa5]\}|\w|(.)/g;
        var result = formulaValue.match(regx);
        BI.each(result, function (i, item) {
            var fieldRegx = /\$[\{][^\}]*[\}]/;
            var str = item.match(fieldRegx);
            if (BI.isNotEmptyArray(str)) {
                var id = str[0].substring(2, item.length - 1);
                var item = BI.find(fieldItems, function (i, item) {
                    return id === item.value;
                });
                formulaString = formulaString + BI.isNull(item) ? id : item.text;
            } else {
                formulaString = formulaString + item;
            }
        });
        return formulaString;
    },

    getFieldsFromFormulaValue: function (formulaValue) {
        var fields = [];
        if (BI.isNull(formulaValue)){
            return [];
        }
        var regx = /\$[\{][^\}]*[\}]|\w*\w|\$\{[^\$\(\)\+\-\*\/)\$,]*\w\}|\$\{[^\$\(\)\+\-\*\/]*\w\}|\$\{[^\$\(\)\+\-\*\/]*[\u4e00-\u9fa5]\}|\w|(.)/g;
        var result = formulaValue.match(regx);
        BI.each(result, function (i, item) {
            var fieldRegx = /\$[\{][^\}]*[\}]/;
            var str = item.match(fieldRegx);
            if (BI.isNotEmptyArray(str)) {
                fields.push(str[0].substring(2, item.length - 1));
            } 
        });
        return fields;
    },

    _buildData : function(model, filterValueGetter) {
        //测试数据
        var header = [];
        var items = [];
        BI.each(model[ETLCst.FIELDS], function(idx, item){
            var head = {
                text:item.field_name,
                field_type:item.field_type,
                field_id:item.field_id,
                filterValueGetter : filterValueGetter
            }
            head[ETLCst.FIELDS] = model[ETLCst.FIELDS]
            header.push(head);
            BI.each(BI.range(0 ,10), function(i){
                if(BI.isNull(items[i])){
                    items[i] = [];
                }
                items[i].push({text:"row:"+i +" column:" +idx})
            })

        })
        return [items, header];
    },

    triggerPreview : function () {
        return BI.throttle(function (widget, previewModel, operatorType, type) {
            switch (type) {
                case ETLCst.PREVIEW.SELECT : {
                    widget.setPreviewOperator(operatorType);
                    var model = {};
                    model[ ETLCst.FIELDS] = previewModel.getTempFields();
                    widget.populatePreview.apply(widget, BI.Utils._buildData(model, widget.controller.getFilterValue))
                    return;
                }
                case ETLCst.PREVIEW.MERGE : {
                    widget.populate.apply(widget, BI.concat(BI.Utils._buildData(previewModel), operatorType));
                    return;
                }
                default : {
                    widget.setPreviewOperator(operatorType);
                    widget.populatePreview.apply(widget, BI.Utils._buildData(previewModel.update(), widget.controller.getFilterValue));
                    return
                }
            }
        }, 300)
    }

})

window.confirm = BI.Msg.confirm;

