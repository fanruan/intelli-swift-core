/**
 * Created by Young's on 2016/7/8.
 */
BI.UnionPreviewTable = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.UnionPreviewTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-union-preview-table"
        })
    },

    _init: function () {
        BI.UnionPreviewTable.superclass._init.apply(this, arguments);

        BI.createWidget({
            type: "bi.preview_table",
            element: this.element,
            header: [this._createHeader()],
            items: this._createItems()
        });
    },

    _createHeader: function () {
        var self = this, o = this.options;
        var header = [];
        var unionArray = o.union_array, data = o.data, index = o.index;
        var namesArray = [];
        if (BI.isNotNull(unionArray)) {
            BI.each(unionArray, function (i, item) {
                namesArray[i] = item[0];
                var cls = "result-table";
                var tIndex = 0, tCount = 0;
                BI.each(item, function (j, it) {
                    if (it !== "" && j !== 0) {
                        tCount++;
                        tIndex = j - 1;
                    }
                });
                if (tCount === 1) {
                    cls = "table-color" + tIndex % 5;
                }
                header.push({
                    type: "bi.union_preview_table_header_cell",
                    text: item[0],
                    value: item[0],
                    merge: item,
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
                        unionArray[i][0] = name;
                        self.fireEvent(BI.UnionPreviewTable.EVENT_RENAME, unionArray);
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
        var data = o.data, unionArray = o.union_array, index = o.index;
        //考虑颜色
        var colorArray = [];
        if (BI.isNotNull(unionArray)) {
            BI.each(unionArray, function (i, item) {
                var cls = "result-table";
                var tIndex = 0, tCount = 0;
                BI.each(item, function (j, it) {
                    if (it !== "" && j !== 0) {
                        tCount++;
                        tIndex = j - 1;
                    }
                });
                if (tCount === 1) {
                    cls = "table-color" + tIndex % 5;
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
BI.UnionPreviewTable.EVENT_RENAME = "EVENT_RENAME";
$.shortcut("bi.union_preview_table", BI.UnionPreviewTable);