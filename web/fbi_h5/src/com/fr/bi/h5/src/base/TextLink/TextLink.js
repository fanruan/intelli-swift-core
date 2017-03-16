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
    TouchableHighlight,
    TouchableOpacity
} from 'lib'

import {Colors} from 'data'
import './TextLink.css'

class TextLink extends Component {
    constructor(props, context) {
        super(props, context);
        const {selected} = props;
        this.state = {selected};
    }

    static propTypes = {};

    static defaultProps = {
        selected: null,
        disabled: false,
        invalid: false,
        stopPropagation: false,
        onSelected: emptyFunction,
        onPress: emptyFunction
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

    _onPress(e) {
        if (this.props.disabled === false && this.props.invalid === false && (this.state.selected === false || this.state.selected === true)) {
            this.setState({
                selected: !this.state.selected
            }, ()=> {
                this.props.onSelected(this.state.selected);
            });
        }
        this.props.onPress(e);
        if (this.props.stopPropagation) {
            e.stopPropagation();
        }
    }

    render() {
        const {...props} = this.props, {...state} = this.state;
        if (props.disabled === true) {
            return <Text className={cn(props.className, 'base-disabled')}
                         style={[styles.disabled, props.style]}>{props.children}</Text>
        }
        if (props.invalid === true) {
            return <Text className={cn(props.className, 'base-disabled')} style={[props.style]}>{props.children}</Text>
        }
        return <Text className="TextLink" style={[props.style]}
                     onPress={this._onPress.bind(this)}>{props.children}</Text>
    }
}
mixin.onClass(TextLink, PureRenderMixin);
const styles = StyleSheet.create({
    disabled: {
        color: Colors.DISABLED
    }
});
export default TextLink
