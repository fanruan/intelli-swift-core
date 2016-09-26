import React from 'react';
const {Component} = React;

class Icon extends Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {
    }

    render() {
        const {width, height} = this.props;
        return <i style={{
            width: width,
            height: height,
            display: 'block',
            margin: '0 auto'
        }} className='x-icon b-font'></i>
    }
}

export default Icon