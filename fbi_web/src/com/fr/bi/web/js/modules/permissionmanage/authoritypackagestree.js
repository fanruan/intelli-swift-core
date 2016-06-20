/**
 * 可以单选多选切换的树
 *用于权限管理中业务包的显示
 * Created by wuk on 16/4/20.
 * @class BI.AuthorityPackagesTree
 * @extends BI.Widget
 */
BI.AuthorityPackagesTree = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AuthorityPackagesTree.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-authority-packages-tree",
            width: 220
        });
    },

    _init: function () {
        BI.AuthorityPackagesTree.superclass._init.apply(this, arguments);
        var self = this;
        this.tree = BI.createWidget({
            type: "bi.switch_tree"
        });
        this.tree.on(BI.SwitchTree.EVENT_CHANGE, function () {
            self.fireEvent(BI.AuthorityPackagesTree.EVENT_CHANGE);
        });

        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: this._buildNorth(),
                height: 50
            }, {
                el: this.tree,
                height: "fill"
            }],
            hgap: 10
        });
        this.populate();
    },
    
    getSelectType: function () {
        return this.tree.getSelect()
    },
    
    setSelect: function(v){
        this.tree.setSelect(v);
        this.tree.setValue([]);
        this.switchButton.setText(v === BI.SwitchTree.SelectType.MultiSelect ?
                                     BI.i18nText("BI-Out_Muti_Setting") : BI.i18nText("BI-Muti_Setting"));
    },

    getValue: function(){
        return this.tree.getValue();  
    },

    _buildNorth: function () {
        var self = this;
        this.switchButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Muti_Setting"),
            height: 30,
            level: "ignore"
        });
        this.switchButton.on(BI.Button.EVENT_CHANGE, function(){
            self.tree.switchSelect();
            self.tree.setValue([]);
            this.setText(self.tree.getSelect() === BI.SwitchTree.SelectType.MultiSelect ?
                            BI.i18nText("BI-Out_Muti_Setting") : BI.i18nText("BI-Muti_Setting"));
            self.fireEvent(BI.AuthorityPackagesTree.EVENT_TYPE_CHANGE);
        });

        return BI.createWidget({
            type: "bi.left",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Package_List"),
                height: 30,
                cls: "package-list-title"
            }, this.switchButton],
            lgap: 10,
            vgap: 10
        });
    },

    populate: function () {
        this.tree.populate( BI.Utils.getAllGroupedPackagesTree());
    }
});
BI.AuthorityPackagesTree.EVENT_TYPE_CHANGE = "AuthorityPackagesTree.EVENT_TYPE_CHANGE";
BI.AuthorityPackagesTree.EVENT_CHANGE = "AuthorityPackagesTree.EVENT_CHANGE";
$.shortcut("bi.authority_packages_tree", BI.AuthorityPackagesTree);
