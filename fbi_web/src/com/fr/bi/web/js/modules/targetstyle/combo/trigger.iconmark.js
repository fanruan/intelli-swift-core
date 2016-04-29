/**
 * Created by Young's on 2016/3/23.
 */
BI.IconMarkTrigger = BI.inherit(BI.BasicButton, {
    _defaultConfig: function(){
        var conf = BI.IconMarkTrigger.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-icon-mark-trigger"
        })
    },

    _init: function(){
        BI.IconMarkTrigger.superclass._init.apply(this, arguments);
        this.value = this.options.value;
        this.container = BI.createWidget({
            type: "bi.left"
        });
        this._createEL();
        BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            element: this.element,
            items: {
                left: [this.container],
                right: [{
                    type: "bi.icon_button",
                    cls: "excel-field-type-pull-down-font"
                }]
            },
            lhgap: 10,
            rhgap: 10,
            height: 28
        })
    },

    _createEL: function(){
        this.container.empty();
        switch (this.value){
            case BICst.TARGET_STYLE.ICON_STYLE.NONE:
                this.container.addItem({
                    type: "bi.label",
                    text: BI.i18nText("BI-Wu")
                });
                break;
            case BICst.TARGET_STYLE.ICON_STYLE.POINT:
            case BICst.TARGET_STYLE.ICON_STYLE.ARROW:
                this.container.addItems([{
                    type: "bi.icon_button",
                    cls: this.value === BICst.TARGET_STYLE.ICON_STYLE.POINT ? "target-style-less-dot-font" : "target-style-less-arrow-font",
                    width: 40,
                    height: 28
                }, {
                    type: "bi.icon_button",
                    cls: this.value === BICst.TARGET_STYLE.ICON_STYLE.POINT ? "target-style-equal-dot-font" : "target-style-equal-arrow-font",
                    width: 40,
                    height: 28
                }, {
                    type: "bi.icon_button",
                    cls: this.value === BICst.TARGET_STYLE.ICON_STYLE.POINT ? "target-style-more-dot-font" : "target-style-more-arrow-font",
                    width: 40,
                    height: 28
                }]);
        }
    },

    setValue: function(v){
        this.value = v[0];
        this._createEL();
    },

    getValue: function(){
        return this.value;
    }
});
$.shortcut("bi.icon_mark_trigger", BI.IconMarkTrigger);