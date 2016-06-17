/**
 * @class BI.CordonItem
 * @extend BI.Widget
 */

BI.CordonItem = BI.inherit(BI.Single,{

    constants:{
        editorWidth:155,
        rgap:10,
        iconSize:30,
        left:20,
        vgap: 10,
        hgap: 10,
        itemHeight: 40,
        initialMax: 100,
        WARNING_COLOR: "#e85050"
    },

    _defaultConfig:function(){
        return BI.extend(BI.CordonItem.superclass._defaultConfig.apply(this,arguments),{
            extraCls:"bi-cordon-item",
            cordon_name:"",
            cordon_value: "",
            cordon_number_level: BICst.TARGET_STYLE.NUM_LEVEL.NORMAL,
            cordon_color: this.constants.WARNING_COLOR
        });
    },

    _init:function(){
        BI.CordonItem.superclass._init.apply(this,arguments);

        var self = this,o = this.options;

        this.editor = BI.createWidget({
            type: "bi.sign_editor",
            cls: "condition-operator-input",
            value: o.cordon_name,
            height: 30,
            hgap: this.constants.hgap
        });

        var text = "";
        switch (o.cordon_number_level) {
            case BICst.TARGET_STYLE.NUM_LEVEL.TEN_THOUSAND:
                text = BI.i18nText("BI-Wan");
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.MILLION:
                text = BI.i18nText("BI-Million");
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.YI:
                text = BI.i18nText("BI-Yi");
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.PERCENT:
                text = "%";
                break;
            case BICst.TARGET_STYLE.NUM_LEVEL.NORMAL:
            default:
                break;
        }

        this.numberLevelLabel = BI.createWidget({
            type: "bi.label",
            text: text,
            height: 30
        });

        this.valueEditor = BI.createWidget({
            type: "bi.sign_editor",
            cls: "condition-operator-input",
            allowBlank: true,
            validationChecker: function(v){
                return BI.isNumeric(v);
            },
            value: o.cordon_value,
            errorText: BI.i18nText("BI-Numerical_Interval_Input_Data"),
            height: 30,
            width: 230
        });

        this.colorSelect = BI.createWidget({
            type: "bi.color_chooser",
            height: 30,
            width: 30
        });

        this.colorSelect.setValue(o.cordon_color);

        this.deleteIcon = BI.createWidget({
            type:"bi.icon_button",
            cls:"close-red-font group-item-icon"
        });

        this.deleteIcon.on(BI.IconButton.EVENT_CHANGE,function(){
            self.destroy();
            self.fireEvent(BI.CordonItem.EVENT_DESTROY);
        });

        this.deleteIcon.setVisible(false);


        BI.createWidget({
            type:"bi.htape",
            element:this.element,
            hgap: 5,
            vgap: 5,
            items: [{
                el: this.editor,
                width: this.constants.editorWidth
            }, {
                el: this.valueEditor
            }, {
                el: this.numberLevelLabel,
                width: BI.DOM.getTextSizeWidth(this.numberLevelLabel.getText())
            }, {
                el:{
                    type:"bi.center_adapt",
                    items:[this.colorSelect]
                },
                width:this.constants.iconSize
            }, {
                el:{
                    type:"bi.center_adapt",
                    items:[this.deleteIcon]
                },
                width:this.constants.iconSize
            }]
        });

        this.element.hover(function(){
            self.deleteIcon.setVisible(true);
        },function(){
            self.deleteIcon.setVisible(false);
        });
    },

    getValue:function(){
        return {
            cordon_name: this.editor.getValue(),
            cordon_value: this.valueEditor.getValue(),
            cordon_color: this.colorSelect.getValue()
        };
    },

    setValue:function(ob){
        this.editor.setValue(ob.cordon_name);
        this.valueEditor.setValue(ob.cordon_value);
        this.numberLevelLabel.setValue(ob.cordon_number_level);
        this.colorSelect.setValue(ob.cordon_color);
    },

    destroy:function(){
        BI.CordonItem.superclass.destroy.apply(this,arguments);
    }
});

$.shortcut("bi.cordon_item",BI.CordonItem);