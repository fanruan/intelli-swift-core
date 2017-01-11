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
            var key = "NUMBER_FIELD_MIN_MAX_VALUE";
            var idString = id + "";
            var cache = Buffer[key] || (Buffer[key] = {});
            if (BI.isNotNull(cache[idString])) {
                callback(cache[idString]);
                return;
            }
            Data.Req.reqDeziNumberFieldMinMaxValueByfieldId({id: idString}, function (res) {
                Buffer[key][idString] = res;
                callback(res);
            });
        },

        getTableById: function (id, callback) {
            var key = "TABLE";
            var idString = id + "";
            var cache = Buffer[key] || (Buffer[key] = {});
            if (BI.isNotNull(cache[idString])) {
                callback(cache[idString]);
                return;
            }
            BIReq.reqTableById(idString, function (res) {
                Buffer[key][idString] = res;
                callback(res);
            });
        },

        getDefaultChartConfig: function () {
            var key = "DEFAULT_CHART_CONFIG";
            var cache = Buffer[key];
            if (BI.isNotNull(cache)) {
                return cache;
            }
            var res = Data.Req.reqGetChartPreStyle();
            if (BI.isNull(res.styleList)) {
                res.styleList = [];
            }
            Buffer[key] = res;
            return res;
        },

        getAllTemplates: function (callback) {
            var key = "TEMPLATES";
            var cache = Buffer[key];
            if (BI.isNotNull(cache)) {
                callback(cache);
                return;
            }
            Data.Req.reqAllTemplates(function (data) {
                Buffer[key] = data;
                callback(data);
            });
        },

        getWidgetsByTemplateId: function (tId, callback) {
            var key = "WIDGETS";
            var cache = Buffer[key + tId];
            if (BI.isNotNull(cache)) {
                callback(cache);
                return;
            }
            Data.Req.reqWidgetsByTemplateId(tId, function (data) {
                Buffer[key] = data;
                callback(data);
            });
        },

        checkAllAnalysisTablesStatus: function() {
            if (BI.isNotNull(Buffer[ETLCst.CHECK_STATUS_INTERVAL])) {
                clearInterval(Buffer[ETLCst.CHECK_STATUS_INTERVAL]);
            }
            if (BI.size(Buffer[ETLCst.PACK_ID]) > 0) {
                Buffer[ETLCst.CHECK_STATUS_INTERVAL] = setInterval(function() {
                    BI.ETLReq.reqTableStatus({}, function(res) {
                        var tables = Buffer[ETLCst.PACK_ID];
                        BI.each(tables, function(id, tableFn) {
                            tableFn(res[id]);
                        });
                    });
                }, 3000);
            }
        },

        putAnalysisTableStatusFn: function(id, callback) {
            if (BI.isNull(Buffer[ETLCst.PACK_ID])) {
                Buffer[ETLCst.PACK_ID] = {};
            }
            Buffer[ETLCst.PACK_ID][id] = callback;
        }
    };
})();