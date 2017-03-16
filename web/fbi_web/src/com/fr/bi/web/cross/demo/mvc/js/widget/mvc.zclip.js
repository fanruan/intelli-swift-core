ZclipView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(ZclipView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-zclip bi-mvc-layout"
        })
    },

    _init: function () {
        ZclipView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var editor = BI.createWidget({
            type: "bi.text_editor",
            width: 200,
            height: 30,
            value: "这是复制的内容"
        });
        var zclip = BI.createWidget({
            type: 'bi.zero_clip',
            width: 100,
            height: 100,
            cls: 'layout-bg1',
            copy: function () {
                return editor.getValue();
            },

            afterCopy: function () {
                BI.Msg.toast(editor.getValue());
            }
        });

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: editor,
                left: 100,
                top: 50,
            }, {
                el: zclip,
                left: 100,
                top: 100
            }]
        })
    }
});

ZclipModel = BI.inherit(BI.Model, {});