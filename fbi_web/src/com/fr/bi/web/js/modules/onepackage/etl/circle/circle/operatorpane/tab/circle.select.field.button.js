/**
 * 底部带居中三角的textbutton
 * @class BI.CircleSelectFieldButton
 * @extends BI.BasicButton
 */

BI.CircleSelectFieldButton = BI.inherit(BI.Widget, {

    constants: {
        normal_color: "#d8f2fd",
        triangleWidth: 16,
        triangleHeight: 10
    },

    _defaultConfig: function() {
        return BI.extend(BI.CircleSelectFieldButton.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-circle-select-field-button",
            height: 25
        })
    },

    _init : function() {
        BI.CircleSelectFieldButton.superclass._init.apply(this, arguments);

        var o = this.options, self = this;

        this.button = BI.createWidget({
            type: "bi.text_button",
            value: o.value,
            cls: "select-field-button"
        });

        this.button.on(BI.TextButton.EVENT_CHANGE, function(){
            self.setSelected(true);
            self.fireEvent(BI.CircleSelectFieldButton.EVENT_CHANGE);
        });

        this.button.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        this.triangle = BI.createWidget({
            type: "bi.svg",
            cls: "file-manager-nav-button-triangle",
            width: this.constants.triangleWidth,
            height: this.constants.triangleHeight
        });

        this.triangle.path("M8,0L0,10L16,10").attr({
            "stroke": this.constants.normal_color,
            "fill": this.constants.normal_color
        });

        BI.createWidget({
            type: "bi.center_adapt",
            element: this.element,
            items: [this.button]
        });

        BI.createWidget({
            type: "bi.absolute",
            cls: "cursor-pointer",
            element: this.element,
            items: [{
                el:{
                    type: "bi.horizontal_auto",
                    items:[this.triangle]
                },
                left: 0,
                right: 0,
                bottom: -10
            }]
        })
    },

    setValue: function(v){
        v || (v = BI.i18nText("BI-Please_Select_Field"));
        this.button.setValue(v);
    },

    getValue: function(){
        var value = this.button.getValue();
        return BI.isEqual(value, BI.i18nText("BI-Please_Select_Field")) ? "" : value;
    },

    setSelected: function(b){
        this.triangle.setVisible(!!b);
    },

    isSelected: function(){
        return this.triangle.isVisible();
    }
});

BI.CircleSelectFieldButton.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.circle_select_field_button", BI.CircleSelectFieldButton);