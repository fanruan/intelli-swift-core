/**
 * create by young
 * 文件夹和模板排序
 */
BI.FolderFileSortCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.FolderFileSortCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: 'bi-folder-file-sort-combo'
        });
    },

    _init: function () {
        BI.FolderFileSortCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.trigger = BI.createWidget({
            type: "bi.icon_combo_trigger",
            items: o.items,
            width: o.width,
            height: o.height
        });
        this.popup = BI.createWidget({
            type: "bi.icon_combo_popup",
            chooseType: o.chooseType,
            items: o.items
        });
        this.popup.on(BI.IconComboPopup.EVENT_CHANGE, function () {
            self.setValue(self.popup.getValue());
            self.iconCombo.hideView();
            self.fireEvent(BI.FolderFileSortCombo.EVENT_CHANGE);
        });
        this.popup.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        this.iconCombo = BI.createWidget({
            type: "bi.combo",
            element: this.element,
            direction: "top,left",
            adjustXOffset: -30,
            adjustYOffset: -30,
            el: this.trigger,
            popup: {
                el: this.popup,
                maxWidth: "",
                maxHeight: 300
            }
        });
    },

    setValue: function (v) {
        this.iconCombo.setValue(v);
    },

    setEnable: function (v) {
        BI.IconCombo.superclass.setEnable.apply(this, arguments);
        this.iconCombo.setEnable(v);
    },

    getValue: function () {
        return this.iconCombo.getValue();
    },

    populate: function (items) {
        this.options.items = items;
        this.iconCombo.populate(items);
    }
});
BI.FolderFileSortCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.folder_file_sort_combo", BI.FolderFileSortCombo);