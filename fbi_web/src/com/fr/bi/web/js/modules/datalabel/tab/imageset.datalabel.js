/**
 * Created by Fay on 2016/7/7.
 */
BI.DataLabelImageSet = BI.inherit(BI.Widget, {
    _defaultImg: [],

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
        this.wId = BI.Utils.getWidgetIDByDimensionID(o.dId);
        this._img = BI.Utils.getImagesByID(this.wId);
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
                cls: "image-set-tab-item",
                height: 30
            }, {
                type: "bi.single_select_item",
                text: BI.i18nText("BI-Custom_Image"),
                value: 2,
                cls: "image-set-tab-item",
                height: 30
            }],
            width: 380,
            height: 30,
            layouts: [{
                type: "bi.left_vertical_adapt",
                items: [{
                    el: {
                        type: "bi.horizontal"
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
                top: 115
            }],
            width: 380,
            height: 145
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
            text: BI.i18nText("BI-Added"),
            cls: "header-label"
        });
        var headerButton = BI.createWidget({
            type: "bi.button",
            cls: "button-ignore",
            text: BI.i18nText("BI-Upload_Image"),
            width: 70,
            height: 26,
            hgap: 5
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
                if (self._img && self._img.length < 14) {
                    var button = BI.createWidget({
                        type: "bi.data_label_image_button",
                        src: src,
                        width: 50,
                        height: 35,
                        iconWidth: 14,
                        iconHeight: 14
                    });
                    button.on(BI.DataLabelImageButton.EVENT_CHANGE, function (src) {
                        self._imageSelect = src;
                        self.fireEvent(BI.DataLabelImageSet.EVENT_CHANGE, arguments);
                    });
                    button.on(BI.DataLabelImageButton.DELETE_IMAGE, function () {
                        self.refreshImg();
                        BI.Broadcasts.send(BICst.BROADCAST.IMAGE_CHANGE_PREFIX + self.wId, self._img);
                    });
                    self.imageGroup.prependItems([button]);
                    self.refreshImg();
                    BI.Broadcasts.send(BICst.BROADCAST.IMAGE_CHANGE_PREFIX + self.wId, self._img);
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
            width: 380,
            height: 35
        });
        return header;
    },

    _createDefaultImgs: function () {
        var self = this, result = [];
        BI.each(this._defaultImg, function (i, item) {
            var img = {
                type: "bi.image_button",
                width: 50,
                height: 50,
                handler: function () {
                    self._imageSelect = this.getSrc();
                    self.fireEvent(BI.DataLabelImageSet.EVENT_CHANGE, arguments);
                }
            };
            img.src = item;
            result.push(img);
        });
        return BI.createWidget({
            type: "bi.inline",
            cls: "image-group",
            items: result,
            hgap: 2,
            tgap: 5
        });
    },

    _createImgs: function () {
        this.imageGroup = BI.createWidget({
            type: "bi.button_group",
            cls: "image-group",
            items: this.convert2Images(BI.Utils.getImagesByID(this.wId)),
            width: 380,
            layouts: [{
                type: "bi.inline",
                hgap: 2,
                vgap: 2
            }]
        });
        return this.imageGroup;
    },

    convert2Images: function (items) {
        var self = this, o = this.options, result = [];
        BI.each(items, function (i, item) {
            var button = BI.createWidget({
                type: "bi.data_label_image_button",
                src: item,
                width: 50,
                height: 35,
                iconWidth: 14,
                iconHeight: 14
            });
            button.on(BI.DataLabelImageButton.EVENT_CHANGE, function (src) {
                self._imageSelect = src;
                self.fireEvent(BI.DataLabelImageSet.EVENT_CHANGE, arguments);
            });
            button.on(BI.DataLabelImageButton.DELETE_IMAGE, function () {
                self.refreshImg();
                BI.Broadcasts.send(BICst.BROADCAST.IMAGE_CHANGE_PREFIX + self.wId, self._img);
            });
            result.push(button)
        });
        return result;
    },

    refreshImg: function () {
        var self = this;
        this._img = [];
        BI.each(self.imageGroup.getAllButtons(), function (i, image) {
            self._img.push(image.getSrc());
        });
    },

    populate: function () {
        var img = BI.Utils.getImagesByID(this.wId);
        if(!BI.isEqual(this._img, img)) {
            this._img = img;
            this.imageGroup && this.imageGroup.populate(this.convert2Images(this._img));
        }
    },

    setValue: function (v) {
        v || (v = {});
        this._imageSelect = v.src || "";
    },

    getValue: function () {
        return {
            src: this._imageSelect
        };
    }
});
BI.DataLabelImageSet.EVENT_CHANGE = "BI.DataLabelImageSet.EVENT_CHANGE";
$.shortcut("bi.data_label_image_set", BI.DataLabelImageSet);