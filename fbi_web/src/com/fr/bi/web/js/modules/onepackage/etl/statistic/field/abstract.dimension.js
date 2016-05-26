/**
 * @class BI.AbstractDimension
 * @extends BI.Widget
 * @author windy
 */
BI.AbstractDimension = BI.inherit(BI.Widget, {

    constants: {
        DIMENSION_BUTTON_HEIGHT: 25,
        COMBO_WIDTH: 25,
        LABEL_GAP : 5
    },

    _defaultConfig: function () {
        return BI.extend(BI.AbstractDimension.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-etl-dimension",
            info: {},
            table: {}
        })
    },

    _init: function () {
        BI.AbstractDimension.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.usedCheck = BI.createWidget({
            type: "bi.checkbox"
        });
        this.usedCheck.setSelected(o.model.getDimensionUsedById(o.dId));
        this.usedCheck.on(BI.Checkbox.EVENT_CHANGE, function () {
            o.model.setDimensionUsedById(o.dId, self.usedCheck.isSelected());
        });

        this.nameEditor = BI.createWidget({
            type: "bi.sign_style_editor",
            cls: "etl-dimension-name",
            height:25,
            validationChecker:function(){
                return self._checkDimensionName(self.nameEditor.getValue());
            }
        });

        this.nameEditor.on(BI.SignEditor.EVENT_CONFIRM, function(){
            o.model.setDimensionNameById(o.dId, self.nameEditor.getValue());
        });

        this.combo = this._createCombo();

        BI.createWidget({
            type:"bi.default",
            element: this.element,
            tagName:"li",
            height:25,
            items:[{
                type:"bi.htape",
                height:25,
                items:[{
                    el: {
                        type: "bi.center_adapt",
                        items: [this.usedCheck]
                    },
                    width: this.constants.COMBO_WIDTH
                }, {
                    el:this.nameEditor
                },{
                    el:this.combo,
                    width: this.constants.COMBO_WIDTH
                }]
            }]
        });
        this.populate(o.info);
    },

    _createCombo: function () {
        return BI.createWidget();
    },

    _checkDimensionName: function(name){
        var o = this.options;
        var currId = o.dId;
        var dims = o.model.getDimension();
        var valid = true;
        BI.any(dims, function(idx, dim){
            if(currId !== idx && dim.name === name){
                valid = false;
                return true;
            }
        });
        return valid;
    },

    checkStatus: function(){
        var o = this.options;
        this.nameEditor.setState(o.model.getTextByType(o.dId, o.groupOrSummary, o.fieldType));
        this.usedCheck.setSelected(o.model.getDimensionUsedById(o.dId));
    },

    populate: function(){
        var o = this.options;
        this.nameEditor.setValue(o.model.getDimensionNameById(o.dId));
        this.checkStatus();
    }
});

BI.AbstractDimension.EVENT_DESTROY = "EVENT_DESTROY";