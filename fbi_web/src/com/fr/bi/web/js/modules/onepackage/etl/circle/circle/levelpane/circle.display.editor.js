/**
 * 可以编辑文本的多选列表项
 * @class BI.CircleDisplayEditor
 * @extends BI.BasicButton
 */

BI.CircleDisplayEditor = BI.inherit(BI.Widget, {

    constants:{
        labelHeight: 30,
        gap: 10
    },

    _defaultConfig: function() {
        return BI.extend(BI.CircleDisplayEditor.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-circle-display-editor",
            validationChecker: BI.emptyFn,
            onFocus: BI.emptyFn
        })
    },

    _init : function() {
        BI.CircleDisplayEditor.superclass._init.apply(this, arguments);

        var o = this.options, self = this;

        this.editor = BI.createWidget({
            type: "bi.sign_editor",
            cls: "level-display-editor",
            validationChecker: function(v){
                return o.validationChecker(v, self.attr("id"));
            },
            //errorText: BI.i18nText("BI-Have_Repeated_Field_Name"),
            value: o.value,
            allowBlank: false,
            height: this.constants.labelHeight
        });

        this.display = BI.createWidget({
            type: "bi.button_tree",
            layouts: [{
                type: "bi.left",
                hgap: 5
            }]
        });

        this.editor.on(BI.SignEditor.EVENT_ERROR, function(v){
            if(v === ""){
                this.setErrorText(BI.i18nText("BI-Field_Name_Cannot_Be_Null"));
            }else{
                this.setErrorText(BI.i18nText("BI-Have_Repeated_Field_Name"));
            }
        });

        this.editor.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        this.editor.on(BI.SignEditor.EVENT_FOCUS, function(){
            o.onFocus.apply(self, arguments);
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            hgap: this.constants.gap,
            items: [this.editor, this.display]
        });

        this.populate(o.items);
    },

    isValid: function(){
        return this.editor.isValid();
    },

    populate: function(items){
        this.display.populate(BI.createItems(items, {
            type: "bi.label",
            cls: "level-display-label",
            textHeight: this.constants.labelHeight,
            height: this.constants.labelHeight
        }));
    },

    setValue: function(v){
        this.options.length = v.length || 0;
        this.options.circle = true;
        this.editor.setValue(v.name);
    },

    getValue: function(){
        return {
            name : this.editor.getValue(),
            length: this.options.length,
            circle: this.options.circle
        };
    }
});

BI.CircleDisplayEditor.EVENT_VALID = "EVENT_VALID";
BI.CircleDisplayEditor.EVENT_ERROR = "EVENT_ERROR";
BI.CircleDisplayEditor.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.circle_display_editor", BI.CircleDisplayEditor);