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
        this.iconCombo = BI.createWidget({
            type: "bi.icon_combo",
            element: this.element,
            items: o.items,
            width: o.width,
            height: o.height,
            offsetStyle: "right"
        });
        this.iconCombo.on(BI.IconCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.FolderFileSortCombo.EVENT_CHANGE);
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