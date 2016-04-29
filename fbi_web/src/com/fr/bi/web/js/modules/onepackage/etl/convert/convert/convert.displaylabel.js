/**
 * 有两种颜色的label
 * @class BI.ConvertDisplayLabel
 * @extends BI.Widget
 */

BI.ConvertDisplayLabel = BI.inherit(BI.Widget, {

    constants: {
        newValuePos: 1,
        initialValuePos: 0
    },

    _defaultConfig: function() {
        return BI.extend(BI.ConvertDisplayLabel.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-convert-display-label",
            height: 25
        })
    },

    _init : function() {
        BI.ConvertDisplayLabel.superclass._init.apply(this, arguments);

        var o = this.options;

        this.initialText = BI.createWidget({
            type: "bi.label",
            cls: "display-label-initial",
            height: o.height,
            textHeight: o.height,
            textAlign: "left",
            rgap:5
        });

        this.newText = BI.createWidget({
            type: "bi.label",
            cls: "display-label-new",
            height: o.height,
            textHeight: o.height,
            textAlign: "left",
            lgap:5
        });

        BI.createWidget({
            type: "bi.horizontal",
            scrollx: false,
            element: this.element,
            items: [this.newText, this.initialText]
        });
    },

    _assertArray: function (array) {
        if(BI.isEmpty(array[this.constants.newValuePos])){
            array[this.constants.newValuePos] = array[0];
        }
        return array;
    },

    setValue: function(v){
        v.lanciValue = this._assertArray(v.lanciValue);
        v.columnValue = this._assertArray(v.columnValue);
        var initialText = v.lanciValue[this.constants.initialValuePos] + "-" + v.columnValue[this.constants.initialValuePos];
        var newText = v.lanciValue[this.constants.newValuePos] + "-" + v.columnValue[this.constants.newValuePos];
        if(initialText === newText){
            this.newText.setValue(initialText);
            this.initialText.setValue("");
            return;
        }
        this.newText.setValue(newText);
        this.initialText.setValue("("+ initialText +")");
    }
});

$.shortcut("bi.convert_display_label", BI.ConvertDisplayLabel);