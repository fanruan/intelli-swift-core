import Dimension from './Dimension'
import Target from './Target'
import {each, invariant} from 'core';
class Widget {
    constructor(widget, id) {
        this.widget = widget;
        this.id = id;
        this._dimensions = {};
        this._targets = {};

    }

    getDimensionById(id) {
        invariant(this.isDimensionById(id), id + "不是维度id");
        if (this._dimensions[id]) {
            return this._dimensions[id];
        }
        this._dimensions[id] = new Dimension(this.widget.dimensions[id], id, this);
        return this._dimensions[id];
    }

    getTargetById(id) {
        invariant(this.isTargetById(id), id + "不是指标id");
        if (this._targets[id]) {
            return this._targets[id];
        }
        this._targets[id] = new Target(this.widget.dimensions[id], id, this);
        return this._targets[id];
    }

    getAllDimensionIds() {
        if (this._dimensionIds) {
            return this._dimensionIds;
        }
        let result = [];
        each(this.widget.view, (dId, key)=> {
            if (parseInt(key) < BICst.REGION.TARGET1) {
                result = result.concat(dId);
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
        each(this.widget.view, (dId, key)=> {
            if (parseInt(key) >= BICst.REGION.TARGET1) {
                result = result.concat(dId);
            }
        });
        this._targetIds = result;
        return result;
    }

    getDimensionOrTargetById(id) {
        if (this.isDimensionById(id)) {
            return this.getDimensionById(id);
        }
        return this.getTargetById(id);
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
            const dim = this.getDimensionOrTargetById(id);
            if (dim.isUsed()) {
                result.push(id);
            }
        });
        return result;
    }

    getAllUsedDimensionIds() {
        const ids = this.getAllDimensionIds();
        const result = [];
        ids.forEach((id)=> {
            const dim = this.getDimensionById(id);
            if (dim.isUsed()) {
                result.push(id);
            }
        });
        return result;
    }

    getAllUsedTargetIds() {
        const ids = this.getAllTargetIds();
        const result = [];
        ids.forEach((id)=> {
            const dim = this.getTargetById(id);
            if (dim.isUsed()) {
                result.push(id);
            }
        });
        return result;
    }

    getType() {
        return this.widget.type;
    }

    createJson() {
        return this.widget;
    }

    isFreeze() {
        return this.widget.settings.freeze_dim;
    }
}

export default Widget;