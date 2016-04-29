/**
 * 上传图片控件
 * Created by GameJian on 2016/1/27.
 */
UploadImageView = FR.extend(BI.View,{
    _defaultConfig: function () {
        return BI.extend(UploadImageView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-layout"
        })
    },

    _init: function () {
        UploadImageView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {

        var self = this, o = this.options;

        var textarea = BI.createWidget({
            type: "bi.upload_image",
            cls: "mvc-border",
            invisible: false,
            width: 300,
            height: 300
        });

        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: textarea,
                left: 200,
                top: 150
            }],
            scrollable: false
        })
    }
});

UploadImageModel = BI.inherit(BI.Model, {});