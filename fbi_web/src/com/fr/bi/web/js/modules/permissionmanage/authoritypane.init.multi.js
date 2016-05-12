/**
 * Created by wuk on 16/5/12.
 * 初始页面-批量编辑
 */
BI.AuthorityPaneMulti = BI.inherit(BI.LoadingPane, {


    _defaultConfig: function () {
        return BI.extend(BI.AuthorityPaneMulti.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-table-pane"
        })
    },

    _init: function () {
        BI.AuthorityPaneMulti.superclass._init.apply(this, arguments);
        var self=this;
        return BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [
                    this.saveButton = BI.createWidget({
                    type: "bi.button",
                    ype: "bi.button",
                    text: '权限配置(0个业务包)',
                    height: 20,
                    width: 200,
                    handler: function () {
                        self.fireEvent(BI.AuthorityPaneMulti.EVENT_CHANGE);
                    }
                }),
                // {
                //     el: {
                //         type: "bi.button",
                //         text: BI.i18nText("BI-Save"),
                //         height: 20,
                //         width:50,
                //         handler: function () {
                //         }
                //     },
                //     top: 0,
                //     left: 300,
                //     right: 0
                // },
                {
                    el: {
                        type: "bi.label",
                        cls: "tip-label",
                        text: '本次权限将覆盖所选业务包的原有权限设置'
                    },
                    top: 0,
                    right: 0
                }]
        });

    },

    populate: function (items) {
        var self = this;
        self.saveButton.setText('权限配置(' + items.length + '个业务包)')
    }
});
BI.AuthorityPaneMulti.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.authority_pane_multi", BI.AuthorityPaneMulti);

