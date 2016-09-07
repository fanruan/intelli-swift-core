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
    Fetch
} from 'lib'

import {Table, AutoSizer} from 'base'

import {Template} from 'data'

import {MultiSelectorWidget} from 'widgets'


class MultiSelectorComponent extends Component {
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
        return <MultiSelectorWidget
            items={[{value: 1}, {value: 2}, {value: 3}, {value: 4}]}
        >
        </MultiSelectorWidget>
    }

}
mixin.onClass(MultiSelectorComponent, PureRenderMixin);
const styles = StyleSheet.create({
    region: {
        position: 'absolute'
    }
});
export default MultiSelectorComponent
