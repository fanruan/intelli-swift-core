import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'

import {requestAnimationFrame, emptyFunction} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    ListView,
    View,
    Fetch,
    ViewPagerAndroid
} from 'lib'

import {Grid, ScrollSync, AutoSizer} from 'base'


class ViewPagerDemo extends Component {
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

    render() {
        const {...props} = this.props;
        const pages = [];
        for (var i = 0; i < 6; i++) {
            pages.push(
                <View key={i} collapsable={false}>
                    <Text>{i}</Text>
                </View>
            );
        }
        return <ViewPagerAndroid
            initialPage={0}
            onPageScroll={this.onPageScroll.bind(this)}
            onPageSelected={this.onPageSelected.bind(this)}
            ref={viewPager => {
                this.viewPager = viewPager;
            }}>
            {pages}
        </ViewPagerAndroid>
    }

    onPageSelected(e) {
        this.setState({page: e.nativeEvent.position});
    }

    onPageScroll(e) {
        this.setState({progress: e.nativeEvent});
    }

}
mixin.onClass(ViewPagerDemo, PureRenderMixin);
const style = StyleSheet.create({
    region: {
        position: 'absolute'
    }
});
export default ViewPagerDemo
