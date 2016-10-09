import mixin from 'react-mixin'
import ReactDOM from 'react-dom'

import {
    ReactComponentWithImmutableRenderMixin,
    cn,
    sc,
    isNil,
    requestAnimationFrame,
    emptyFunction,
    shallowEqual,
    isEqual,
    each
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
    Modal,
    Navigator
} from 'lib'

import {Colors, Size, Template} from 'data'

import {IconButton, Table, AutoSizer, HtapeLayout, VtapeLayout, GridLayout, CenterLayout} from 'base'

import {MultiSelectorWidget} from 'widgets'

import Controls from './Controls/Controls'


class Toolbar extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {};

    state = {};

    componentWillMount() {

    }

    componentDidMount() {

    }

    componentWillReceiveProps() {

    }

    componentWillUpdate() {

    }

    componentWillUnmount() {

    }

    _onPress() {
        this.props.navigator.push({
            ...this.props,
            name: 'list',
            Component: Controls,
            title: '参数查询',
            //sceneConfig: Navigator.SceneConfigs.FloatFromBottom
        })
    }

    render() {
        const {...props} = this.props, {...state} = this.state;
        return <IconButton onPress={this._onPress.bind(this)} className={'tool-filter'}
                           style={[styles.filter]}/>
    }

}
mixin.onClass(Toolbar, ReactComponentWithImmutableRenderMixin);
const styles = StyleSheet.create({
    filter: {
        borderTop: '1px solid ' + Colors.BORDER,
        height: 44
    }
});
export default Toolbar
