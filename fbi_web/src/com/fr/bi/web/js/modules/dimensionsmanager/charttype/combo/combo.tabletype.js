/**
 * @class BI.TableTypeCombo
 * @extend BI.AbstractTypeCombo
 *
 */
BI.TableTypeCombo = BI.inherit(BI.AbstractTypeCombo, {
    _defaultConfig: function () {
        return BI.extend(BI.TableTypeCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-table-type-combo",
            invalid: false,
            items: []
        })
    },

    _init: function () {
        BI.TableTypeCombo.superclass._init.apply(this, arguments);
    },

    _switchIcon: function(v){
        switch (v) {
            case BICst.Widget.TABLE:
                break;
            case BICst.Widget.CROSS_TABLE:
                break;
            case BICst.Widget.COMPLEX_TABLE:
                break;
            default:
                this.trigger.setIcon("detail-chart-summary-table-font");
                break;
        }
    }
});
$.shortcut("bi.table_type_combo", BI.TableTypeCombo);
