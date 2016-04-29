/**
 * Created by 小灰灰 on 2016/3/10.
 */
BI.ETLFilterFormulaPopup = BI.inherit(BI.BarPopoverSection, {
    _constants:{
        WIDTH : 580,
        HEIGHT : 370,
        NORTH_HEIGHT : 40
    },

    _init: function () {
        BI.ETLFilterFormulaPopup.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        self.formula = BI.createWidget({
            type : 'bi.formula_insert',
            height : self._constants.HEIGHT,
            width : self._constants.WIDTH,
            fieldItems : o.fieldItems
        });
        self.formula.on(BI.FormulaInsert.EVENT_CHANGE, function () {
            self.sure.setEnable(self.formula.checkValidation());
        })
    },

    rebuildNorth: function (north) {
        var self = this, o = this.options;
        BI.createWidget({
            type: "bi.label",
            element: north,
            text:  BI.i18nText('BI-Formula'),
            textAlign: "left",
            height: self._constants.NORTH_HEIGHT
        });
        return true
    },

    rebuildCenter: function (center) {
        var self = this;
        BI.createWidget({
            type : 'bi.absolute',
            element : center,
            items :[{
                el : self.formula
            }]
        })
    },

    end: function(){
        this.fireEvent(BI.ETLFilterFormulaPopup.EVENT_CHANGE);
    },

    getValue: function () {
        return this.formula.getValue();
    },

    setValue: function (v) {
        this.formula.setValue(v);
    }

});
BI.ETLFilterFormulaPopup.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.etl_filter_formula_popup", BI.ETLFilterFormulaPopup);