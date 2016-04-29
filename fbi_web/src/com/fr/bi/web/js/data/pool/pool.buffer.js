/**
 * 缓冲池
 * @type {{Buffer: {}}}
 */
;
(function () {
    var Buffer = {};
    var MODE = false;

    Data.BufferPool = {
        Buffer: {},
        MODE: false,//设置缓存模式为关闭
        put: function (name, cache) {
            if (BI.isNotNull(Buffer[name])) {
                throw new Error("Buffer Pool has the key already!");
            }
            Buffer[name] = cache;
        },

        get: function (name) {
            return Buffer[name];
        },

        getTableById: function (id, callback) {
            var self = this, idString = id + "";
            var cache = Buffer["TABLE"] || (Buffer["TABLE"] = {});
            if (BI.isNotNull(cache[idString])) {
                callback(cache[idString]);
                return;
            }
            BIReq.reqTableById(idString, function (res) {
                if (self.MODE === true) {
                    self.Buffer[idString] = res;
                }
                callback(res);
            });
        },

        getTablesByPackId: function (packId, callback) {
            BIReq.reqTablesByPackId(packId, function (res) {
                callback(res);
            })
        },

        getFieldsByTableId: function (tableId, callback) {
            BIReq.reqFieldsByTableId(tableId, function (res) {
                callback(res);
            })
        },

        getTableByConnSchemaTName: function (table, callback) {
            var self = this, connName = table.connection_name, schemaName = table.schema_name, tableName = table.table_name;
            var cache = Buffer["TABLE_INFO"] || (Buffer["TABLE_INFO"] = {});
            if (BI.isNotNull(cache[connName])
                && BI.isNotNull(cache[connName][schemaName])
                && BI.isNotNull(cache[connName][schemaName][tableName])) {
                callback(cache[connName][schemaName][tableName]);
                return;
            }
            BIReq.reqTableByConnSchemaTName(connName, schemaName, tableName, function (res) {
                if (self.MODE === true) {
                    self.Buffer[connName] || (self.Buffer[connName] = {});
                    self.Buffer[connName][schemaName] || (self.Buffer[connName][schemaName] = {});
                    self.Buffer[connName][schemaName][tableName] = res;
                }
                callback(res);
            });
        },

        getField: function (id) {
            var idString = id + "";
            var cache = Buffer["Field"] || (Buffer["Field"] = {});
            if (BI.isNotNull(cache[idString])) {
                return cache[idString];
            }
            var table = BI.requestSync("fr_bi_base", "get_field", {"id": idString});

            if (MODE === true) {
                cache[idString] = table;
            }
            return table;
        },

        getPackage: function (callback) {
            var self = this, cache = Buffer["PACKAGE"] || (Buffer["PACKAGE"] = []);
            if (BI.isNotNull(cache)) {
                callback(cache);
                return;
            }
            BIReq.reqPackage(function (res) {
                if (self.MODE === true) {
                    self.Buffer["PACKAGE"] = res;
                }
                callback(res);
            });
        },

        getGroups: function () {
            var cache = Buffer["GROUP"] || (Buffer["GROUP"] = []);
            if (BI.isNotNull(cache)) {
                return cache;
            }
            var self = this;
            var groups = BI.requestSync("fr_bi_dezi", "get_accessable_group_packages");
            var packages = this.getPackage();
            var store = {};//记录哪些业务包在分组中
            BI.each(groups, function (i, group) {
                BI.each(group, function (j, pack) {
                    store[pack.package_name] = true;
                })
            })
            BI.each(packages, function (i, pack) {
                if (store[pack.package_name] === true) {
                    return true;
                }
                groups.push({"text": pack.package_name, "value": pack.package_name});
            });
            if (MODE === true) {
                Buffer["GROUP"] = groups;
            }
            return groups;
        },

        getConnectionName: function (callback) {
            var self = this, cache = Buffer["CONNECTION_NAME"] || (Buffer["CONNECTION_NAME"] = {});
            if (BI.isNotNull(cache.length)) {
                callback(cache);
                return;
            }
            BIReq.reqConnectionName(function (res) {
                if (self.MODE === true) {
                    self.Buffer["CONNECTION_NAME"] = res;
                }
                callback(res);
            });
        },

        getTablesByConnectionName: function (connectionName, callback) {
            var self = this, cache = Buffer["CONNECTION_TABLES"] || (Buffer["CONNECTION_TABLES"] = {});
            if (BI.isNotNull(cache[connectionName])) {
                callback(cache[connectionName]);
                return;
            }
            BIReq.reqTablesByConnectionName(connectionName, function (res) {
                if (self.MODE === true) {
                    self.Buffer["CONNECTION_TABLES"] = res;
                }
                callback(res);
            });
        }
    };
})();