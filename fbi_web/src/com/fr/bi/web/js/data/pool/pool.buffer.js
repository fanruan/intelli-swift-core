/**
 * 缓冲池
 * @type {{Buffer: {}}}
 */
;
(function () {
    var Buffer = {};
    var MODE = false;//设置缓存模式为关闭

    Data.BufferPool = {
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
                Buffer[idString] = res;
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
                Buffer[idString] = res;
                callback(res);
            });
        },

        getTablesByPackId: function (packId, callback) {
            var cache = Buffer["TABLES"] || (Buffer["TABLES"] = {});
            if (BI.isNotNull(cache[packId])) {
                return cache[packId];
            }
            BIReq.reqTablesByPackId(packId, function (res) {
                Buffer["TABLES"][packId] = res;
                callback(res);
            })
        },

        getConnectionName: function (callback) {
            // var self = this, cache = Buffer["CONNECTION_NAME"];
            // if (BI.isNotNull(cache)) {
            //     callback(cache);
            //     return;
            // }
            BIReq.reqConnectionName(function (res) {
                Buffer["CONNECTION_NAME"] = res;
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
                Buffer["CONNECTION_TABLES"][connectionName] = res;
                callback(res);
            });
        },

        getDefaultChartConfig: function () {
            var self = this, cache = Buffer["DEFAULT_CHART_CONFIG"];
            if (BI.isNotNull(cache)) {
                return cache;
            }
            var res = Data.Req.reqGetChartPreStyle();
            if (BI.isNull(res.styleList)) {
                res.styleList = [];
            }
            Buffer["DEFAULT_CHART_CONFIG"] = res;
            return res;
        }
    };
})();