/**
 * @class BIDezi.DetailDimensionView
 * @extend BI.View
 * 明细表的单个维度
 */
BIDezi.DetailDimensionView = BI.inherit(BI.View, {

    constants: {
        DIMENSION_BUTTON_HEIGHT: 25,
        COMBO_WIDTH: 60,
        CHECKBOX_WIDTH: 25,
        CONTAINER_HEIGHT: 25,
        ICON_BUTTON_WIDTH: 12,
        ICON_BUTTON_POS: 2
    },

    _defaultConfig: function () {
        return BI.extend(BIDezi.DetailDimensionView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-dimension-view"
        })
    },

    _init: function () {
        BIDezi.DetailDimensionView.superclass._init.apply(this, arguments);
    },

    change: function (changed) {
        if (BI.has(changed, "name")) {
            this.editor.setValue(this.model.get("name"));
        }
        if (BI.has(changed, "used")) {
            this.usedCheck.setSelected(this.model.get("used"))
        }
        if (BI.has(changed, "filter_value")) {
            this.htape.attr("items")[this.constants.ICON_BUTTON_POS].width = (BI.isEmpty(changed.filter_value) ? 0 : this.constants.ICON_BUTTON_WIDTH);
            this.htape.resize();
        }
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
            height: this.constants.DIMENSION_BUTTON_HEIGHT,
            cls: "bi-dimension-name",
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


        switch (this.model.get("type")) {
            case BICst.TARGET_TYPE.STRING:
                this._createStringCombo();
                break;
            case BICst.TARGET_TYPE.NUMBER:
                this._createNumberCombo();
                break;
            case BICst.TARGET_TYPE.DATE:
                this._createDateCombo();
                break;
            case BICst.TARGET_TYPE.FORMULA:
                this._createFormulaCombo();
                break;
            default :
                this._createStringCombo();
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
                width: this.constants.CHECKBOX_WIDTH
            }, this.editor, {el: this.iconButton, width: filterIconWidth},
                {
                    el: {
                        type: "bi.center_adapt",
                        items: [this.combo]
                    },
                    width: this.constants.CHECKBOX_WIDTH
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

    _createStringCombo: function () {
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.detail_string_dimension_combo",
            dId: self.model.get("id")
        });
        this.combo.on(BI.DetailStringDimensionCombo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.DETAIL_STRING_COMBO.FILTER:
                    self._buildFilterPane();
                    break;
                case BICst.DETAIL_STRING_COMBO.HYPERLINK:
                    self._buildHyperlinkPane();
                    break;
                case BICst.DETAIL_STRING_COMBO.DELETE:
                    self._deleteDimension();
                    break;
                case BICst.DETAIL_STRING_COMBO.INFO:
                    break;
            }
        });
        this.east = BI.createWidget({
            type: "bi.right",
            items: [this.combo]
        })
    },

    _createNumberCombo: function () {
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.detail_number_dimension_combo",
            dId: self.model.get("id")
        });
        this.combo.on(BI.DetailNumberDimensionCombo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.DETAIL_NUMBER_COMBO.FORM_SETTING:
                    self._buildStyleSettingPane();
                    break;
                case BICst.DETAIL_NUMBER_COMBO.FILTER:
                    self._buildFilterPane();
                    break;
                case BICst.DETAIL_NUMBER_COMBO.HYPERLINK:
                    self._buildHyperlinkPane();
                    break;
                case BICst.DETAIL_NUMBER_COMBO.DELETE:
                    self._deleteDimension();
                    break;
                case BICst.DETAIL_NUMBER_COMBO.INFO:
                    break;
            }
        });
        this.east = BI.createWidget({
            type: "bi.right",
            items: [this.combo]
        })
    },

    _createDateCombo: function () {
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.detail_date_dimension_combo",
            dId: self.model.get("id")
        });
        this.combo.on(BI.DetailDateDimensionCombo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.DETAIL_DATE_COMBO.YMD:
                    self.model.set("group", {type: BICst.GROUP.YMD});
                    break;
                case BICst.DETAIL_DATE_COMBO.YMD_HMS:
                    self.model.set("group", {type: BICst.GROUP.YMDHMS});
                    break;
                case BICst.DETAIL_DATE_COMBO.YEAR:
                    self.model.set("group", {type: BICst.GROUP.Y});
                    break;
                case BICst.DETAIL_DATE_COMBO.SEASON:
                    self.model.set("group", {type: BICst.GROUP.S});
                    break;
                case BICst.DETAIL_DATE_COMBO.MONTH:
                    self.model.set("group", {type: BICst.GROUP.M});
                    break;
                case BICst.DETAIL_DATE_COMBO.WEEK:
                    self.model.set("group", {type: BICst.GROUP.W});
                    break;
                case BICst.DETAIL_DATE_COMBO.FILTER:
                    self._buildFilterPane();
                    break;
                case BICst.DETAIL_DATE_COMBO.HYPERLINK:
                    self._buildHyperlinkPane();
                    break;
                case BICst.DETAIL_DATE_COMBO.DELETE:
                    self._deleteDimension();
                    break;
                case BICst.DETAIL_DATE_COMBO.INFO:
                    break;
            }
        });
        this.east = BI.createWidget({
            type: "bi.right",
            items: [this.combo]
        })
    },

    _createFormulaCombo: function () {
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.detail_formula_dimension_combo"
        });
        this.combo.on(BI.DetailFormulaDimensionCombo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.DETAIL_FORMULA_COMBO.FORM_SETTING:
                    break;
                case BICst.DETAIL_FORMULA_COMBO.UPDATE_FORMULA:
                    self._updateFormula();
                    break;
                case BICst.DETAIL_FORMULA_COMBO.HYPERLINK:
                    self._buildHyperlinkPane();
                    break;
                case BICst.DETAIL_FORMULA_COMBO.DELETE:
                    self._deleteDimension();
                    break;
            }
        });

        this.calculateTargetButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "calculate-target-font",
            height: this.constants.DIMENSION_BUTTON_HEIGHT
        });

        this.calculateTargetButton.on(BI.IconButton.EVENT_CHANGE, function () {
            self._updateFormula()
        });
        this.east = BI.createWidget({
            type: "bi.right",
            items: [this.combo, this.calculateTargetButton]
        })
    },

    _buildFilterPane: function () {
        var self = this, id = this.model.get("id");
        BI.Popovers.remove(id);
        var popup = BI.createWidget({
            type: "bi.target_filter_popup",
            dId: this.model.get("id")
        });
        popup.on(BI.DimensionFilterPopup.EVENT_CHANGE, function (v) {
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

    _buildHyperlinkPane: function(){
        var self = this, id = this.model.get("id");
        BI.Popovers.remove(id);
        var popup = BI.createWidget({
            type: "bi.hyper_link_popup",
            dId: this.model.get("id")
        });
        popup.on(BI.HyperLinkPopup.EVENT_CHANGE, function (v) {
            self.model.set("hyperlink", v);
        });
        BI.Popovers.create(id, popup).open(id);
        popup.populate();
    },

    _deleteDimension: function () {
        this.model.destroy();
    },

    _updateFormula: function () {
        var self = this;
        var dId = this.model.get("id");
        this.popup = BI.createWidget({
            type: "bi.calculate_target_popup_detail",
            wId: BI.Utils.getWidgetIDByDimensionID(dId),
            targetId: dId
        });
        this.popup.on(BI.CalculateTargetPopupDetail.EVENT_CHANGE, function () {
            var data = self.popup.getValue().data;
            self.model.set({
                _src: data._src,
                name: data.name
            });

        });
        BI.Popovers.remove(dId + "calculate_set");
        BI.Popovers.create(dId + "calculate_set", this.popup).open(dId + "calculate_set");
    },

    local: function () {

    },

    refresh: function () {
        this.usedCheck.setSelected(this.model.get("used"));
        this.editor.setValue(this.model.get("name"));
        this.editor.setState(this.model.get("name"));
    }
});