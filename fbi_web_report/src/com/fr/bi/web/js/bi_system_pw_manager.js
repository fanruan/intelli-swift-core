/**
 * Created by 小灰灰 on 2016/6/21.
 */
$(function () {
    /**
     * 初次登录平台，配置管理员用户名和密码的页面。
     */
    $.extend(FS.Trans, {
        setSystemManagerPW: function (username, pw, verifyNumber, callback) {
            var data = {
                userName: username,
                pw: pw
            };

            if (verifyNumber != undefined) {
                data.verifyNumber = verifyNumber;
            }

            var completeFn = function (res, status) {
                if ($.isFunction(callback)) {
                    callback(FR.jsonDecode(res.responseText));
                }
            };
            var config = {
                url: FR.servletURL + "?op=fs_load&cmd=setsystem_manager_pw",
                data: data
            };
            FS.Async.ajax(config, completeFn);
        }
    });
   
    var pwPane = {};
    var $mask = $('<div class="fs-login-errmask"/>');
    var $settingPane = createSettingPane();
    var $showPane = createShowPane();
    $settingPane.show();
    $showPane.hide();

    function showErrorMsg($pos, msg){
        $mask.hide().insertAfter($pos).text(msg);
        $mask.click(function(){
            $(this).fadeOut();
            $pos.select();
        }).fadeIn();
    };


    function createSettingPane() {
        $('#fs-setting-title').text(FR.i18nText('FS-Admin-First_Set_FS'));
        //用户名
        pwPane.userNameText = $('input.fs-login-username').attr("placeholder", FR.i18nText("FS-Generic-Simple_Username")).attr('title',FR.i18nText("FS-Generic-Simple_Username"));
        //密码
        pwPane.passwordText = $('input.fs-login-password').attr("placeholder", FR.i18nText("FS-Generic-Simple_Password")).attr('title',FR.i18nText("FS-Generic-Simple_Password"));
        //确认密码
        pwPane.confirmPasswordText = $('input.fs-login-password-confirm').attr("placeholder", FR.i18nText("FS-Admin-Confirm_Password")).attr('title',FR.i18nText("FS-Admin-Confirm_Password"));
        var okButton = $('#fs-login-confirm-btn').text(FR.i18nText("FS-User_OK"));

        okButton.unbind('click').bind('click', function (e) {
            var userName = pwPane.userNameText.val();
            var passText = pwPane.passwordText.val();
            var confirmPassText = pwPane.confirmPasswordText.val();
            if (userName == undefined || userName === "") {
                showErrorMsg(pwPane.userNameText, FR.i18nText("FS-User-User_Can_Not_Be_Null"))
                return;
            }
            if (passText == undefined || passText === "") {
                showErrorMsg(pwPane.passwordText, FR.i18nText("FS-Admin-Password_Can_Not_Be_Null"))
                return;
            }
            if (passText !== confirmPassText) {
                showErrorMsg(pwPane.confirmPasswordText, FR.i18nText("FS-Admin-Passwords_Not_Match"))
                return;
            }
            /**
             * gril是用来迷惑对方的，嘿嘿
             */
            FS.Trans.setSystemManagerPW(FR.encrypt(userName, 'boys'), FR.encrypt(passText, 'girls'), pwPane.verifyNumber, function (data) {
                if (data.state === 'success') {
                    $settingPane.hide();
                    $showPane.show();
                    setShowPaneText();
                    pwPane.verifyNumber = data.verifyNumber
                } else if (data.state === 'failed') {
                    FR.Msg.toast(FR.i18nText("FS-Admin-Account_Has_Set"));
                } else {
                    FR.Msg.toast(FR.i18nText("FS-Admin-Unknown_Error"));
                }
            });
        });
        return $('#fs-setting-area');
    }

    function setShowPaneText() {
        $('#fs-sure-title').text(FR.i18nText('FS-Admin-Account_Is') + '：');
        $('#fs-login-info-username').text(FR.i18nText('FS-Generic-Simple_Username') + '：' + pwPane.userNameText.val());
        $('#fs-login-info-password').text(FR.i18nText('FS-Generic-Simple_Password') + '：' + pwPane.passwordText.val());
        $('#fs-sure-warning').text(FR.i18nText("FS-Admin-Remember_Username_Password") + '！');
    }

    function createShowPane() {
        setShowPaneText();
        var $button = $('#fs-login-info-confirm-btn');
        if (isSupportFS != "true") {
            $button.text(FR.i18nText("FS-User_Login2PlatForm")).click(function (e) {
                FR.doHyperlinkByPost(FR.buildServletUrl({op: 'fr_auth', cmd: 'ah_login'}), {
                    fr_username: FR.cjkEncode(pwPane.userNameText.val()),
                    fr_password: pwPane.passwordText.val()
                }, '_self');
            })
        } else {
            $button.text(FR.i18nText("FS-User_Login2FS")).click(function (e) {
                FR.ajax({
                    url: FR.servletURL + '?op=fs_load&cmd=login',
                    data: {
                        //FR.ajax会做一次cjkencode的。
                        fr_username: pwPane.userNameText.val(),
                        fr_password: pwPane.passwordText.val()
                    },
                    type: 'POST',
                    async: false,
                    error: function () {
                        FR.Msg.toast("Error!");
                    },
                    complete: function (res, status) {
                        if (res.responseText == "") {
                            FR.Msg.toast(FR.i18nText("FS-Admin-Authentication_failed") + "！");
                            return;
                        }
                        var signResult = FR.jsonDecode(res.responseText);
                        if (signResult.fail) {
                            FR.Msg.toast(FR.i18nText("FS-Admin-Authentication_failed") + "！");
                        } else if (signResult.url) {
                            window.location.href = signResult.url;
                        }
                    }
                });
            })
        }

        $('#fs-login-reset').text(FR.i18nText("FS-User_BackAndReset")).click(function (e) {
            $settingPane.show();
            $showPane.hide();
        })
        return $('#fs-sure-area');
    }
});