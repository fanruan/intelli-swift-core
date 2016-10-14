import Immutable from 'immutable'
import Dimension from './Dimension'
import Target from './Target'
import {each, invariant, isNil, find, findKey} from 'core';
import {Fetch} from 'lib'
class Widget {
    constructor($widget, $template, wId) {
        this.$widget = $widget;
        this.$template = $template;
        this.wId = wId;
    }

    $get() {
        return this.$widget;
    }

    get$$DimensionById(id) {
        invariant(this.isDimensionById(id), id + "不是维度id");
        return this.$widget.getIn(['dimensions', id]);
    }

    get$$TargetById(id) {
        invariant(this.isTargetById(id), id + "不是指标id");
        return this.$widget.getIn(['dimensions', id])
    }

    get$$DimensionOrTargetById(id) {
        if (this.isDimensionById(id)) {
            return this.get$$DimensionById(id);
        }
        return this.get$$TargetById(id);
    }

    getDimensionById(id) {
        return new Dimension(this.get$$DimensionById(id));
    }

    getTargetById(id) {
        return new Target(this.get$$TargetById(id));
    }

    getDimensionOrTargetById(id) {
        if (this.isDimensionById(id)) {
            return new Dimension(this.get$$DimensionById(id));
        }
        return new Target(this.get$$TargetById(id));
    }


    getAllDimensionIds() {
        if (this._dimensionIds) {
            return this._dimensionIds;
        }
        let result = [];
        this.$widget.get('view').forEach(($$id, key)=> {
            if (parseInt(key) < BICst.REGION.TARGET1) {
                result = result.concat($$id.toArray());
            }
        });
        this._dimensionIds = result;
        return result;
    }

    getAllTargetIds() {
        if (this._targetIds) {
            return this._targetIds;
        }
        let result = [];
        this.$widget.get('view').forEach(($$id, key)=> {
            if (parseInt(key) >= BICst.REGION.TARGET1) {
                result = result.concat($$id.toArray());
            }
        });
        this._targetIds = result;
        return result;
    }

    isDimensionById(id) {
        const dimensionIds = this.getAllDimensionIds();
        return dimensionIds.indexOf(id) > -1;
    }

    isTargetById(id) {
        const targetIds = this.getAllTargetIds();
        return targetIds.indexOf(id) > -1;
    }

    getAllDimensionAndTargetIds() {
        return this.getAllDimensionIds().concat(this.getAllTargetIds());
    }

    getAllUsedDimensionAndTargetIds() {
        const ids = this.getAllDimensionAndTargetIds();
        const result = [];
        ids.forEach((id)=> {
            const $$dim = this.get$$DimensionOrTargetById(id);
            if (new Dimension($$dim).isUsed()) {
                result.push(id);
            }
        });
        return result;
    }

    getAllUsedDimensionIds() {
        const ids = this.getAllDimensionIds();
        const result = [];
        ids.forEach((id)=> {
            const $$dim = this.get$$DimensionById(id);
            if (new Dimension($$dim).isUsed()) {
                result.push(id);
            }
        });
        return result;
    }

    getAllUsedTargetIds() {
        const ids = this.getAllTargetIds();
        const result = [];
        ids.forEach((id)=> {
            const $$dim = this.get$$TargetById(id);
            if (new Target($$dim).isUsed()) {
                result.push(id);
            }
        });
        return result;
    }

    getRowDimensionIds() {
        let result = [];
        this.$widget.get('view').forEach(($$id, key)=> {
            if (parseInt(key) === BICst.REGION.DIMENSION1) {
                result = result.concat($$id.toArray());
            }
        });
        return result;
    }

    getColDimensionIds() {
        let result = [];
        this.$widget.get('view').forEach(($$id, key)=> {
            if (parseInt(key) === BICst.REGION.DIMENSION2) {
                result = result.concat($$id.toArray());
            }
        });
        return result;
    }

    getType() {
        return this.$widget.get('type');
    }

    getName() {
        return this.$widget.get('name');
    }

    createJson() {
        return this.$widget.toJS();
    }

    isFreeze() {
        return this.$widget.getIn(['settings', 'freeze_dim']);
    }

    getWidgetBounds() {
        return this.$widget.get('bounds').toJS() || {};
    }

    getWidgetLinkage() {
        return this.$widget.get('linkages').toJS() || [];
    }

    getWidgetView() {
        return this.$widget.get('view').toJS() || {};
    }

    getWidgetSubType() {
        return this.$widget.get('sub_type');
    }


    getWidgetValue() {
        return this.$widget.get('value').toJS();
    }

    getRegionTypeByDimensionID(dId) {
        var view = this.getWidgetView();
        return findKey(view, function (regionType, dIds) {
            if (dIds.indexOf(dId) > -1) {
                return true;
            }
        });
    }

    isControl() {
        switch (this.getType()) {
            case BICst.WIDGET.STRING:
            case BICst.WIDGET.NUMBER:
            case BICst.WIDGET.TREE:
            case BICst.WIDGET.DATE:
            case BICst.WIDGET.YEAR:
            case BICst.WIDGET.QUARTER:
            case BICst.WIDGET.MONTH:
            case BICst.WIDGET.YMD:
                return true;
        }
    }

    getSelectType() {
        return this.$widget.getIn(['value', 'type']);
    }

    getSelectValue() {
        const value = this.$widget.getIn(['value', 'value']);
        return value ? value.toArray() : [];
    }

    getTreeFloors() {
        return this.getAllDimensionIds().length;
    }

    getSelectedTreeValue() {
        return this.$widget.get('value').toJS();
    }

    //文本组件 内容
    getContent() {
        return this.$widget.get('content');
    }

    //文本组件 样式
    getStyle() {
        return this.$widget.get('style').toJS();
    }

    getData(options) {
        const wi = this.createJson();
        switch (this.getType()) {
            case BICst.WIDGET.TABLE:
            case BICst.WIDGET.CROSS_TABLE:
            case BICst.WIDGET.COMPLEX_TABLE:
                return Fetch(BH.servletURL + '?op=fr_bi_dezi&cmd=widget_setting', {
                    method: "POST",
                    body: JSON.stringify({
                        widget: {
                            expander: {
                                x: {
                                    type: true,
                                    value: [[]]
                                },
                                y: {
                                    type: true,
                                    value: [[]]
                                }
                            }, ...wi
                        }, sessionID: BH.sessionID
                    })
                }).then(function (response) {
                    return response.json();
                });
            case BICst.WIDGET.DETAIL:
                return Fetch(BH.servletURL + '?op=fr_bi_dezi&cmd=widget_setting', {
                    method: "POST",
                    body: JSON.stringify({widget: wi, sessionID: BH.sessionID})
                }).then(function (response) {
                    return response.json();
                });
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
            case BICst.WIDGET.FUNNEL:
                return Fetch(BH.servletURL + '?op=fr_bi_dezi&cmd=chart_setting', {
                    method: "POST",

                    body: JSON.stringify({widget: {...wi, page: -1}, sessionID: BH.sessionID})
                }).then(function (response) {
                    return response.json();// 转换为JSON
                });

            case BICst.WIDGET.DATE:
            case BICst.WIDGET.YEAR :
            case BICst.WIDGET.QUARTER :
            case BICst.WIDGET.MONTH:
            case BICst.WIDGET.YMD :
                return;
            case BICst.WIDGET.STRING:
                return Fetch(BH.servletURL + '?op=fr_bi_dezi&cmd=widget_setting', {
                    method: "POST",
                    body: JSON.stringify({widget: {...wi, text_options: options}, sessionID: BH.sessionID})
                }).then(function (response) {
                    return response.json();
                });
            case BICst.WIDGET.TREE :
                return Fetch(BH.servletURL + '?op=fr_bi_dezi&cmd=widget_setting', {
                    method: "POST",
                    body: JSON.stringify({widget: {...wi, tree_options: options}, sessionID: BH.sessionID})
                }).then(function (response) {
                    return response.json();
                });
            case BICst.WIDGET.NUMBER :
            case BICst.WIDGET.GENERAL_QUERY:
        }

    }

    getWidgetSettings() {
        return this.$widget.get('settings').toJS();
    }

    //settings  ---- start ----
    getWSTableForm() {
        var ws = this.getWidgetSettings();
        return isNil(ws.table_form) ? ws.table_form :
            BICst.DEFAULT_CHART_SETTING.table_form;
    }

    getWSThemeColor() {
        var ws = this.getWidgetSettings();
        return isNil(ws.theme_color) ? ws.theme_color :
            BICst.DEFAULT_CHART_SETTING.theme_color;
    }

    getWSTableStyle() {
        var ws = this.getWidgetSettings();
        return isNil(ws.table_style) ? ws.table_style :
            BICst.DEFAULT_CHART_SETTING.table_style;
    }

    getWSShowNumber() {
        var ws = this.getWidgetSettings();
        return isNil(ws.show_number) ? ws.show_number :
            BICst.DEFAULT_CHART_SETTING.show_number;
    }

    getWSShowRowTotal() {
        var ws = this.getWidgetSettings();
        return isNil(ws.show_row_total) ? ws.show_row_total :
            BICst.DEFAULT_CHART_SETTING.show_row_total;
    }

    getWSShowColTotal() {
        var ws = this.getWidgetSettings();
        return isNil(ws.show_col_total) ? ws.show_col_total :
            BICst.DEFAULT_CHART_SETTING.show_col_total;
    }

    getWSOpenRowNode() {
        var ws = this.getWidgetSettings();
        return isNil(ws.open_row_node) ? ws.open_row_node :
            BICst.DEFAULT_CHART_SETTING.open_row_node;
    }

    getWSOpenColNode() {
        var ws = this.getWidgetSettings();
        return isNil(ws.open_col_node) ? ws.open_col_node :
            BICst.DEFAULT_CHART_SETTING.open_col_node;
    }

    getWSMaxRow() {
        var ws = this.getWidgetSettings();
        return isNil(ws.max_row) ? ws.max_row :
            BICst.DEFAULT_CHART_SETTING.max_row;
    }

    getWSMaxCol() {
        var ws = this.getWidgetSettings();
        return isNil(ws.max_col) ? ws.max_col :
            BICst.DEFAULT_CHART_SETTING.max_col;
    }

    getWSFreezeDim() {
        var ws = this.getWidgetSettings();
        return isNil(ws.freeze_dim) ? ws.freeze_dim :
            BICst.DEFAULT_CHART_SETTING.freeze_dim;
    }

    getWSFreezeFirstColumnById() {
        var ws = this.getWidgetSettings();
        return isNil(ws.freeze_first_column) ? ws.freeze_first_column :
            BICst.DEFAULT_CHART_SETTING.freeze_first_column;
    }

    isShowWidgetRealData() {
        return this.$widget.get('real_data');
    }


    isDimensionExist(did) {
        return this.getAllDimensionIds().indexOf(did) > -1;
    }

    getWidgetInitTime() {
        return this.$widget.get('init_time') || new Date().getTime();
    }

    getClicked() {
        return this.$widget.get('clicked') || {};
    }

    getDrill() {
        var clicked = this.getClickedByID();
        var drills = {};
        each(clicked, (dId, value)=> {
            if (this.isDimensionExist(dId) && this.isDimensionById(dId)) {
                drills[dId] = value;
            }
        });
        return drills;
    }

    getLinkageValues(wid) {
        var clicked = this.getClickedByID(wid);
        var drills = {};
        each(clicked, (dId, value)=> {
            if (this.isDimensionExist(dId) && !this.isDimensionById(dId)) {
                drills[dId] = value;
            }
        });
        return drills;
    }

    getWidgetFilterValue(wid) {
        if (this.isWidgetExistByID(wid)) {
            return this.$widget.get('filter_value').toJS() || {};
        }
        return {};
    }


    setWidgetValue(value) {
        this.$widget = this.$widget.set('value', Immutable.fromJS(value));
        return this;
    }

    setWidgetView(view) {
        this.$widget = this.$widget.set('view', Immutable.fromJS(view));
        return this;
    }

    set$Dimension($dimension, dId) {
        this.$widget = this.$widget.setIn(['dimensions', dId], $dimension);
        return this;
    }
}

export default Widget;