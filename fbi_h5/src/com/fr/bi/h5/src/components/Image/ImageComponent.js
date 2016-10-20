/**
 * 图片组件
 * Created by Young's on 2016/10/10.
 */
import mixin from 'react-mixin'
import {findDOMNode} from 'react-dom'
import Immutable from 'immutable'

import {
    ReactComponentWithPureRenderMixin, ReactComponentWithImmutableRenderMixin,
    cn, sc, math, isNil, emptyFunction, shallowEqual, immutableShallowEqual, isEqual, isEmpty, each,
    translateDOMPositionXY, requestAnimationFrame
} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    PixelRatio,
    ListView,
    View,
    Fetch,
    Promise,
    TouchableHighlight,
    Image
} from 'lib'

import {Colors, Sizes, Template, Widget, Dimension, Target} from 'data'
import {CenterLayout, Icon, Table} from 'base'
import WidgetFactory from '../../data/Template/Widget/WidgetFactory'
import TemplateFactory from '../../data/Template/TemplateFactory'

class ImageComponent extends Component {
    constructor(props, context) {
        super(props, context);
    }

    _getNextState(props, state = {}) {

    }

    componentWillMount() {

    }

    componentDidMount() {

    }

    render() {
        const {$widget, wId} = this.props;
        const widget = WidgetFactory.createWidget($widget, wId, TemplateFactory.createTemplate(this.context.$template));
        return <Image
            style={{height: this.props.height, ...styles.wrapper}}
            source={widget.getSrc()}
            resizeMode={widget.getSizeMode()}
        />
    }

    componentWillReceiveProps(nextProps) {

    }

    componentWillUpdate(nextProps, nextState) {

    }

    componentDidUpdate(prevProps, prevState) {

    }

    componentWillUnmount() {

    }

}
mixin.onClass(ImageComponent, ReactComponentWithPureRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        flex: 1
    }
});
export default ImageComponent
