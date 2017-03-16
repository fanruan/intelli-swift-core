/**
 * Created by Young's on 2016/10/17.
 */
BI.UpdateTypeButton = BI.inherit(BI.TextButton, {
    _defaultConfig: function () {
        return BI.extend(BI.UpdateTypeButton.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-update-type-button"
        });
    },

    _init: function () {
        BI.UpdateTypeButton.superclass._init.apply(this, arguments);
        this.icon = BI.createWidget({
            type: "bi.icon_button",
            cls: "tab-select-active-font",
            iconWidth: 28,
            iconHeight: 28
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.icon,
                top: 0,
                left: 0,
                bottom: 0
            }]
        });
    },
    
    toggleIcon: function(toggle) {
        this.icon.setVisible(toggle);
    }
});
BI.UpdateTypeButton.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.update_type_button", BI.UpdateTypeButton);