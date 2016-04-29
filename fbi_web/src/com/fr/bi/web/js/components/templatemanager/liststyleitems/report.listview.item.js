/**
 * 模板文件控件
 *
 * Created by GUY on 2016/1/29.
 *
 * @class BI.ReportListViewItem
 * @extends BI.Single
 */
BI.ReportListViewItem = BI.inherit(BI.Single, {

    _defaultConfig: function () {
        return BI.extend(BI.ReportListViewItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-template-manager-file-item bi-list-item",
            height: 40,
            validationChecker: BI.emptyFn,
            id: null,
            value: null,
            buildUrl: null
        })
    },

    _init: function () {
        BI.ReportListViewItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.checkbox = BI.createWidget({
            type: "bi.checkbox"
        });
        this.checkbox.on(BI.Controller.EVENT_CHANGE, function () {
            arguments[2] = self;
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        this.editor = BI.createWidget({
            type: "bi.shelter_editor",
            width: 200,
            height: o.height,
            value: o.text,
            title: o.text,
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

        var renameIcon = BI.createWidget({
            type: "bi.icon_button",
            cls: 'rename-font template-item-icon',
            title: BI.i18nText("BI-Rename"),
            invisible: true,
            stopPropagation: true
        });
        renameIcon.on(BI.IconButton.EVENT_CHANGE, function () {
            self.editor.focus();
        });

        var deleteIcon = BI.createWidget({
            type: "bi.icon_button",
            cls: 'delete-template-font template-item-icon',
            title: BI.i18nText("BI-Remove"),
            invisible: true,
            stopPropagation: true
        });
        deleteIcon.on(BI.IconButton.EVENT_CHANGE, function () {
            o.onDeleteReport.apply(this, arguments);
        });

        var timeText = BI.createWidget({
            type: 'bi.label',
            cls: "template-file-item-date",
            text: FR.date2Str(new Date(o.lastModify), "yyyy.MM.dd HH:mm:ss")
        });

        this.element.hover(function () {
            renameIcon.setVisible(true);
            deleteIcon.setVisible(true);
        }, function () {
            renameIcon.setVisible(false);
            deleteIcon.setVisible(false);
        });

        this.blankSpace = BI.createWidget({
            type: "bi.text_button",
            text: "",
            height: 40
        });
        this.blankSpace.on(BI.TextButton.EVENT_CHANGE, function(){
            o.onClickReport.apply(self, arguments);
        });

        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.center_adapt",
                    items: [this.checkbox],
                    height: 40,
                    width: 50
                },
                width: 50
            }, {
                el: {
                    type: "bi.center_adapt",
                    items: [{
                        type: "bi.icon_button",
                        cls: "file-font",
                        iconWidth: 16,
                        iconHeight: 16
                    }],
                    height: 40,
                    width: 40
                },
                width: 40
            },{
                el: this.editor,
                width: 230
            }, {
                el: this.blankSpace,
                width: "fill"
            }, {
                el: {
                    type: "bi.left_right_vertical_adapt",
                    items: {
                        left: [renameIcon, deleteIcon],
                        right: [timeText]
                    },
                    llgap: 20,
                    rrgap: 20
                },
                width: 260
            }]
        });
    },

    isSelected: function () {
        return this.checkbox.isSelected();
    },

    setSelected: function (v) {
        this.checkbox.setSelected(v);
    },

    getText: function () {
        return this.editor.getValue();
    },

    doRedMark: function (keyword) {
        this.editor.doRedMark(keyword);
    },

    destroy: function () {
        BI.ReportListViewItem.superclass.destroy.apply(this, arguments);
    }
});
$.shortcut("bi.report_list_view_item", BI.ReportListViewItem);