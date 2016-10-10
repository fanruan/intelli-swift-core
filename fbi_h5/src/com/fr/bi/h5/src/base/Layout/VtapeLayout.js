import React, {
    Component,
    StyleSheet,
    View
} from 'lib'
import cn from 'classnames'

class VtapeLayout extends Component {
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
            if (child.props.height) {
                style = {height: child.props.height, ...child.props.style}
            } else {
                style = {...child.props.style, ...styles.row};
            }
            return React.cloneElement(child, {...child.props, style});
        });
        return <View {...props} className={cn('react-view', props.className)}
                     style={[styles.wrapper, style]}>{cs}</View>
    }
}
const styles = StyleSheet.create({
    wrapper: {
        flexDirection: 'col',
        flexWrap: 'nowrap'
    },
    row: {
        flex: 1
    }
});
export default VtapeLayout
