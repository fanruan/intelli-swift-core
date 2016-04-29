/**
 * 模板文件夹控件
 *
 * @class BI.FolderListViewItem
 * @extends BI.Single
 */
BI.FolderListViewItem = BI.inherit(BI.Single, {

    constants: {
        minGap: 10,
        maxGap: 20
    },
    _defaultConfig: function () {
        return BI.extend(BI.FolderListViewItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-template-manager-folder-item bi-list-item",
            height: 40,
            validationChecker: BI.emptyFn,
            id: null,
            value: null
        })
    },

    _init: function () {
        BI.FolderListViewItem.superclass._init.apply(this, arguments);
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
            width: 230,
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
            o.onRenameFolder(this.getValue());
            this.setTitle(this.getValue());
        });
        this.editor.on(BI.ShelterEditor.EVENT_CLICK_LABEL, function(){
            o.onClickItem.apply(self, arguments);
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
            o.onDeleteFolder.apply(this, arguments);
        });

        var timeText = BI.createWidget({
            type: 'bi.label',
            cls: "template-folder-item-date",
            text: FR.date2Str(new Date(o.lastModify), "yyyy.MM.dd HH:mm:ss")
        });
        this.tree = new BI.Tree();
        this.tree.initTree([{
            id: o.id,
            children: o.children
        }]);
        this.selectValue = [];

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
            o.onClickItem.apply(self, arguments);
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
                        cls: "folder-font",
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

    focus: function () {
        this.editor.focus();
    },

    getText: function () {
        return this.editor.getValue();
    },

    destroy: function () {
        BI.FolderListViewItem.superclass.destroy.apply(this, arguments);
    }
});
$.shortcut("bi.folder_list_view_item", BI.FolderListViewItem);