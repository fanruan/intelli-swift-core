/**
 * 数据标签中的图片上传预览
 * Created by Fay on 2016/7/7.
 */
BI.DataLabelImageSet = BI.inherit(BI.Widget, {
    _defaultImg: [],

    _img: [],

    _defaultConfig: function () {
        var conf = BI.DataLabelImageSet.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "bi-image-set",
            defaultSelect: 1
        });
    },

    _init: function () {
        BI.DataLabelImageSet.superclass._init.apply(this, arguments);
        var o = this.options;
        this._createTab();
        this.tabs.setSelect(o.defaultSelect);
    },

    _createTab: function () {
        var tab = BI.createWidget({
            type: "bi.button_group",
            cls: "image-set-tab",
            items: [{
                text: BI.i18nText("BI-Default_Image"),
                value: 1,
                width: 50,
                cls: "image-set-tab-item"
            }, {
                text: BI.i18nText("BI-Custom_Image"),
                value: 2,
                width: 70,
                cls: "image-set-tab-item"
            }],
            width: 400,
            height: 24,
            layouts: [{
                type: "bi.left_vertical_adapt",
                items: [{
                    el: {
                        type: "bi.horizontal",
                        width: 150,
                        lgap: 6
                    }
                }]
            }]
        });
        this.tabs = BI.createWidget({
            direction: "custom",
            element: this.element,
            type: "bi.tab",
            tab: tab,
            cardCreator: BI.bind(this._createPanel, this)
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: tab,
                left: 0,
                top: 105
            }],
            width: 400,
            height: 130
        })
    },

    _createPanel: function (v) {
        switch (v) {
            case 1:
                return this._createPanelOne();
            case 2:
                return this._createPanelTwo();
        }
    },

    _createPanelOne: function () {
        var imgs = this._createDefaultImgs();
        return BI.createWidget({
            type: "bi.vertical",
            items: [imgs],
            tgap: 2,
            lgap: 3
        })
    },

    _createPanelTwo: function () {
        var header = this._createHeader();
        var imgs = this._createImgs();
        return BI.createWidget({
            type: "bi.vertical",
            items: [header, {
                el: imgs,
                tgap: 2,
                lgap: 3,
            }]
        })
    },

    _createHeader: function () {
        var self = this;
        var headerLabel = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Added")
        });
        var headerButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Upload_Image"),
            width: 70,
            height: 26,
            hgap: 6
        });
        var image = BI.createWidget({
            type: "bi.multifile_editor",
            accept: "*.jpg;*.png;*.gif;"
        });
        headerButton.on(BI.Button.EVENT_CHANGE, function () {
            image.select();
        });
        image.on(BI.MultifileEditor.EVENT_CHANGE, function (data) {
            this.upload();
        });
        image.on(BI.MultifileEditor.EVENT_UPLOADED, function () {
            var files = this.getValue();
            var file = files[files.length - 1];
            var attachId = file.attach_id, fileName = file.filename;
            var src = FR.servletURL + "?op=fr_bi&cmd=get_uploaded_image&image_id=" + attachId + "_" + fileName;
            BI.Utils.saveUploadedImage(attachId, function () {
                if (self._img.length < 14) {
                    self._img.push(src);
                    self._refresh();
                    self.tabs.setSelect(2);
                }
            });
        });
        var header = BI.createWidget({
            type: "bi.center_adapt",
            cls: "image-set-header",
            items: [{
                type: "bi.left",
                items: [headerLabel],
                lgap: 6
            }, {
                type: 'bi.right',
                items: [headerButton],
                rgap: 6
            }],
            width: 400,
            height: 30
        });
        return header;
    },

    _createDefaultImgs: function () {
        var resultImgs = [];
        BI.each(this._defaultImg, function (i, src) {
            var img = {
                column: i % 7,
                row: parseInt(i / 7),
                type: "bi.img",
                src: "",
                width: 50,
                height: "45%"
            };
            img.src = src;
            resultImgs.push(img);
        });
        return BI.createWidget({
            type: "bi.grid",
            cls: "image-set-grid",
            columns: 7,
            rows: 2,
            items: resultImgs,
            width: 400,
            height: 105
        });
    },

    _createImgs: function () {
        var self = this,
            resultImgs = [];
        BI.each(this._img, function (i, src) {
            var img = {
                type: "bi.img",
                src: "",
                width: 50,
                height: "90%"
            };
            img.src = src;
            var set = {
                type: "bi.absolute",
                column: i % 7,
                row: parseInt(i / 7),
                items: [{
                    el: img
                }, {
                    el: {
                        type: "bi.icon_button",
                        cls: "image-set-delete close-font",
                        width: 14,
                        height: 14,
                        handler: function () {
                            self._img.splice(i, 1);
                            self._refresh();
                            self.tabs.setSelect(2);
                        }
                    },
                    right: 0
                }],
                width: 50
            };
            resultImgs.push(set);
        });
        return BI.createWidget({
            type: "bi.grid",
            cls: "image-set-grid",
            columns: 7,
            rows: 2,
            items: resultImgs,
            width: 400,
            height: 75
        });
    },

    _refresh: function () {
        this.empty();
        this._createTab();
    },

    setValue: function () {

    },

    getValue: function () {
        return this._img;
    }
});

BI.DataLabelImageSet.EVENT_CHANGE = "BI.DataLabelImageSet.EVENT_CHANGE";
$.shortcut("bi.data_label_image_set", BI.DataLabelImageSet);