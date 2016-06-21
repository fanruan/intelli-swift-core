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
        var renameButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "rename-report-font tool-rename-icon",
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

        var packageButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "card-view-report-icon",
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
                el: deleteButton,
                right: 0,
                top: 0
            }, {
                el: renameButton,
                right: 0,
                top: 25
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
        }, function(){
            if(!self.checkbox.isSelected()){
                self.checkbox.setVisible(false);
            }
            deleteButton.setVisible(false);
            renameButton.setVisible(false);
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