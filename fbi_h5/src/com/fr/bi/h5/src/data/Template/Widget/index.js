import Dimension from './Dimension'
import Target from './Target'
import {each, invariant} from 'core';
import {Fetch} from 'lib'
class Widget {
    constructor(widget, id) {
        this.$$widget = widget;
        this.id = id;
    }

    get$$DimensionById(id) {
        invariant(this.isDimensionById(id), id + "不是维度id");
        return this.$$widget.getIn(['dimensions', id]);
    }

    get$$TargetById(id) {
        invariant(this.isTargetById(id), id + "不是指标id");
        return this.$$widget.getIn(['dimensions', id])
    }

    getAllDimensionIds() {
        if (this._dimensionIds) {
            return this._dimensionIds;
        }
        let result = [];
        this.$$widget.get('view').forEach(($$id, key)=> {
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
        this.$$widget.get('view').forEach(($$id, key)=> {
            if (parseInt(key) >= BICst.REGION.TARGET1) {
                result = result.concat($$id.toArray());
            }
        });
        this._targetIds = result;
        return result;
    }

    get$$DimensionOrTargetById(id) {
        if (this.isDimensionById(id)) {
            return this.get$$DimensionById(id);
        }
        return this.get$$TargetById(id);
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

    getType() {
        return this.$$widget.get('type');
    }

    getName() {
        return this.$$widget.get('name');
    }

    createJson() {
        return this.$$widget.toJS();
    }

    isFreeze() {
        return this.$$widget.getIn(['settings', 'freeze_dim']);
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
        return this.$$widget.getIn(['value', 'type']);
    }

    getSelectValue() {
        return this.$$widget.getIn(['value', 'value']).toArray();
    }

    getTreeFloors() {
        return this.getAllDimensionIds().length;
    }

    getSelectedTreeValue() {
        return this.$$widget.get('value').toJS();
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
}

export default Widget;