BI.NewAnalysisFloatBox = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function () {
        return BI.extend(BI.NewAnalysisFloatBox.superclass._defaultConfig.apply(this, arguments), {});
    },

    _init: function () {
        BI.NewAnalysisFloatBox.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function (north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Add_Analysis"),
            height: 50,
            textAlign: "left"
        })
    },

    rebuildCenter: function (center) {
        var self = this;
        this.allItems = [];
        this.templateName = BI.createWidget({
            type: "bi.sign_editor",
            width: 260,
            height: 28,
            cls: "template-name-input",
            allowBlank: false,
            errorText: function (v) {
                if (v === "") {
                    return BI.i18nText("BI-Report_Name_Not_Null");
                } else {
                    return BI.i18nText("BI-Template_Name_Already_Exist");
                }
            },
            validationChecker: function (v) {
                return !self._getCurrentNodeReportNames().contains(v);
            }
        });
        this.templateName.on(BI.SignEditor.EVENT_ERROR, function (v) {
            if (BI.isEmptyString(v)) {
                self.saveButton.setWarningTitle(BI.i18nText("BI-Report_Name_Not_Null"));
            } else {
                self.saveButton.setWarningTitle(BI.i18nText("BI-Template_Name_Already_Exist"));
            }
            self.saveButton.setEnable(false);
        });
        this.templateName.on(BI.SignEditor.EVENT_VALID, function () {
            self.saveButton.setEnable(true);
        });
        this.reportLocation = BI.createWidget({
            type: "bi.multilayer_select_tree_combo",
            cls: "template-location-combo",
            width: 260,
            height: 28
        });
        this.reportLocation.on(BI.MultiLayerSelectTreeCombo.EVENT_CHANGE, function () {
            var name = self.templateName.getValue();
            if (self._getCurrentNodeReportNames().contains(name)) {
                self.templateName.setValid();
                self.templateName.focus();
            }
        });
        //这里直接使用请求
        BI.requestAsync("fr_bi", "get_folder_report_list", {}, function (data) {
            //在这边找到所有的模板名称 和 文件夹
            self.allItems = data;
            self.reportLocation.populate(self._formatItems(self.allItems));
            self.reportLocation.setValue(BI.FileManagerNav.ROOT_CREATE_BY_ME);
        });
        BI.createWidget({
            type: "bi.vertical",
            cls: "bi-new-analysis-center",
            element: center,
            items: [{
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Report_Name"),
                    height: 30,
                    width: 100,
                    textAlign: "left",
                    cls: "template-name-label",
                    hgap: 10
                }, this.templateName]
            }, {
                type: "bi.left",
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Template_Location"),
                    height: 30,
                    width: 100,
                    textAlign: "left",
                    cls: "template-location-label",
                    hgap: 10
                }, this.reportLocation]
            }],
            vgap: 20
        })
    },

    rebuildSouth: function (south) {
        var self = this;
        var checkBox = BI.createWidget({
            type: "bi.checkbox"
        });
        this.saveButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-OK"),
            title: BI.i18nText("BI-OK"),
            height: 28
        });
        this.saveButton.on(BI.Button.EVENT_CHANGE, function () {
            var data = {
                reportName: self.templateName.getValue(),
                reportLocation: self.reportLocation.getValue()[0],
                realTime: checkBox.isSelected()
            };
            self.fireEvent(BI.NewAnalysisFloatBox.EVENT_CHANGE, data);
            self.close();
        });
        BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            element: south,
            cls: "bi-new-analysis-south",
            items: {
                left: [checkBox, {
                    type: "bi.label",
                    cls: "real-time-report",
                    text: BI.i18nText("BI-Realtime_Report")
                }],
                right: [{
                    type: "bi.button",
                    level: "ignore",
                    text: BI.i18nText("BI-Cancel"),
                    height: 28,
                    handler: function () {
                        self.close();
                    }
                }, this.saveButton]
            },
            lhgap: 5,
            rhgap: 5
        })
    },

    _formatItems: function (items) {
        var formatItems = [];
        BI.each(items, function (i, item) {
            if (BI.isNull(item.buildUrl)) {
                formatItems.push({
                    id: item.id,
                    pId: item.pId,
                    text: item.text,
                    value: item.value
                })
            }
        });
        formatItems.push({
            id: BI.FileManagerNav.ROOT_CREATE_BY_ME,
            text: BI.i18nText("BI-Created_By_Me"),
            value: BI.FileManagerNav.ROOT_CREATE_BY_ME
        });
        return formatItems;
    },

    _getCurrentNodeReportNames: function () {
        var currNode = this.reportLocation.getValue()[0];
        var reportNames = [];
        BI.each(this.allItems, function (i, item) {
            if (BI.isNotNull(item.buildUrl) && item.pId === currNode) {
                reportNames.push(item.text);
            }
        });
        return reportNames;
    },

    setTemplateNameFocus: function () {
        this.templateName.focus();
    }
});
BI.NewAnalysisFloatBox.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.new_analysis_float_box", BI.NewAnalysisFloatBox);