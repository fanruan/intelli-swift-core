/**
 * Web组件
 * Created by Young's on 2016/10/20.
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
    TextInput,
    WebView,
    Dimensions,
    PixelRatio,
    ListView,
    View,
    Fetch,
    Promise,
    TouchableHighlight
} from 'lib'

import {Colors, Sizes, TemplateFactory, DimensionFactory, WidgetFactory} from 'data'

import {CenterLayout, Icon, Table} from 'base'


class WebComponent extends Component {

    constructor(props, context) {
        super(props, context);
        this.state = {content: ''}
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
        return <WebView
            style={{height: this.props.height, ...styles.wrapper}}
            src={ widget.getUrl()}
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
mixin.onClass(WebComponent, ReactComponentWithPureRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        flex: 1
    }
});
export default WebComponent;
