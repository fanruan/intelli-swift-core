/**
 * Created by fay on 2016/8/22.
 */
BI.DataLabelMethods = {
    showDataLabel: function (data, options) {
        var o = options;
        var hasSeries = false;
        BI.each(BI.Utils.getWidgetViewByID(o.wId)[20000], function (idx, dId) {
            if (BI.Utils.isDimensionUsable(dId)) {
                hasSeries = true
            }
        });
        BI.each(BI.Utils.getAllUsableTargetDimensionIDs(o.wId), function (idx, dId) {
            BI.each(BI.Utils.getDatalabelByID(dId), function (id, dataLabel) {
                if (BI.isEmpty(dataLabel.target_id)) {
                    return;
                }
                if (hasSeries && BI.Utils.getWidgetViewByID(o.wId)[20000].indexOf(dataLabel.target_id) != -1) { //有系列过滤
                    BI.each(data, function (i, item) {
                        filterForSeries(item, dataLabel);
                    });
                } else {
                    BI.each(data, function (i, item) {
                        BI.each(item.data, function (k, dt) {
                            filterForSelf(dt, dataLabel);
                            filterForClassify(dt, dataLabel);
                        })
                    });
                }
            })
        });
        return data;

        function filterForSelf(data, label) {
            switch (label.filter_type) {
                case BICst.DIMENSION_FILTER_NUMBER.BELONG_VALUE:
                    var min = label.filter_value.closemin ? data.y >= label.filter_value.min : data.y > label.filter_value.min;
                    var max = label.filter_value.closemax ? data.y <= label.filter_value.max : data.y < label.filter_value.max;
                    if (min && max) {
                        createDataLabel(data, label);
                    }
                    break;
                case BICst.DIMENSION_FILTER_NUMBER.NOT_BELONG_VALUE:
                    var min = label.filter_value.closemin ? data.y < label.filter_value.min : data.y <= label.filter_value.min;
                    var max = label.filter_value.closemax ? data.y > label.filter_value.max : data.y >= label.filter_value.max;
                    if (min || max) {
                        createDataLabel(data, label);
                    }
                    break;
                case BICst.TARGET_FILTER_NUMBER.EQUAL_TO:
                    if (data.y == label.filter_value) {
                        createDataLabel(data, label);
                    }
                    break;
                case BICst.TARGET_FILTER_NUMBER.NOT_EQUAL_TO:
                    if (data.y != label.filter_value) {
                        createDataLabel(data, label);
                    }
                    break;
                case BICst.DIMENSION_FILTER_NUMBER.IS_NULL:
                    if (data.y == null) {
                        createDataLabel(data, label);
                    }
                    break;
                case BICst.DIMENSION_FILTER_NUMBER.NOT_NULL:
                    if (data.y != null) {
                        createDataLabel(data, label);
                    }
                    break;
                case BICst.TARGET_FILTER_NUMBER.LARGE_OR_EQUAL_CAL_LINE:
                    break;
                case BICst.TARGET_FILTER_NUMBER.SMALL_THAN_CAL_LINE:
                    break;
                case BICst.DIMENSION_FILTER_NUMBER.TOP_N:
                    break;
            }
        }

        function filterForClassify(data, label) {
            switch (label.filter_type) {
                case BICst.DIMENSION_FILTER_STRING.BELONG_VALUE:
                    if (!label.filter_value.value || label.filter_value.value.indexOf(data.value) != -1) {
                        createDataLabel(data, label);
                    }
                    break;
                case BICst.DIMENSION_FILTER_STRING.NOT_BELONG_VALUE:
                    if (label.filter_value.value && label.filter_value.value.indexOf(data.value) == -1) {
                        createDataLabel(data, label);
                    }
                    break;
                case BICst.DIMENSION_FILTER_STRING.CONTAIN:
                    if (!label.filter_value || data.value.indexOf(label.filter_value) != -1 && label.filter_value) {
                        createDataLabel(data, label);
                    }
                    break;
                case BICst.DIMENSION_FILTER_STRING.NOT_CONTAIN:
                    if (!label.filter_value || data.value.indexOf(label.filter_value) == -1) {
                        createDataLabel(data, label);
                    }
                    break;
                case BICst.DIMENSION_FILTER_STRING.IS_NULL:
                    if (data.value == null) {
                        createDataLabel(data, label);
                    }
                    break;
                case BICst.DIMENSION_FILTER_STRING.NOT_NULL:
                    if (data.value != null) {
                        createDataLabel(data, label);
                    }
                    break;
                case BICst.DIMENSION_FILTER_STRING.BEGIN_WITH:
                    if (!label.filter_value || data.value.startWith(label.filter_value)) {
                        createDataLabel(data, label);
                    }
                    break;
                case BICst.DIMENSION_FILTER_STRING.END_WITH:
                    if (!label.filter_value || data.value.endWith(label.filter_value || "")) {
                        createDataLabel(data, label);
                    }
                    break;
            }
        }

        function filterForSeries(data, label) {
            switch (label.filter_type) {
                case BICst.DIMENSION_FILTER_STRING.BELONG_VALUE:
                    if (!label.filter_value.value || label.filter_value.value.indexOf(data.name) != -1) {
                        BI.each(data.data, function (idx, item) {
                            createDataLabel(item, label);
                        })
                    }
                    break;
                case BICst.DIMENSION_FILTER_STRING.NOT_BELONG_VALUE:
                    if (label.filter_value.value && label.filter_value.value.indexOf(data.name) == -1) {
                        BI.each(data.data, function (idx, item) {
                            createDataLabel(item, label);
                        })
                    }
                    break;
                case BICst.DIMENSION_FILTER_STRING.CONTAIN:
                    if (!label.filter_value || data.name.indexOf(label.filter_value) != -1 && label.filter_value) {
                        BI.each(data.data, function (idx, item) {
                            createDataLabel(item, label);
                        })
                    }
                    break;
                case BICst.DIMENSION_FILTER_STRING.NOT_CONTAIN:
                    if (!label.filter_value || data.name.indexOf(label.filter_value) == -1) {
                        BI.each(data.data, function (idx, item) {
                            createDataLabel(item, label);
                        })
                    }
                    break;
                case BICst.DIMENSION_FILTER_STRING.IS_NULL:
                    if (data.name == null) {
                        BI.each(data.data, function (idx, item) {
                            createDataLabel(item, label);
                        })
                    }
                    break;
                case BICst.DIMENSION_FILTER_STRING.NOT_NULL:
                    if (data.name != null) {
                        BI.each(data.data, function (idx, item) {
                            createDataLabel(item, label);
                        })
                    }
                    break;
                case BICst.DIMENSION_FILTER_STRING.BEGIN_WITH:
                    if (!label.filter_value || data.name.startWith(label.filter_value)) {
                        BI.each(data.data, function (idx, item) {
                            createDataLabel(item, label);
                        })
                    }
                    break;
                case BICst.DIMENSION_FILTER_STRING.END_WITH:
                    if (!label.filter_value || data.name.endWith(label.filter_value || "")) {
                        BI.each(data.data, function (idx, item) {
                            createDataLabel(item, label);
                        })
                    }
                    break;
            }
        }

        function createDataLabel(data, label) {
            var dataLables = {
                enabled: true,
                align: "outside",
                useHtml: true,
                style: {},
                formatter: "function(){return '<div>"+data.y+"</div>'}"
            };
            switch (label.style_setting.type) {
                case BICst.DATA_LABEL_STYLE_TYPE.TEXT:
                    dataLables.style = label.style_setting.textStyle;
                    break;
                case BICst.DATA_LABEL_STYLE_TYPE.IMG:
                    dataLables.formatter = "function(){return '<div><img width=\"20px\" height=\"20px\" src=\""+label.style_setting.imgStyle.src+"\"></div>';}";
                    break;
            }
            data.dataLabels =  dataLables;
        }

        function createDataImage(data, label) {
            data.image = label.style_setting.imgStyle.src;
        }
    }
};