/**
 * Created by Young's on 2016/3/23.
 */
BI.IconMarkItem = BI.inherit(BI.BasicButton, {
    _defaultConfig: function(){
        var conf = BI.IconMarkItem.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-icon-mark-item"
        })
    },

    _init: function(){
        BI.IconMarkItem.superclass._init.apply(this, arguments);
        var o = this.options;
        var value = o.value;
        var item = BI.createWidget({
            type: "bi.left"
        });
        switch (value){
            case BICst.TARGET_STYLE.ICON_STYLE.NONE:
                item.addItem({
                    type: "bi.label",
                    text: BI.i18nText("BI-Wu"),
                    height: 30,
                    width: 40
                });
                break;
            case BICst.TARGET_STYLE.ICON_STYLE.POINT:
            case BICst.TARGET_STYLE.ICON_STYLE.ARROW:
                item.addItems([{
                    type: "bi.icon_button",
                    cls: value === BICst.TARGET_STYLE.ICON_STYLE.POINT ? "target-style-less-dot-font" : "target-style-less-arrow-font",
                    width: 40,
                    height: 30
                }, {
                    type: "bi.icon_button",
                    cls: value === BICst.TARGET_STYLE.ICON_STYLE.POINT ? "target-style-equal-dot-font" : "target-style-equal-arrow-font",
                    width: 40,
                    height: 30
                }, {
                    type: "bi.icon_button",
                    cls: value === BICst.TARGET_STYLE.ICON_STYLE.POINT ? "target-style-more-dot-font" : "target-style-more-arrow-font",
                    width: 40,
                    height: 30
                }])
        }
        this.checkMark = BI.createWidget({
            type: "bi.icon_button",
            cls: "item-check-font",
            width: 30,
            height: 30
        });
        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [this.checkMark, item]
        });
    },

    doClick: function(){
        BI.ExcelFieldTypeItem.superclass.doClick.apply(this, arguments);
    },

    setSelected: function(v){
        BI.ExcelFieldTypeItem.superclass.setSelected.apply(this, arguments);
        this.checkMark.setSelected(v);
    },

    getValue: function(){
        return this.options.value;
    }
});
$.shortcut("bi.icon_mark_item", BI.IconMarkItem);