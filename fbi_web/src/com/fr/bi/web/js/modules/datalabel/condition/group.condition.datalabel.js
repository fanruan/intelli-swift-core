/**
 * Created by lfhli on 2016/7/15.
 */
BI.DataLabelConditionGroup = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        var conf = BI.DataLabelConditionGroup.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "data-label-group"
        });
    },

    _init: function () {
        BI.DataLabelConditionGroup.superclass._init.apply(this, arguments);
        var o = this.options;
        this.wId = BI.Utils.getWidgetIDByDimensionID(o.dId);
        this.chartType = BI.Utils.getWidgetTypeByID(this.wId);
        var imageSet = this._createImageSet();
        this.buttonGroup = BI.createWidget({
            type: "bi.button_group",
            cls: "",
            element: this.element,
            items: o.items,
            layouts: [{
                type: "bi.vertical"
            }]
        });

        this.buttons = this.buttonGroup.getAllButtons();
    },

    _createImageSet: function () {
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
                if (self._img.length < 14) {
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
                        self.fireEvent(BI.DataLabelImageSet.IMAGE_CHANGE, arguments);
                    });
                    self.imageGroup.prependItems([button]);
                    self.refreshImg();
                    self.fireEvent(BI.DataLabelImageSet.IMAGE_CHANGE, arguments);
                }
            });
        });
        return BI.createWidget({
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
            items: this.convert2Images(this._img),
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
        var self = this, result = [];
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
                self.fireEvent(BI.DataLabelImageSet.IMAGE_CHANGE, arguments);
            });
            result.push(button)
        });
        return result;
    },

    addItem: function () {
        var o = this.options, type;
        if (this.chartType === BICst.WIDGET.SCATTER) {
            type = "bi.scatter_no_type_field_filter_item";
        } else if (this.chartType === BICst.WIDGET.BUBBLE) {
            type = "bi.bubble_no_type_field_filter_item";
        } else {
            type = "bi.data_label_no_type_field_filter_item";
        }
        var item = {
            type: type,
            sdId: o.dId,
            chartType: this.chartType
        };
        this.buttonGroup.addItems([item]);
        this.buttons = this.buttonGroup.getAllButtons();
    },

    populate: function () {
        var self = this, o = this.options, conditions;
        if (this.chartType === BICst.WIDGET.SCATTER || this.chartType === BICst.WIDGET.BUBBLE) {
            conditions = BI.Utils.getDatalabelByWidgetID(this.wId)
        } else {
            conditions = BI.Utils.getDatalabelByID(o.dId);
        }
        var items = [];
        BI.each(conditions, function (idx, cdt) {
            var t = {};
            switch (self.chartType) {
                case BICst.WIDGET.SCATTER:
                    if (cdt.key === "z") {
                        t = {
                            type: "bi.scatter_no_type_field_filter_item"
                        };
                    } else {
                        t = BI.ScatterFilterItemFactory.createFilterItemByFilterType(cdt.filter_type);
                    }
                    break;
                case BICst.WIDGET.BUBBLE:
                    t = BI.BubbleFilterItemFactory.createFilterItemByFilterType(cdt.filter_type);
                    break;
                default:
                    t = BI.DataLabelFilterItemFactory.createFilterItemByFilterType(cdt.filter_type);
            }
            items.push({
                type: t.type,
                sdId: o.dId,
                chartType: self.chartType,
                key: cdt.key,
                dId: cdt.target_id,
                filter_type: cdt.filter_type,
                filter_value: cdt.filter_value,
                filter_range: cdt.filter_range,
                style_setting: cdt.style_setting
            });
        });
        this.buttonGroup.addItems(items);
        this.buttons = this.buttonGroup.getAllButtons();
    },

    getValue: function () {
        var result = [];
        BI.each(this.buttons, function (i, el) {
            var value = el.getValue();
            if (!BI.isEmptyString(value)) {
                result.push(value);
            }
        });
        return {
            filterValues: result,
            images: this.images
        };
    }
});
BI.DataLabelConditionGroup.EVENT_CHANGE = "BI.DataLabelConditionGroup.EVENT_CHANGE";
$.shortcut("bi.data_label_condition_group", BI.DataLabelConditionGroup);