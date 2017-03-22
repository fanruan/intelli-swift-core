/**
 * Created by zcf on 2017/3/15.
 */
BI.DealWithCubeLogDataDriver = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DealWithCubeLogDataDriver.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        BI.DealWithCubeLogDataDriver.superclass._init.apply(this, arguments);

        this.errorDataItems = [];
        this.tableTransportItems = {};
        this.tableFieldIndexItems = {};
        this.relationIndexItems = {};
        this.process = 0;
        this.remainingTime = "";
    },

    _formatTime: function (time) {
        if (time < 1000) {
            return BI.parseInt(time) + BI.i18nText("BI-Basic_Millisecond");
        }
        var second = BI.parseInt(time / 1000);
        if (time < 60000) {
            return second + BI.i18nText("BI-Basic_Seconds") + BI.parseInt(time % 1000) + BI.i18nText("BI-Basic_Millisecond");
        }
        var minute = BI.parseInt(second / 60);
        return minute + BI.i18nText("BI-Basic_Minute") + BI.parseInt(second % 60) + BI.i18nText("BI-Basic_Seconds");
    },

    _updateErrorDataItems: function () {
        var self = this;
        BI.Utils.getCubeLogErrorInfo(function (data) {
            var changeFlag = false;
            BI.each(data, function (tableId, array) {
                BI.each(array, function (i, text) {
                    if (!BI.contains(self.errorDataItems, text)) {
                        self.errorDataItems.push(text);
                        change();
                    }
                })
            });

            if (changeFlag) {
                self.fireEvent(BI.DealWithCubeLogDataDriver.EVENT_CUBE_ERROR_DATA_UPDATED);
            }

            function change() {
                if (!changeFlag) {
                    changeFlag = !changeFlag;
                }
            }

            // callback(getText(data));
            // var items = {};
            // items.tableItems = formatTableItems(data);
            // items.tableHeader = getTableHeader();
            // callback(items);
        }, function () {

        });
    },

    _updateCubeLogTableTransportItems: function () {
        var self = this;
        BI.Utils.getCubeLogTableTransportInfo(function (data) {
            var items = {};
            items.tableItems = formatTableItems(data);
            items.tableHeader = getTableHeader();
            items.chartItems = formatChartItems(data);
            items.totalTime = getTotalTime(data);
            if (!BI.isEqual(self.tableTransportItems, items)) {
                self.tableTransportItems = items;
                self.fireEvent(BI.DealWithCubeLogDataDriver.EVENT_CUBE_TABLE_TRANSPORT_UPDATED);
            }
        }, function () {

        });
        function formatChartItems(data) {
            return [];
            // var chartItems = [];
            // var chartItem = {};
            // var data = [];
            // var name = items.paneName;
            // var itemName = items.name;
            // BI.each(items.time, function (id, time) {
            //     var item = {};
            //     item.x = itemName[id];
            //     item.xValue = itemName[id];
            //     item.y = time;
            //     item.yValue = time;
            //     item.z = name;
            //     item.zValue = name;
            //     data = updateData(item, data);
            // });
            // chartItem.data = data;
            // chartItem.name = name;
            //
            // chartItems.push(chartItem);
            //
            // return chartItems;
            //
            // function updateData(item, data) {
            //     data.push(item);
            //     data = BI.sortBy(data, "y").reverse();
            //     if (data.length < 10) {
            //         return data;
            //     }
            //     data = data.slice(0, 10);
            //     return data;
            // }
        }

        function formatTableItems(data) {
            var i = 1, info = data.data, items = [];
            BI.each(info, function (id, mes) {
                var item = [];
                item.push({text: i});
                item.push({text: mes.name});
                item.push({text: self._formatTime(mes.time)});
                items.push(item);
                i++;
            });
            return items
        }

        function getTableHeader() {
            return [[{text: BI.i18nText("BI-Number_Index")}, {text: BI.i18nText("BI-Table_Name")}, {text: BI.i18nText("BI-Basic_Time")}]]
        }

        function getTotalTime(data) {
            var info = data.data, totalTime = 0;
            BI.each(info, function (id, mes) {
                totalTime += mes.time;
            });
            return self._formatTime(totalTime);
        }
    },

    _updateCubeLogTableFieldIndexItems: function () {
        var self = this;
        BI.Utils.getCubeLogTableFieldIndexInfo(function (data) {
            var items = {};
            items.tableItems = formatTableItems(data);
            items.tableHeader = getTableHeader();
            items.chartItems = formatChartItems(data);
            items.totalTime = getTotalTime(data);
            if (!BI.isEqual(self.tableFieldIndexItems, items)) {
                self.tableFieldIndexItems = items;
                self.fireEvent(BI.DealWithCubeLogDataDriver.EVENT_CUBE_TABLE_FIELD_INDEX_UPDATED);
            }
        }, function () {

        });

        function formatChartItems(data) {
            return [];
        }

        function formatTableItems(data) {
            var i = 1, info = data.data, items = [];
            BI.each(info, function (id, fieldArray) {
                BI.each(fieldArray, function (j, field) {
                    var item = [];
                    item.push({text: i});
                    item.push({text: field.name});
                    item.push({text: self._formatTime(field.time)});
                    items.push(item);
                    i++;
                })
            });
            return items;
        }

        function getTotalTime(data) {
            var info = data.data, totalTime = 0;
            BI.each(info, function (id, fieldArray) {
                BI.each(fieldArray, function (j, field) {
                    totalTime += field.time;
                })
            });
            return self._formatTime(totalTime);
        }

        function getTableHeader() {
            return [[{text: BI.i18nText("BI-Number_Index")}, {text: BI.i18nText("BI-Basic_Field")}, {text: BI.i18nText("BI-Basic_Time")}]]
        }
    },

    _updateCubeLogRelationIndexItems: function () {
        var self = this;
        BI.Utils.getCubeLogRelationIndexInfo(function (data) {
            var items = {};
            items.tableItems = formatTableItems(data);
            items.tableHeader = getTableHeader();
            items.chartItems = formatChartItems(data);
            items.totalTime = getTotalTime(data);
            if (!BI.isEqual(self.relationIndexItems, items)) {
                self.relationIndexItems = items;
                self.fireEvent(BI.DealWithCubeLogDataDriver.EVENT_CUBE_RELATION_INDEX_UPDATED);
            }
        }, function () {

        });

        function formatChartItems(data) {
            return [];
        }

        function formatTableItems(data) {
            var i = 1, info = data.data, items = [];
            BI.each(info, function (id, mes) {
                var item = [];
                item.push({text: i});
                item.push({text: mes.name});
                item.push({text: self._formatTime(mes.time)});
                items.push(item);
                i++;
            });
            return items
        }

        function getTotalTime(data) {
            var info = data.data, totalTime = 0;
            BI.each(info, function (id, mes) {
                totalTime += mes.time;
            });
            return self._formatTime(totalTime);
        }

        function getTableHeader() {
            return [[{text: BI.i18nText("BI-Number_Index")}, {text: BI.i18nText("BI-Link_Infor")}, {text: BI.i18nText("BI-Basic_Time")}]]
        }
    },

    _updateProcess: function (data) {
        var process = data.process || 0;
        if (process < 1) {
            this.process = BI.parseInt(process * 100);
            this.fireEvent(BI.DealWithCubeLogDataDriver.EVENT_PROCESS_UPDATED);
        }
    },

    _updateRemainingTime: function (data) { //预估时间 使用process预估
        var process = data.process || 0;
        if (process < 1 && process > 0 && BI.isNotEmptyObject(data.generateStart)) {
            var startTime = data.generateStart.time;
            this.remainingTime = this._formatTime((new Date().getTime() - startTime) * (1 - process) / process);
            this.fireEvent(BI.DealWithCubeLogDataDriver.EVENT_REMAINING_TIME_UPDATED);
        }
    },

    _checkCubeStatus: function () {
        var self = this;
        BI.Utils.getCubeLogGenerateTime(function (data) {
            if (data.hasTask === false) {
                self._clearCheckInterval();
                self.fireEvent(BI.DealWithCubeLogDataDriver.EVENT_CUBE_UPDATE_COMPLETE);
            } else {
                self._populate(data);
                self.fireEvent(BI.DealWithCubeLogDataDriver.EVENT_CUBE_UPDATING);
            }
        }, function () {

        })
    },

    _clearCheckInterval: function () {
        var self = this;
        if (undefined != self.interval) {
            clearInterval(self.interval);
        }
    },

    _createCheckInterval: function () {
        var self = this;
        this.interval = setInterval(function () {
            self._checkCubeStatus();
        }, 2000)
    },

    _clearAllData: function () {
        this.errorDataItems = [];
        this.tableTransportItems = {};
        this.tableFieldIndexItems = {};
        this.relationIndexItems = {};
        this.process = 0;
        this.remainingTime = "";
        this.fireEvent(BI.DealWithCubeLogDataDriver.EVENT_CLEAR_ALL_DATA);
    },

    _populate: function (data) {
        this._updateErrorDataItems();
        this._updateCubeLogTableTransportItems();
        this._updateCubeLogTableFieldIndexItems();
        this._updateCubeLogRelationIndexItems();
        this._updateProcess(data);
        this._updateRemainingTime(data);
    },

    getRemainingTime: function () {
        return this.remainingTime;
    },

    getCubeLogErrorDataItems: function () {
        return this.errorDataItems;
    },

    getCubeLogTableTransportItems: function () {
        return this.tableTransportItems;
    },

    getCubeLogTableFieldIndexItems: function () {
        return this.tableFieldIndexItems;
    },

    getCubeLogRelationIndexItems: function () {
        return this.relationIndexItems;
    },

    getProcess: function () {
        return this.process;
    },

    populate: function () {
        this._clearAllData();
        this._checkCubeStatus();
        this._createCheckInterval();
    }
});
BI.DealWithCubeLogDataDriver.EVENT_CLEAR_ALL_DATA = "EVENT_CLEAR_ALL_DATA";

BI.DealWithCubeLogDataDriver.EVENT_CUBE_UPDATING = "EVENT_CUBE_UPDATING";
BI.DealWithCubeLogDataDriver.EVENT_CUBE_UPDATE_COMPLETE = "EVENT_CUBE_UPDATE_COMPLETE";

BI.DealWithCubeLogDataDriver.EVENT_CUBE_ERROR_DATA_UPDATED = "EVENT_CUBE_ERROR_DATA_UPDATED";
BI.DealWithCubeLogDataDriver.EVENT_CUBE_TABLE_TRANSPORT_UPDATED = "EVENT_CUBE_TABLE_TRANSPORT_UPDATED";
BI.DealWithCubeLogDataDriver.EVENT_CUBE_TABLE_FIELD_INDEX_UPDATED = "EVENT_CUBE_TABLE_FIELD_INDEX_UPDATED";
BI.DealWithCubeLogDataDriver.EVENT_CUBE_RELATION_INDEX_UPDATED = "EVENT_CUBE_RELATION_INDEX_UPDATED";
BI.DealWithCubeLogDataDriver.EVENT_PROCESS_UPDATED = "EVENT_PROCESS_UPDATED";
BI.DealWithCubeLogDataDriver.EVENT_REMAINING_TIME_UPDATED = "EVENT_REMAINING_TIME_UPDATED";
$.shortcut("bi.deal_with_cube_log_data_driver", BI.DealWithCubeLogDataDriver);