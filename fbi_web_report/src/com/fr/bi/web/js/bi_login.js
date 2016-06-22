/**
 * Created by 小灰灰 on 2016/6/21.
 */
$(function() {
    var imgOffsetX, imgOffsetY, loginImgWidth, loginImgHeight, scale;
    //报错蒙板
    var $mask = $('<div class="fs-login-errmask"/>');
    var windowWidth = document.body.clientWidth;
    var windowHeight = document.body.clientHeight;
    $('#fs-chicken-soup').text(FR.i18nText("BI-Login_Soup"))
    //用户名
    var $username = $('input.fs-login-username').attr("placeholder", FR.i18nText("FS-Generic-Simple_Username")).attr('title',FR.i18nText("FS-Generic-Simple_Username"));
    //密码
    var $password = $('input.fs-login-password').attr("placeholder", FR.i18nText("FS-Generic-Simple_Password")).attr('title',FR.i18nText("FS-Generic-Simple_Password"));
    $('input').focus(function(){
        $mask.hide();
    });
    //是否保持登录状态
    var $keep = $('span.fs-login-remember').text(FR.i18nText("FS-Generic-Privilege_Keep_Login_State")).click(
        function(){
            $(this).toggleClass('fs-login-remember-selected');
        }
    );
    //登录按钮
    $('a').text(FR.i18nText("FS-Generic-Sign_In")).click(
        function(){
            signIN();
        }
    );
    //绑定回车
    $(document).keydown(function(e){
        if(e.keyCode===13){
            signIN();
        }
    });
    /**
     * 初始化FS的登录背景图片
     */
    var initBackgroundImage = function () {
        var self = this;
        var ran = new Date().getTime() + "" + (Math.random() * 1000);
        FR.ajax({
            url: FR.servletURL + "?op=fs_load&cmd=getLoginImageInfo&_ran=" + ran,
            complete: function (res, status) {
                if (status == 'success') {
                    var loginInfo = FR.jsonDecode(res.responseText);
                    var loginImgID = loginInfo.id;
                    loginImgWidth = parseInt(loginInfo.width);
                    loginImgHeight = parseInt(loginInfo.height);
                    calcBackgroundScale();
                    var url = FR.servletURL + ((loginImgID && loginImgID != 'null') ?
                            ('?op=fr_attach&cmd=ah_image&id=' + loginImgID + '&isAdjust=false')
                            : '?op=resource&resource=/com/fr/bi/web/images/login/login.png');
                    if ($('body').length > 0) {
                        var loginImg = $('img.fs-login-img');
                        loginImg.attr("src", url);
                        loginImg.css({
                            "margin-left": "-" + imgOffsetX + "px",
                            "margin-top": "-" + imgOffsetY + "px",
                            width: loginImgWidth * scale + "px",
                            height: loginImgHeight * scale + "px"
                        });
                    }
                }
            }
        })
    };

    var calcBackgroundScale = function () {
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
        $('#fs-chicken-soup').css({
            zoom:Math.min(windowWidth/1920, windowHeight/1080)
        });
        $('#fs-login-logo').css({
            zoom: Math.min(windowWidth/1920, windowHeight/1080)
        });
        $('#fs-login-content').css({
            zoom: Math.min(windowWidth/1920, windowHeight/1080)
        })
    };

    var showErrorMsg = function($pos, msg){
        $mask.hide().insertAfter($pos).text(msg);
        $mask.click(function(){
            $(this).fadeOut();
            $pos.select();
        }).fadeIn();
    };

    var signIN = function(){
        $mask.hide();
        var user = $username.val();
        var pw = $password.val();
        //用户名为空
        if(FR.isEmpty(user)){
            showErrorMsg($username,FR.i18nText('FS-User-User_Can_Not_Be_Null'));
            return;
        }
        //密码为空
        if(FR.isEmpty(pw)){
            showErrorMsg($password,FR.i18nText('FS-Admin-Password_Can_Not_Be_Null'));
            return;
        }
        FR.ajax({
            url : FR.servletURL + '?op=fs_load&cmd=login',
            data : FR.cjkEncodeDO({
                fr_username : encodeURIComponent(user),
                fr_password : encodeURIComponent(pw),
                fr_remember : $keep.hasClass('fs-login-remember-selected')
            }),
            type : 'POST',
            async : false,
            error : function() {
                FR.Msg.toast("Error!");
            },
            complete : function(res, status) {
                if (res.responseText == "") {
                    showErrorMsg($username,FR.i18nText('FS-Admin-Authentication_failed'));
                    return;
                }
                var signResult = FR.jsonDecode(res.responseText);
                if (signResult.fail) {
                    //用户名和密码不匹配
                    showErrorMsg($username,FR.i18nText("FS-Generic-Privilege_Name_Not_Match_Password"));
                } else if (signResult.url) {
                    window.location.href = signResult.url;
                }
            }
        });
    };
    initBackgroundImage();
    $username.focus();
    $(window).resize(function(){
        calcBackgroundScale();
        $('img.fs-login-img').css({
            "margin-left": "-" + imgOffsetX +"px",
            "margin-top": "-" + imgOffsetY +"px",
            width: loginImgWidth * scale + "px",
            height: loginImgHeight * scale + "px"
        });
    });
});