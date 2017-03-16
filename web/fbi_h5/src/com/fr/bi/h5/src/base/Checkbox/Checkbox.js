import PureRenderMixin from 'react-addons-pure-render-mixin'
import mixin from 'react-mixin'
import {findDOMNode} from 'react-dom'
import './Checkbox.css'

import {cn, sc, math, isNil, requestAnimationFrame, emptyFunction, shallowEqual, isEqual, each} from 'core'
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
    TouchableHighlight,
    TouchableWithoutFeedback
} from 'lib'


class Checkbox extends Component {
    constructor(props, context) {
        super(props, context);
    }

    static propTypes = {};

    static defaultProps = {
        checked: true,
        halfCheck: false
    };

    state = {
        checked: this.props.checked,
        halfCheck: this.props.halfCheck,
        onChecked: emptyFunction
    };

    componentWillMount() {

    }

    componentDidMount() {

    }

    componentWillReceiveProps(nextProps) {
        const {checked, halfCheck} = nextProps;
        this.setState({checked, halfCheck});
    }

    render() {
        const {...props} = this.props, {...state} = this.state;
        return <TouchableWithoutFeedback onPress={(e)=> {
            if (this.state.halfCheck === true) {
                this.setState({
                    checked: true,
                    halfCheck: false
                }, ()=> {
                    props.onChecked(this.state);
                });
            } else {
                this.setState({
                    checked: !this.state.checked
                }, ()=> {
                    props.onChecked(this.state);
                });
            }
            e.stopPropagation();
        }}>
            <View className={cn({
                checked: state.checked,
                halfCheck: state.halfCheck
            }, props.className, 'Checkbox')}{...props} style={[styles.wrapper, props.style]}>

            </View>
        </TouchableWithoutFeedback>
    }

}
mixin.onClass(Checkbox, PureRenderMixin);
const styles = StyleSheet.create({
    wrapper: {
        width: 28,
        height: 28
    }
});
export default Checkbox
