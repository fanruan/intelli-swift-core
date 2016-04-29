/**
 * guy
 * @extends BI.Single
 * @type {*|void|Object}
 */
BI.HalfIconButton = BI.inherit(BI.IconButton, {
    _defaultConfig: function() {
        var conf = BI.HalfIconButton.superclass._defaultConfig.apply(this,arguments);
        return BI.extend(conf, {
            extraCls: "bi-half-icon-button check-half-select-icon",
            height: 13,
            width: 13,
            iconWidth: 13,
            iconHeight: 13,
            selected: false
        })
    },

    _init : function() {
        BI.HalfIconButton.superclass._init.apply(this, arguments);
    },

    doClick: function(){
        BI.HalfIconButton.superclass.doClick.apply(this, arguments);
        if(this.isValid()){
            this.fireEvent(BI.HalfIconButton.EVENT_CHANGE);
        }
    }
});
BI.HalfIconButton.EVENT_CHANGE = "HalfIconButton.EVENT_CHANGE";

$.shortcut("bi.half_icon_button", BI.HalfIconButton);