(function ($) {
    /**
     * 自定义用于消息传递的模态对话框类，用于替代浏览器原生的比较丑的alert,prompt,confirm
     *
     *      @example
     *      var showToast = function() {
     *          FR.Msg.toast("Hello");
     *      };
     *      var showConfirm = function() {
     *          FR.Msg.confirm("Confirm", "Content", showToast);
     *      };
     *      var showPrompt = function() {
     *          FR.Msg.prompt("Prompt", "Message", "Value", showConfirm);
     *      };
     *      var showAlert = function() {
     *          FR.Msg.alert("Alert", "Content", showPrompt);
     *      };
     *      $("<button>Click To Test</button>").appendTo("body").on("click", showAlert);
     *
     * @class FR.Msg
     */
    $.extend(FR, {

        Msg: function () {
            return {

                verticalOffset: 0.77,                // vertical offset of the dialog from center screen
                horizontalOffset: 1,                // horizontal offset of the dialog from center screen\
                repositionOnResize: true,           // re-centers the dialog on window resize
                overlayOpacity: .01,                // transparency level of overlay
                overlayColor: '#FFF',               // base color of overlay
                draggable: true,                    // make the dialogs draggable (requires UI Draggables plugin)
                okButton: FR.i18nText("FR-Basic_OK"),         // text for the OK button
                cancelButton: FR.i18nText("FR-Basic_Cancel"), // text for the Cancel button
                dialogClass: null,                  // if specified, this class will be applied to all dialogs
                style:null,                          //message样式，默认为blue，具有blue,green共2种样式
                textSpace : FR.i18nText("FR-Widget_Message_Letter_Space") + "px",


                msgStyle:function(){
                    var style = this.style;
                    var defaultStyle = "blue";
                    $.each(["green", "blue"], function (i, item) {
                        if (style && item == style.toLowerCase()) {
                            defaultStyle = style.toLowerCase();
                        }
                    });
                    this.style = defaultStyle;
                },

                /**
                 * 弹出消息的提示对话框
                 * @static
                 * @param title 对话框的标题
                 * @param message 对话框显示的消息
                 * @param callback 对话框显示以后的回调函数。如果该参数是数字类型，则表明该对话框在指定的时间（单位：毫秒）后自动关闭
                 */
                alert: function (title, message, callback) {
                    FR.Keys.enable(false);
                    if (!title) {
                        title = 'Alert';
                    }
                    this._show(title, message, null, 'alert', function (result) {
                        FR.Keys.enable(true);
                        if ($.isFunction(callback)) {
                            callback(result);
                        }
                    }, -1);
                    if (typeof callback == "number" && callback > 0) {
                        setTimeout(function () {
                            FR.Msg._hide();
                        }, callback);
                    }
                },

                /**
                 * 弹出的确认对话框
                 * @static
                 * @param title  确认对话框的标题
                 * @param message  确认对话框的显示消息
                 * @param callback  点击确认后的回调函数
                 * @param min_width  确认对话框的最小宽度
                 */
                confirm: function (title, message, callback, min_width) {
                    FR.Keys.enable(false);
                    if (!title) {
                        title = 'Confirm';
                    }
                    this._show(title, message, null, 'confirm', function (result) {
                        FR.Keys.enable(true);
                        if (callback) {
                            callback(result);
                        }
                    }, min_width ? min_width : -1);
                },

                /**
                 * 弹出的输入对话框
                 * @static
                 * @param title  输入对话框的标题
                 * @param message  输入对话框的显示消息
                 * @param value   输入对话框的默认值
                 * @param callback 点击确认输入后的回调函数
                 * @param min_width  输入对话框的最小宽度
                 */
                prompt: function (title, message, value, callback, min_width) {
                    FR.Keys.enable(false);
                    if (!title) {
                        title = 'Prompt';
                    }
                    this._show(title, message, value, 'prompt', function (result) {
                        FR.Keys.enable(true);
                        if (callback) {
                            callback(result);
                        }
                    }, min_width ? min_width : -1);
                },

                /**
                 * 出现与页面左边并在5秒后消失的一个消失提示页
                 * @static
                 * @param message 要显示的消息
                 */
                toast: function (message) {
                    var toright = -300;
                    if ($.browser.msie && parseInt($.browser.version) < 8) {
                        toright = -370;
                    }
                    var $dv = $('body').children('div.toast');
                    if ($dv.length === 0) {
                        var top = 60;
                        // carl:ie6不支持fixed, 改用absolute及下面方法模拟
                        if ($.browser.msie && parseInt($.browser.version) <= 6) {
                            top += $('body')[0].scrollTop;
                        }
                        $dv = $("<div/>").addClass('toast').css({
                            right: toright,
                            top: top,
                            'z-Index': FR.widget.opts.zIndex++
                        }).appendTo("body");

                    }
                    $dv.text(message);

                    var $showbutton = $('body').children('div.toastIcon');
                    if ($showbutton.length === 0) {
                        $showbutton = $("<div/>").addClass('toastIcon').css({
                            right: 0,
                            top: $dv.css('top'),
                            'z-index': $dv.css('z-index') + 1
                        }).appendTo("body");
                        new FR.IconButton({
                            width: $showbutton.css('width'),
                            height: $showbutton.css('height'),
                            imgsrc: 'fr_show_toast',
                            renderEl: $('<div/>').appendTo($showbutton),
                            handler: function () {
                                $dv.animate({
                                    right: -4
                                }, "slow");
                                setTimeout(function () {
                                    $dv.animate({
                                        right: toright
                                    }, "slow");
                                }, 5000);
                            }
                        });
                    }
                    $showbutton.hide();

                    $dv.animate({
                        right: -15
                    }, "slow");


                    (function() {
                        $dv.animate({
                            right: toright
                        }, "slow",function () {
                            $showbutton.show();
                            $showbutton.fadeOut(5000);
                        });
                    }).defer(5000);
                },

                // Private methods

                _show: function (title, msg, value, type, callback, min_width) {
                    this.msgStyle();
                    this._hide();
                    this._overlay('show');

                    $("BODY").append(
                        '<div id="popup_container">' +
                            '<div id="popup_header" >' +
                            '<h1 id="popup_title"></h1></div>' +
                            '<div id="popup_content">' +
                            '<div id="popup_message"></div>' +
                            '</div>' +
                            '</div>');

                    if (this.dialogClass) {
                        $("#popup_container").addClass(this.dialogClass);
                    }

                    var pos = ($.browser.msie) ? 'absolute' : 'fixed';

                    $("#popup_container").css({
                        position: pos,
                        zIndex: 99999,
                        padding: 0,
                        margin: 0,
                        top: 0,
                        left: 0
                    });

                    $("#popup_title").html(title);
                    $("#popup_content").addClass(type);
                    $("#popup_message").text(msg);
                    $("#popup_message").html($("#popup_message").text().replace(/\n/g, '<br />'));

                    var minw = $("#popup_container").outerWidth();
                    var maxw = $("#popup_container").outerWidth();
                    $("#popup_container").css({
                        minWidth: min_width > 0 ? Math.max(minw, min_width) : minw,
                        maxWidth: min_width > 0 ? Math.max(maxw, min_width) : maxw
                    });
                    $("<div id = 'popup_close' class = 'close_mouseout' style='position: absolute;top: 0;right: 7px;width: 25px;height: 30px'>").appendTo("#popup_header")
                        .bind("mouseover",function(){
                            $("#popup_close").removeClass('close_mouseout');
                            $("#popup_close").addClass('close_mouseover');
                        })
                        .bind("mouseout",function(){
                            $("#popup_close").removeClass('close_mouseover');
                            $("#popup_close").removeClass('close_click');
                            $("#popup_close").addClass('close_mouseout');
                        })
                        .bind("mousedown",function(){
                            $("#popup_close").removeClass('close_mouseover');
                            $("#popup_close").addClass('close_click');
                        })
                        .bind("click", function () {
                            FR.Msg._hide();
                            if (callback) {
                                callback(null);
                            }
                        });

                    msg = min_width == -1 ? msg :msg.replace(/([\u4E00-\u9FA5\uf900-\ufa2d])/g,'aa');
                    var msgLength = msg ? msg.length : 0;
                    switch (type) {
                        case 'alert':
                            $("#popup_container").css({
                                minWidth:"400px",
                                maxWidth:"400px",
                                width:"400px"
                            });
                            $("#popup_message").css({
                                width:"380px",
                                wordBreak:"break-all",
                                fontSize:"14px",
                                fontWeight:"bold"
                        });
                            var msHeight = $("#popup_message").height();
                            var ctHeight = msHeight + 87;
                            ctHeight = ctHeight > 150 ? ctHeight :150;
                            $("#popup_container").css({
                                height:ctHeight
                            });
                            $("#popup_header").css({
                                width:"400px"
                            });
                            $("#popup_content").css({
                                top:"40px",
                                left:"10px",
                                background:"none",
                                padding:"0px" ,
                                fontSize:"14px",
                                textAlign:"left"
                            });

                            var ok_btn = new FR.createWidget({
                                type:"quickbutton",
                                style:this.style,
                                text:FR.i18nText("FR-Basic_OK"),
                                width:65,
                                height:28,
                                handler:function(){
                                    FR.Msg._hide();
                                    callback(true);
                                }
                            });

                            var $ok_button = ok_btn.element.appendTo("#popup_container").css({
                                position: 'absolute',
                                right:"20px",
                                bottom:"8px",
                                letterSpacing: FR.Msg.textSpace,
                                textIndent: $.browser.msie && document.documentMode < 8 ? 0 : FR.Msg.textSpace
                            });

                            $ok_button.focus();
                            $ok_button.keydown(function (e) {
                                if (e.keyCode === 13 || e.keyCode === 27) {
                                    $ok_button.trigger('click');
                                }
                            });
                            break;
                        case 'confirm':
                            if(msg){
                            if(msgLength < 7){
                                $("#popup_container").css({
                                    minWidth:"233px",
                                    maxWidth:"233px",
                                    width:"233px",
                                    height:"149px"
                                });
                                $("#popup_content").css({
                                    left:"32px",
                                    width:"100px"
                                });
                                $("#popup_header").css({
                                    width:"233px"
                                });
                                $("#popup_message").css({
                                    width:"100px"
                                });
                            }else if(msgLength > 6 && msgLength < 11){
                                $("#popup_container").css({
                                    minWidth:"233px",
                                    maxWidth:"233px",
                                    width:"233px",
                                    height:"149px"
                                });
                                $("#popup_content").css({
                                    left:"33px",
                                    width:"98px",
                                    backgroundPosition:"23px 31px"
                                });
                                $("#popup_header").css({
                                    width:"233px"
                                });
                                $("#popup_message").css({
                                    width:"98px"
                                });
                            }else{
                                var wd = msgLength * 17/2 + 16;
                                 min_width = min_width > 233 ? min_width :233;
                                var cwd = (wd + 100) > min_width ? wd + 100 : min_width;
                                $("#popup_container").css({
                                    minWidth:cwd + "px",
                                    maxWidth:cwd + "px",
                                    width:cwd + "px",
                                    height:"169px"
                                });
                                $("#popup_content").css({
                                    left:"15.5px",
                                    width:wd + "px",
                                    backgroundPosition:"23px 31px"
                                });
                                $("#popup_header").css({
                                    width:cwd + "px"
                                });
                                $("#popup_message").css({
                                    width:wd + "px"
                                });
                            }
                        }else{
                                $("#popup_container").css({
                                    minWidth:"233px",
                                    maxWidth:"233px",
                                    width:"233px",
                                    height:"149px"
                                });
                                $("#popup_header").css({
                                    width:"233px"
                                });
                                $("#popup_content").css({
                                    left:"46px",
                                    top:"45px"
                                });
                            }

                            var containerwd = document.getElementById("popup_container").offsetWidth;
                            var lf = (containerwd - 140)/2 - 1;
                            var ok_btn = new FR.createWidget({
                                type:"quickbutton",
                                style:this.style,
                                text:FR.i18nText("FR-Basic_OK"),
                                width:65,
                                height:28,
                                handler:function(){
                                    FR.Msg._hide();
                                    callback(true);
                                }
                            });
                            var $ok_button = ok_btn.element.appendTo("#popup_container").css({
                                position: 'absolute',
                                left:lf + "px",
                                bottom:"8px",
                                letterSpacing: FR.Msg.textSpace,
                                textIndent: $.browser.msie && document.documentMode < 8 ? 0 : FR.Msg.textSpace
                            });

                            var cancel_btn = new FR.createWidget({
                                type:"quickbutton",
                                style:"gray",
                                text:FR.i18nText("FR-Basic_Cancel"),
                                width:65,
                                height:28,
                                handler:function(){
                                    FR.Msg._hide();
                                    callback(false);
                                }
                            });
                            var $cancel_button = cancel_btn.element.appendTo("#popup_container").css({
                                position: 'absolute',
                                right:lf + "px",
                                bottom:"8px",
                                letterSpacing: FR.Msg.textSpace,
                                textIndent: $.browser.msie && document.documentMode < 8 ? 0 : FR.Msg.textSpace
                            });

                            break;
                        case 'prompt':
                            $("#popup_content").append('<input type="text" size="21" id="popup_prompt" >');
                        if(msg){
                            min_width = min_width > 233 ? min_width :233;
                            var wd =( msgLength * 14 + 15) > min_width - 100 ? (msgLength * 14 + 15) : min_width - 100;
                            var cwd = wd + 100;
                            $("#popup_container").css({
                                minWidth: cwd,
                                maxWidth: cwd,
                                width:cwd
                            });
                            $("#popup_header").css({
                               width:cwd
                            });
                            $("#popup_content").css({
                                left:"50px",
                                top:"45px",
                                width:wd,
                                height:'59px',
                                padding:"0"
                            });
                            $("#popup_message").css({
                                fontSize:"14px",
                                width:wd,
                                textAlign:"left"
                            });
                            }else{
                            $("#popup_container").css({
                                minWidth: "233px",
                                maxWidth: "233px",
                                width:"233px"
                            });
                            $("#popup_header").css({
                                width:"233px"
                            });
                            $("#popup_content").css({
                                left:"50px",
                                top:"52px",
                                width:"130px",
                                height:"59px",
                                padding:"0"
                            });
                            $("#popup_message").css({
                                width:"130px"
                            });
                            }

                            var containerwd = document.getElementById("popup_container").offsetWidth;
                            var lf = (containerwd - 140)/2 - 1;
                            var ok_btn = new FR.createWidget({
                                type:"quickbutton",
                                style:this.style,
                                text:FR.i18nText("FR-Basic_OK"),
                                width:65,
                                height:28,
                                handler:function(){
                                    var val = $("#popup_prompt").val();
                                    FR.Msg._hide();
                                    if (callback) {
                                        callback(val);
                                    }
                                }
                            });
                            var $ok_button = ok_btn.element.appendTo("#popup_container").css({
                                position: 'absolute',
                                left:lf + "px",
                                bottom:"8px",
                                letterSpacing: FR.Msg.textSpace,
                                textIndent: $.browser.msie && document.documentMode < 8 ? 0 : FR.Msg.textSpace
                            });

                            var cancel_btn = new FR.createWidget({
                                type:"quickbutton",
                                style:"gray",
                                text:FR.i18nText("FR-Basic_Cancel"),
                                width:65,
                                height:28,
                                handler:function(){
                                    FR.Msg._hide();
                                    if (callback) {
                                        callback(null);
                                    }
                                }
                            });
                            var $cancel_button = cancel_btn.element.appendTo("#popup_container").css({
                                position: 'absolute',
                                right:lf + "px",
                                bottom:"8px",
                                letterSpacing: FR.Msg.textSpace,
                                textIndent: $.browser.msie && document.documentMode < 8 ? 0 : FR.Msg.textSpace
                            });

                            $("#popup_prompt").width($("#popup_message").width());
                            if (value) {
                                $("#popup_prompt").val(value);
                            }
                            $("#popup_prompt").focus().select();
                            break;
                    }

                    this._reposition();
                    this._maintainPosition(true);

                    // Make draggable
                    if (this.draggable) {
                        try {
                            $("#popup_container").draggable({
                                handle: $("#popup_title"),
                                onStopDrag:function(){
                                    if($("#popup_container").offset().top < 0){
                                        $("#popup_container").css({
                                            top:"0px"
                                        })
                                    }
                                    if($("#popup_container").offset().left < 0){
                                        $("#popup_container").css({
                                            left:"0px"
                                        })
                                    }
                                }
                            });
                            $("#popup_title").css({
                                cursor: 'move'
                            });
                        } catch (e) { /* requires jQuery UI draggables */
                        }
                    }
                },

                _hide: function () {
                    $("#popup_container").remove();
                    this._overlay('hide');
                    this._maintainPosition(false);
                },

                _overlay: function (status) {
                    switch (status) {
                        case 'show':
                            this._overlay('hide');
                            $("BODY").append('<div id="popup_overlay"></div>');
                            $("#popup_overlay").css({
                                position: 'absolute',
                                zIndex: 99998,
                                top: '0px',
                                left: '0px',
                                width: '100%',
                                height: FR.windowHeight,
                                background: this.overlayColor,
                                opacity: this.overlayOpacity
                            });
                            break;
                        case 'hide':
                            $("#popup_overlay").remove();
                            break;
                    }
                },

                _reposition: function () {
                	var top = FR.windowHeight;
                	top = (top - $("#popup_container").outerHeight()) / 2 + this.verticalOffset;
                    top = top * this.verticalOffset;
                    var left = FR.windowWidth;
               		left = (left - $("#popup_container").outerWidth()) / 2 + this.horizontalOffset;
                    left = left * this.horizontalOffset;
                    if (top < 0) {
                        top = 0;
                    }
                    if (left < 0) {
                        left = 0;
                    }

                    // IE fix
                    if ($.browser.msie) {
                        top = top + $(window).scrollTop();
                    }

                    $("#popup_container").css({
                        top: top + 'px',
                        left: left + 'px'
                    });
                    $("#popup_overlay").height(FR.windowHeight);
                },

                _maintainPosition: function (status) {
                    if (this.repositionOnResize) {
                        switch (status) {
                            case true:
                                $(window).bind('resize', function () {
                                    FR.Msg._reposition();
                                });
                                break;
                            case false:
                                $(window).unbind('resize');
                                break;
                        }
                    }
                }
            };
        }()
    });
})(jQuery);