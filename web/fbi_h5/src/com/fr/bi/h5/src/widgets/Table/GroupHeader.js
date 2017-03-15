import mixin from 'react-mixin'
import {findDOMNode} from 'react-dom'

import {
    ReactComponentWithPureRenderMixin, ReactComponentWithImmutableRenderMixin,
    cn, sc, math, isNil, emptyFunction, shallowEqual, isEqual, each,
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
    TouchableHighlight
} from 'lib'

import {CenterLayout, Icon, Table} from 'base'


class GroupHeader extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {
        headerRowHeight: 30,
        groupHeader: [],
        groupHeaderCellRenderer: emptyFunction,
        width: 200,
        height: 30
    };

    state = {};

    _getNextState(props, state = {}) {

    }

    componentWillMount() {

    }

    componentDidMount() {

    }

    render() {
        const {width, height, groupHeader, headerRowHeight, groupHeaderCellRenderer, ...props} = this.props, {...state} = this.state;
        const items = [];
        each(groupHeader, (cell, rowIndex)=> {
            const style = {
                position: 'absolute',
                width: width,
                height: headerRowHeight,
                borderBottomWidth: 1 / PixelRatio.get(),
                borderRightWidth: 1 / PixelRatio.get(),
                borderBottomColor: '#d3d3d3',
                borderRightColor: '#d3d3d3'
            };
            translateDOMPositionXY(style, 0, rowIndex * headerRowHeight);

            items.push(<View style={style}>
                {groupHeaderCellRenderer({rowIndex, width, headerRowHeight: height, ...cell})}
            </View>);
        });
        return <View style={[styles.wrapper, {width: width, height: height}]}>
            {items}
        </View>
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
mixin.onClass(GroupHeader, ReactComponentWithPureRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        position: 'relative'
    }
});
export default GroupHeader
