/**
 * Created by GUY on 2015/9/6.
 * @class BI.DetailSelectDataLevel0Item
 * @extends BI.Single
 */
BI.DetailSelectDataNoRelationMatchSearchItem = BI.inherit(BI.Single, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailSelectDataNoRelationMatchSearchItem.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-detail-select-data-no-relation-match-search-item bi-select-data-level0-item",
            height: 25,
            layer: 1,
            hgap: 0,
            fieldType: BICst.COLUMN.STRING,
            lgap: 0,
            rgap: 35
        })
    },

    _getFieldClass: function (type) {
        switch (type) {
            case BICst.COLUMN.STRING:
                return "select-data-field-string-font";
            case BICst.COLUMN.NUMBER:
                return "select-data-field-number-font";
            case BICst.COLUMN.DATE:
                return "select-data-field-date-font";
            case BICst.COLUMN.COUNTER:
                return "select-data-field-number-font";
            default:
                return "select-data-field-number-font";
        }
    },

    _init: function () {
        BI.DetailSelectDataNoRelationMatchSearchItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.button = BI.createWidget({
            cls: "select-data-level0-item-button " + this._getFieldClass(o.fieldType),
            type: "bi.blank_icon_text_item",
            blankWidth: o.layer * 20,
            element: this.element,
            text: o.text,
            value: o.value,
            height: 25,
            textLgap: 10,
            textRgap: 5,
            disabled: true
        });
    },

    isSelected: function () {
        return false;
    },

    setSelected: function (b) {

    },

    setTopLineVisible: function () {

    },

    setTopLineInVisible: function () {

    },

    setBottomLineVisible: function () {

    },

    setBottomLineInVisible: function () {

    },

    doRedMark: function () {
        this.button.doRedMark.apply(this.button, arguments);
    },

    unRedMark: function () {
        this.button.unRedMark.apply(this.button, arguments);
    },

    doHighLight: function () {
        this.button.doHighLight.apply(this.button, arguments);
    },

    unHighLight: function () {
        this.button.unHighLight.apply(this.button, arguments);
    }
});

$.shortcut("bi.detail_select_data_no_relation_match_search_item", BI.DetailSelectDataNoRelationMatchSearchItem);