import React, {
    Component,
    StyleSheet,
    View
} from 'lib'

import Layout from './Layout'

class HorizontalCenterLayout extends Component {
    constructor(props, context) {
        super(props, context);
    }

    render() {
        const {children, ...props} = this.props;
        return <Layout main='center'  {...props}>{children}</Layout>
    }
}
export default HorizontalCenterLayout
