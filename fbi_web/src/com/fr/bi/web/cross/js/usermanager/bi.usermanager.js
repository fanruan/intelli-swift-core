/**
 * 用户管理
 * Created by Young's on 2017/3/21.
 */
BI.FSUserManager = BI.inherit(FR.Widget, {

    _constants: {
        NO_AUTHORIZED: "NO_AUTHORIZED",
        AUTHORIZED: "AUTHORIZED",
        MOVE_RIGHT: 1,
        MOVE_LEFT: 2
    },

    _defaultConfig: function () {
        return BI.extend(BI.FSUserManager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-fs-user-manager"
        })
    },

    _init: function () {
        var mode = this.options.mode;
        this._initAuthUserLimit();
        BI.createWidget({
            type: "bi.vertical",
            element: this.options.renderEl,
            cls: "bi-fs-user-manager",
            items: [{
                el: this._createLimitTip(mode),
                height: 50
            }, {
                el: this._createMainPane(mode)
            }],
            vgap: 10
        });
    },

    _initAuthUserLimit: function () {
        var self = this;
        this.editUserAuthLimit = 0;
        this.viewUserAuthLimit = 0;
        this.mobileUserAuthLimit = 0;
        FR.ajax({
            type: "POST",
            url: FR.servletURL + '?op=' + "fr_bi" + '&cmd=' + "get_bi_limit_user" + "&_=" + Math.random(),
            async: false,
            complete: function (res, status) {
                var userAuthJo = FR.jsonDecode(res.responseText);
                if (status === 'success') {
                    self.editUserAuthLimit = BI.parseInt(userAuthJo['edit']) || 0;
                    self.viewUserAuthLimit = BI.parseInt(userAuthJo['view']) || 0;
                    self.mobileUserAuthLimit = BI.parseInt(userAuthJo['mobile']) || 0;
                }
            }
        });
    },

    _getAuthLimitByMode: function (mode) {
        switch (mode) {
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

    _createLimitTip: function (mode) {
        var authLimit = this._getAuthLimitByMode(mode);
        if (authLimit === -1) {
            authLimit = BI.i18nText("BI-Unrestricted");
        }
        return BI.createWidget({
            type: "bi.left",
            cls: "support-tip",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Basic_License_Support")
            }, {
                type: "bi.label",
                text: authLimit,
                cls: "count",
                hgap: 5
            }, {
                type: "bi.label",
                text: mode === Consts.BIEDIT ? BI.i18nText("BI-Basic_User_Has_Edit_Auth") : BI.i18nText("BI-Basic_User_Has_View_Auth")
            }]
        })
    },

    _createMainPane: function (mode) {
        this.users = {};
        this.selectedUsers = {};
        this.usersPane = {};
        this.searcherPane = {};
        return BI.createWidget({
            type: "bi.left",
            items: [
                this._createUsersPane(this._constants.NO_AUTHORIZED, mode),
                this._createMoveButtons(mode),
                this._createUsersPane(this._constants.AUTHORIZED, mode)
            ]
        });
    },

    _createMoveButtons: function (mode) {
        var self = this;
        this.moveRight = BI.createWidget({
            type: "bi.text_button",
            cls: "move-button",
            text: "＞",
            width: 60,
            height: 28,
            disabled: true,
            value: this._constants.MOVE_RIGHT
        });
        this.moveRight.on(BI.TextButton.EVENT_CHANGE, function () {
            self._moveUser(this.getValue(), mode);
        });

        this.moveLeft = BI.createWidget({
            type: "bi.text_button",
            cls: "move-button",
            text: "＜",
            width: 60,
            height: 28,
            disabled: true,
            value: this._constants.MOVE_LEFT
        });
        this.moveLeft.on(BI.TextButton.EVENT_CHANGE, function () {
            self._moveUser(this.getValue(), mode);
        });
        return BI.createWidget({
            type: "bi.vertical_adapt",
            items: [{
                type: "bi.vertical",
                items: [this.moveRight, this.moveLeft],
                vgap: 10,
                hgap: 20
            }],
            width: 100,
            height: 400
        })
    },

    _moveUser: function (move, mode) {
        var toRight = move === this._constants.MOVE_RIGHT;
        var users = this.selectedUsers[toRight ? this._constants.NO_AUTHORIZED : this._constants.AUTHORIZED];
        var removed = [];
        BI.each(this.users[toRight ? this._constants.NO_AUTHORIZED : this._constants.AUTHORIZED], function (i, user) {
            if (users.contains(user.id)) {
                removed.push(user);
            }
        });
        BI.remove(this.users[toRight ? this._constants.NO_AUTHORIZED : this._constants.AUTHORIZED], function (i, user) {
            return users.contains(user.id);
        });
        this.selectedUsers[toRight ? this._constants.NO_AUTHORIZED : this._constants.AUTHORIZED] = [];
        this.users[toRight ? this._constants.AUTHORIZED : this._constants.NO_AUTHORIZED] =
            this.users[toRight ? this._constants.AUTHORIZED : this._constants.NO_AUTHORIZED].concat(removed);
        this.usersPane[this._constants.NO_AUTHORIZED].populate(this._createItems(this.users[this._constants.NO_AUTHORIZED], this._constants.NO_AUTHORIZED));
        this.usersPane[this._constants.AUTHORIZED].populate(this._createItems(this.users[this._constants.AUTHORIZED], this._constants.AUTHORIZED));
        this._toggleButtons();
        this.searcherPane[this._constants.NO_AUTHORIZED].stopSearch();
        this.searcherPane[this._constants.AUTHORIZED].stopSearch();
        BI.requestAsync("fr_bi", "set_auth_user", {
            mode: mode,
            users: removed,
            remove: !toRight
        }, BI.emptyFn, BI.emptyFn);
    },

    _toggleButtons: function () {
        this.moveRight.setEnable(this.selectedUsers[this._constants.NO_AUTHORIZED].length > 0);
        this.moveLeft.setEnable(this.selectedUsers[this._constants.AUTHORIZED].length > 0);
    },

    _createItems: function (users, type, keyword) {
        var self = this;
        var items = [];
        BI.each(users, function (i, user) {
            var item = {
                type: "bi.multi_select_item",
                text: user.text,
                value: user.id,
                selected: self.selectedUsers[type].contains(user.id),
                keyword: keyword,
                listeners: [{
                    eventName: BI.BasicButton.EVENT_CHANGE,
                    action: function () {
                        var isSelected = this.isSelected(),
                            value = this.getValue();
                        if (isSelected) {
                            self.selectedUsers[type].push(value);
                        } else {
                            BI.remove(self.selectedUsers[type], function (i, v) {
                                return v === value;
                            })
                        }
                        self._toggleButtons();
                    }
                }]
            };
            items[i] = [item];
        });
        return items;
    },

    _createUsersPane: function (type, mode) {
        var self = this;
        this.users[type] = [];
        this.selectedUsers[type] = [];

        this.usersPane[type] = BI.createWidget({
            type: "bi.grid_view",
            cls: "user-list",
            rowHeightGetter: function () {
                return 30;
            },
            columnWidthGetter: function () {
                return 276;
            },
            width: 280
        });

        var cmd = type === this._constants.AUTHORIZED ? "get_auth_user_list" : "get_all_auth_user_list";
        BI.requestAsync("fr_bi", cmd, {mode: mode}, function (users) {
            BI.each(users, function (i, user) {
                user.text = user.realname + "(" + user.username + ")";
            });
            self.users[type] = users;
            self.usersPane[type].populate(self._createItems(users, type));
        }, BI.emptyFn);

        this.searcherPane[type] = BI.createWidget({
            type: "bi.searcher",
            isAutoSync: true,
            isAutoSearch: false,
            onSearch: function (op, callback) {
                var keyword = op.keyword;
                var res = BI.Func.getSearchResult(self.users[type], keyword, "text");
                var matched = res.matched, finded = res.finded;
                callback(self._createItems(matched.concat(finded), type, keyword));
            },
            popup: {
                type: "bi.grid_view",
                cls: "user-list",
                rowHeightGetter: function () {
                    return 30;
                },
                columnWidthGetter: function () {
                    return 276;
                },
                width: 280
            }
        });
        this.searcherPane[type].setAdapter(this.usersPane[type]);
        this.searcherPane[type].on(BI.Searcher.EVENT_STOP, function () {
            self.usersPane[type].populate(self._createItems(self.users[type], type));
        });

        return BI.createWidget({
            type: "bi.vtape",
            cls: "users-pane",
            items: [{
                type: "bi.label",
                text: type === this._constants.AUTHORIZED ?
                    BI.i18nText("BI-Basic_Authorized_Users") :
                    BI.i18nText("BI-Basic_No_Authorized_Users"),
                cls: "auth-title",
                textAlign: "left",
                height: 30,
                lgap: 5
            }, {
                el: this.searcherPane[type],
                height: 30
            }, {
                el: this.usersPane[type]
            }],
            width: 280,
            height: 400
        })
    }
});
$.shortcut("bi.fs_user_manager", BI.FSUserManager);