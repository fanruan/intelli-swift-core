/**
 * 文本组件
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
    TextInput,
    Dimensions,
    PixelRatio,
    ListView,
    View,
    Fetch,
    Promise,
    TouchableHighlight
} from 'lib'

import {Colors, Size, Template, Widget, Dimension, Target} from 'data'

import {CenterLayout, Icon, Table} from 'base'

import {MultiSelectorWidget} from 'widgets'


class ContentComponent extends Component {
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
        const widget = new Widget($widget, wId, new Template(this.context.$template));
        const style = widget.getStyle();
        return <TextInput
            style={{height: this.props.height, ...styles.wrapper, ...style}}
            ref="content"
            autoCapitalize="none"
            multiline="true"
            value={widget.getContent()}
            editable="true"
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
mixin.onClass(ContentComponent, ReactComponentWithPureRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        flex: 1
    }
});
export default ContentComponent