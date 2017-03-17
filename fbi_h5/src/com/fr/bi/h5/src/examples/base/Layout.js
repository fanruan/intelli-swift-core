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
    PropTypes,
    StyleSheet,
    Text,
    Dimensions,
    PixelRatio,
    ListView,
    ScrollView,
    View,
    Fetch,
    Promise,
    TouchableHighlight
} from 'lib'

import {Colors, Size, Template, Widget, Dimension, Target} from 'data'

import {Layout, CenterLayout, HorizontalCenterLayout, VerticalCenterLayout} from 'base'


class LayoutDemo extends Component {
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
        return <ScrollView>
            <CenterLayout style={styles.item}>
                <Text style={{backgroundColor: Colors.HIGHLIGHT}}>123</Text>
            </CenterLayout>
            <HorizontalCenterLayout style={styles.item}>
                <Text style={{backgroundColor: Colors.HIGHLIGHT}}>123</Text>
            </HorizontalCenterLayout>
            <VerticalCenterLayout style={styles.item}>
                <Text style={{backgroundColor: Colors.HIGHLIGHT}}>123</Text>
            </VerticalCenterLayout>
            <Layout dir='left' box='justify' style={styles.item}>
                <CenterLayout style={{width: 100, backgroundColor: Colors.PRESS}}>
                    <Text>123</Text>
                </CenterLayout>
                <CenterLayout style={{backgroundColor: Colors.HIGHLIGHT}}>
                    <Text>456</Text>
                </CenterLayout>
                <CenterLayout style={{width: 100, backgroundColor: Colors.PRESS}}>
                    <Text>789</Text>
                </CenterLayout>
            </Layout>
            <Layout dir='left' main='justify' style={styles.item}>
                <CenterLayout style={{width: 100, backgroundColor: Colors.PRESS}}>
                    <Text>123</Text>
                </CenterLayout>
                <CenterLayout style={{backgroundColor: Colors.HIGHLIGHT}}>
                    <Text>456</Text>
                </CenterLayout>
                <CenterLayout style={{width: 100, backgroundColor: Colors.PRESS}}>
                    <Text>789</Text>
                </CenterLayout>
            </Layout>
        </ScrollView>
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
mixin.onClass(LayoutDemo, ReactComponentWithImmutableRenderMixin);
const styles = StyleSheet.create({
    item: {
        height: 100,
        borderBottomColor: Colors.BORDER,
        borderBottomWidth: 1 / PixelRatio.get()
    }
});
export default LayoutDemo
