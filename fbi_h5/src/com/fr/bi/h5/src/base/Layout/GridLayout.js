import ReactDOM from 'react-dom'
import React, {
    Component,
    StyleSheet,
    View
} from 'lib'


class GridLayout extends Component {
    constructor(props, context) {
        super(props, context);
    }

    render() {
        const {children, ...props} = this.props;
        const cs = React.Children.map(children, (child)=> {
            let style;
            if (child.props.height) {
                style = {height: child.props.height, ...child.props.style}
            } else {
                style = {...child.props.style, ...styles.row};
            }
            const children = React.Children.map(child.props.children, (ch)=> {
                let style;
                if (ch.props.width) {
                    style = {width: ch.props.width, ...ch.props.style};
                } else {
                    style = {...ch.props.style, ...styles.col};
                }
                return React.cloneElement(ch, {...ch.props, style});
            });
            return React.cloneElement(child, {...child.props, style, children});
        });
        return <View {...props}>{cs}</View>;
    }
}
const styles = StyleSheet.create({
    row: {
        flex: 1,
        flexDirection: 'row'
    },
    col: {
        flex: 1
    }
});
export default GridLayout
