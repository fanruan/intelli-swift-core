import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'

import {cn, sc, requestAnimationFrame, emptyFunction} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    ListView,
    View,
    Fetch,
    TouchableHighlight
    } from 'lib'

import {Colors} from 'data'

import {Table, AutoSizer} from 'base'


class Item extends Component {
    constructor(props, context) {
        super(props, context);
        const {text, value, selected} = props;
        this.state = {text, value, selected};
    }

    static propTypes = {};

    static defaultProps = {
        text: '',
        value: '',
        selected: false,
        onSelected: emptyFunction
    };

    state = {};

    componentWillMount() {

    }

    componentDidMount() {

    }

    componentWillReceiveProps(props) {
        const {text, value, selected} = props;
        this.state = {text, value, selected};
    }

    componentWillUpdate() {

    }

    render() {
        const {...props} = this.props, {...state} = this.state;
        return <TouchableHighlight onPress={() => {
            this.setState({
                selected: !this.state.selected
            }, ()=> {
                this.props.onSelected(this.state.selected);
            })
        }} underlayColor={Colors.PRESS}>
            <View style={[styles.row, sc([styles.selected, state.selected])]}>
                <Text>
                    {state.value == null ? state.text : state.value}
                </Text>
            </View>
        </TouchableHighlight>
    }

}
mixin.onClass(Item, PureRenderMixin);
const styles = StyleSheet.create({
    row: {
        justifyContent: 'center',
        height: 30
    },

    selected: {
        backgroundColor: Colors.SELECTED
    }
});
export default Item
