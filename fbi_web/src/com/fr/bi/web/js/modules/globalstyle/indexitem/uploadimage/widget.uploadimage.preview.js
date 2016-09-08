/**
 * Created by Young's on 2016/9/6.
 */
BI.UploadImagePreview = BI.inherit(BI.Widget, {
    _defaultConfig: function() {
        return BI.extend(BI.UploadImagePreview.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-upload-image-preview"
        })
    },

    _init: function() {
        BI.UploadImagePreview.superclass._init.apply(this, arguments);
        var self = this;

        var tab = BI.createWidget({
            type: "bi.tab",
            element: this.element,
            cardCreator: function(v) {
                switch (v) {
                    case BI.UploadImagePreview.TO_UPLOAD:
                        var fileUpload = BI.createWidget({
                            type: "bi.multifile_editor",
                            accept: "*.jpg;*.png;*.gif;*.bmp;*.jpeg;",
                            maxSize: 1024 * 1024 * 100,
                            width: 30,
                            height: 30
                        });
                        fileUpload.on(BI.MultifileEditor.EVENT_CHANGE, function() {
                            tab.setSelect(BI.UploadImagePreview.UPLOADED);
                        });
                        fileUpload.on(BI.MultifileEditor.EVENT_UPLOADED, function() {
                            tab.setSelect(BI.UploadImagePreview.UPLOADED);
                        });
                        return BI.createWidget({
                            type: "bi.absolute",
                            items: [{
                                el: {
                                    type: "bi.icon_button",
                                    cls: "img-upload-font",
                                    width: 30,
                                    height: 30
                                }
                            }, {
                                el: fileUpload,
                                top: -10
                            }, {
                                el: {
                                    type: "bi.label",
                                    text: ".jpg/.png/.gif/.bmp/.jpeg",
                                    cls: "",
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
                        var removeFile = BI.createWidget({
                            type: "bi.text_button",
                            text: BI.i18nText("BI-Delete"),
                            cls: "",
                            height: 25
                        });

                        return BI.createWidget({
                            type: "bi.absolute",
                            items: [{
                                el: self.previewArea,
                                top: -10,
                                left: 0
                            }, {
                                el: {
                                    type: "bi.label",
                                    text: BI.i18nText("BI-Modify"),
                                    height: 25,
                                    cls: ""
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
                                left: 100
                            }, {
                                el: {
                                    type: "bi.label",
                                    text: ".jpg/.png/.gif/.bmp",
                                    cls: "",
                                    height: 25
                                },
                                top: 15,
                                left: 40
                            }]
                        })
                }
            }
        });
        tab.setSelect(BI.UploadImagePreview.TO_UPLOAD);
    },
    
    getValue: function() {
        
    },
    
    setValue: function() {
        
    }
});
BI.extend(BI.UploadImagePreview, {
    TO_UPLOAD: 1,
    UPLOADED: 2
});
$.shortcut("bi.upload_image_preview", BI.UploadImagePreview);
