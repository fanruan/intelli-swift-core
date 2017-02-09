/**
 * Created by lei.wang on 2017/2/7.
 */
BI.PopupSaveAs = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.PopupSaveAs.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-popup-saveas"
        });
    },

    _init: function () {
        BI.PopupSaveAs.superclass._init.apply(this, arguments);
        var o = this.options, self = this;
        this.rebuildCenter = function () {
            var self = this, o = this.options;
            this.allItems = [];
            this.templateName = BI.createWidget({
                type: "bi.sign_editor",
                width: 230,
                height: 24,
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
                width: 230,
                height: 26
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
            return BI.createWidget({
                type: 'bi.vertical',
                width: 340,
                height: 90,
                items: [
                    BI.createWidget({
                        type: 'bi.inline_vertical_adapt',
                        width: 340,
                        items: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Report_Name"),
                            height: 40,
                            width: 80,
                            textAlign: "center",
                            cls: "template-name-label",
                        }, this.templateName]
                    }),
                    BI.createWidget({
                        type: 'bi.inline_vertical_adapt',
                        width: 340,
                        items: [{
                                type: "bi.label",
                                text: BI.i18nText("BI-Template_Location"),
                                height: 40,
                                width: 80,
                                textAlign: "center",
                                cls: "template-location-label",
                            }, this.reportLocation]
                    })
                ],
                top: 10
            });
        };
        this.rebuildSouth = function () {
            var self = this;
            this.saveButton = BI.createWidget({
                type: "bi.button",
                text: BI.i18nText("BI-OK"),
                title: BI.i18nText("BI-OK"),
                height: 30
            });
            this.saveButton.on(BI.Button.EVENT_CHANGE, function () {
                self.fireEvent(BI.PopupSaveAs.EVENT_CHANGE, {
                    report_name: self.templateName.getValue(),
                    report_location: self.reportLocation.getValue()[0]
                });
            });
            this.cancelButton = BI.createWidget({
                type: "bi.button",
                level: "ignore",
                text: BI.i18nText("BI-Cancel"),
                height: 30
            });
            this.cancelButton.on(BI.Button.EVENT_CHANGE, function () {
                self.fireEvent(BI.PopupSaveAs.EVENT_COLLAPSE);
            });

            return BI.createWidget({
                type: 'bi.absolute',
                width: 340,
                height: 50,
                items: [{
                    el: this.saveButton,
                    top: 10,
                    right: 10
                },{
                    el: this.cancelButton,
                    top: 10,
                    right: 120
                }]
            });
        };
        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [
                this.rebuildCenter(),
                this.rebuildSouth()
            ]
        });
    },
    _createDistinctReportName: function (name) {
        var allNames = [];
        BI.each(this.allItems, function (i, item) {
            if (BI.isNotNull(item.buildUrl)) {
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
    }
});

BI.PopupSaveAs.EVENT_COLLAPSE = "EVENT_COLLAPSE";
BI.PopupSaveAs.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.popup_saveas", BI.PopupSaveAs);