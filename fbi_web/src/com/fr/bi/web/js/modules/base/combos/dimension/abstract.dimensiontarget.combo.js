/**
 * @class BI.AbstractDimensionTargetCombo
 * @extend BI.Widget
 * @abstract
 */
BI.AbstractDimensionTargetCombo = BI.inherit(BI.Widget, {

    _defaultConfig: function(){
        return BI.extend(BI.AbstractDimensionTargetCombo.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    _init: function(){
        BI.AbstractDimensionTargetCombo.superclass._init.apply(this, arguments);
        var self = this,o = this.options;

        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            stopPropagation: true,
            height: 25,
            iconCls: "detail-dimension-set-font"
        });

        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function(v){
            self.fireEvent(BI.AbstractDimensionTargetCombo.EVENT_CHANGE, v);
        });

        this.combo.on(BI.DownListCombo.EVENT_SON_VALUE_CHANGE,function(v, father){
            self.fireEvent(BI.AbstractDimensionTargetCombo.EVENT_CHANGE, father, v);
        });

        this.combo.on(BI.DownListCombo.EVENT_BEFORE_POPUPVIEW,function(){
            this.populate(self._rebuildItems());
            this.setValue(self._createValue());
        });
    },

    _rebuildItems: function () {
        return [];
    },

    _createValue: function(){
        return [];
    },

    setValue:function(v){
        this.combo.setValue(v);
    },

    getValue: function(){
        return this.combo.getValue();
    }
});
BI.AbstractDimensionTargetCombo.EVENT_CHANGE = "EVENT_CHANGE";