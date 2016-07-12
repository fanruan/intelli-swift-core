/**
 * @class BI.JoinResultHeader
 * @extend BI.Widget
 * join 结果
 */
BI.JoinResultHeader = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.JoinResultHeader.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-merge-result-header"
        })
    },

    _init: function(){
        BI.JoinResultHeader.superclass._init.apply(this, arguments);
        this.table = BI.createWidget({
            type: "bi.table_view",
            element: this.element,
            items: []
        });
    },

    populate: function(joinFields, joinNames, allFields) {
        var self = this;
        var items = [], namesArray = [];
        BI.each(joinNames, function(i, nameOb) {
            namesArray.push(nameOb.name);
            var cls = "result-table";
            var merge = [];
            if(joinFields[i].indexOf(-1) > -1) {
                cls = "table-color" + (joinFields[i].indexOf(-1) === 0 ? 1 : 0 );
            } else {
                BI.each(joinFields[i], function(j, joinIndex){
                    merge.push(allFields[j][joinIndex].field_name);
                });
            }
            items.push({
                type: "bi.join_preview_table_header_cell",
                text: nameOb.name,
                value: nameOb.name,
                merge: merge,
                cls: cls,
                validationChecker: function (v) {
                    if(v === "") {
                        return false;
                    }
                    if(namesArray.indexOf(v) > -1 && namesArray.indexOf(v) !== i) {
                        return false;
                    }
                },
                onRenameField: function(name) {
                    namesArray[i] = name;
                    joinNames[i].name = name;
                    self.fireEvent(BI.JoinResultHeader.EVENT_CHANGE, joinNames);
                }
            })
        });
        this.table.populate([items]);
    }
});
BI.JoinResultHeader.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.join_result_header", BI.JoinResultHeader);