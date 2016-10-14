/**
 * ContentWidget
 * Created by Young's on 2016/10/12.
 */
import AbstractWidget from './AbstractWidget'

class ContentWidget extends AbstractWidget{
    constructor($widget, $template, wId) {
        super($widget, $template, wId);
    }

    getContent() {
        return this.$widget.get('content');
    }

    getStyle() {
        return this.$widget.get('style').toJS();
    }
}

export default ContentWidget;