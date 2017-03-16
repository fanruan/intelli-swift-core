/**
 * 决策平台所使用的对话框控件，不同于常见的对话框，该控件没有标题栏
 *
 *     @example
 *     var dialog = FS.Dialog({
     *         width : 400,
     *         height : 500
     *     });
 *     dialog.setVisible(true);
 *     // 也可以采用下面的方式调用
 *     var anotherDialog = FR.createWidget({type : 'fs.dialog', width : 400, height : 500});
 *     anotherDialog.setVisible(true);
 *
 * @class FS.Dialog
 * @extends FR.Widget
 */
FS.Dialog = BI.inherit(BI.Widget, {

    constants: {
        buttonHeight: 30,
        buttonWidth: 90,
        buttonPaddingBottom: 20,
        buttonPaddingRightMin: 20,
        buttonPaddingRightMax: 120,
        southHeight: 60,
        titleHeight: 50,
        titleGap: 20
    },
    _defaultConfig: function () {
        return $.extend(FS.Dialog.superclass._defaultConfig.apply(this, arguments), {
            baseCls: 'fs-dialog',
            size: {
                width: 400,
                height: 360
            },
            title: null,
            content: null,
            onDialogOK: FR.emptyFn(),
            onDialogCancel: FR.emptyFn()
        });
    },
    _init: function () {
        FS.Dialog.superclass._init.apply(this, arguments);
        var opts = this.options, self = this;
        this.$mask = $('<div>').css({
            position: 'absolute',
            'zIndex': 99998,
            backgroundColor: 'grey',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            opacity: 0.5
        }).appendTo('body');
        this.element.css({
            position: 'absolute',
            zIndex: 99999,
            left: 0,
            top: 0,
            right: 0,
            bottom: 0
        }).appendTo('body');
        var self = this, center = this.createContent(opts.content), title = this.createTitle(opts.title),
            south = this.createButton(opts);
        var conf = {
            type: 'bi.center_adapt',
            element: this.element,
            items: [
                {
                    type: 'bi.border',
                    cls: 'fs-dialog-content',
                    items: {
                        center: center,
                        north: title,
                        south: south
                    },
                    width: opts.size.width,
                    height: opts.size.height
                }
            ]
        };
        this.layout = BI.createWidget(conf);
    },

    createContent: function (content) {
        return content || {
                el: {
                    type: 'bi.layout',
                    cls: 'fs-dialog-content'
                }
            }
    },

    createTitle: function (title) {
        var self = this;
        return {
            el: {
                type: 'fs.label',
                text: title,
                cls: 'fs-dialog-title',
                textAlign: 'left',
                whiteSpace: 'normal',
                hgap: 20,
                height: 50
            },
            height: 50
        }
    },
    createButton: function (opts) {
        var self = this;
        return {
            el: {
                type: 'bi.absolute',
                cls: this.clsForControl(),
                items: [
                    {
                        el: {
                            type: 'fs.button',
                            widgetName: "shareTemplateButton",
                            text: BI.i18nText("BI-OK"),
                            width: 90,
                            height: 30,
                            handler: function () {
                                self.destroy();
                                if (BI.isFunction(opts.onDialogOK)) {
                                    opts.onDialogOK.apply(self, [true, opts]);
                                }
                            }
                        },
                        right: 20,
                        bottom: 20
                    },
                    {
                        el: {
                            type: 'fs.button',
                            text: BI.i18nText("BI-Cancel"),
                            level: 'ignore',
                            width: 90,
                            height: 30,
                            handler: function () {
                                self.destroy();
                                if (BI.isFunction(opts.onDialogCancel)) {
                                    opts.onDialogCancel.apply(null, [false]);
                                }
                            }
                        },
                        right: 120,
                        bottom: 20
                    }
                ]

            },
            height: 60
        }
    },
    clsForControl: function () {
        return '';
    },
    destroy: function () {
        FS.Dialog.superclass.destroy.apply(this, arguments);
        this.$mask.remove();
    }
});
$.shortcut('fs.dialog', FS.Dialog);
FS.DialogTitle = BI.inherit(FR.Widget, {

    _defaultConfig: function () {
        return $.extend(FS.DialogTitle.superclass._defaultConfig.apply(this, arguments), {
            baseCls: 'fs-dialog-title',
            text: null,
            height: 50
        });
    },
    _init: function () {
        FS.DialogTitle.superclass._init.apply(this, arguments);
        var opts = this.options, self = this;
        this.element.css({
            position: 'absolute',
            top: 0,
            left: 0,
            bottom: 0,
            right: 0,
            lineHeight: opts.height + 'px'
        }).text(opts.text);
    }
});
$.shortcut('fs.dialog.title', FS.DialogTitle);