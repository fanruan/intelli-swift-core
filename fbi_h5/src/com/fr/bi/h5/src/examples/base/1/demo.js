/**
 * Created by Root on 2016/7/29.
 */

////子对象只是实现一个累累加的计算
//const Son = React.createClass({
//
//    propTypes: {
//        A: React.PropTypes.number,
//        onChange: React.PropTypes.func
//    },
//
//    getDefaultProps (){
//        return {
//            A: 0,
//            onChange: new Function
//        };
//    },
//
//    getInitialState(){
//        return {A: this.props.A}
//    },
//
//    componentWillReceiveProps(nextProps) {
//        this.setState({A: nextProps.A});
//    },
//
//    handleAdd() {
//        let A = ++this.state.A;
//        this.setState({A}, ()=> this.props.onChange(A));
//    },
//
//    render() {
//        return (
//            <div>
//                <button onClick={this.handleAdd}> 累加</button>
//                <div>{this.state.A} </div>
//            </div>
//        )
//    }
//});
//
////父对象
//const Dad = React.createClass({
//    getDefaultProps () {
//        return {}
//    },
//
//    getInitialState () {
//        return {
//            SA: 1
//        }
//    },
//
//    handleChange () {
//        this.setState({SA: Math.random()});
//    },
//
//    render () {
//        return (<div>
//            <Son A={this.state.SA} onChange={val => this.setState({SA: val})}/>
//            <br/>
//            <button onClick={this.handleChange}>修改基础值</button>
//            <div>{this.state.SA}</div>
//        </div>)
//    }
//
//});

//子对象只是实现一个累累加的计算
class Son extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            A: props.A
        }
    }

    static defaultProps = {
        A: 0,
        onChange: new Function
    };

    static propTypes = {
        A: React.PropTypes.number,
        onChange: React.PropTypes.func
    };

    componentWillReceiveProps(nextProps) {
        this.setState({A: nextProps.A});
    }

    handleAdd() {
        let A = ++this.state.A;
        this.setState({A}, ()=> this.props.onChange(A));
    }

    render() {
        return (
            <div>
                <button onClick={this.handleAdd.bind(this)}> 累加</button>
                <div>{this.state.A} </div>
            </div>
        )
    }
}

//父对象
class Dad extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            SA: 1
        };
    }

    handleChangeA() {
        //目的是将Son A的属性可以动态修改
        this.setState({SA: Math.random()});
    }

    render() {
        return (<div>
            <Son A={this.state.SA} onChange={val => this.setState({SA: val})}/>
            <br/>
            <button onClick={this.handleChangeA.bind(this)}>修改基础值</button>
            <div>{this.state.SA}</div>
        </div>)
    }

}