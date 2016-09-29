import Dimension from './Dimension'
import Target from './Target'
import {each, invariant} from 'core';
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

    getSelectType(){
        return this.$$widget.getIn(['value', 'type']);
    }

    getSelectValue(){
        return this.$$widget.getIn(['value', 'value']).toArray();
    }
}

export default Widget;