/**
 * Created by GUY on 2015/7/3.
 */
BIShow.DimensionView = BI.inherit(BI.View, {

    constants: {
        DIMENSION_BUTTON_HEIGHT: 25,
        COMBO_WIDTH: 25,
        CONTAINER_HEIGHT: 25,
        ICON_BUTTON_WIDTH: 12,
        // ICON_BUTTON_POS: bi.dimensions_manager2
    },

    _defaultConfig: function () {
        return BI.extend(BIShow.DimensionView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension"
        })
    },

    events: {},

    _init: function () {
        BIShow.DimensionView.superclass._init.apply(this, arguments);
    },

    _vessel: function () {
        return this.element;
    },

    change: function (changed) {
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
        this.usedCheck.setSelected(this.model.get("used"));
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
            case BICst.COLUMN.STRING:
                this._createStringCombo();
                break;
            case BICst.COLUMN.NUMBER:
                this._createNumberCombo();
                break;
            case BICst.COLUMN.DATE:
                this._createDateCombo();
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
            type: "bi.dimension_string_combo_show",
            dId: self.model.get("id")

        });
        this.combo.on(BI.AbstractDimensionCombo.EVENT_CHANGE, function (v, s) {
            switch (v) {
                case BICst.DIMENSION_STRING_COMBO.ASCEND:
                    self.model.set("changeSort", {type: BICst.SORT.ASC, sort_target: s});
                    break;
                case BICst.DIMENSION_STRING_COMBO.DESCEND:
                    self.model.set("changeSort", {type: BICst.SORT.DESC, sort_target: s});
                    break;
                case BICst.DIMENSION_STRING_COMBO.SORT_BY_CUSTOM:
                    self._buildCustomSortPane();
                    break;
                case BICst.DIMENSION_STRING_COMBO.GROUP_BY_VALUE:
                    BI.Msg.confirm("", BI.i18nText("BI-Ensure_Group_By_Value"), function (v) {
                        if (v === true) {
                            self.model.set({
                                changeGroup: {type: BICst.GROUP.ID_GROUP},
                                changeSort: {type: BICst.SORT.ASC, sort_target: self.model.get("id")}
                            });
                        }
                    });

                    break;
                case BICst.DIMENSION_STRING_COMBO.GROUP_BY_CUSTOM:
                    self._buildCustomGroupPane();
                    break;

            }
        })
    },

    _createNumberCombo: function () {
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.dimension_number_combo_show",
            dId: self.model.get("id")
        });
        this.combo.on(BI.AbstractDimensionCombo.EVENT_CHANGE, function (v, s) {
            switch (v) {
                case BICst.DIMENSION_NUMBER_COMBO.ASCEND:
                    self.model.set("changeSort", {type: BICst.SORT.ASC, sort_target: s});
                    break;
                case BICst.DIMENSION_NUMBER_COMBO.DESCEND:
                    self.model.set("changeSort", {type: BICst.SORT.DESC, sort_target: s});
                    break;
                case BICst.DIMENSION_NUMBER_COMBO.NOT_SORT:
                    self.model.set("changeSort", {type: BICst.SORT.NONE, sort_target: s});
                    break;
                case BICst.DIMENSION_NUMBER_COMBO.SORT_BY_CUSTOM:
                    break;
                case BICst.DIMENSION_NUMBER_COMBO.GROUP_BY_VALUE:
                    self.model.set("changeGroup", {type: BICst.GROUP.ID_GROUP});
                    break;
                case BICst.DIMENSION_NUMBER_COMBO.GROUP_SETTING:
                    self._setGroups();
                    break;
                case BICst.DIMENSION_NUMBER_COMBO.FILTER:
                    self._buildFilterPane();
                    break;
                case BICst.DIMENSION_NUMBER_COMBO.INFO:
                    break;
            }
        });
    },

    _createDateCombo: function () {
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.dimension_date_combo_show",
            dId: self.model.get("id")
        });
        this.combo.on(BI.AbstractDimensionCombo.EVENT_CHANGE, function (v, s) {
            switch (v) {
                case BICst.DIMENSION_DATE_COMBO.YEAR:
                    break;
                case BICst.DIMENSION_DATE_COMBO.QUARTER:
                    break;
                case BICst.DIMENSION_DATE_COMBO.MONTH:
                    break;
                case BICst.DIMENSION_DATE_COMBO.WEEK:
                    break;
                case BICst.DIMENSION_DATE_COMBO.ASCEND:
                    self.model.set("changeSort", {type: BICst.SORT.ASC, sort_target: s});
                    break;
                case BICst.DIMENSION_DATE_COMBO.DESCEND:
                    self.model.set("changeSort", {type: BICst.SORT.DESC, sort_target: s});
                    break;
                case BICst.DIMENSION_DATE_COMBO.FILTER:
                    self._buildFilterPane();
                    break;
                case BICst.DIMENSION_DATE_COMBO.INFO:
                    break;
            }
        })
    },

    _setGroups: function () {
        BIShow.FloatBoxes.open("numberCustomGroup", "group", {}, this, {id: this.model.get("id")});
    },

    _buildCustomSortPane: function () {
        BIShow.FloatBoxes.open("customSort", "sort", {}, this, {id: this.model.get("id")});
    },

    _buildCustomGroupPane: function () {
        BIShow.FloatBoxes.open("customGroup", "group", {}, this, {id: this.model.get("id")});
    },

    _buildFilterPane: function () {
        var self = this, id = this.model.get("id");
        BI.Popovers.remove(id);
        var popup = BI.createWidget({
            type: "bi.dimension_filter_popup",
            dId: this.model.get("id")
        });
        popup.on(BI.DimensionFilterPopup.EVENT_CHANGE, function (v) {
            self.model.set("filter_value", v);
        });
        BI.Popovers.create(id, popup).open(id);
        popup.populate();
    },



    local: function () {
        if (this.model.has("changeSort")) {
            this.model.get("changeSort");
            return true;
        }
        if (this.model.has("changeGroup")) {
            this.model.get("changeGroup");
            return true;
        }
        return false;
    },

    refresh: function () {
        this.usedCheck.setSelected(this.model.get("used"));
    }
});
