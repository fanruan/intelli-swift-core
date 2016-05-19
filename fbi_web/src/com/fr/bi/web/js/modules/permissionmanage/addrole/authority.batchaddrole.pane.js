/**
 * Created by Young's on 2016/5/16.
 */
BI.AuthorityBatchAddRolePane = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.AuthorityBatchAddRolePane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-authority-batch-add-role-pane"
        })
    },

    _init: function(){
        BI.AuthorityBatchAddRolePane.superclass._init.apply(this, arguments);
        var self = this;
        this.addRoleButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Batch_Package_Add_Role", 0),
            height: 30
        });
        this.addRoleButton.setEnable(false);
        this.addRoleButton.on(BI.Button.EVENT_CHANGE, function(){
            self.fireEvent(BI.AuthorityBatchAddRolePane.EVENT_ADD_ROLE);
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                el: {
                    type: "bi.left",
                    items: [this.addRoleButton],
                    vgap: 10
                },
                height: 50
            }, {
                el: {
                    type: "bi.left",
                    items: [{
                        type: "bi.label",
                        text: BI.i18nText("BI-Muti_Package_Auth_Hint"),
                        cls: "overwrite-tip",
                        height: 30
                    }]
                },
                height: 30
            }]
        })
    },

    populate: function(v){
        if(BI.isNotNull(v)) {
            var size = BI.uniq(v).length;
            this.addRoleButton.setText(BI.i18nText("BI-Batch_Package_Add_Role", size));
            this.addRoleButton.setEnable(size !== 0);
        }
    }
});
BI.AuthorityBatchAddRolePane.EVENT_ADD_ROLE = "EVENT_ADD_ROLE";
$.shortcut("bi.authority_batch_add_role_pane", BI.AuthorityBatchAddRolePane);