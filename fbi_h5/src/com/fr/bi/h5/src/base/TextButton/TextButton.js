import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import ReactDOM from 'react-dom'

import {cn, sc, requestAnimationFrame, emptyFunction} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Image,
    Dimensions,
    ListView,
    View,
    PixelRatio,
    Fetch,
    TouchableHighlight
    } from 'lib'

import {Colors} from 'data'

import Icon from '../Icon'


class TextButton extends Component {
    constructor(props, context) {
        super(props, context);
        const {selected} = props;
        this.state = {selected};
    }

    static propTypes = {};

    static defaultProps = {
        height: 30,
        selected: null,
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

    _onPress() {
        this.setState({
            selected: !this.state.selected
        }, ()=> {
            this.props.onSelected(this.state.selected);
        })
    }

    render() {
        const {...props} = this.props, {...state} = this.state;
        return <TouchableHighlight style={[props.style]} onPress={this._onPress.bind(this)}
                                   underlayColor={props.underlayColor || Colors.PRESS}>
            <View className={cn(props.className, 'react-view', cn({
                'active': this.state.selected
            }))} style={[styles.wrapper]}>
                <Icon width={props.iconWidth} height={props.iconHeight}></Icon>
            </View>
        </TouchableHighlight>
    }

}
mixin.onClass(TextButton, PureRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center'
    },

    selected: {
        backgroundColor: Colors.SELECTED
    }
});
export default TextButton
