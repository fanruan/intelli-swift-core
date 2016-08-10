/**
 *
 * Created by GUY on 2016/8/10.
 * @class BI.SequenceTableSummayNumber
 * @extends BI.SequenceTableTreeNumber
 */
BI.SequenceTableSummayNumber = BI.inherit(BI.SequenceTableTreeNumber, {

    _defaultConfig: function () {
        return BI.extend(BI.SequenceTableSummayNumber.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-sequence-table-summay-number",
        });
    },

    _init: function () {
        BI.SequenceTableSummayNumber.superclass._init.apply(this, arguments);
    },

    _formatNumber: function (nodes) {
        var self = this, o = this.options;
        var result = [];
        var count = this._getStartSequence(nodes);

        function getLeafCount(node) {
            var cnt = 0;
            if (BI.isNotEmptyArray(node.children)) {
                BI.each(node.children, function (index, child) {
                    cnt += getLeafCount(child);
                });
                if (BI.isNotEmptyArray(node.values)) {
                    cnt++;
                }
            } else {
                cnt++;
            }
            return cnt;
        }

        var start = 0;
        BI.each(nodes, function (i, node) {
            if (BI.isArray(node.children)) {
                BI.each(node.children, function (index, child) {
                    var cnt = getLeafCount(child);
                    result.push({
                        text: count++,
                        start: start,
                        cnt: cnt,
                        cls: "sequence-table-number",
                        height: cnt * o.rowSize + (cnt - 1)
                    });
                    start += cnt;
                });
                if (BI.isNotEmptyArray(node.values)) {
                    result.push({
                        text: BI.i18nText("BI-Summary_Values"),
                        start: start++,
                        cnt: 1,
                        cls: "sequence-table-number sequence-table-summary",
                        height: o.rowSize
                    });
                }
            }
        });
        return result;
    }
});
$.shortcut('bi.sequence_table_summary_number', BI.SequenceTableSummayNumber);