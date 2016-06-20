/**
 * Created by GUY on 2016/5/16.
 *
 * @class BI.DetailTablePopupDetailDimensionView
 * @extend BI.View
 */
BI.DetailTablePopupDetailDimensionView = BI.inherit(BI.View, {

    constants: {
        DIMENSION_BUTTON_HEIGHT: 25,
        COMBO_WIDTH: 60,
        CHECKBOX_WIDTH: 25,
        CONTAINER_HEIGHT: 25,
        ICON_BUTTON_WIDTH: 12,
        ICON_BUTTON_POS: 2
    },

    _defaultConfig: function () {
        return BI.extend(BI.DetailTablePopupDetailDimensionView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-table-popup-detail-dimension-view"
        })
    },

    _init: function () {
        BI.DetailTablePopupDetailDimensionView.superclass._init.apply(this, arguments);
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

    _checkDimensionName: function (name) {
        var currentId = this.model.get("id");
        var widget = Data.SharingPool.cat(BI.DetailTablePopup.SHARING_POOL_KEY);
        var dimensions = widget.dimensions;
        return !BI.some(dimensions, function (id, dimension) {
            if (id === currentId) {
                return;
            }
            return dimension.name === name;
        })
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
            value: this.model.get("name"),
            validationChecker: function (v) {
                return self._checkDimensionName(v);
            }
        });
        this.editor.on(BI.SignEditor.EVENT_CONFIRM, function () {
            self.model.set("name", self.editor.getValue());
        });

        this.combo = this._createCombo();
        this.htape = BI.createWidget({
            type: "bi.htape",
            height: this.constants.CONTAINER_HEIGHT,
            items: [{
                el: {
                    type: "bi.center_adapt",
                    items: [this.usedCheck]
                },
                width: this.constants.CHECKBOX_WIDTH
            }, this.editor,
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

    _createCombo: function () {
        var self = this;
        var combo = BI.createWidget({
            type: "bi.down_list_combo",
            height: 25,
            iconCls: "detail-dimension-set-font",
            items: [
                [{
                    text: BI.i18nText("BI-Remove"),
                    value: BICst.DETAIL_STRING_COMBO.DELETE
                }],
                [{
                    text: BI.i18nText("BI-This_Target_From") + ":" + this.model.get("name"),
                    title: BI.i18nText("BI-This_Target_From") + ":" + this.model.get("name"),
                    tipType: "warning",
                    value: BICst.DETAIL_STRING_COMBO.INFO,
                    disabled: true
                }]
            ]
        });
        combo.on(BI.DownListCombo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.DETAIL_STRING_COMBO.DELETE:
                    self._deleteDimension();
                    break;
            }
        });
        return combo;
    },

    _deleteDimension: function () {
        this.model.destroy();
    },

    local: function () {

    },

    refresh: function () {
        this.usedCheck.setSelected(this.model.get("used"));
        this.editor.setValue(this.model.get("name"));
        this.editor.setState(this.model.get("name"));
    }
});