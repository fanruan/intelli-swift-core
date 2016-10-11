import React, {
    Component,
    StyleSheet,
    View
} from 'lib'
import cn from 'classnames'


class CenterLayout extends Component {
    constructor(props, context) {
        super(props, context);
    }

    render() {
        const {children, style, ...props} = this.props;
        return <View {...props} className={cn('react-view', props.className)}
                     style={[styles.wrapper, style]}>{children}</View>
    }
}
const styles = StyleSheet.create({
    wrapper: {
        flexWrap: 'nowrap',
        alignItems: 'center',
        justifyContent: 'center'
    }
});
export default CenterLayout
