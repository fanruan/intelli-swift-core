/**
 * Created by Young's on 2016/5/24.
 */
BI.ReportSaveAsFloatBox = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function () {
        return BI.extend(BI.ReportSaveAsFloatBox.superclass._defaultConfig.apply(this, arguments), {});
    },

    _init: function () {
        BI.ReportSaveAsFloatBox.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function (north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Save_As"),
            height: 50,
            textAlign: "left"
        })
    },

    rebuildCenter: function (center) {
        var self = this, o = this.options;
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
            self.templateName.setValue(self._createDistinctReportName(o.name));
        });
        BI.createWidget({
            type: "bi.vertical",
            cls: "bi-report-save-as-float-box",
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
        this.saveButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-OK"),
            title: BI.i18nText("BI-OK"),
            height: 28
        });
        this.saveButton.on(BI.Button.EVENT_CHANGE, function () {
            self.close();
            self.fireEvent(BI.ReportSaveAsFloatBox.EVENT_CHANGE, {
                report_name: self.templateName.getValue(),
                report_location: self.reportLocation.getValue()[0]
            });
        });
        BI.createWidget({
            type: "bi.right",
            element: south,
            items: [this.saveButton, {
                type: "bi.button",
                level: "ignore",
                text: BI.i18nText("BI-Cancel"),
                height: 28,
                handler: function () {
                    self.close();
                }
            }],
            lgap: 10
        })
    },

    _createDistinctReportName: function(name){
        var allNames = [];
        BI.each(this.allItems, function(i, item){
            if(BI.isNotNull(item.buildUrl)) {
                allNames.push({name: item.text});
            }
        });
        return BI.Func.createDistinctName(allNames, name);
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
BI.ReportSaveAsFloatBox.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.report_save_as_float_box", BI.ReportSaveAsFloatBox);