/**
 * Created by Young's on 2016/5/19.
 */
BI.LoginInfoCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.LoginInfoCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-login-info-combo"
        })
    },

    _init: function(){
        BI.LoginInfoCombo.superclass._init.apply(this, arguments);
        var loginInfo = BI.Utils.getAuthorityLoginInfo();
        var combo = BI.createWidget({
            type: "bi.single_tree_combo"
        });
        combo.on(BI.SingleTreeCombo.EVENT_BEFORE_POPUPVIEW, function(){

        });
    }
});
$.shortcut("bi.login_info_combo", BI.LoginInfoCombo);