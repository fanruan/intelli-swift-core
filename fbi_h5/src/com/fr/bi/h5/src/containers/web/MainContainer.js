import mixin from 'react-mixin'
import ReactDOM from 'react-dom'
import {requestAnimationFrame, ReactComponentWithImmutableRenderMixin} from 'core'
import React, {
    Component,
    StyleSheet,
    Text,
    Dimensions,
    ListView,
    View,
    Fetch,
    Navigator,
    TouchableOpacity,
    TouchableHighlight
} from 'lib'

import {Colors, Sizes, TemplateFactory} from 'data'
import {Layout} from 'layout'

import LayoutComponent from '../pad/horizontal/Layout/LayoutContainer'

class MainContainerWeb extends Component {
    static contextTypes = {
        actions: React.PropTypes.object,
        $template: React.PropTypes.object
    };

    static propTypes = {};

    constructor(props, context) {
        super(props, context);
    }

    render() {
        return <LayoutComponent {...this.props}/>;
    }

    componentWillMount() {

    }

    componentDidMount() {

    }

    componentWillReceiveProps(nextProps) {

    }

    componentWillUpdate(nextProps, nextState) {

    }

    componentDidUpdate(prevProps, prevState) {

    }

    componentWillUnmount() {

    }
}
mixin.onClass(MainContainerWeb, ReactComponentWithImmutableRenderMixin);

export default MainContainerWeb
