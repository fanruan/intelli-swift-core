/**
 * 文件管理控件组
 *
 * Created by GUY on 2015/12/11.
 * @class BI.TemplateManagerButtonGroup
 * @extends BI.Widget
 */
BI.TemplateManagerButtonGroup = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.TemplateManagerButtonGroup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-template-manager-button_group",
            items: []
        })
    },

    _init: function () {
        BI.TemplateManagerButtonGroup.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.viewType = BI.TemplateManager.LIST_VIEW;
        this.button_group = BI.createWidget({
            type: "bi.button_group",
            element: this.element,
            chooseType: BI.Selection.Multi,
            items: this._formatItems(o.items),
            layouts: [{
                type: "bi.vertical"
            }]
        });
        this.button_group.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
    },

    _formatItems: function (items) {
        var self = this, o = this.options;
        BI.each(items, function (i, item) {
            if (BI.isNotNull(item.children)) {
                BI.extend(item, {
                    type: self.viewType === BI.TemplateManager.LIST_VIEW ? "bi.folder_list_view_item" : "bi.folder_card_view_item",
                    onClickItem: function(){
                        self.fireEvent(BI.TemplateManagerButtonGroup.EVENT_CHANGE, item.id);
                    },
                    onRenameFolder: function(name){
                        self.fireEvent(BI.TemplateManagerButtonGroup.EVENT_FOLDER_RENAME, item.id, name, BI.TemplateManagerButtonGroup.RENAME_FOLDER);
                    },
                    onDeleteFolder: function(){
                        self.fireEvent(BI.TemplateManagerButtonGroup.EVENT_DELETE, item.id, BI.TemplateManagerButtonGroup.DELETE_FOLDER);
                    },
                    validationChecker: function(name){
                        return o.folderChecker(name, item.id);
                    }
                });
            } else {
                BI.extend(item, {
                    type: self.viewType === BI.TemplateManager.LIST_VIEW ? "bi.report_list_view_item" : "bi.report_card_view_item",
                    onClickReport: function(){
                        FS.tabPane.addItem({
                            title: item.text,
                            src: FR.servletURL + item.buildUrl + "&edit=_bi_edit_"
                        });
                    },
                    onRenameReport: function(name){
                        self.fireEvent(BI.TemplateManagerButtonGroup.EVENT_FOLDER_RENAME, item.id, name, BI.TemplateManagerButtonGroup.RENAME_REPORT);
                    },
                    onDeleteReport: function(){
                        self.fireEvent(BI.TemplateManagerButtonGroup.EVENT_DELETE, item.id, BI.TemplateManagerButtonGroup.DELETE_REPORT);
                    },
                    validationChecker: function(name){
                        return o.reportChecker(name, item.id);
                    }
                });
            }
        });
        return items;
    },

    setValue: function (v) {
        this.button_group.setValue(v);
    },

    getValue: function () {
        return this.button_group.getValue();
    },

    getNotSelectedValue: function () {
        return this.button_group.getNotSelectedValue();
    },

    setNotSelectedValue: function(v){
        this.button_group.setNotSelectedValue(v);
    },

    getAllLeaves: function () {
        return this.button_group.getAllLeaves();
    },

    getAllButtons: function () {
        return this.button_group.getAllButtons();
    },

    getSelectedButtons: function () {
        return this.button_group.getSelectedButtons();
    },

    getNotSelectedButtons: function () {
        return this.button_group.getNotSelectedButtons();
    },

    prependItems: function(items){
        this.button_group.prependItems(items);
    },

    changeViewType: function(viewType){
        this.viewType = viewType;
        switch (viewType){
            case BI.TemplateManager.LIST_VIEW:
                this.button_group.attr("layouts", [{
                    type: "bi.vertical"
                }]);
                break;
            case BI.TemplateManager.CARD_VIEW:
                this.button_group.attr("layouts", [{
                    type: "bi.left",
                    hgap: 10,
                    vgap: 10
                }]);
                break;
        }
    },

    populate: function (items, keyword) {
        this.button_group.populate(this._formatItems(items), keyword);
    }
});
BI.extend(BI.TemplateManagerButtonGroup, {
    RENAME_FOLDER: 1,
    RENAME_REPORT: 2,
    DELETE_FOLDER: 1,
    DELETE_REPORT: 2
});
BI.TemplateManagerButtonGroup.EVENT_CHANGE = "TemplateManagerButtonGroup.EVENT_CHANGE";
BI.TemplateManagerButtonGroup.EVENT_FOLDER_RENAME = "TemplateManagerButtonGroup.EVENT_FOLDER_RENAME";
BI.TemplateManagerButtonGroup.EVENT_DELETE = "TemplateManagerButtonGroup.EVENT_DELETE";
$.shortcut("bi.template_manager_button_group", BI.TemplateManagerButtonGroup);