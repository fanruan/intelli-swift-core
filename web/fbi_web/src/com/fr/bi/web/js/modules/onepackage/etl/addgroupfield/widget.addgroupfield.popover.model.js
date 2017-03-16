BI.AddGroupFieldPopoverModel = BI.inherit(FR.OB, {
    _init: function () {
        BI.AddGroupFieldPopoverModel.superclass._init.apply(this, arguments);
        var o = this.options;
        var info = o.info;
        this.id = info.id;
        this.tableInfo = info.tableInfo;
        this.newGroupsData = info.newGroupsData;
        this.reopen = info.reopen;
        this.fields = info.fields;
        this.open2change = info.open2change || false;
    },

    initData: function (callback) {
        var fields = this.getFields();
        var fieldData = {};
        BI.each(fields[0], function (i, fieldObj) {
            var id = BI.UUID();
            if (fieldObj.field_type === BICst.COLUMN.STRING) {
                fieldData[id] = fieldObj
            }
        });
        this.allFields = fieldData;
        callback();
    },


    isDuplicate: function (fieldName) {
        var isDuplicated = false;
        var newGroupsData = this.getNewGroupsData();
        var fields = this.getFields();
        var id = this.getID();
        delete newGroupsData[id];
        var findDuplicate = BI.find(newGroupsData, function (id, groupObj) {
            if (fieldName === groupObj.table_infor.field_name) {
                return true;
            }
        });

        BI.each(fields, function (i, fieldArray) {
            findDuplicate = findDuplicate || BI.find(fieldArray, function (id, field) {
                    if (field.field_name === fieldName) {
                        return true
                    }
                })
        });

        if (BI.isNotNull(findDuplicate)) {
            isDuplicated = true;
        }
        return isDuplicated
    },

    isReopen: function () {
        return BI.deepClone(this.reopen);
    },

    isOpen2Change: function () {
        return BI.deepClone(this.open2change)
    },

    getNewGroupsData: function () {
        return BI.deepClone(this.newGroupsData);
    },

    getFields: function () {
        return BI.deepClone(this.fields);
    },

    getTableInfo: function () {
        return BI.deepClone(this.tableInfo)
    },

    getAllTables: function () {
        var tables = this.getTableInfo();
        if (this.isReopen() === true) {
            tables = tables.tables;
        }
        return tables;
    },


    getAllFields: function () {
        return BI.deepClone(this.allFields);
    },

    getStringFields: function () {
        var fields = BI.deepClone(this.allFields);

    },

    getID: function () {
        return BI.deepClone(this.id);
    }


});