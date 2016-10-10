import React, {
    Component,
    StyleSheet,
    View
} from 'lib'


class VerticalCenterLayout extends Component {
    constructor(props, context) {
        super(props, context);
    }

    render() {
        const {children, style, ...props} = this.props;
        return <View {...props} style={[styles.wrapper, style]}>{children}</View>
    }
}
const styles = StyleSheet.create({
    wrapper: {
        flexWrap: 'nowrap',
        justifyContent: 'center'
    }
});
export default VerticalCenterLayout
