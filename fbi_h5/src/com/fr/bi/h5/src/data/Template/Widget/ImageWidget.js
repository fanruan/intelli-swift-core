/**
 * ImageWidget
 * Created by Young's on 2016/10/12.
 */
import {isNotEmptyString} from 'core'
import {Image} from 'lib'
import AbstractWidget from './AbstractWidget'

class ImageWidget extends AbstractWidget {
    constructor($widget, ...props) {
        super($widget, ...props);
    }

    getSizeMode() {
        const size = this.$widget.get('size');
        switch (size) {
            case BICst.IMAGE_RESIZE_MODE.ORIGINAL:
                return Image.resizeMode.cover;
            case BICst.IMAGE_RESIZE_MODE.EQUAL:
                return Image.resizeMode.contain;
            case BICst.IMAGE_RESIZE_MODE.STRETCH:
                return Image.resizeMode.stretch;
            default:
                return Image.resizeMode.contain;
        }
    }

    getImageSrc() {
        const src = this.$widget.get('src');
        if (isNotEmptyString(src)) {
            return BH.servletURL + "?op=fr_bi&cmd=get_uploaded_image&image_id=" + src;
        }
        return '';
    }

    getHref() {
        return this.$widget.get('href');
    }
}

export default ImageWidget;
