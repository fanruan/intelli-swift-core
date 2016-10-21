/**
 * Web组件
 * Created by Young's on 2016/10/20.
 */
import AbstractWidget from './AbstractWidget'
class WebWidget extends AbstractWidget {
    constructor($widget, ...props) {
        super($widget, ...props);
    }

    getUrl() {
        return this.$widget.get('url');
    }
}

export default WebWidget;
