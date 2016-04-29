/**
 * Created by GUY on 2015/7/3.
 */
BIShow.TargetView = BI.inherit(BI.View, {

    constants: {
        TARGET_BUTTON_HEIGHT: 25,
        COMBO_WIDTH: 25,
        CONTAINER_HEIGHT: 25,
        ICON_BUTTON_WIDTH: 12
    },

    _defaultConfig: function () {
        return BI.extend(BIShow.TargetView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target"
        })
    },

    events: {},

    _init: function () {
        BIShow.TargetView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var self = this;
        this.usedCheck = BI.createWidget({
            type: "bi.checkbox"
        });
        this.usedCheck.setSelected(this.model.get("used"));
        this.usedCheck.on(BI.Checkbox.EVENT_CHANGE, function () {
            self.model.set("used", self.usedCheck.isSelected());
        });

        this.editor = BI.createWidget({
            type: "bi.sign_editor",
            height: this.constants.TARGET_BUTTON_HEIGHT,
            cls: "bi-target-name",
            validationChecker: function () {
                return self._checkDimensionName(self.editor.getValue());
            }
        });
        this.editor.setValue(this.model.get("name"));
        this.editor.setState(this.model.get("name"));
        this.editor.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.model.set("name", self.editor.getValue());
        });

        this.iconButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "filter-font",
            height: this.constants.DIMENSION_BUTTON_HEIGHT
        });

        this.iconButton.on(BI.IconButton.EVENT_CHANGE, function () {
            self._buildFilterPane();
        });

        switch (this.model.get("type")) {
            case BICst.COLUMN.NUMBER:
                this.createCommonCombo();
                break;
            case BICst.COLUMN.COUNTER:
                this.createCountCombo();
                break;
        }

        var filterIconWidth = BI.isEmpty(this.model.get("filter_value")) ? 0 : this.constants.ICON_BUTTON_WIDTH;

        this.htape = BI.createWidget({
            type: "bi.htape",
            height: this.constants.CONTAINER_HEIGHT,
            items: [{
                el: {
                    type: "bi.center_adapt",
                    items: [this.usedCheck]
                },
                width: this.constants.COMBO_WIDTH
            }, this.editor, {el: this.iconButton, width: filterIconWidth},
                {
                    el: {
                        type: "bi.center_adapt",
                        items: [this.combo]
                    },
                    width: this.constants.COMBO_WIDTH
                }]
        });

        BI.createWidget({
            type: "bi.default",
            element: vessel,
            height: this.constants.CONTAINER_HEIGHT,
            data: {id: this.model.get("id")},
            items: [this.htape]
        });
    },

    createCommonCombo: function () {
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.target_combo_show",
            dId: this.model.get("id")
        });
        this.combo.on(BI.TargetCombo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.TARGET_COMBO.SUMMERY_TYPE:
                    break;
                case BICst.TARGET_COMBO.CHART_TYPE:
                    break;
                case BICst.TARGET_COMBO.STYLE_SETTING:
                    self._buildStyleSettingPane();
                    break;
                case BICst.TARGET_COMBO.FILTER:
                    self._buildFilterPane();
                    break;
                case BICst.TARGET_COMBO.COPY:
                    self._copyTarget();
                    break;
                case BICst.TARGET_COMBO.DELETE:
                    self._deleteTarget();
                    break;
            }
        });
    },

    createCountCombo: function () {
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.count_target_combo",
            dId: this.model.get("id")
        });
        this.combo.on(BI.CountTargetCombo.EVENT_CHANGE, function (v, s) {
            switch (v) {
                case BICst.TARGET_COMBO.DEPEND_TYPE:
                    self.model.set("_src", {
                        field_id: s
                    });
                    break;
                case BICst.TARGET_COMBO.CHART_TYPE:
                    break;
                case BICst.TARGET_COMBO.STYLE_SETTING:
                    break;
                case BICst.TARGET_COMBO.FILTER:
                    self._buildFilterPane();
                    break;
                case BICst.TARGET_COMBO.COPY:
                    self._copyTarget();
                    break;
                case BICst.TARGET_COMBO.DELETE:
                    self._deleteTarget();
                    break;
            }
        });
    },

    _checkDimensionName: function (name) {
        var currId = this.model.get("id");
        var widgetId = BI.Utils.getWidgetIDByDimensionID(currId);
        var dimsId = BI.Utils.getAllDimensionIDs(widgetId);
        var valid = true;
        BI.some(dimsId, function (i, id) {
            if (currId !== id && BI.Utils.getDimensionNameByID(id) === name) {
                valid = false;
                return true;
            }
        });
        return valid;
    },

    _buildFilterPane: function () {
        var self = this, id = this.model.get("id");
        BI.Popovers.remove(id);
        var popup = BI.createWidget({
            type: "bi.target_filter_popup",
            dId: this.model.get("id")
        });
        popup.on(BI.TargetFilterPopup.EVENT_CHANGE, function (v) {
            self.model.set("filter_value", v);
        });
        BI.Popovers.create(id, popup).open(id);
        popup.populate();
    },

    _buildStyleSettingPane: function () {
        var self = this, id = this.model.get("id");
        BI.Popovers.remove(id);
        var popup = BI.createWidget({
            type: "bi.target_style_setting",
            dId: this.model.get("id")
        });
        popup.on(BI.TargetStyleSetting.EVENT_CHANGE, function () {
            self.model.set("settings", this.getValue());
        });
        BI.Popovers.create(id, popup).open(id);
    },

    _copyTarget: function () {
        this.model.copy();
    },

    _deleteTarget: function () {
        this.model.destroy();
    },

    local: function () {
        return false;
    },

    change: function (changed) {
        if (BI.has(changed, "filter_value")) {
            this.htape.attr("items")[2].width = (BI.isEmpty(changed.filter_value) ? 0 : this.constants.ICON_BUTTON_WIDTH);
            this.htape.resize();
        }
    },

    refresh: function () {

    }
});
