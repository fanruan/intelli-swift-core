/**
 * create by young
 * 列表视图下的文件夹
 */
BI.ReportCardViewItem = BI.inherit(BI.Single, {
    _defaultConfig: function () {
        var conf = BI.ReportCardViewItem.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-report-card-view-item",
            height: 130,
            width: 140
        })
    },

    _init: function () {
        BI.ReportCardViewItem.superclass._init.apply(this, arguments);
        var o = this.options, self = this;
        this.status = o.status;
        var renameButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "report-rename-font tool-rename-icon",
            title: BI.i18nText("BI-Table_Rename"),
            iconWidth: 20,
            iconHeight: 20,
            stopPropagation: true
        });

        renameButton.on(BI.IconButton.EVENT_CHANGE, function () {
            self.editor.focus();
        });

        var deleteButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "remove-report-font tool-delete-icon",
            title: BI.i18nText("BI-Remove"),
            iconWidth: 20,
            iconHeight: 20,
            stopPropagation: true
        });
        if (o.status === BICst.REPORT_STATUS.HANGOUT) {
            deleteButton.setEnable(false);
            deleteButton.setWarningTitle(BI.i18nText("BI-Hangout_Report_Can_Not_Delete"));
        }
        deleteButton.on(BI.IconButton.EVENT_CHANGE, function () {
            o.onDeleteReport.apply(this, arguments);
        });

        this.checkbox = BI.createWidget({
            type: "bi.checkbox"
        });
        this.checkbox.on(BI.Controller.EVENT_CHANGE, function () {
            self._refreshActive();
            arguments[2] = self;
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        if (FS.config.isAdmin === false) {
            var markCls = "report-apply-hangout-ing-font";
            this.hangout = BI.createWidget({
                type: "bi.icon_change_button",
                cls: "tool-rename-icon",
                title: function(){
                    if(self.status === BICst.REPORT_STATUS.NORMAL) {
                        return BI.i18nText("BI-Report_Hangout_Applying");
                    }
                    if(self.status === BICst.REPORT_STATUS.APPLYING) {
                        return BI.i18nText("BI-Cancel_Apply_Hangout");
                    }
                    if(self.status === BICst.REPORT_STATUS.HANGOUT) {
                        return BI.i18nText("BI-Hangouted");
                    }
                },
                stopPropagation: true,
                invisible: true,
                width: 20,
                height: 20
            });
            if (o.status === BICst.REPORT_STATUS.HANGOUT) {
                markCls = "report-hangout-font";
                this.hangout.setEnable(false);
                this.hangout.setWarningTitle(BI.i18nText("BI-Hangout_Report_Can_Not_Mark"));
            }
            this.markButton = BI.createWidget({
                type: "bi.icon_change_button",
                cls: "template-item-mark-icon",
                title: function(){
                    if(self.status === BICst.REPORT_STATUS.APPLYING) {
                        return BI.i18nText("BI-Report_Hangout_Applying");
                    }
                    if(self.status === BICst.REPORT_STATUS.HANGOUT) {
                        return BI.i18nText("BI-Hangouted");
                    }
                },
                stopPropagation: true,
                width: 20,
                height: 20
            });
            this.markButton.setIcon(markCls);
            this.hangout.on(BI.IconChangeButton.EVENT_CHANGE, function () {
                o.onClickHangout();
                self._onClickHangout();
            });
            this._refreshHangout();

            //查看已分享
            var sharedButton = BI.createWidget({
                type: "bi.icon_button",
                cls: "share-font tool-rename-icon",
                title: BI.i18nText("BI-Cancel_Shared_Users"),
                width: 20,
                height: 20,
                invisible: true
            });
            sharedButton.on(BI.IconButton.EVENT_CHANGE, function(){
                var id = BI.UUID();
                var sharedUsers = BI.createWidget({
                    type: "bi.edit_shared_pane",
                    shared: o.shared
                });
                sharedUsers.on(BI.EditSharedPane.EVENT_CLOSE, function(){
                    BI.Popovers.remove(id);
                });
                sharedUsers.on(BI.EditSharedPane.EVENT_SAVE, function(){
                    o.editSharedUsers(this.getValue());
                    BI.Popovers.remove(id);
                });
                BI.Popovers.create(id, sharedUsers, {width: 600, height: 500}).open(id);
            });
            if(BI.isNull(o.shared) || o.shared.length === 0) {
                sharedButton.setEnable(false);
                sharedButton.setWarningTitle(BI.i18nText("BI-The_Report_Not_Shared"));
            }
        }

        var packageButton = BI.createWidget({
            type: "bi.icon_button",
            cls: o.description === "true" ? "card-view-real-time-icon" : "card-view-report-icon",
            iconWidth: 90,
            iconHeight: 75
        });

        packageButton.on(BI.IconButton.EVENT_CHANGE, function () {
            o.onClickReport.apply(self, arguments);
        });

        this.editor = BI.createWidget({
            type: "bi.shelter_editor",
            height: 30,
            value: o.text,
            title: o.text,
            textAlign: "center",
            allowBlank: false,
            validationChecker: function (v) {
                return o.validationChecker(v);
            }
        });
        this.editor.on(BI.ShelterEditor.EVENT_ERROR, function () {
            self.editor.setErrorText(BI.i18nText("BI-File_Name_Cannot_Be_Repeated"));
        });
        this.editor.on(BI.ShelterEditor.EVENT_CONFIRM, function () {
            o.onRenameReport(this.getValue());
            this.setTitle(this.getValue());
        });
        this.editor.on(BI.ShelterEditor.EVENT_CLICK_LABEL, function(){
            o.onClickReport.apply(self, arguments);
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: packageButton,
                left: 30,
                top: 20
            }, {
                el: this.editor,
                left: 10,
                right: 10,
                bottom: 0
            }, {
                el: this.checkbox,
                top: 0,
                left: 0
            }, {
                el: this.markButton || BI.createWidget(),
                top: 16,
                left: 36
            }, {
                el: {
                    type: "bi.vertical",
                    items: [sharedButton, this.hangout || BI.createWidget(), renameButton, deleteButton],
                    bgap: 2,
                    width: 25
                },
                top: 0,
                right: 0
            }]
        });
        if(!self.checkbox.isSelected()){
            self.checkbox.setVisible(false);
        }
        deleteButton.setVisible(false);
        renameButton.setVisible(false);
        this.element.hover(function(){
            self.checkbox.setVisible(true);
            deleteButton.setVisible(true);
            renameButton.setVisible(true);
            self.hangout && self.hangout.setVisible(true);
            sharedButton && sharedButton.setVisible(true);
        }, function(){
            if(!self.checkbox.isSelected()){
                self.checkbox.setVisible(false);
            }
            deleteButton.setVisible(false);
            renameButton.setVisible(false);
            self.hangout && self.hangout.setVisible(false);
            sharedButton && sharedButton.setVisible(false);
        });
    },

    _refreshActive: function(){
        if(this.checkbox.isSelected()){
            this.element.addClass("active");
            this.checkbox.setVisible(true);
        } else {
            this.element.removeClass("active");
            this.checkbox.setVisible(false);
        }
    },

    _onClickHangout: function () {
        if (this.status === BICst.REPORT_STATUS.NORMAL) {
            this.status = BICst.REPORT_STATUS.APPLYING;
        } else {
            this.status = BICst.REPORT_STATUS.NORMAL;
        }
        this._refreshHangout();
    },

    _refreshHangout: function () {
        if (this.status === BICst.REPORT_STATUS.NORMAL) {
            this.hangout.setIcon("report-apply-hangout-ing-font");
            if(BI.isNotNull(this.markButton)) {
                this.markButton.setVisible(false);
            }
        }
        if(this.status === BICst.REPORT_STATUS.APPLYING) {
            this.hangout.setIcon("report-hangout-ing-mark-font");
            if(BI.isNotNull(this.markButton)) {
                this.markButton.setIcon("report-hangout-ing-mark-font");
                this.markButton.setVisible(true);
            }
        }
        if(this.status === BICst.REPORT_STATUS.HANGOUT) {
            this.hangout.setIcon("report-apply-hangout-normal-font");
            this.markButton && this.markButton.setVisible(true);
        }
    },

    isSelected: function () {
        return this.checkbox.isSelected();
    },

    setSelected: function (v) {
        this.checkbox.setSelected(v);
        this._refreshActive();
    },

    getText: function () {
        return this.editor.getValue();
    },

    doRedMark: function (keyword) {
        this.editor.doRedMark(keyword);
    },

    destroy: function () {
        BI.FolderListViewItem.superclass.destroy.apply(this, arguments);
    }
});
$.shortcut("bi.report_card_view_item", BI.ReportCardViewItem);