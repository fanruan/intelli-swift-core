/**
 * Web组件
 * Created by Young's on 2016/10/20.
 */
import AbstractWidget from './AbstractWidget'
import {formatAddress} from 'core'
class WebWidget extends AbstractWidget {
    constructor($widget, ...props) {
        super($widget, ...props);
    }

    getUrl() {
        return formatAddress(this.$widget.get('url'));
    }
}

export default WebWidget;
