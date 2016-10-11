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
    TouchableOpacity,
    TouchableWithoutFeedback
} from 'lib'

import Icon from '../Icon/Icon'
import {Colors} from 'data'

class IconButton extends Component {
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
        effect: true,
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
            return <View className={cn(props.className, 'react-view', 'base-disabled')}
                         style={[styles.wrapper, styles.disabled, props.style]}>
                <Icon width={props.iconWidth} height={props.iconHeight}/>
            </View>
        }
        if (props.invalid === true) {
            return <View className={cn(props.className, 'react-view', 'base-invalid')}
                         style={[styles.wrapper, props.style]}>
                <Icon width={props.iconWidth} height={props.iconHeight}/>
            </View>
        }
        if (props.effect === true) {
            const {paddingLeft, paddingRight, paddingTop, paddingBottom, marginTop, marginBottom, marginLeft, marginRight, ...other} = props.style || {};
            return <TouchableHighlight style={[{...other}]} onPress={this._onPress.bind(this)}
                                       underlayColor={props.underlayColor || Colors.PRESS}>
                <View className={cn(props.className, 'react-view')} style={[styles.wrapper, {flex: 1}, props.style]}>
                    <Icon width={props.iconWidth} height={props.iconHeight}/>
                </View>
            </TouchableHighlight>
        }
        return <TouchableWithoutFeedback onPress={this._onPress.bind(this)}>
            <View className={cn(props.className, 'react-view')} style={[styles.wrapper, props.style]}>
                <Icon width={props.iconWidth} height={props.iconHeight}/>
            </View>
        </TouchableWithoutFeedback>
    }

}
mixin.onClass(IconButton, PureRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        justifyContent: 'center',
        alignItems: 'center',
        color: Colors.HIGHLIGHT
    },
    disabled: {
        color: Colors.DISABLED
    }
});
export default IconButton
