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

        getNumberFieldMinMaxValueById: function (id, callback) {
            var self = this, idString = id + "";
            var cache = Buffer["NUMBER_FIELD_MIN_MAX_VALUE"] || (Buffer["NUMBER_FIELD_MIN_MAX_VALUE"] = {});
            if (BI.isNotNull(cache[idString])) {
                callback(cache[idString]);
                return;
            }
            Data.Req.reqDeziNumberFieldMinMaxValueByfieldId({id: idString}, function (res) {
                self.Buffer[idString] = res;
                callback(res);
            });
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