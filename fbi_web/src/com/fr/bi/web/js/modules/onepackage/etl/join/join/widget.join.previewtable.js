/**
 * Created by Young's on 2016/7/9.
 */
BI.JoinPreviewTable = BI.inherit(BI.Widget, {
    _defaultConfig: function() {
        return BI.extend(BI.JoinPreviewTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-join-preview-table"
        })
    },

    _init: function() {
        BI.JoinPreviewTable.superclass._init.apply(this, arguments);
        BI.createWidget({
            type: "bi.preview_table",
            element: this.element,
            header: [this._createHeader()],
            items: this._createItems()
        });
    },

    _createHeader: function() {
        var self = this, o = this.options;
        var header = [];
        var joinNames = o.join_names, joinFields = o.join_fields,
            data = o.data, index = o.index, allFields = o.all_fields;
        var namesArray = [];
        if(BI.isNotNull(joinNames)) {
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
                header.push({
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
                        self.fireEvent(BI.JoinPreviewTable.EVENT_RENAME, joinNames);
                    }
                })
            });
        } else if (BI.isNotNull(data.fields)) {
            BI.each(data.fields, function (i, fieldName) {
                header.push({
                    type: "bi.label",
                    text: fieldName,
                    height: 30,
                    cls: "table-color" + index % 5
                })
            })
        }
        return header;
    },

    _formatDate: function (d) {
        if (BI.isNull(d) || !BI.isNumeric(d)) {
            return d || "";
        }
        var date = new Date(BI.parseInt(d));
        return date.print("%Y/%X/%d %H:%M:%S")
    },

    _createItems: function () {
        var self = this, o = this.options;
        var joinNames = o.join_names, joinFields = o.join_fields,
            data = o.data, index = o.index, allFields = o.all_fields;
        //考虑颜色
        var colorArray = [];
        if (BI.isNotNull(joinFields)) {
            BI.each(joinFields, function (i, item) {
                var cls = "result-table";
                if(joinFields[i].indexOf(-1) > -1) {
                    cls = "table-color" + (joinFields[i].indexOf(-1) === 0 ? 1 : 0 );
                }
                colorArray.push(cls);
            });
        }
        var items = [];
        BI.each(data.value, function (i, value) {
            var isDate = data.type[i] === BICst.COLUMN.DATE;
            BI.each(value, function (j, v) {
                if (BI.isNotNull(items[j])) {
                    items[j].push({
                        text: isDate === true ? self._formatDate(v) : v,
                        height: "100%",
                        cls: colorArray[i] || ("table-color" + index % 5)
                    });
                } else {
                    items.push([{
                        text: isDate === true ? self._formatDate(v) : v,
                        height: "100%",
                        cls: colorArray[i] || ("table-color" + index % 5)
                    }]);
                }
            });
        });
        return items;
    }
});
BI.JoinPreviewTable.EVENT_RENAME = "EVENT_RENAME";
$.shortcut("bi.join_preview_table", BI.JoinPreviewTable);