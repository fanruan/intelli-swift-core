/**
 * Created by Young's on 2016/5/16.
 */
BI.AuthorityBatchSetPane = BI.inherit(BI.Widget, {

    _constants: {
        SHOW_EMPTY: 1,
        SHOW_PANE: 2,
        SHOW_SEARCHER: 3
    },
    
    _defaultConfig: function(){
        return BI.extend(BI.AuthorityBatchSetPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-authority-batch-set-pane"
        })
    },

    _init: function(){
        BI.AuthorityBatchSetPane.superclass._init.apply(this, arguments);
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
                    items: [{
                        type: "bi.label",
                        text: BI.i18nText("BI-Muti_Auth_Set"),
                        height: 30,
                        cls: "package-title"
                    }, this.packageName],
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
            case this._constants.SHOW_PANE:
                this.roles = BI.createWidget({
                    type: "bi.authority_batch_add_role_pane"
                });
                this.roles.on(BI.AuthorityBatchAddRolePane.EVENT_ADD_ROLE, function(){
                    self.rolesTab.setSelect(self._constants.SHOW_SEARCHER);
                    self.searcher.populate(self.packageIds);
                });
                return this.roles;
            case this._constants.SHOW_SEARCHER:
                this.searcher = BI.createWidget({
                    type: "bi.batch_add_role_searcher"
                });
                this.searcher.on(BI.BatchAddRoleSearcher.EVENT_CANCEL, function(){
                    self.rolesTab.setSelect(self._constants.SHOW_PANE);
                });
                this.searcher.on(BI.BatchAddRoleSearcher.EVENT_SAVE, function(v){
                    self._updatePackageRoles(v);
                    self.rolesTab.setSelect(self._constants.SHOW_PANE);
                    self.fireEvent(BI.AuthorityBatchSetPane.EVENT_CHANGE);
                });
                return BI.createWidget({
                    type: "bi.absolute",
                    items: [{
                        el: this.searcher,
                        top: 0,
                        left: -220,
                        bottom: 0,
                        right: 0
                    }]
                })
        }
    },

    _updatePackageRoles: function(roles){
        var authSettings = Data.SharingPool.get("authority_settings");
        var packagesAuth = authSettings.packages_auth;
        var pAuth = packagesAuth[this.packageId] || [];
        var newPAuth = pAuth.concat(roles);
        BI.each(this.packageIds, function(i, pId){
            packagesAuth[pId] = newPAuth;    
        });
        Data.SharingPool.put("authority_settings", authSettings);
        BI.Utils.savePackageAuthority({
            package_ids: this.packageIds,
            roles: newPAuth
        }, function(){});
    },

    setValue: function(v){
        this.packageIds = v;
        this.packageName.setText(BI.isNotNull(v) ? BI.i18nText("BI-N_Packages", BI.uniq(v).length) : "");
        this.rolesTab.setSelect(this._constants.SHOW_PANE);
        BI.isNotNull(this.roles) && this.roles.populate(v);
    },

    populate: function(){

    }
});
BI.AuthorityBatchSetPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.authority_batch_set_pane", BI.AuthorityBatchSetPane);