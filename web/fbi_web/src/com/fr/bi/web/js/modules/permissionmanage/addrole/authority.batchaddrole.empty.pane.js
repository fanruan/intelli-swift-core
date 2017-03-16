/**
 * Created by Young's on 2017/2/28.
 */
BI.AuthorityBatchAddRoleEmptyPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.AuthorityBatchAddRoleEmptyPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-authority-batch-add-role-pane"
        })
    },

    _init: function () {
        BI.AuthorityBatchAddRoleEmptyPane.superclass._init.apply(this, arguments);
        var self = this;
        this.addRoleButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Batch_Package_Add_Role", 0),
            height: 30,
            warningTitle: BI.i18nText("BI-Please_Select_Package")
        });
        this.addRoleButton.setEnable(false);
        this.addRoleButton.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.AuthorityBatchAddRoleEmptyPane.EVENT_CHANGE);
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

    setValue: function(v) {
        var size = BI.uniq(v).length;
        this.addRoleButton.setText(BI.i18nText("BI-Batch_Package_Add_Role", size));
        this.addRoleButton.setEnable(size !== 0);
    }
});
BI.AuthorityBatchAddRoleEmptyPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.authority_batch_add_role_empty_pane", BI.AuthorityBatchAddRoleEmptyPane);