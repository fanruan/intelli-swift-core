/**
 * Created by GUY on 2015/7/3.
 */
BIDezi.TargetView = BI.inherit(BI.View, {
    constants: {
        TARGET_BUTTON_HEIGHT: 25,
        COMBO_WIDTH: 25,
        CALC_COMBO_WIDTH: 47,
        CHECKBOX_WIDTH: 25,
        CONTAINER_HEIGHT: 25,
        ICON_BUTTON_WIDTH: 12
    },

    _defaultConfig: function () {
        return BI.extend(BIDezi.TargetView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-target"
        })
    },

    _init: function () {
        BIDezi.TargetView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var self = this;
        this.usedCheck = BI.createWidget({
            type: "bi.checkbox"
        });
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

        var item = [], comboWidth = this.constants.COMBO_WIDTH;
        switch (this.model.get("type")) {
            case BICst.TARGET_TYPE.NUMBER:
                item = this.createCommonCombo();
                break;
            case BICst.TARGET_TYPE.COUNTER:
                item = this.createCountCombo();
                break;
            case BICst.TARGET_TYPE.FORMULA:
            case BICst.TARGET_TYPE.MONTH_ON_MONTH_RATE:
            case BICst.TARGET_TYPE.MONTH_ON_MONTH_VALUE:
            case BICst.TARGET_TYPE.RANK:
            case BICst.TARGET_TYPE.RANK_IN_GROUP:
            case BICst.TARGET_TYPE.SUM_OF_ABOVE:
            case BICst.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP:
            case BICst.TARGET_TYPE.SUM_OF_ALL:
            case BICst.TARGET_TYPE.SUM_OF_ALL_IN_GROUP:
            case BICst.TARGET_TYPE.YEAR_ON_YEAR_RATE:
            case BICst.TARGET_TYPE.YEAR_ON_YEAR_VALUE:
                item = this.createCalculateCombo();
                comboWidth = this.constants.CALC_COMBO_WIDTH;
                break;


        }

        this.htape = BI.createWidget({
            type: "bi.htape",
            height: this.constants.CONTAINER_HEIGHT,
            items: [{
                el: {
                    type: "bi.center_adapt",
                    items: [this.usedCheck]
                },
                width: this.constants.CHECKBOX_WIDTH
            }, this.editor, {el: this.iconButton, width: 0},
                {
                    el: {
                        type: "bi.center_adapt",
                        items: item
                    },
                    width: comboWidth
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
            type: "bi.target_combo",
            dId: this.model.get("id")
        });
        this.combo.on(BI.AbstractDimensionTargetCombo.EVENT_CHANGE, function (v, s) {
            switch (v) {
                case BICst.TARGET_COMBO.SUMMERY_TYPE:
                    self.model.set("group", {type: s});
                    break;
                case BICst.TARGET_COMBO.CHART_TYPE:
                    self.model.set("style_of_chart", {type: s});
                    break;
                case BICst.TARGET_COMBO.STYLE_SETTING:
                    if(s === BICst.TARGET_COMBO.CORDON){
                        self._buildCordonPane();
                    }else{
                        self._buildStyleSettingPane();
                    }
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

        return [this.combo];
    },

    createCountCombo: function () {
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.count_target_combo",
            dId: this.model.get("id")
        });
        this.combo.on(BI.AbstractDimensionTargetCombo.EVENT_CHANGE, function (v, s) {
            switch (v) {
                case BICst.TARGET_COMBO.DEPEND_TYPE:
                    self.model.set("_src", {
                        field_id: s
                    });
                    break;
                case BICst.TARGET_COMBO.CHART_TYPE:
                    self.model.set("style_of_chart", {type: s});
                    break;
                case BICst.TARGET_COMBO.STYLE_SETTING:
                    if(s === BICst.TARGET_COMBO.CORDON){
                        self._buildCordonPane();
                    }else{
                        self._buildStyleSettingPane();
                    }
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
        return [this.combo];
    },

    createCalculateCombo: function () {
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.calculate_target_combo",
            dId: this.model.get("id")
        });
        this.combo.on(BI.AbstractDimensionTargetCombo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.CALCULATE_TARGET_COMBO.FORM_SETTING:
                    self._buildStyleSettingPane();
                    break;
                case BICst.CALCULATE_TARGET_COMBO.UPDATE_TARGET:
                    self._updateTarget();
                    break;
                case BICst.CALCULATE_TARGET_COMBO.HIDDEN:
                    self.model.set("used", false);
                    break;
                case BICst.CALCULATE_TARGET_COMBO.DISPLAY:
                    self.model.set("used", true);
                    break;
                case BICst.CALCULATE_TARGET_COMBO.DELETE:
                    self._deleteTarget();
                    break;
                case BICst.CALCULATE_TARGET_COMBO.RENAME:
                    self.editor.focus();
                    break;
                case BICst.CALCULATE_TARGET_COMBO.COPY:
                    self._copyTarget();
                    break;

            }
        });

        this.calculateTargetButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "calculate-target-font",
            height: this.constants.DIMENSION_BUTTON_HEIGHT

        });

        this.calculateTargetButton.on(BI.IconButton.EVENT_CHANGE, function () {
            self._updateTarget()
        });

        return [this.calculateTargetButton, this.combo]
    },

    _updateTarget: function () {
        var self = this;
        var dId = this.model.get("id");
        this.popup = BI.createWidget({
            type: "bi.calculate_target_popup_summary",
            wId: BI.Utils.getWidgetIDByDimensionID(dId),
            targetId: dId
        });
        this.popup.on(BI.CalculateTargetPopupSummary.EVENT_CHANGE, function () {
            var data = self.popup.getValue().data;
            self.model.set({
                _src: data._src,
                name: data.name,
                type: data.type
            });
        });
        BI.Popovers.remove(dId + "calculate_set");
        BI.Popovers.create(dId + "calculate_set", this.popup).open(dId + "calculate_set");
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

    _buildCordonPane: function(){
        var self = this, id = this.model.get("id");
        BI.Popovers.remove(id);
        var popup = BI.createWidget({
            type: "bi.cordon_popup",
            dId: this.model.get("id")
        });
        popup.on(BI.CordonPopup.EVENT_CHANGE, function (v) {
            self.model.set("cordon", v);
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
        if (BI.has(changed, "name")) {
            this.editor.setValue(this.model.get("name"));
        }
        if (BI.has(changed, "used")) {
            this.usedCheck.setSelected(this.model.get("used"))
        }
    },

    refresh: function () {
        this.usedCheck.setSelected(this.model.get("used"));
        this.editor.setValue(this.model.get("name"));
        this.editor.setState(this.model.get("name"));
        var filterIconWidth = BI.isEmpty(this.model.get("filter_value")) ? 0 : this.constants.ICON_BUTTON_WIDTH;
        var items = this.htape.attr("items");
        items[2].width = filterIconWidth;
        this.htape.attr("items", items);
        this.htape.resize();
    }
});