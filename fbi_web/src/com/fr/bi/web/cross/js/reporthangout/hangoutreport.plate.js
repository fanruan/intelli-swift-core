/**
 * Created by Young's on 2016/6/1.
 */
BI.PlateHangoutReport = BI.inherit(BI.BarPopoverSection, {

    _constant: {
        ID_PREFIX: "__id_prefix__",
        ALL_REPORT_NODE: "__all_report_node__"
    },

    _defaultConfig: function () {
        return BI.extend(BI.PlateHangoutReport.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-plate-hangout-report"
        })
    },

    _init: function () {
        BI.PlateHangoutReport.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function (north) {
        var text = (BI.isNotNull(this.options.report) ? BI.i18nText("BI-Modify") : BI.i18nText("BI-Add")) + "BI"
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: text,
            height: 50,
            textAlign: "left",
            hgap: 10
        });
    },

    rebuildCenter: function (center) {
        var self = this, o = this.options;
        this.reportsCombo = BI.createWidget({
            type: "bi.multilayer_single_tree_combo",
            cls: "all-reports",
            items: [],
            width: 220,
            height: 30
        });
        this.reportsCombo.on(BI.MultiLayerSingleTreeCombo.EVENT_CHANGE, function(){
            var v = this.getValue()[0];
            self.reportName.setValue(self.valueMap[v].reportName);
        });
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: center,
            text: BI.i18nText("BI-Loading")
        });
        BI.requestAsync("fr_bi", "get_all_hangout_reports", {}, function (data) {
            var allReports = data.all_reports, users = data.users;
            var items = self._formatItems(allReports, users);
            self.reportsCombo.populate(items);
            if (BI.isNotNull(o.report)) {
                var vId = "";
                BI.some(self.valueMap, function (id, v) {
                    if (v.reportId === o.report.reportId &&
                        v.reportName === o.report.reportName &&
                        v.createBy === o.report.createBy.toString()) {
                        vId = id;
                    }
                });
                self.reportsCombo.setValue(vId);
                self.reportName.setValue(o.report.text);
                self.description.setValue(o.report.description);
            }
            mask.destroy();
        });

        this.reportName = BI.createWidget({
            type: "bi.sign_editor",
            cls: "report-name",
            height: 30,
            width: 220
        });

        this.description = BI.createWidget({
            type: "bi.textarea_editor",
            cls: "report-description",
            width: 220,
            height: 100
        });

        BI.createWidget({
            type: "bi.vertical",
            element: center,
            cls: "bi-plate-hangout-report",
            items: [{
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Path"),
                    height: 30,
                    width: 90,
                    textAlign: "left",
                    cls: "config-name"
                }, this.reportsCombo]
            }, {
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Name_Title"),
                    height: 30,
                    width: 90,
                    textAlign: "left",
                    cls: "config-name"
                }, this.reportName]
            }, {
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Describe"),
                    height: 30,
                    width: 90,
                    textAlign: "left",
                    cls: "config-name"
                }, this.description]
            }],
            hgap: 10,
            vgap: 10
        })
    },

    _formatItems: function (allReports, users) {
        var self = this;
        var items = [];
        var manager = "";
        this.valueMap = {};
        BI.each(allReports, function (userId, reports) {
            //普通用户
            if (BI.isNotNull(users[userId])) {
                items.push({
                    id: userId,
                    pId: self._constant.ALL_REPORT_NODE,
                    text: users[userId],
                    value: userId,
                    isParent: true
                });
                BI.each(reports, function (i, report) {
                    var v = BI.UUID();
                    self.valueMap[v] = {
                        reportId: report.id,
                        reportName: report.text,
                        createBy: userId
                    };
                    items.push({
                        id: BI.UUID(),
                        pId: userId,
                        text: report.text,
                        value: v
                    })
                });
            } else {
                manager = userId;
            }
        });
        var managerReports = allReports[manager];
        BI.each(managerReports, function (i, report) {
            if (BI.isNotNull((report.buildUrl))) {
                var v = BI.UUID();
                self.valueMap[v] = {
                    reportName: report.text,
                    reportId: report.id,
                    createBy: manager
                };
                items.push({
                    id: self._constant.ID_PREFIX + report.id,
                    pId: self._constant.ID_PREFIX + report.pId,
                    text: report.text,
                    value: v
                });
            } else {
                items.push({
                    id: self._constant.ID_PREFIX + report.id,
                    pId: self._constant.ID_PREFIX + report.pId,
                    text: report.text,
                    value: report.id,
                    isParent: true
                });
            }
        });
        items.push({
            id: this._constant.ID_PREFIX + "-1",
            text: BI.i18nText("BI-My_Created"),
            value: -1,
            isParent: true,
            open: true
        });
        items.push({
            id: this._constant.ALL_REPORT_NODE,
            text: BI.i18nText("BI-All_Reports"),
            value: this._constant.ALL_REPORT_NODE,
            isParent: true,
            open: true
        });
        return items;
    },

    end: function () {
        if(BI.isNull(this.reportsCombo.getValue()) || this.reportsCombo.getValue().length===0){
            BI.Msg.alert(BI.i18nText("BI-Attention"), BI.i18nText("BI-Please_Select_Report"), function(){});
            return;
        }
        this.fireEvent(BI.PlateHangoutReport.EVENT_SAVE);
    },

    getValue: function () {
        var report = this.valueMap[this.reportsCombo.getValue()[0]];
        return {
            reportName: report.reportName,
            reportId: report.reportId,
            createBy: report.createBy,
            text: this.reportName.getValue(),
            description: this.description.getValue()
        }
    }
});
BI.PlateHangoutReport.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.plate_hangout_report", BI.PlateHangoutReport);