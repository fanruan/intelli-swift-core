import mixin from 'react-mixin'
import {findDOMNode} from 'react-dom'
import Immutable from 'immutable'

import {
    ReactComponentWithPureRenderMixin, ReactComponentWithImmutableRenderMixin,
    cn, sc, math, isNil, emptyFunction, shallowEqual, immutableShallowEqual, isEqual, isEmpty, each, map,
    translateDOMPositionXY, requestAnimationFrame
} from 'core'
import React, {
    Component,
    PropTypes,
    StyleSheet,
    Text,
    Portal,
    PixelRatio,
    ListView,
    View,
    Fetch,
    Promise,
    PickerIOS,
    TouchableHighlight
} from 'lib'

import {Colors, Size, TemplateFactory, WidgetFactory, DimensionFactory} from 'data'

import {Layout, CenterLayout, HorizontalCenterLayout, VerticalCenterLayout} from 'layout';

import {Button, TextButton, IconButton, Table} from 'base'

import {MultiSelectorWidget} from 'widgets'

import TargetSortComponentHelper from './TargetSortComponentHelper'


class TargetSortComponent extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {};

    state = {};

    _getNextState(props, state = {}) {

    }

    componentWillMount() {

    }

    componentDidMount() {

    }

    render() {
        const {...props} = this.props, {...state} = this.state;
        this._helper = new TargetSortComponentHelper(props, this.context);
        return <Layout box='mean' style={styles.wrapper}>
            <PickerIOS selectedValue={this._helper.getSortType()} onValueChange={(type)=> {
                this.props.onValueChange(this._helper.setSortType(type));
            }}>
                <PickerIOS.Item value={BICst.SORT.ASC} label='升序'/>
                <PickerIOS.Item value={BICst.SORT.DESC} label='降序'/>
                <PickerIOS.Item value={BICst.SORT.NONE} label='不排序'/>
            </PickerIOS>
        </Layout>
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
mixin.onClass(TargetSortComponent, ReactComponentWithImmutableRenderMixin);
const styles = StyleSheet.create({
    wrapper: {}
});
export default TargetSortComponent
