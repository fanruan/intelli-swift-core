import React, {
    Component,
    StyleSheet,
    View
} from 'lib'

import Layout from './Layout'

class VerticalCenterLayout extends Component {
    constructor(props, context) {
        super(props, context);
    }

    render() {
        const {children, ...props} = this.props;
        return <Layout cross='center'  {...props}>{children}</Layout>
    }
}
export default VerticalCenterLayout
