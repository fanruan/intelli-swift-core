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

import {MultiTreeSelectorWidget} from 'widgets'


class MultiTreeSelectorComponent extends Component {
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
        const items = [];
        for (let i = 0; i < 1000; i++) {
            for (let j = 0; j < 10; j++) {
                items.push({
                    id: i + '_' + j,
                    pId: i,
                    value: i + '_' + j
                })
            }
            items.push({
                id: i,
                value: i,
                isParent: true,
                expanded: true
            })
        }
        return <MultiTreeSelectorWidget
            items={items}
            width={props.width}
            height={props.height}
            >
        </MultiTreeSelectorWidget>
    }
}
mixin.onClass(MultiTreeSelectorComponent, PureRenderMixin);
const styles = StyleSheet.create({
    region: {
        position: 'absolute'
    }
});
export default MultiTreeSelectorComponent
