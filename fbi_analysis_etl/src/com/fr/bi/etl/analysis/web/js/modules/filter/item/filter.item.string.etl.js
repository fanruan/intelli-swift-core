BI.StringETLFilterItem = BI.inherit(BI.AbstractETLFilterItem, {
    _defaultConfig: function () {
        return BI.extend(BI.StringETLFilterItem.superclass._defaultConfig.apply(this, arguments), {
            filterTypes : BICst.ETL_FILTER_STRING_COMBO,
            defaultType : BICst.TARGET_FILTER_STRING.BELONG_VALUE
        })
    },

    _init: function () {
        BI.StringETLFilterItem.superclass._init.apply(this, arguments);
    },

    _refreshFilterWidget: function (value) {
        var item;
        switch (this.filter_type) {
            case BICst.TARGET_FILTER_STRING.BELONG_VALUE:
            case BICst.TARGET_FILTER_STRING.NOT_BELONG_VALUE:
                item = this._createMultiChooserPane();
                break;
            case BICst.TARGET_FILTER_STRING.CONTAIN:
            case BICst.TARGET_FILTER_STRING.NOT_CONTAIN:
            case BICst.TARGET_FILTER_STRING.BEGIN_WITH:
            case BICst.TARGET_FILTER_STRING.NOT_BEGIN_WITH:
            case BICst.TARGET_FILTER_STRING.END_WITH:
            case BICst.TARGET_FILTER_STRING.NOT_END_WITH:
                item = this._createInput();
                break;
            case BICst.FILTER_TYPE.FORMULA:
                item = this._createFormular();
                break;
        }
        BI.isNotNull(value) && this.filterWidget.setValue(value);
        if (BI.isFunction(item.populate)){
            item.populate.apply(item, arguments);
        }
        this.filterValueContainer.empty();
        this.filterValueContainer.addItem(item);
    },

    _createInput: function () {
        var self = this;
        this.filterWidget = BI.createWidget({
            type: "bi.text_editor",
            allowBlank: true,
            height: this._constant.LINE_SIZE,
            width: this._constant.INPUT_WIDTH
        });
        this.filterWidget.on(BI.TextEditor.EVENT_CONFIRM, function () {
            self.fireEvent(BI.AbstractETLFilterItem.EVENT_VALUE_CHANGED);
        })
        return this.filterWidget;
    }
});
$.shortcut('bi.string_filter_item_etl', BI.StringETLFilterItem)