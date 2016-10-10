import immutableShallowEqual from '../utils/immutableShallowEqual'
function compare(instance, nextProps, nextState) {
    return !immutableShallowEqual(instance.props, nextProps) || !immutableShallowEqual(instance.state, nextState);
}

var ReactComponentWithImmutableRenderMixin = {
    shouldComponentUpdate: function (nextProps, nextState) {
        return compare(this, nextProps, nextState);
    }
};

module.exports = ReactComponentWithImmutableRenderMixin;