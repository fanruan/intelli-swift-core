/**
 * Created by richie on 15/9/8.
 */
/**
 * 常量
 */
var Consts = {
    //1代码编辑权限 2代码查看权限 3 代码移动权限
    BIEDIT: 1,
    BIVIEW: 2,
    BIMOBILE: 3,

    reportCoverConfigPanelWD: "reportCoverConfigPanel",
    userLimitConfigPanelWD: "userLimitConfigPanel",
    userLimitConfigTableWD: "userLimitConfigTable",
    userLimitUnauthorizedPanelWD: "userLimitUnauthorizedPanel",
    userLimitAuthorizedPanelWD: "userLimitAuthorizedPanel",
    userLimitUnauthorizedListWD: "userLimitUnauthorizedList",
    userLimitAuthorizedListWD: "userLimitAuthorizedList",
    userLimitUnauthorizedListPanelWD: "userLimitUnauthorizedListPanel",
    userLimitAuthorizedListPanelWD: "userLimitAuthorizedListPanel",
    userLimitAuthMoveRightButtonWD: "userLimitAuthMoveRightButton",
    userLimitAuthMoveLeftButtonWD: "userLimitAuthMoveLeftButton",
    deviceBindConfigPanelWD: "deviceBindConfigPanel",
    deviceBindConfigTableWD: "deviceBindConfigTable"
};

FS.BIUSERMGR = {
    getUserItems: function() {
        FR.$defaultImport('/com/fr/bi/web/cross/css/bi.user.manager.css', 'css');
        //todo 这里结构不好，每次调用都是生成
        this._initAuthUserLimit();
        var items = [];
        items.push({
            ui : this.userLimitTabConfig(Consts.BIEDIT),
            action : FR.emptyFn()
        });
        items.push({
            ui : this.userLimitTabConfig(Consts.BIVIEW),
            action : FR.emptyFn()
        });
        return items;
    },

    _initAuthUserLimit: function() {
        var self = this;
        if( "editUserAuthLimit" in this ) {
            return;
        }
        this.editUserAuthLimit = 0;
        this.viewUserAuthLimit = 0;
        this.mobileUserAuthLimit = 0;
        var userAuthJo = BI.requestSync("fr_bi", "get_bi_limit_user", {
            type: 'POST',
            async: false,
            math:Math.random()
        });
        self.editUserAuthLimit = parseInt( userAuthJo['edit'] ) || 0;
        self.viewUserAuthLimit = parseInt( userAuthJo['view'] ) || 0;
        self.mobileUserAuthLimit = parseInt( userAuthJo['mobile'] ) || 0;
    },

    getAuthLimitByMode: function( mode ){
        switch (mode ) {
            case Consts.BIEDIT:
                return this.editUserAuthLimit;
            case Consts.BIVIEW:
                return this.viewUserAuthLimit;
            case Consts.BIMOBILE:
                return this.mobileUserAuthLimit;
            default :
                return 0;
        }
    },

    /**
     * 用户管理
     */
    userLimitTabConfig : function( mode ){
        var self = this;
        var title = mode === Consts.BIEDIT ? BI.i18nText("BI-BI_Edit_User") : (mode === Consts.BIVIEW ? BI.i18nText("BI-BI_View_User") : BI.i18nText("BI-BI_Move_User"));
        return {
            title : FR.i18nText(title),
            content : {
                type : 'panel',
                widgetName: Consts.userLimitConfigPanelWD+mode,
                doSize : true,
//                width : self.renderEl.width(),
//                height : self.renderEl.height() - 70,
                width: 883,
                height: 600 - 70,
                closeAfterAction: false,
                contentWidget : {
                    type : 'absolute',
                    widgetName: Consts.userLimitConfigTableWD+mode,
                    scrollable: true,
                    items: [
                        self._createUserLimitTip(mode),
                        self._createUserPanel(false, mode),
                        self._createUserAuthButtons(mode),
                        self._createUserPanel(true, mode)
                    ]
                }
            }
        }
    },

    _createUserLimitTip: function( mode ) {

        return {
            type: "label",
            value: FR.i18nText("FS-Mobile_User_Limit_Tip_Left")
            + " " + this.getAuthLimitByMode( mode ) + " "
            + (mode===1 ? BI.i18nText("BI-User_With_Edit_Privilege") : BI.i18nText("BI-User_With_View_Privilege")),
            x: 0,
            y: 0,
            width: 800,
            height: 20
        }
    },

    _createUserPanel: function (isAuthorizedList, mode ) {
        var self = this;
        var userPanelOptions = isAuthorizedList ? {
            panelWidgetName: Consts.userLimitUnauthorizedPanelWD + mode,
            panelTitle: FR.i18nText("FS-Mobile_Authorized_Users"),
            listWidgetName:  Consts.userLimitAuthorizedListWD + mode,
            listPanelWidgetName: Consts.userLimitAuthorizedListPanelWD + mode,
            listUrl: FR.servletURL + "?op=fr_bi&cmd=get_auth_user_list&mode=" + mode
        } : {
            panelWidgetName: Consts.userLimitAuthorizedPanelWD + mode,
            panelTitle: FR.i18nText("FS-Mobile_Unauthorized_Users"),
            listWidgetName: Consts.userLimitUnauthorizedListWD + mode,
            listPanelWidgetName: Consts.userLimitUnauthorizedListPanelWD + mode,
            listUrl: FR.servletURL + "?op=fr_bi&cmd=get_all_auth_user_list&mode=" + mode
        };
        var searchFunc = function(e){
            if (self.searchId != null) {
                clearTimeout(self.searchId);
                self.searchId = null;
            }
            self.searchId = setTimeout(function () {
                if (!FR.isNavKeyPress(e)) {
                    var val = this.getValue();
                    var userList = FS.USERMGR.tabPane.getWidgetByName(userPanelOptions.listWidgetName);
                    userList.refresh({
                        async: {
                            url: userPanelOptions.listUrl,
                            data: {
                                keyword: val
                            }
                        }
                    });
                }
            }.createDelegate(this), 200);
        };
        var items = [{
            type: 'search',
            width: 268,
            x: 0,
            y: 0,
            autoSearch: true,
            onKeyup: searchFunc
        },{
            type: 'panel',
            widgetName: userPanelOptions.listPanelWidgetName,
            width: 268,
            height: 321,
            x:0,
            y:28,
            doSize: true,
            contentWidget: {
                type: 'quicklist',
                width: 268,
                fit: false,
                scrollLoading: true,
                widgetName: userPanelOptions.listWidgetName,
                async: {
                    url: userPanelOptions.listUrl
                },
                dataFilter: function (user) {
                    return {
                        text: (user.realname || "") + '(' + user.username + ')',
                        value: user.username,
                        realname: user.realname
                    }
                },
                itemCanBeSelect: true,
                onItemSelect: function () {
                    self.setMobileAuthButtonStatus( mode );
                }
            }
        }];

        return {
            type: 'panel',
            fit: false,
            title: userPanelOptions.panelTitle,
            width: 270,
            height: 375,
            widgetName: userPanelOptions.panelWidgetName,
            border: true,
            contentWidget: {
                type: 'absolute',
//                colSize: [270],
//                rowSize: [28, 'fill'],
                items: items
            },
            y: 20,
            x: !isAuthorizedList ? 0 : 350
        }
    },

    _createUserAuthButtons: function ( mode ) {
        var self = this;
        return {
            type: 'tablepane',
            y: 0,
            x: 290,
            colSize: ['fill'],
            rowSize: [148, 24, 30, 24, 'fill'],
            items: [
                [[]], [{
                    type: 'iconbutton',
                    widgetName: Consts.userLimitAuthMoveRightButtonWD + mode ,
                    baseClass: 'fs-mobile-user-auth-move-right-icon',
                    imgsrc: 'fs-mobile-user-auth-move-right',
                    disabled: true,
                    width: 44,
                    height: 24,
                    handler: function () {
                        var unauthorizedList = FS.USERMGR.tabPane.getWidgetByName(Consts.userLimitUnauthorizedListWD + mode);
                        var index = unauthorizedList.getSelectedIndex();
                        if (index == null) {
                            return;
                        }
                        self.setLoginUser(index, false, mode);
                    }
                }], [[]], [{
                    type: 'iconbutton',
                    widgetName: Consts.userLimitAuthMoveLeftButtonWD + mode,
                    baseClass: 'fs-mobile-user-auth-move-left-icon',
                    imgsrc: 'fs-mobile-user-auth-move-left',
                    disabled: true,
                    width: 44,
                    height: 24,
                    handler: function () {
                        var authorizedList = FS.USERMGR.tabPane.getWidgetByName(Consts.userLimitAuthorizedListWD + mode);
                        var index = authorizedList.getSelectedIndex();
                        if (index === null) {
                            return;
                        }
                        self.setLoginUser(index, true, mode );
                    }
                }], [[]]
            ]
        }
    },

    /**
     * 支持移动端访问用户设置设置按钮状态
     */
    setMobileAuthButtonStatus : function( mode ) {
        var pane = FS.USERMGR.tabPane;
        var authorizedList = pane.getWidgetByName(Consts.userLimitAuthorizedListWD + mode),
            unauthorizedList = pane.getWidgetByName(Consts.userLimitUnauthorizedListWD + mode);
        var moveLeftButton = pane.getWidgetByName(Consts.userLimitAuthMoveLeftButtonWD + mode),
            moveRightButton = pane.getWidgetByName(Consts.userLimitAuthMoveRightButtonWD + mode);
        var enableLeft = true,
            enableRight = true;
        enableLeft &= authorizedList.getSelectedIndex() !== undefined;
        enableLeft &= authorizedList.getItemLength() !== 0;
        moveLeftButton.setEnable(Boolean(enableLeft));
        enableRight &= unauthorizedList.getSelectedIndex() !== undefined;
        enableRight &= unauthorizedList.getItemLength() !== 0;
        enableRight &= authorizedList.getItemLength() < this.getAuthLimitByMode( mode );
        moveRightButton.setEnable(Boolean(enableRight));
    },

    /**
     * 设置支持移动端访问的用户
     * @param index 列表中的索引顺序
     * @param isRemove 是否为移除操作
     */
    setLoginUser : function (index, isRemove, mode) {
        var self = this;
        var tabPane = FS.USERMGR.tabPane;
        var sourceList = isRemove ? tabPane.getWidgetByName(Consts.userLimitAuthorizedListWD + mode)
            : tabPane.getWidgetByName(Consts.userLimitUnauthorizedListWD + mode);
        var targetList = isRemove ? tabPane.getWidgetByName(Consts.userLimitUnauthorizedListWD + mode)
            : tabPane.getWidgetByName(Consts.userLimitAuthorizedListWD + mode);
        var username = sourceList.getValue();
        var realname = sourceList.options.listItems[index].options.realname;
        BI.requestAsync("fr_bi","set_auth_user", {
            'username': username,
            'fullname': realname,
            'remove': isRemove,
            'type': "POST",
            'mode':mode
        },function(res, status) {
            var success = res['success'];
            if (success === true) {
                sourceList.remove(index);
                targetList.addItem({
                    text: (realname || "") + '(' + username + ')',
                    value: username,
                    realname: realname
                });
                //重新选取
                if (sourceList.getItemLength() === 0) {
                    index = -1;
                } else if (index >= sourceList.getItemLength()) {
                    index = sourceList.getItemLength() - 1;
                }
                if (index !== -1) {
                    sourceList.selectItemByIndex(index);
                }
                self.setMobileAuthButtonStatus( mode );
            }
        });
    }
};

var items = FS.BIUSERMGR.getUserItems();
BI.each(items,function(idx,item){
    //debugger;
    FS.Plugin.UserManagerItems.push(item);
});