/**
 * create by young
 * 以选中用户
 */
BI.SelectedUserButton = BI.inherit(BI.BasicButton, {
    _defaultConfig: function () {
        var conf = BI.SelectedUserButton.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-selected-user-button"
        })
    },

    _init: function(){
        BI.SelectedUserButton.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var deleteButton = BI.createWidget({
            type: "bi.icon_button",
            cls: "close-font",
            width: 20,
            height: 20
        });
        deleteButton.on(BI.Controller.EVENT_CHANGE, function(){
            arguments[1] = o.user_id.toString();
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [{
                type: "bi.label",
                text: o.user_name,
                height: 30
            }, {
                type: "bi.center_adapt",
                items: [deleteButton],
                width: 20,
                height: 30
            }],
            hgap: 3,
            height: 30
        })
    }

});
BI.SelectedUserButton.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.selected_user_button", BI.SelectedUserButton);