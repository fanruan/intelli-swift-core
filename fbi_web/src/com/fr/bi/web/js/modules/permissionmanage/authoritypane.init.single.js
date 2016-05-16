/**
 * Created by wuk on 16/5/12.
 * 初始页面-单独编辑
 */
BI.AuthorityPaneSingle = BI.inherit(BI.LoadingPane, {


    _defaultConfig: function () {
        return BI.extend(BI.AuthorityPaneSingle.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-table-pane"
        })
    },

    _init: function () {
        BI.AuthorityPaneSingle.superclass._init.apply(this, arguments);
        return BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: {
                    type: "bi.label",
                    cls: "tip-label",
                    text:'请选择一个业务包查看或设置权限'
                },
                top: 30,
                left: 0,
                right: 0
            }]
        });
    },

    populate: function () {
    }
})
;
$.shortcut("bi.authority_pane_single", BI.AuthorityPaneSingle);

