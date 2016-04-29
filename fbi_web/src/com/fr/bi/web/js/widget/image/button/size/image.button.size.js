/**
 * 选择图片大小
 * Created by GameJian on 2016/1/28.
 */
/**
 * 单选框
 * Created by GameJian on 2016/1/28.
 */
BI.ImageButtonSize = BI.inherit(BI.Widget, {

    _defaultConfig: function(){
        return BI.extend(BI.ImageButtonSize.superclass._defaultConfig.apply(this, arguments),{
            baseCls: "bi-image-button-size",
            width: 230,
            height: 30
        })
    },

    _init:function() {
        BI.ImageButtonSize.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.sizeChooser = BI.createWidget({
            type: "bi.button_group",
            scrollable: false,
            items: BI.createItems([{
                text: BI.i18nText("BI-Original_Size"),
                cls: "image-button-size-button-group",
                width: 55,
                selected: true,
                value: BI.ImageButtonSize.ORIGINAL
            },{
                text: BI.i18nText("BI-Equal_Size_Adapt"),
                cls: "image-button-size-button-group",
                width: 67,
                value: BI.ImageButtonSize.EQUAL
            },{
                text: BI.i18nText("BI-Widget_Size_Adapt"),
                cls: "image-button-size-button-group",
                width: 67,
                value: BI.ImageButtonSize.WIDGET_SIZE
            }],{
                type: "bi.image_button_size_radio"
            }),
            layouts: [{
                type: "bi.left",
                hgap: 5
            }]
        });

        this.sizeChooser.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

        this.sizeChooser.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.fireEvent(BI.ImageButtonSize.EVENT_CHANGE, arguments);
        });
        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [this.sizeChooser],
            hgap: 5
        });
        this.getValue()
    },

    getValue: function () {
        return this.sizeChooser.getValue()[0]
    },

    setValue: function (v) {
        this.sizeChooser.setValue(v)
    }
});

BI.extend(BI.ImageButtonSize, {
    ORIGINAL: "original",
    EQUAL: "equal",
    WIDGET_SIZE: "widget_size"
});
BI.ImageButtonSize.EVENT_CHANGE = "BI.ImageButtonSize.EVENT_CHANGE";
$.shortcut("bi.image_button_size" , BI.ImageButtonSize);