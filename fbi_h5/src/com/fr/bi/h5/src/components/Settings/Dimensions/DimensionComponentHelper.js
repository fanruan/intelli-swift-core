import {each, first, arrayMove, values, keys} from 'core'
import {Widget, Template, Dimension} from 'data'
export default class DimensionComponentHelper {
    constructor(props, context) {
        this.widget = new Widget(props.$widget, props.wId, context.$template);
        this.wId = props.wId;
        this.dId = props.dId;
        this.dimension = this.widget.getDimensionOrTargetById(this.dId);
    }

    isUsed() {
        return this.dimension.isUsed();
    }

    switchSelect() {
        return this.dimension.setUsed(!this.isUsed()).$get();
    }

    switchSort() {
        switch (this.dimension.getSortType()) {
            case BICst.SORT.ASC:
                return this.dimension.setSortType(BICst.SORT.DESC).$get();
            case BICst.SORT.DESC:
                return this.dimension.setSortType(BICst.SORT.ASC).$get();
        }
    }

    getSortTargetName() {
        return this.dimension.getSortTargetName()
    }

    getSortTargetTypeFont() {
        switch (this.dimension.getSortType()) {
            case BICst.SORT.ASC:
                return 'asc-sort-font';
            case BICst.SORT.DESC:
                return 'dsc-sort-font';
        }
    }
}