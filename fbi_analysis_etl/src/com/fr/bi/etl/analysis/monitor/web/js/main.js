/**
 * Created by daniel on 2017/2/3.
 */
BI.Monitor = BI.Monitor || {};

BI.extend(BI.Monitor, {

    constants : {
        COLUMN_GAP : 200,
        ROW_GAP : 30,
        TABLE_WIDTH : 200,
        TABLE_HEIGHT:30,
        ERROR: 0,
        GOOD : 1,
        WARNING : 2,
        GENERATING:3,
        SINGLE_LAYER:"single_monitor_layer"
    },

    ajaxHealthData : function (callback) {
        return BI.requestAsync("sppa_monitor", "check_all_table_health", {}, function (res) {
            callback(res);
        })
    },

    ajaxSingleData : function (id, callback) {
        return BI.requestAsync("sppa_monitor", "check_single_table_health", {
            id:id
        }, function (res) {
            callback(res);
        })
    },

    createSingleTableView : function (id) {
        var layer = BI.Layers.create(BI.Monitor.constants.SINGLE_LAYER, "body")
        BI.Layers.show(BI.Monitor.constants.SINGLE_LAYER)
        var self = this;
        this.ajaxSingleData(id, function (data) {
            var button = BI.createWidget({
                type:"bi.button",
                value: BI.i18nText("BI-Close"),
                width: BI.Monitor.constants.TABLE_WIDTH,
                height: BI.Monitor.constants.TABLE_HEIGHT
            });
            button.on(BI.Button.EVENT_CHANGE, function(){
                BI.Layers.hide(BI.Monitor.constants.SINGLE_LAYER)
                BI.Layers.remove(BI.Monitor.constants.SINGLE_LAYER);
            });
            var titleArray = self.createTitleArray();
            titleArray.unshift({
                el:{
                    type:"bi.center_adapt",
                    height:50,
                    items:[button]
                },
                width:BI.Monitor.constants.TABLE_WIDTH
            });
            titleArray.unshift({
                type:"bi.layout",
                width:BI.Monitor.constants.ROW_GAP
            });
            BI.createWidget({
                type:"bi.vtape",
                cls:"single-table-layer",
                element:layer,
                items:[{
                    type:"bi.htape",
                    cls:"monitor-content",
                    height:50,
                    items:titleArray
                }]
            })
        })

    },

    createTitleArray : function () {
        return [{
            type:"bi.layout",
            width:BI.Monitor.constants.ROW_GAP
        }, {
            el:{
                type:"bi.center_adapt",
                height:50,
                items:[{
                    type:"bi.label",
                    cls:"bi-monitor-table bi-monitor-table-1",
                    value:"Very Good",
                    width:BI.Monitor.constants.TABLE_WIDTH,
                    height: BI.Monitor.constants.TABLE_HEIGHT
                }]
            },
            width:BI.Monitor.constants.TABLE_WIDTH
        }, {
            type:"bi.layout",
            width:BI.Monitor.constants.ROW_GAP
        }, {
            el:{
                type:"bi.center_adapt",
                height:50,
                items:[{
                    type:"bi.label",
                    cls:"bi-monitor-table bi-monitor-table-3",
                    value:"Generating",
                    width:BI.Monitor.constants.TABLE_WIDTH,
                    height: BI.Monitor.constants.TABLE_HEIGHT
                }]
            },
            width:BI.Monitor.constants.TABLE_WIDTH
        }, {
            type:"bi.layout",
            width:BI.Monitor.constants.ROW_GAP
        }, {
            el:{
                type:"bi.center_adapt",
                height:50,
                items:[{
                    type:"bi.label",
                    cls:"bi-monitor-table bi-monitor-table-2",
                    value:"more than one cube inuse or parent table has been deleted",
                    title:"more than one cube inuse or parent table has been deleted",
                    width:BI.Monitor.constants.TABLE_WIDTH,
                    height: BI.Monitor.constants.TABLE_HEIGHT
                }]
            },
            width:BI.Monitor.constants.TABLE_WIDTH
        }, {
            type:"bi.layout",
            width:BI.Monitor.constants.ROW_GAP
        }, {
            el:{
                type:"bi.center_adapt",
                height:50,
                items:[{
                    type:"bi.label",
                    cls:"bi-monitor-table bi-monitor-table-0",
                    value:"current table has been deleted",
                    width:BI.Monitor.constants.TABLE_WIDTH,
                    height: BI.Monitor.constants.TABLE_HEIGHT
                }]
            },
            width:BI.Monitor.constants.TABLE_WIDTH
        }]
    },

    init : function () {
        var self = this;
        this.ajaxHealthData(function (data) {
            var tables = data["data"];
            var relation = data["relation"]
            var id_table = {};
            var monitorTables = [];
            BI.each(tables, function (idx, item) {
                var monitorTable = BI.createWidget({
                    type:"bi.monitor_table",
                    name:item.name,
                    value:item.id,
                    column:item.c,
                    row:item.r,
                    status:item.h,
                    count:item.count,
                    percent:item.p
                })
                id_table[item["id"]] = monitorTable;
                monitorTables.push(monitorTable)
            })
            var res = [];
            BI.each(monitorTables, function (idx, item) {
                res.push({
                    el : item,
                    top : item.getY(),
                    left: item.getX()
                })
            })
            BI.each(relation, function (idx, item) {
                var pid = item["pid"];
                var id = item["id"];
                var end = id_table[id].getRightPointer();
                var start =  id_table[pid].getLeftPointer();
                var width = end.x- start.x;
                var height = end.y - start.y;
                var x_move = width < 0 ? (0-width) : 0;
                var y_move = height < 0 ? (0-height) : 0;
                var line = BI.createWidget({
                    type:"bi.monitor_line",
                    status:id_table[id].getStatus(),
                    generating : id_table[id].isGenerating(),
                    x:x_move,
                    y:y_move,
                    w: width + x_move,
                    h: height + y_move,
                    width: Math.abs(width),
                    height: Math.abs(height),
                    tables:[id_table[id], id_table[pid]]
                })
                id_table[id].pushRelation(line);
                id_table[pid].pushRelation(line);
                res.push({
                    el: line,
                    top:start.y - y_move,
                    left:start.x - x_move,
                    bottom:0,
                    right:0
                })
            })
            var button = BI.createWidget({
                type:"bi.button",
                value: BI.i18nText("BI-Refresh"),
                width: BI.Monitor.constants.TABLE_WIDTH,
                height: BI.Monitor.constants.TABLE_HEIGHT
            });


            button.on(BI.Button.EVENT_CHANGE, function(){
                window.location.href = window.location.href;
            });
            var titleArray = self.createTitleArray();
            titleArray.unshift({
                el:{
                    type:"bi.center_adapt",
                    height:50,
                    items:[button]
                },
                width:BI.Monitor.constants.TABLE_WIDTH
            });
            titleArray.unshift({
                type:"bi.layout",
                width:BI.Monitor.constants.ROW_GAP
            });
            BI.createWidget({
                type:"bi.vtape",
                element:"body",
                items:[{
                    type:"bi.htape",
                    cls:"monitor-content",
                    height:50,
                    items:titleArray
                },{
                    type:"bi.center_adapt",
                    items:[{
                        type:"bi.absolute",
                        cls:"monitor-content",
                        scrollable:true,
                        items:res
                    }]
                }]
            })
        })
    }
})