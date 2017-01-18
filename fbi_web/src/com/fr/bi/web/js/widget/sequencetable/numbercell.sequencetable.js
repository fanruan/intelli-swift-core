/**
 *
 * Created by GUY on 2016/5/26.
 * @class BI.SequenceTableNumberCell
 * @extends BI.Single
 */
BI.SequenceTableNumberCell = BI.inherit(BI.Single, {

    _defaultConfig: function () {
        return BI.extend(BI.SequenceTableNumberCell.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-sequence-table-number-cell",
            text: ""
        });
    },

    _init: function () {
        BI.SequenceTableNumberCell.superclass._init.apply(this, arguments);
        var o = this.options;
        this.text = BI.createWidget({
            type: "bi.label",
            element: this.element,
            textAlign: "left",
            forceCenter: true,
            hgap: 5,
            text: o.text
        });
    },

    setText: function (text) {
        this.text.setText(text);
    }
});
$.shortcut('bi.sequence_table_number_cell', BI.SequenceTableNumberCell);