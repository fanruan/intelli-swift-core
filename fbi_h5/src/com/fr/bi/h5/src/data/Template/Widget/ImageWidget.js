/**
 * ImageWidget
 * Created by Young's on 2016/10/12.
 */
import AbstractWidget from './AbstractWidget'

class ImageWidget extends AbstractWidget{
    constructor($widget, ...props) {
        super($widget, ...props);
    }

    getSizeMode() {
        const size = this.$widget.get('size');
        switch (size) {
            case BICst.IMAGE_RESIZE_MODE.ORIGINAL:
                return 'contain';
            case BICst.IMAGE_RESIZE_MODE.EQUAL:
                return 'cover';
            case BICst.IMAGE_RESIZE_MODE.STRETCH:
                return 'stretch';
            default:
                return 'contain'
        }
    }

    getSrc() {
        return this.$widget.get('src');
    }

    getHref() {
        return this.$widget.get('href');
    }
}

export default ImageWidget;
