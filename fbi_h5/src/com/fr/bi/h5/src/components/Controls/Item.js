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
    TouchableHighlight
} from 'lib'

import {Colors, Size, Template, Widget} from 'data'
import {Layout, VerticalCenterLayout} from 'layout'
import {Icon, Button, Table, AutoSizer} from 'base'

import {MultiSelectorWidget} from 'widgets'


class Item extends Component {
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

    render() {
        const {...props} = this.props, {...state} = this.state;
        return <Button onPress={this.props.onPress}>
            <VerticalCenterLayout style={styles.wrapper}>
                <Text>{new Widget(props.$widget).getName()}</Text>
            </VerticalCenterLayout>
        </Button>
    }

}
mixin.onClass(Item, ReactComponentWithImmutableRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        height: Size.ITEM_HEIGHT,
        paddingLeft: 20,
        paddingRight: 20,
        borderBottom: '1px solid ' + Colors.SPLIT
    }
});
export default Item
