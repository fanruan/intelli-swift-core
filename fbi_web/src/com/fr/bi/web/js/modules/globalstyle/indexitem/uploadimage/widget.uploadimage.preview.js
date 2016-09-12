/**
 * Created by Young's on 2016/9/6.
 */
BI.UploadImagePreview = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.UploadImagePreview.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-upload-image-preview"
        })
    },

    _init: function () {
        BI.UploadImagePreview.superclass._init.apply(this, arguments);
        var self = this;

        this.tab = BI.createWidget({
            type: "bi.tab",
            element: this.element,
            cardCreator: function (v) {
                switch (v) {
                    case BI.UploadImagePreview.TO_UPLOAD:
                        var fileUpload = BI.createWidget({
                            type: "bi.multifile_editor",
                            title:BI.i18nText("BI-Upload_Image"),
                            accept: "*.jpg;*.png;*.gif;*.bmp;*.jpeg;",
                            maxSize: 1024 * 1024 * 100,
                            width: 30,
                            height: 30
                        });
                        self._bindUploadEvents(fileUpload);
                        return BI.createWidget({
                            type: "bi.absolute",
                            items: [{
                                el: {
                                    type: "bi.icon_button",
                                    cls: "upload-image-button img-upload-font",
                                    width: 30,
                                    height: 30
                                }
                            }, {
                                el: fileUpload,
                                top: 0
                            }, {
                                el: {
                                    type: "bi.label",
                                    text: ".jpg/.png/.gif/.bmp",
                                    cls: "support-files",
                                    height: 25
                                },
                                top: 15,
                                left: 40
                            }],
                            width: 30,
                            height: 30
                        });
                    case BI.UploadImagePreview.UPLOADED:
                        self.previewArea = BI.createWidget({
                            type: "bi.layout",
                            cls: "preview-area",
                            width: 30,
                            height: 30
                        });
                        var modifyFile = BI.createWidget({
                            type: "bi.multifile_editor",
                            accept: "*.jpg;*.png;*.gif;*.bmp;*.jpeg;",
                            maxSize: 1024 * 1024 * 100,
                            width: 30,
                            height: 30
                        });
                        self._bindUploadEvents(modifyFile);

                        var removeFile = BI.createWidget({
                            type: "bi.text_button",
                            text: BI.i18nText("BI-Delete"),
                            cls: "remove-button",
                            height: 25
                        });
                        removeFile.on(BI.TextButton.EVENT_CHANGE, function () {
                            self._removeFile();
                        });

                        return BI.createWidget({
                            type: "bi.absolute",
                            items: [{
                                el: self.previewArea,
                                top: 0,
                                left: 0
                            }, {
                                el: {
                                    type: "bi.label",
                                    text: BI.i18nText("BI-Modify"),
                                    height: 25,
                                    cls: "modify-button"
                                },
                                top: -10,
                                left: 40
                            }, {
                                el: modifyFile,
                                top: -10,
                                left: 40
                            }, {
                                el: removeFile,
                                top: -10,
                                left: 120
                            }, {
                                el: {
                                    type: "bi.label",
                                    text: ".jpg/.png/.gif/.bmp",
                                    cls: "support-files",
                                    height: 25
                                },
                                top: 15,
                                left: 40
                            }]
                        })
                }
            }
        });
        this.tab.setSelect(BI.UploadImagePreview.TO_UPLOAD);
    },

    _bindUploadEvents: function (widget) {
        var self = this;
        widget.on(BI.MultifileEditor.EVENT_CHANGE, function () {
            this.upload();
        });
        widget.on(BI.MultifileEditor.EVENT_UPLOADED, function () {
            var files = this.getValue();
            var file = files[files.length - 1];
            self.attachId = file.attach_id;
            var fileName = file.filename;
            BI.requestAsync("fr_bi_dezi", "save_upload_image", {
                attach_id: self.attachId
            }, function () {
                self.tab.setSelect(BI.UploadImagePreview.UPLOADED);
                self.imageURL = FR.servletURL + "?op=fr_bi&cmd=get_uploaded_image&image_id=" + self.attachId + "_" + fileName;
                self.previewArea.element.css({
                    background: "url(" + self.imageURL + ")",
                    backgroundSize: "100%"
                });
            });
        });
    },

    _removeFile: function () {
        //delete this.imageId;
        this.imageURL = "";
        this.tab.setSelect(BI.UploadImagePreview.TO_UPLOAD);
    },

    getValue: function () {
        return this.imageURL;
    },

    setValue: function (imageURL) {
        this.imageURL = imageURL;
        if (BI.isNotNull(imageURL) && (imageURL != "")) {
            this.tab.setSelect(BI.UploadImagePreview.UPLOADED);
            this.previewArea.element.css({
                background: "url(" + imageURL + ")",
                backgroundSize: "100%"
            });
        } else {
            this.tab.setSelect(BI.UploadImagePreview.TO_UPLOAD);
        }
    }
});
BI.extend(BI.UploadImagePreview, {
    TO_UPLOAD: 1,
    UPLOADED: 2
});
$.shortcut("bi.upload_image_preview", BI.UploadImagePreview);
