BIShow.DateDimensionView = BI.inherit(BI.View, {

    constants: {
        DIMENSION_BUTTON_HEIGHT: 25,
        COMBO_WIDTH: 25,
        LABEL_GAP: 5
    },

    _defaultConfig: function () {
        return BI.extend(BIShow.DateDimensionView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimension"
        })
    },

    events: {},

    _init: function () {
        BIShow.DateDimensionView.superclass._init.apply(this, arguments);
    },

    _vessel: function () {
        return this.element;
    },

    change: function (changed) {

    },

    _render: function (vessel) {
        var self = this;
        this.label = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            lgap: this.constants.LABEL_GAP,
            height: this.constants.DIMENSION_BUTTON_HEIGHT,
            cls: "bi-dimension-name"
        });

        this._createCombo();

        BI.createWidget({
            type: "bi.border",
            element: vessel,
            items: {
                east: {el: this.combo, width: this.constants.COMBO_WIDTH},
                center: {el: this.label}
            }
        });

        var tableId = BI.Utils.getTableIdByFieldID(this.model.get("_src").field_id);

        this.label.setValue(BI.Utils.getTableNameByID(tableId) + "." + this.model.get("name"));
    },

    _createCombo: function () {
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.control_dimension_combo",
            dId: this.model.get("id")
        });
        this.combo.on(BI.ControlDimensionCombo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.CONTROL_COMBO.DELETE:
                    self._deleteDimension();
                    break;
            }
        });
    },

    _deleteDimension: function () {
        this.model.destroy();
    }
});
