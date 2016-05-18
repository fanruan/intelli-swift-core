/**
 * Created by Young's on 2016/5/16.
 */
BI.AuthoritySinlgeSetPane = BI.inherit(BI.Widget, {

    _constants: {
        SHOW_EMPTY: 1,
        SHOW_PANE: 2,
        SHOW_SEARCHER: 3
    },

    _defaultConfig: function(){
        return BI.extend(BI.AuthoritySinlgeSetPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-authority-single-set-pane"
        })
    },

    _init: function(){
        BI.AuthoritySinlgeSetPane.superclass._init.apply(this, arguments);
        this.packageName = BI.createWidget({
            type: "bi.label",
            text: "",
            height: 30,
            cls: "package-title"
        });

        this.rolesTab = BI.createWidget({
            type: "bi.tab",
            tab: "",
            direction: "custom",
            cardCreator: BI.bind(this._createTab, this)
        });
        this.rolesTab.setSelect(this._constants.SHOW_EMPTY);

        BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [{
                el: {
                    type: "bi.left",
                    cls: "authority-set-title",
                    items: [this.packageName, {
                        type: "bi.label",
                        text: BI.i18nText("BI-Permissions_Setting"),
                        height: 30,
                        cls: "package-title"
                    }],
                    vgap: 5,
                    hgap: 2
                },
                height: 40
            }, {
                el: this.rolesTab,
                height: "fill"
            }],
            hgap: 20
        });
    },

    _createTab: function(v) {
        var self = this;
        switch (v) {
            case this._constants.SHOW_EMPTY:
                return BI.createWidget({
                    type: "bi.label",
                    text: BI.i18nText("BI-Select_One_Package_Setting"),
                    height: 50,
                    cls: "select-package-empty-tip"
                });
            case this._constants.SHOW_PANE:
                this.roles = BI.createWidget({
                    type: "bi.authority_add_role_pane"
                });
                this.roles.on(BI.AuthorityAddRolePane.EVENT_ADD_ROLE, function(){
                     self.rolesTab.setSelect(self._constants.SHOW_SEARCHER);
                });
                return this.roles;
            case this._constants.SHOW_SEARCHER:
                this.searcher = BI.createWidget({
                    type: "bi.add_role_searcher"
                });
                this.searcher.on(BI.AddRoleSearcher.EVENT_CANCEL, function(){
                    self.rolesTab.setSelect(self._constants.SHOW_PANE);
                });
                this.searcher.on(BI.AddRoleSearcher.EVENT_SAVE, function(){
                    self.rolesTab.setSelect(self._constants.SHOW_PANE);
                });
                return this.searcher;
        }
    },

    setValue: function(v){
        var pId = v[0];
        this.packageName.setText(BI.Utils.getPackageNameByID4Conf(pId));
        this.rolesTab.setSelect(this._constants.SHOW_PANE);
        BI.isNotNull(this.roles) && this.roles.populate();
        BI.isNotNull(this.searcher) && this.searcher.populate();
    },

    populate: function(packageIds, roles){
        if(packageIds.length !== 1) {
            this.packageName.setText("");
            this.rolesTab.setSelect(this._constants.SHOW_EMPTY);
            return;
        }
        this.packageName.setText(BI.Utils.getPackageNameByID4Conf(packageIds[0]));
        this.roles.setValue(roles);
        this.rolesTab.setSelect(this._constants.SHOW_PANE);
    }
});
$.shortcut("bi.authority_single_set_pane", BI.AuthoritySinlgeSetPane);