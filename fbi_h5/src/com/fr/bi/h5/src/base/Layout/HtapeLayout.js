
import React, {
    Component,
    StyleSheet,
    View
} from 'lib'

class HtapeLayout extends Component {
    constructor(props, context) {
        super(props, context);
    }

    render() {
        const {children, style, ...props} = this.props;
        const cs = React.Children.map(children, (child)=> {
            if (!child) {
                return null;
            }
            let style;
            if (child.props.width) {
                style = {width: child.props.width, ...child.props.style}
            } else {
                style = {...child.props.style, ...styles.col};
            }
            return React.cloneElement(child, {...child.props, style});
        });
        return <View {...props} style={[styles.wrapper, style]}>{cs}</View>
    }

}
const styles = StyleSheet.create({
    wrapper: {
        flexDirection: 'row',
        flexWrap: 'nowrap'
    },
    col: {
        flex: 1
    }
});
export default HtapeLayout
