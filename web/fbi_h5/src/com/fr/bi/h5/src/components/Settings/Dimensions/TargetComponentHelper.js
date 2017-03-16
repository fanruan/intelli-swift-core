import {each, first, arrayMove, values, keys} from 'core'
import {WidgetFactory, TemplateFactory, DimensionFactory} from 'data'
export default class TargetComponentHelper {
    constructor(props, context) {
        this.widget = WidgetFactory.createWidget(props.$widget, props.wId, TemplateFactory.createTemplate(context.$template));
        this.wId = props.wId;
        this.dId = props.dId;
        this.dimension = this.widget.getDimensionByDimensionId(this.dId);
    }

    isUsed() {
        return this.dimension.isUsed();
    }

    getSortTargetName() {
        return this.dimension.getName();
    }

    getSortTargetTypeFont() {
        switch (this.widget.getSortType()) {
            case BICst.SORT.ASC:
                return 'asc-sort-font';
            case BICst.SORT.DESC:
                return 'dsc-sort-font';
        }
    }

    switchSelect() {
        this.dimension.setUsed(!this.isUsed()).$get();
        this.widget.set$Dimension(this.dimension.$get(), this.dId);
        return this.widget.$get();
    }
}