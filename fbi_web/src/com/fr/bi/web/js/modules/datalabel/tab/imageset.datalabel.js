/**
 * Created by Fay on 2016/7/7.
 */
BI.DataLabelImageSet = BI.inherit(BI.Widget, {
    _defaultImg: [],

    _img: [],

    _imageSelect: "",

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
                type: "bi.single_select_item",
                text: BI.i18nText("BI-Default_Image"),
                value: 1,
                cls: "image-set-tab-item"
            }, {
                type: "bi.single_select_item",
                text: BI.i18nText("BI-Custom_Image"),
                value: 2,
                cls: "image-set-tab-item"
            }],
            width: 380,
            height: 24,
            layouts: [{
                type: "bi.left_vertical_adapt",
                items: [{
                    el: {
                        type: "bi.horizontal",
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
            width: 380,
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
        this.imgs = this._createDefaultImgs();
        return BI.createWidget({
            type: "bi.vertical",
            items: [this.imgs]
        })
    },

    _createPanelTwo: function () {
        var header = this._createHeader();
        this.imgs = this._createImgs();
        return BI.createWidget({
            type: "bi.vertical",
            items: [header, {
                el: this.imgs
            }]
        })
    },

    _createHeader: function () {
        var self = this, o = this.options;
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
        image.on(BI.MultifileEditor.EVENT_CHANGE, function () {
            this.upload();
        });
        image.on(BI.MultifileEditor.EVENT_UPLOADED, function () {
            var files = this.getValue();
            var file = files[files.length - 1];
            var attachId = file.attach_id, fileName = file.filename;
            var src = FR.servletURL + "?op=fr_bi&cmd=get_uploaded_image&image_id=" + attachId + "_" + fileName;
            BI.requestAsync("fr_bi_dezi", "save_upload_image", {
                attach_id: attachId
            }, function () {
                if (self._img.length < 14) {
                    self._img.push(src);
                    self.populate();
                    self.tabs.setSelect(2);
                }
            })
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
            width: 380,
            height: 30
        });
        return header;
    },

    _createDefaultImgs: function () {
        var self = this, tmp = [], result= [];
        BI.each(this._defaultImg, function (i, item) {
            var img = {
                type: "bi.image_button",
                width: 50,
                height: 48,
                handler: function () {
                    self._imageSelect = this.getSrc();
                    self.fireEvent(BI.DataLabelImageSet.EVENT_CHANGE, arguments);
                }
            };
            if(!tmp[BI.parseInt(i/7)]) {
                tmp[BI.parseInt(i/7)] = []
            }
            img.src = item;
            tmp[BI.parseInt(i/7)].push(img);
        });
        BI.each(tmp, function (i, item) {
            result.push({
                type: "bi.horizontal",
                items: item,
                hgap: 2
            })
        });
        return BI.createWidget({
            type: "bi.vertical",
            items: result,
            tgap: 3
        });
    },

    _createImgs: function () {
        this.imageGroup = BI.createWidget({
            type: "bi.vertical",
            items: this.convert2Images(this._img),
            tgap: 3
        });
        return this.imageGroup;
    },

    convert2Images: function (items) {
        var self = this, result = [], tmp = [];
        var img = {
            type: "bi.image_button",
            width: 50,
            height: 32,
            handler: function () {
                self._imageSelect = this.getSrc();
                self.fireEvent(BI.DataLabelImageSet.EVENT_CHANGE, arguments);
            }
        };
        var icon = {
            type: "bi.icon_button",
            cls: "image-set-delete close-font",
            width: 14,
            height: 14
        };
        BI.each(items, function (i, item) {
            if(!tmp[BI.parseInt(i/7)]) {
                tmp[BI.parseInt(i/7)] = []
            }
            img.src = item;
            icon.handler = function (i) {
                self._img.splice(i, 1);
                self.populate();
                self.tabs.setSelect(2);
            };
            var iconButton = BI.createWidget(icon);
            iconButton.setVisible(false);
            var button = BI.createWidget({
                type: "bi.absolute",
                cls: "image-button",
                items: [{
                    el: img
                }, {
                    el: iconButton,
                    right: 0
                }],
                width: 50,
                height: 32
            });
            button.element.hover(function () {
                iconButton.setVisible(true);
            }, function () {
                iconButton.setVisible(false);
            });
            tmp[BI.parseInt(i/7)].push(button)
        });
        BI.each(tmp, function (i, item) {
            result.push({
                type: "bi.horizontal",
                items: item,
                hgap: 2
            })
        });
        return result;
    },

    populate: function () {
        this.empty();
        this._createTab();
    },

    setValue: function (v) {
        v || (v = {});
        this._img = v.urls || [];
        this._imageSelect = v.src || "";
    },

    getValue: function () {
        return {
            src: this._imageSelect,
            urls: this._img
        };
    }
});
BI.DataLabelImageSet.EVENT_CHANGE = "BI.DataLabelImageSet.EVENT_CHANGE";
$.shortcut("bi.data_label_image_set", BI.DataLabelImageSet);