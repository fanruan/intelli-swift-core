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
    var imgOffsetX, imgOffsetY, loginImgWidth = 1920, loginImgHeight = 1080, scale;
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
        $('#fs-chicken-soup').text(FR.i18nText("BI-Login_Soup"))
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

    function calcBackgroundScale () {
        var windowWidth = document.body.clientWidth;
        var windowHeight = document.body.clientHeight;

        if (windowWidth / windowHeight >= loginImgWidth / loginImgHeight) {
            scale = windowWidth / loginImgWidth;
            imgOffsetX = 0;
            imgOffsetY = (loginImgHeight * scale - windowHeight) / 2;
        } else {
            scale = windowHeight / loginImgHeight;
            imgOffsetX = (loginImgWidth * scale - windowWidth) / 2;
            imgOffsetY = 0;
        }
        $('img.fs-login-img').css({
            "margin-left": "-" + imgOffsetX +"px",
            "margin-top": "-" + imgOffsetY +"px",
            width: loginImgWidth * scale + "px",
            height: loginImgHeight * scale + "px"
        });
        $('#fs-chicken-soup').css({
            zoom:Math.min(windowWidth/1920, windowHeight/1080),
            "-moz-transform":"scale(" + Math.min(windowWidth/1920, windowHeight/1080) +")"
        });
        // $('#fs-login-logo').css({
        //     zoom: Math.min(windowWidth/1920, windowHeight/1080),
        //     "-moz-transform":"scale(" + Math.min(windowWidth/1920, windowHeight/1080) + ")"
        // });
        // $('#fs-setting-title').css({
        //     zoom: Math.min(windowWidth/1920, windowHeight/1080),
        //     "-moz-transform":"scale(" + Math.min(windowWidth/1920, windowHeight/1080) + ")"
        // });
        // $('#fs-login-content').css({
        //     zoom: Math.min(windowWidth/1920, windowHeight/1080)
        // })
    };

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
        calcBackgroundScale()
        $(window).resize(function() {
            calcBackgroundScale();
        })
        return $('#fs-sure-area');
    }
});