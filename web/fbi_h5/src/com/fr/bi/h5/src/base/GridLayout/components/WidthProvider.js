import React from "react";
import ReactDOM from 'react-dom';

/*
 * A simple HOC that provides facility for listening to container resizes.
 */
export default (ComposedComponent) => class extends React.Component {

  static defaultProps = {
    measureBeforeMount: false
  };

  static propTypes = {
    // If true, will not render children until mounted. Useful for getting the exact width before
    // rendering, to prevent any unsightly resizing.
    measureBeforeMount: React.PropTypes.bool
  };

  state = {
    mounted: false,
    width: 1280
  };

  componentDidMount() {
    this.setState({mounted: true});

    window.addEventListener('resize', this.onWindowResize);
    // Call to properly set the breakpoint and resize the elements.
    // Note that if you're doing a full-width element, this can get a little wonky if a scrollbar
    // appears because of the grid. In that case, fire your own resize event, or set `overflow: scroll` on your body.
    this.onWindowResize();
  }

  componentWillUnmount() {
    window.removeEventListener('resize', this.onWindowResize);
  }

  onWindowResize = (_event, cb) => {
    const node = ReactDOM.findDOMNode(this);
    this.setState({width: node.offsetWidth}, cb);
  }

  render() {
    if (this.props.measureBeforeMount && !this.state.mounted) return <div {...this.props} {...this.state} />;
    return <ComposedComponent {...this.props} {...this.state} />;
  }
};
