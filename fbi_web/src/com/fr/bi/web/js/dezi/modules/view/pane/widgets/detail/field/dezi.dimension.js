/**
 * Created by GUY on 2015/7/3.
 */
BIDezi.DimensionView = BI.inherit(BI.View, {

    constants: {
        DIMENSION_BUTTON_HEIGHT: 25,
        COMBO_WIDTH: 25,
        CONTAINER_HEIGHT: 25,
        ICON_BUTTON_WIDTH: 12,
        ICON_BUTTON_POS: 2
    },

    _defaultConfig: function () {
        return BI.extend(BIDezi.DimensionView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension"
        })
    },

    _init: function () {
        BIDezi.DimensionView.superclass._init.apply(this, arguments);
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
        this.usedCheck.on(BI.Checkbox.EVENT_CHANGE, function () {
            self.model.set("used", self.usedCheck.isSelected());
        });
        this.usedRadio = BI.createWidget({
            type: "bi.radio"
        });
        this.usedRadio.on(BI.Radio.EVENT_CHANGE, function () {
            self.model.set("used", self.usedRadio.isSelected());
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
            default :
                this._createStringCombo();
        }

        this.htape = BI.createWidget({
            type: "bi.htape",
            height: this.constants.CONTAINER_HEIGHT,
            items: [{
                el: {
                    type: "bi.center_adapt",
                    items: [this.usedCheck, this.usedRadio]
                },
                width: this.constants.COMBO_WIDTH
            }, this.editor, {el: this.iconButton, width: 0},
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

    _refreshCheckType: function(){
        var wType = BI.Utils.getWidgetTypeByID(BI.Utils.getWidgetIDByDimensionID(this.model.get("id")));
        if (wType === BICst.WIDGET.TABLE ||
            wType === BICst.WIDGET.CROSS_TABLE ||
            wType === BICst.WIDGET.COMPLEX_TABLE ||
            wType === BICst.WIDGET.MAP) {
            this.usedCheck.setVisible(true);
            this.usedRadio.setVisible(false);
            return;
        }
        this.usedCheck.setVisible(false);
        this.usedRadio.setVisible(true);
    },

    _checkUsedEnable: function () {
        var isUsed = this.model.get("used");
        var wId = BI.Utils.getWidgetIDByDimensionID(this.model.get("id"));
        this.usedCheck.setEnable(true);
        this.usedCheck.setSelected(isUsed);
        this.usedRadio.setEnable(true);
        this.usedRadio.setSelected(isUsed);
        var wType = BI.Utils.getWidgetTypeByID(wId);
        if ((wType !== BICst.WIDGET.TABLE &&
            wType !== BICst.WIDGET.CROSS_TABLE &&
            wType !== BICst.WIDGET.COMPLEX_TABLE &&
            wType !== BICst.WIDGET.GIS_MAP)
            && BI.Utils.getRegionTypeByDimensionID(this.model.get("id")) === BICst.REGION.DIMENSION2
            && BI.Utils.getAllUsableTargetDimensionIDs(wId).length > 1) {
            this.usedCheck.setEnable(false);
            this.usedRadio.setEnable(false);
        }
        if ((wType === BICst.WIDGET.DASHBOARD || wType === BICst.WIDGET.PIE)
            && BI.Utils.getRegionTypeByDimensionID(this.model.get("id")) === BICst.REGION.DIMENSION1
            && BI.Utils.getAllUsableTargetDimensionIDs(wId).length > 1) {
            this.usedCheck.setEnable(false);
            this.usedRadio.setEnable(false);
        }
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
            type: "bi.dimension_string_combo",
            dId: self.model.get("id")
        });
        this.combo.on(BI.AbstractDimensionTargetCombo.EVENT_CHANGE, function (v, s) {
            switch (v) {
                case BICst.DIMENSION_STRING_COMBO.ADDRESS:
                    self.model.set("position", {type: BICst.GIS_POSITION_TYPE.ADDRESS});
                    break;
                case BICst.DIMENSION_STRING_COMBO.LNG_LAT:
                    self.model.set("position", {type: s});
                    break;
                case BICst.DIMENSION_STRING_COMBO.ASCEND:
                    BIDezi.FloatBoxes.remove("customSort", self);
                    self.model.set("changeSort", {type: BICst.SORT.ASC, sort_target: s});
                    break;
                case BICst.DIMENSION_STRING_COMBO.DESCEND:
                    BIDezi.FloatBoxes.remove("customSort", self);
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
                case BICst.DIMENSION_STRING_COMBO.FILTER:
                    self._buildFilterPane();
                    break;
                case BICst.DIMENSION_STRING_COMBO.DT_RELATION:
                    self._buildMatchingRelationShipPane();
                    break;
                case BICst.DIMENSION_STRING_COMBO.COPY:
                    self._copyDimension();
                    break;
                case BICst.DIMENSION_STRING_COMBO.DELETE:
                    self._deleteDimension();
                    break;
                case BICst.DIMENSION_STRING_COMBO.INFO:
                    break;
            }
        })
    },

    _createNumberCombo: function () {
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.dimension_number_combo",
            dId: self.model.get("id")
        });
        this.combo.on(BI.AbstractDimensionTargetCombo.EVENT_CHANGE, function (v, s) {
            switch (v) {
                case BICst.DIMENSION_NUMBER_COMBO.ADDRESS:
                    self.model.set("position", {type: BICst.GIS_POSITION_TYPE.ADDRESS});
                    break;
                case BICst.DIMENSION_NUMBER_COMBO.LNG_LAT:
                    self.model.set("position", {type: s});
                    break;
                case BICst.DIMENSION_NUMBER_COMBO.ASCEND:
                    self.model.set("sort", {type: BICst.SORT.ASC, sort_target: s});
                    break;
                case BICst.DIMENSION_NUMBER_COMBO.DESCEND:
                    self.model.set("sort", {type: BICst.SORT.DESC, sort_target: s});
                    break;
                case BICst.DIMENSION_NUMBER_COMBO.SORT_BY_CUSTOM:
                    self._buildCustomSortPane();
                    break;
                case BICst.DIMENSION_NUMBER_COMBO.GROUP_BY_VALUE:
                    self.model.set("changeGroup", {type: BICst.GROUP.ID_GROUP});
                    break;
                case BICst.DIMENSION_NUMBER_COMBO.CORDON:
                    self._buildCordonPane();
                    break;
                case BICst.DIMENSION_NUMBER_COMBO.GROUP_SETTING:
                    self._setGroups();
                    break;
                case BICst.DIMENSION_NUMBER_COMBO.FILTER:
                    self._buildFilterPane();
                    break;
                case BICst.DIMENSION_NUMBER_COMBO.DT_RELATION:
                    self._buildMatchingRelationShipPane();
                    break;
                case BICst.DIMENSION_NUMBER_COMBO.COPY:
                    self._copyDimension();
                    break;
                case BICst.DIMENSION_NUMBER_COMBO.DELETE:
                    self._deleteDimension();
                    break;
                case BICst.DIMENSION_NUMBER_COMBO.INFO:
                    break;
            }
        });
    },

    _createDateCombo: function () {
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.dimension_date_combo",
            dId: self.model.get("id")
        });
        this.combo.on(BI.AbstractDimensionTargetCombo.EVENT_CHANGE, function (v, s) {
            switch (v) {
                case BICst.DIMENSION_DATE_COMBO.ADDRESS:
                    self.model.set("position", {type: BICst.GIS_POSITION_TYPE.ADDRESS});
                    break;
                case BICst.DIMENSION_DATE_COMBO.LNG_LAT:
                    self.model.set("position", {type: s});
                    break;
                case BICst.DIMENSION_DATE_COMBO.DATE:
                    self.model.set("group", {type: BICst.GROUP.YMD});
                    break;
                case BICst.DIMENSION_DATE_COMBO.YEAR:
                    self.model.set("group", {type: BICst.GROUP.Y});
                    break;
                case BICst.DIMENSION_DATE_COMBO.QUARTER:
                    self.model.set("group", {type: BICst.GROUP.S});
                    break;
                case BICst.DIMENSION_DATE_COMBO.MONTH:
                    self.model.set("group", {type: BICst.GROUP.M});
                    break;
                case BICst.DIMENSION_DATE_COMBO.WEEK:
                    self.model.set("group", {type: BICst.GROUP.W});
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
                case BICst.DIMENSION_DATE_COMBO.DT_RELATION:
                    self._buildMatchingRelationShipPane();
                    break;
                case BICst.DIMENSION_DATE_COMBO.COPY:
                    self._copyDimension();
                    break;
                case BICst.DIMENSION_DATE_COMBO.DELETE:
                    self._deleteDimension();
                    break;
                case BICst.DIMENSION_DATE_COMBO.INFO:
                    break;
            }
        })
    },

    _setGroups: function () {
        BIDezi.FloatBoxes.open("numberCustomGroup", "group", {}, this, {id: this.model.get("id")});
    },

    _buildCustomSortPane: function () {
        BIDezi.FloatBoxes.open("customSort", "sort", {}, this, {id: this.model.get("id")});
    },

    _buildCustomGroupPane: function () {
        BIDezi.FloatBoxes.open("customGroup", "group", {}, this, {id: this.model.get("id")});
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

    _buildMatchingRelationShipPane: function () {
        var self = this, id = this.model.get("id");
        BI.Popovers.remove(id);
        var popup = BI.createWidget({
            type: "bi.matching_relation_popup",
            dId: this.model.get("id")
        });
        popup.on(BI.MatchingRelationPopup.EVENT_CHANGE, function (v) {
            self.model.set("dimension_map", v);
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

    _copyDimension: function () {
        this.model.copy();
    },

    _deleteDimension: function () {
        this.model.destroy();
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
        this._checkUsedEnable();
        this.editor.setValue(this.model.get("name"));
        this.editor.setState(this.model.get("name"));
        var filterIconWidth = BI.isEmpty(this.model.get("filter_value")) ? 0 : this.constants.ICON_BUTTON_WIDTH;
        var items = this.htape.attr("items");
        items[this.constants.ICON_BUTTON_POS].width = filterIconWidth;
        this.htape.attr("items", items);
        this.htape.resize();
        this._refreshCheckType();
    }
});