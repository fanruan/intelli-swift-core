/**
 * Created by Young's on 2016/12/6.
 */
BI.IconChangeTextButton = BI.inherit(BI.BasicButton, {
    _defaultConfig: function() {
        var conf = BI.IconChangeTextButton.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-icon-change-text-button"
        })
    },
    
    _init: function() {
        BI.IconChangeTextButton.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.icon = BI.createWidget({
            type: "bi.icon_change_button",
            cls: "icon-button",
            width: 16,
            height: 16
        });
        this.icon.setIcon(o.iconCls);
        BI.createWidget({
            type: "bi.vertical_adapt",
            element: this.element,
            items: [this.icon, {
                type: "bi.label",
                text: o.text,
                textAlign: "left"
            }],
            lgap: 5
        });
    },
    
    setIcon: function(icon) {
        this.icon.setIcon(icon);
    }
    
});
$.shortcut("bi.icon_change_text_button", BI.IconChangeTextButton);