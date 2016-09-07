webpackJsonp([0],{

/***/ 0:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

	var _react = __webpack_require__(1);

	var _react2 = _interopRequireDefault(_react);

	var _reactDom = __webpack_require__(34);

	var _reactDom2 = _interopRequireDefault(_reactDom);

	var _reactRedux = __webpack_require__(172);

	var _stores = __webpack_require__(195);

	var _stores2 = _interopRequireDefault(_stores);

	var _App = __webpack_require__(201);

	var _App2 = _interopRequireDefault(_App);

	var _StyleSheet = __webpack_require__(203);

	var _StyleSheet2 = _interopRequireDefault(_StyleSheet);

	var _View = __webpack_require__(213);

	var _View2 = _interopRequireDefault(_View);

	var _Portal = __webpack_require__(514);

	var _Portal2 = _interopRequireDefault(_Portal);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

	function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

	function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

	var AppContainer = function (_Component) {
	    _inherits(AppContainer, _Component);

	    function AppContainer() {
	        _classCallCheck(this, AppContainer);

	        return _possibleConstructorReturn(this, (AppContainer.__proto__ || Object.getPrototypeOf(AppContainer)).apply(this, arguments));
	    }

	    _createClass(AppContainer, [{
	        key: 'render',
	        value: function render() {
	            return _react2.default.createElement(
	                _View2.default,
	                {
	                    ref: 'main',
	                    className: _StyleSheet2.default.rootClassName,
	                    style: styles.appContainer },
	                _react2.default.createElement(_App2.default, this.props),
	                _react2.default.createElement(_Portal2.default, null)
	            );
	        }
	    }]);

	    return AppContainer;
	}(_react.Component);

	var styles = _StyleSheet2.default.create({
	    // This is needed so the application covers the whole screen
	    // and therefore the contents of the React are not clipped.
	    appContainer: {
	        position: 'absolute',
	        left: 0,
	        top: 0,
	        right: 0,
	        bottom: 0
	    }
	});

	var store = (0, _stores2.default)();

	_reactDom2.default.render(_react2.default.createElement(
	    _reactRedux.Provider,
	    { store: store },
	    _react2.default.createElement(AppContainer, null)
	), document.getElementById('app'));

/***/ },

/***/ 172:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	exports.__esModule = true;
	exports.connect = exports.Provider = undefined;

	var _Provider = __webpack_require__(173);

	var _Provider2 = _interopRequireDefault(_Provider);

	var _connect = __webpack_require__(176);

	var _connect2 = _interopRequireDefault(_connect);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }

	exports.Provider = _Provider2["default"];
	exports.connect = _connect2["default"];

/***/ },

/***/ 173:
/***/ function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(process) {'use strict';

	exports.__esModule = true;
	exports["default"] = undefined;

	var _react = __webpack_require__(1);

	var _storeShape = __webpack_require__(174);

	var _storeShape2 = _interopRequireDefault(_storeShape);

	var _warning = __webpack_require__(175);

	var _warning2 = _interopRequireDefault(_warning);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

	function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

	function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

	var didWarnAboutReceivingStore = false;
	function warnAboutReceivingStore() {
	  if (didWarnAboutReceivingStore) {
	    return;
	  }
	  didWarnAboutReceivingStore = true;

	  (0, _warning2["default"])('<Provider> does not support changing `store` on the fly. ' + 'It is most likely that you see this error because you updated to ' + 'Redux 2.x and React Redux 2.x which no longer hot reload reducers ' + 'automatically. See https://github.com/reactjs/react-redux/releases/' + 'tag/v2.0.0 for the migration instructions.');
	}

	var Provider = function (_Component) {
	  _inherits(Provider, _Component);

	  Provider.prototype.getChildContext = function getChildContext() {
	    return { store: this.store };
	  };

	  function Provider(props, context) {
	    _classCallCheck(this, Provider);

	    var _this = _possibleConstructorReturn(this, _Component.call(this, props, context));

	    _this.store = props.store;
	    return _this;
	  }

	  Provider.prototype.render = function render() {
	    var children = this.props.children;

	    return _react.Children.only(children);
	  };

	  return Provider;
	}(_react.Component);

	exports["default"] = Provider;

	if (process.env.NODE_ENV !== 'production') {
	  Provider.prototype.componentWillReceiveProps = function (nextProps) {
	    var store = this.store;
	    var nextStore = nextProps.store;

	    if (store !== nextStore) {
	      warnAboutReceivingStore();
	    }
	  };
	}

	Provider.propTypes = {
	  store: _storeShape2["default"].isRequired,
	  children: _react.PropTypes.element.isRequired
	};
	Provider.childContextTypes = {
	  store: _storeShape2["default"].isRequired
	};
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(3)))

/***/ },

/***/ 174:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	exports.__esModule = true;

	var _react = __webpack_require__(1);

	exports["default"] = _react.PropTypes.shape({
	  subscribe: _react.PropTypes.func.isRequired,
	  dispatch: _react.PropTypes.func.isRequired,
	  getState: _react.PropTypes.func.isRequired
	});

/***/ },

/***/ 175:
/***/ function(module, exports) {

	'use strict';

	exports.__esModule = true;
	exports["default"] = warning;
	/**
	 * Prints a warning in the console if it exists.
	 *
	 * @param {String} message The warning message.
	 * @returns {void}
	 */
	function warning(message) {
	  /* eslint-disable no-console */
	  if (typeof console !== 'undefined' && typeof console.error === 'function') {
	    console.error(message);
	  }
	  /* eslint-enable no-console */
	  try {
	    // This error was thrown as a convenience so that you can use this stack
	    // to find the callsite that caused this warning to fire.
	    throw new Error(message);
	    /* eslint-disable no-empty */
	  } catch (e) {}
	  /* eslint-enable no-empty */
	}

/***/ },

/***/ 176:
/***/ function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(process) {'use strict';

	var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

	exports.__esModule = true;
	exports["default"] = connect;

	var _react = __webpack_require__(1);

	var _storeShape = __webpack_require__(174);

	var _storeShape2 = _interopRequireDefault(_storeShape);

	var _shallowEqual = __webpack_require__(177);

	var _shallowEqual2 = _interopRequireDefault(_shallowEqual);

	var _wrapActionCreators = __webpack_require__(178);

	var _wrapActionCreators2 = _interopRequireDefault(_wrapActionCreators);

	var _warning = __webpack_require__(175);

	var _warning2 = _interopRequireDefault(_warning);

	var _isPlainObject = __webpack_require__(181);

	var _isPlainObject2 = _interopRequireDefault(_isPlainObject);

	var _hoistNonReactStatics = __webpack_require__(193);

	var _hoistNonReactStatics2 = _interopRequireDefault(_hoistNonReactStatics);

	var _invariant = __webpack_require__(194);

	var _invariant2 = _interopRequireDefault(_invariant);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

	function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

	function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

	var defaultMapStateToProps = function defaultMapStateToProps(state) {
	  return {};
	}; // eslint-disable-line no-unused-vars
	var defaultMapDispatchToProps = function defaultMapDispatchToProps(dispatch) {
	  return { dispatch: dispatch };
	};
	var defaultMergeProps = function defaultMergeProps(stateProps, dispatchProps, parentProps) {
	  return _extends({}, parentProps, stateProps, dispatchProps);
	};

	function getDisplayName(WrappedComponent) {
	  return WrappedComponent.displayName || WrappedComponent.name || 'Component';
	}

	var errorObject = { value: null };
	function tryCatch(fn, ctx) {
	  try {
	    return fn.apply(ctx);
	  } catch (e) {
	    errorObject.value = e;
	    return errorObject;
	  }
	}

	// Helps track hot reloading.
	var nextVersion = 0;

	function connect(mapStateToProps, mapDispatchToProps, mergeProps) {
	  var options = arguments.length <= 3 || arguments[3] === undefined ? {} : arguments[3];

	  var shouldSubscribe = Boolean(mapStateToProps);
	  var mapState = mapStateToProps || defaultMapStateToProps;

	  var mapDispatch = undefined;
	  if (typeof mapDispatchToProps === 'function') {
	    mapDispatch = mapDispatchToProps;
	  } else if (!mapDispatchToProps) {
	    mapDispatch = defaultMapDispatchToProps;
	  } else {
	    mapDispatch = (0, _wrapActionCreators2["default"])(mapDispatchToProps);
	  }

	  var finalMergeProps = mergeProps || defaultMergeProps;
	  var _options$pure = options.pure;
	  var pure = _options$pure === undefined ? true : _options$pure;
	  var _options$withRef = options.withRef;
	  var withRef = _options$withRef === undefined ? false : _options$withRef;

	  var checkMergedEquals = pure && finalMergeProps !== defaultMergeProps;

	  // Helps track hot reloading.
	  var version = nextVersion++;

	  return function wrapWithConnect(WrappedComponent) {
	    var connectDisplayName = 'Connect(' + getDisplayName(WrappedComponent) + ')';

	    function checkStateShape(props, methodName) {
	      if (!(0, _isPlainObject2["default"])(props)) {
	        (0, _warning2["default"])(methodName + '() in ' + connectDisplayName + ' must return a plain object. ' + ('Instead received ' + props + '.'));
	      }
	    }

	    function computeMergedProps(stateProps, dispatchProps, parentProps) {
	      var mergedProps = finalMergeProps(stateProps, dispatchProps, parentProps);
	      if (process.env.NODE_ENV !== 'production') {
	        checkStateShape(mergedProps, 'mergeProps');
	      }
	      return mergedProps;
	    }

	    var Connect = function (_Component) {
	      _inherits(Connect, _Component);

	      Connect.prototype.shouldComponentUpdate = function shouldComponentUpdate() {
	        return !pure || this.haveOwnPropsChanged || this.hasStoreStateChanged;
	      };

	      function Connect(props, context) {
	        _classCallCheck(this, Connect);

	        var _this = _possibleConstructorReturn(this, _Component.call(this, props, context));

	        _this.version = version;
	        _this.store = props.store || context.store;

	        (0, _invariant2["default"])(_this.store, 'Could not find "store" in either the context or ' + ('props of "' + connectDisplayName + '". ') + 'Either wrap the root component in a <Provider>, ' + ('or explicitly pass "store" as a prop to "' + connectDisplayName + '".'));

	        var storeState = _this.store.getState();
	        _this.state = { storeState: storeState };
	        _this.clearCache();
	        return _this;
	      }

	      Connect.prototype.computeStateProps = function computeStateProps(store, props) {
	        if (!this.finalMapStateToProps) {
	          return this.configureFinalMapState(store, props);
	        }

	        var state = store.getState();
	        var stateProps = this.doStatePropsDependOnOwnProps ? this.finalMapStateToProps(state, props) : this.finalMapStateToProps(state);

	        if (process.env.NODE_ENV !== 'production') {
	          checkStateShape(stateProps, 'mapStateToProps');
	        }
	        return stateProps;
	      };

	      Connect.prototype.configureFinalMapState = function configureFinalMapState(store, props) {
	        var mappedState = mapState(store.getState(), props);
	        var isFactory = typeof mappedState === 'function';

	        this.finalMapStateToProps = isFactory ? mappedState : mapState;
	        this.doStatePropsDependOnOwnProps = this.finalMapStateToProps.length !== 1;

	        if (isFactory) {
	          return this.computeStateProps(store, props);
	        }

	        if (process.env.NODE_ENV !== 'production') {
	          checkStateShape(mappedState, 'mapStateToProps');
	        }
	        return mappedState;
	      };

	      Connect.prototype.computeDispatchProps = function computeDispatchProps(store, props) {
	        if (!this.finalMapDispatchToProps) {
	          return this.configureFinalMapDispatch(store, props);
	        }

	        var dispatch = store.dispatch;

	        var dispatchProps = this.doDispatchPropsDependOnOwnProps ? this.finalMapDispatchToProps(dispatch, props) : this.finalMapDispatchToProps(dispatch);

	        if (process.env.NODE_ENV !== 'production') {
	          checkStateShape(dispatchProps, 'mapDispatchToProps');
	        }
	        return dispatchProps;
	      };

	      Connect.prototype.configureFinalMapDispatch = function configureFinalMapDispatch(store, props) {
	        var mappedDispatch = mapDispatch(store.dispatch, props);
	        var isFactory = typeof mappedDispatch === 'function';

	        this.finalMapDispatchToProps = isFactory ? mappedDispatch : mapDispatch;
	        this.doDispatchPropsDependOnOwnProps = this.finalMapDispatchToProps.length !== 1;

	        if (isFactory) {
	          return this.computeDispatchProps(store, props);
	        }

	        if (process.env.NODE_ENV !== 'production') {
	          checkStateShape(mappedDispatch, 'mapDispatchToProps');
	        }
	        return mappedDispatch;
	      };

	      Connect.prototype.updateStatePropsIfNeeded = function updateStatePropsIfNeeded() {
	        var nextStateProps = this.computeStateProps(this.store, this.props);
	        if (this.stateProps && (0, _shallowEqual2["default"])(nextStateProps, this.stateProps)) {
	          return false;
	        }

	        this.stateProps = nextStateProps;
	        return true;
	      };

	      Connect.prototype.updateDispatchPropsIfNeeded = function updateDispatchPropsIfNeeded() {
	        var nextDispatchProps = this.computeDispatchProps(this.store, this.props);
	        if (this.dispatchProps && (0, _shallowEqual2["default"])(nextDispatchProps, this.dispatchProps)) {
	          return false;
	        }

	        this.dispatchProps = nextDispatchProps;
	        return true;
	      };

	      Connect.prototype.updateMergedPropsIfNeeded = function updateMergedPropsIfNeeded() {
	        var nextMergedProps = computeMergedProps(this.stateProps, this.dispatchProps, this.props);
	        if (this.mergedProps && checkMergedEquals && (0, _shallowEqual2["default"])(nextMergedProps, this.mergedProps)) {
	          return false;
	        }

	        this.mergedProps = nextMergedProps;
	        return true;
	      };

	      Connect.prototype.isSubscribed = function isSubscribed() {
	        return typeof this.unsubscribe === 'function';
	      };

	      Connect.prototype.trySubscribe = function trySubscribe() {
	        if (shouldSubscribe && !this.unsubscribe) {
	          this.unsubscribe = this.store.subscribe(this.handleChange.bind(this));
	          this.handleChange();
	        }
	      };

	      Connect.prototype.tryUnsubscribe = function tryUnsubscribe() {
	        if (this.unsubscribe) {
	          this.unsubscribe();
	          this.unsubscribe = null;
	        }
	      };

	      Connect.prototype.componentDidMount = function componentDidMount() {
	        this.trySubscribe();
	      };

	      Connect.prototype.componentWillReceiveProps = function componentWillReceiveProps(nextProps) {
	        if (!pure || !(0, _shallowEqual2["default"])(nextProps, this.props)) {
	          this.haveOwnPropsChanged = true;
	        }
	      };

	      Connect.prototype.componentWillUnmount = function componentWillUnmount() {
	        this.tryUnsubscribe();
	        this.clearCache();
	      };

	      Connect.prototype.clearCache = function clearCache() {
	        this.dispatchProps = null;
	        this.stateProps = null;
	        this.mergedProps = null;
	        this.haveOwnPropsChanged = true;
	        this.hasStoreStateChanged = true;
	        this.haveStatePropsBeenPrecalculated = false;
	        this.statePropsPrecalculationError = null;
	        this.renderedElement = null;
	        this.finalMapDispatchToProps = null;
	        this.finalMapStateToProps = null;
	      };

	      Connect.prototype.handleChange = function handleChange() {
	        if (!this.unsubscribe) {
	          return;
	        }

	        var storeState = this.store.getState();
	        var prevStoreState = this.state.storeState;
	        if (pure && prevStoreState === storeState) {
	          return;
	        }

	        if (pure && !this.doStatePropsDependOnOwnProps) {
	          var haveStatePropsChanged = tryCatch(this.updateStatePropsIfNeeded, this);
	          if (!haveStatePropsChanged) {
	            return;
	          }
	          if (haveStatePropsChanged === errorObject) {
	            this.statePropsPrecalculationError = errorObject.value;
	          }
	          this.haveStatePropsBeenPrecalculated = true;
	        }

	        this.hasStoreStateChanged = true;
	        this.setState({ storeState: storeState });
	      };

	      Connect.prototype.getWrappedInstance = function getWrappedInstance() {
	        (0, _invariant2["default"])(withRef, 'To access the wrapped instance, you need to specify ' + '{ withRef: true } as the fourth argument of the connect() call.');

	        return this.refs.wrappedInstance;
	      };

	      Connect.prototype.render = function render() {
	        var haveOwnPropsChanged = this.haveOwnPropsChanged;
	        var hasStoreStateChanged = this.hasStoreStateChanged;
	        var haveStatePropsBeenPrecalculated = this.haveStatePropsBeenPrecalculated;
	        var statePropsPrecalculationError = this.statePropsPrecalculationError;
	        var renderedElement = this.renderedElement;

	        this.haveOwnPropsChanged = false;
	        this.hasStoreStateChanged = false;
	        this.haveStatePropsBeenPrecalculated = false;
	        this.statePropsPrecalculationError = null;

	        if (statePropsPrecalculationError) {
	          throw statePropsPrecalculationError;
	        }

	        var shouldUpdateStateProps = true;
	        var shouldUpdateDispatchProps = true;
	        if (pure && renderedElement) {
	          shouldUpdateStateProps = hasStoreStateChanged || haveOwnPropsChanged && this.doStatePropsDependOnOwnProps;
	          shouldUpdateDispatchProps = haveOwnPropsChanged && this.doDispatchPropsDependOnOwnProps;
	        }

	        var haveStatePropsChanged = false;
	        var haveDispatchPropsChanged = false;
	        if (haveStatePropsBeenPrecalculated) {
	          haveStatePropsChanged = true;
	        } else if (shouldUpdateStateProps) {
	          haveStatePropsChanged = this.updateStatePropsIfNeeded();
	        }
	        if (shouldUpdateDispatchProps) {
	          haveDispatchPropsChanged = this.updateDispatchPropsIfNeeded();
	        }

	        var haveMergedPropsChanged = true;
	        if (haveStatePropsChanged || haveDispatchPropsChanged || haveOwnPropsChanged) {
	          haveMergedPropsChanged = this.updateMergedPropsIfNeeded();
	        } else {
	          haveMergedPropsChanged = false;
	        }

	        if (!haveMergedPropsChanged && renderedElement) {
	          return renderedElement;
	        }

	        if (withRef) {
	          this.renderedElement = (0, _react.createElement)(WrappedComponent, _extends({}, this.mergedProps, {
	            ref: 'wrappedInstance'
	          }));
	        } else {
	          this.renderedElement = (0, _react.createElement)(WrappedComponent, this.mergedProps);
	        }

	        return this.renderedElement;
	      };

	      return Connect;
	    }(_react.Component);

	    Connect.displayName = connectDisplayName;
	    Connect.WrappedComponent = WrappedComponent;
	    Connect.contextTypes = {
	      store: _storeShape2["default"]
	    };
	    Connect.propTypes = {
	      store: _storeShape2["default"]
	    };

	    if (process.env.NODE_ENV !== 'production') {
	      Connect.prototype.componentWillUpdate = function componentWillUpdate() {
	        if (this.version === version) {
	          return;
	        }

	        // We are hot reloading!
	        this.version = version;
	        this.trySubscribe();
	        this.clearCache();
	      };
	    }

	    return (0, _hoistNonReactStatics2["default"])(Connect, WrappedComponent);
	  };
	}
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(3)))

/***/ },

/***/ 177:
/***/ function(module, exports) {

	"use strict";

	exports.__esModule = true;
	exports["default"] = shallowEqual;
	function shallowEqual(objA, objB) {
	  if (objA === objB) {
	    return true;
	  }

	  var keysA = Object.keys(objA);
	  var keysB = Object.keys(objB);

	  if (keysA.length !== keysB.length) {
	    return false;
	  }

	  // Test for A's keys different from B.
	  var hasOwn = Object.prototype.hasOwnProperty;
	  for (var i = 0; i < keysA.length; i++) {
	    if (!hasOwn.call(objB, keysA[i]) || objA[keysA[i]] !== objB[keysA[i]]) {
	      return false;
	    }
	  }

	  return true;
	}

/***/ },

/***/ 178:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	exports.__esModule = true;
	exports["default"] = wrapActionCreators;

	var _redux = __webpack_require__(179);

	function wrapActionCreators(actionCreators) {
	  return function (dispatch) {
	    return (0, _redux.bindActionCreators)(actionCreators, dispatch);
	  };
	}

/***/ },

/***/ 179:
/***/ function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(process) {'use strict';

	exports.__esModule = true;
	exports.compose = exports.applyMiddleware = exports.bindActionCreators = exports.combineReducers = exports.createStore = undefined;

	var _createStore = __webpack_require__(180);

	var _createStore2 = _interopRequireDefault(_createStore);

	var _combineReducers = __webpack_require__(188);

	var _combineReducers2 = _interopRequireDefault(_combineReducers);

	var _bindActionCreators = __webpack_require__(190);

	var _bindActionCreators2 = _interopRequireDefault(_bindActionCreators);

	var _applyMiddleware = __webpack_require__(191);

	var _applyMiddleware2 = _interopRequireDefault(_applyMiddleware);

	var _compose = __webpack_require__(192);

	var _compose2 = _interopRequireDefault(_compose);

	var _warning = __webpack_require__(189);

	var _warning2 = _interopRequireDefault(_warning);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }

	/*
	* This is a dummy function to check if the function name has been altered by minification.
	* If the function has been minified and NODE_ENV !== 'production', warn the user.
	*/
	function isCrushed() {}

	if (process.env.NODE_ENV !== 'production' && typeof isCrushed.name === 'string' && isCrushed.name !== 'isCrushed') {
	  (0, _warning2["default"])('You are currently using minified code outside of NODE_ENV === \'production\'. ' + 'This means that you are running a slower development build of Redux. ' + 'You can use loose-envify (https://github.com/zertosh/loose-envify) for browserify ' + 'or DefinePlugin for webpack (http://stackoverflow.com/questions/30030031) ' + 'to ensure you have the correct code for your production build.');
	}

	exports.createStore = _createStore2["default"];
	exports.combineReducers = _combineReducers2["default"];
	exports.bindActionCreators = _bindActionCreators2["default"];
	exports.applyMiddleware = _applyMiddleware2["default"];
	exports.compose = _compose2["default"];
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(3)))

/***/ },

/***/ 180:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	exports.__esModule = true;
	exports.ActionTypes = undefined;
	exports["default"] = createStore;

	var _isPlainObject = __webpack_require__(181);

	var _isPlainObject2 = _interopRequireDefault(_isPlainObject);

	var _symbolObservable = __webpack_require__(186);

	var _symbolObservable2 = _interopRequireDefault(_symbolObservable);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }

	/**
	 * These are private action types reserved by Redux.
	 * For any unknown actions, you must return the current state.
	 * If the current state is undefined, you must return the initial state.
	 * Do not reference these action types directly in your code.
	 */
	var ActionTypes = exports.ActionTypes = {
	  INIT: '@@redux/INIT'
	};

	/**
	 * Creates a Redux store that holds the state tree.
	 * The only way to change the data in the store is to call `dispatch()` on it.
	 *
	 * There should only be a single store in your app. To specify how different
	 * parts of the state tree respond to actions, you may combine several reducers
	 * into a single reducer function by using `combineReducers`.
	 *
	 * @param {Function} reducer A function that returns the next state tree, given
	 * the current state tree and the action to handle.
	 *
	 * @param {any} [initialState] The initial state. You may optionally specify it
	 * to hydrate the state from the server in universal apps, or to restore a
	 * previously serialized user session.
	 * If you use `combineReducers` to produce the root reducer function, this must be
	 * an object with the same shape as `combineReducers` keys.
	 *
	 * @param {Function} enhancer The store enhancer. You may optionally specify it
	 * to enhance the store with third-party capabilities such as middleware,
	 * time travel, persistence, etc. The only store enhancer that ships with Redux
	 * is `applyMiddleware()`.
	 *
	 * @returns {Store} A Redux store that lets you read the state, dispatch actions
	 * and subscribe to changes.
	 */
	function createStore(reducer, initialState, enhancer) {
	  var _ref2;

	  if (typeof initialState === 'function' && typeof enhancer === 'undefined') {
	    enhancer = initialState;
	    initialState = undefined;
	  }

	  if (typeof enhancer !== 'undefined') {
	    if (typeof enhancer !== 'function') {
	      throw new Error('Expected the enhancer to be a function.');
	    }

	    return enhancer(createStore)(reducer, initialState);
	  }

	  if (typeof reducer !== 'function') {
	    throw new Error('Expected the reducer to be a function.');
	  }

	  var currentReducer = reducer;
	  var currentState = initialState;
	  var currentListeners = [];
	  var nextListeners = currentListeners;
	  var isDispatching = false;

	  function ensureCanMutateNextListeners() {
	    if (nextListeners === currentListeners) {
	      nextListeners = currentListeners.slice();
	    }
	  }

	  /**
	   * Reads the state tree managed by the store.
	   *
	   * @returns {any} The current state tree of your application.
	   */
	  function getState() {
	    return currentState;
	  }

	  /**
	   * Adds a change listener. It will be called any time an action is dispatched,
	   * and some part of the state tree may potentially have changed. You may then
	   * call `getState()` to read the current state tree inside the callback.
	   *
	   * You may call `dispatch()` from a change listener, with the following
	   * caveats:
	   *
	   * 1. The subscriptions are snapshotted just before every `dispatch()` call.
	   * If you subscribe or unsubscribe while the listeners are being invoked, this
	   * will not have any effect on the `dispatch()` that is currently in progress.
	   * However, the next `dispatch()` call, whether nested or not, will use a more
	   * recent snapshot of the subscription list.
	   *
	   * 2. The listener should not expect to see all state changes, as the state
	   * might have been updated multiple times during a nested `dispatch()` before
	   * the listener is called. It is, however, guaranteed that all subscribers
	   * registered before the `dispatch()` started will be called with the latest
	   * state by the time it exits.
	   *
	   * @param {Function} listener A callback to be invoked on every dispatch.
	   * @returns {Function} A function to remove this change listener.
	   */
	  function subscribe(listener) {
	    if (typeof listener !== 'function') {
	      throw new Error('Expected listener to be a function.');
	    }

	    var isSubscribed = true;

	    ensureCanMutateNextListeners();
	    nextListeners.push(listener);

	    return function unsubscribe() {
	      if (!isSubscribed) {
	        return;
	      }

	      isSubscribed = false;

	      ensureCanMutateNextListeners();
	      var index = nextListeners.indexOf(listener);
	      nextListeners.splice(index, 1);
	    };
	  }

	  /**
	   * Dispatches an action. It is the only way to trigger a state change.
	   *
	   * The `reducer` function, used to create the store, will be called with the
	   * current state tree and the given `action`. Its return value will
	   * be considered the **next** state of the tree, and the change listeners
	   * will be notified.
	   *
	   * The base implementation only supports plain object actions. If you want to
	   * dispatch a Promise, an Observable, a thunk, or something else, you need to
	   * wrap your store creating function into the corresponding middleware. For
	   * example, see the documentation for the `redux-thunk` package. Even the
	   * middleware will eventually dispatch plain object actions using this method.
	   *
	   * @param {Object} action A plain object representing “what changed”. It is
	   * a good idea to keep actions serializable so you can record and replay user
	   * sessions, or use the time travelling `redux-devtools`. An action must have
	   * a `type` property which may not be `undefined`. It is a good idea to use
	   * string constants for action types.
	   *
	   * @returns {Object} For convenience, the same action object you dispatched.
	   *
	   * Note that, if you use a custom middleware, it may wrap `dispatch()` to
	   * return something else (for example, a Promise you can await).
	   */
	  function dispatch(action) {
	    if (!(0, _isPlainObject2["default"])(action)) {
	      throw new Error('Actions must be plain objects. ' + 'Use custom middleware for async actions.');
	    }

	    if (typeof action.type === 'undefined') {
	      throw new Error('Actions may not have an undefined "type" property. ' + 'Have you misspelled a constant?');
	    }

	    if (isDispatching) {
	      throw new Error('Reducers may not dispatch actions.');
	    }

	    try {
	      isDispatching = true;
	      currentState = currentReducer(currentState, action);
	    } finally {
	      isDispatching = false;
	    }

	    var listeners = currentListeners = nextListeners;
	    for (var i = 0; i < listeners.length; i++) {
	      listeners[i]();
	    }

	    return action;
	  }

	  /**
	   * Replaces the reducer currently used by the store to calculate the state.
	   *
	   * You might need this if your app implements code splitting and you want to
	   * load some of the reducers dynamically. You might also need this if you
	   * implement a hot reloading mechanism for Redux.
	   *
	   * @param {Function} nextReducer The reducer for the store to use instead.
	   * @returns {void}
	   */
	  function replaceReducer(nextReducer) {
	    if (typeof nextReducer !== 'function') {
	      throw new Error('Expected the nextReducer to be a function.');
	    }

	    currentReducer = nextReducer;
	    dispatch({ type: ActionTypes.INIT });
	  }

	  /**
	   * Interoperability point for observable/reactive libraries.
	   * @returns {observable} A minimal observable of state changes.
	   * For more information, see the observable proposal:
	   * https://github.com/zenparsing/es-observable
	   */
	  function observable() {
	    var _ref;

	    var outerSubscribe = subscribe;
	    return _ref = {
	      /**
	       * The minimal observable subscription method.
	       * @param {Object} observer Any object that can be used as an observer.
	       * The observer object should have a `next` method.
	       * @returns {subscription} An object with an `unsubscribe` method that can
	       * be used to unsubscribe the observable from the store, and prevent further
	       * emission of values from the observable.
	       */

	      subscribe: function subscribe(observer) {
	        if (typeof observer !== 'object') {
	          throw new TypeError('Expected the observer to be an object.');
	        }

	        function observeState() {
	          if (observer.next) {
	            observer.next(getState());
	          }
	        }

	        observeState();
	        var unsubscribe = outerSubscribe(observeState);
	        return { unsubscribe: unsubscribe };
	      }
	    }, _ref[_symbolObservable2["default"]] = function () {
	      return this;
	    }, _ref;
	  }

	  // When a store is created, an "INIT" action is dispatched so that every
	  // reducer returns their initial state. This effectively populates
	  // the initial state tree.
	  dispatch({ type: ActionTypes.INIT });

	  return _ref2 = {
	    dispatch: dispatch,
	    subscribe: subscribe,
	    getState: getState,
	    replaceReducer: replaceReducer
	  }, _ref2[_symbolObservable2["default"]] = observable, _ref2;
	}

/***/ },

/***/ 181:
/***/ function(module, exports, __webpack_require__) {

	var getPrototype = __webpack_require__(182),
	    isHostObject = __webpack_require__(184),
	    isObjectLike = __webpack_require__(185);

	/** `Object#toString` result references. */
	var objectTag = '[object Object]';

	/** Used for built-in method references. */
	var funcProto = Function.prototype,
	    objectProto = Object.prototype;

	/** Used to resolve the decompiled source of functions. */
	var funcToString = funcProto.toString;

	/** Used to check objects for own properties. */
	var hasOwnProperty = objectProto.hasOwnProperty;

	/** Used to infer the `Object` constructor. */
	var objectCtorString = funcToString.call(Object);

	/**
	 * Used to resolve the
	 * [`toStringTag`](http://ecma-international.org/ecma-262/7.0/#sec-object.prototype.tostring)
	 * of values.
	 */
	var objectToString = objectProto.toString;

	/**
	 * Checks if `value` is a plain object, that is, an object created by the
	 * `Object` constructor or one with a `[[Prototype]]` of `null`.
	 *
	 * @static
	 * @memberOf _
	 * @since 0.8.0
	 * @category Lang
	 * @param {*} value The value to check.
	 * @returns {boolean} Returns `true` if `value` is a plain object, else `false`.
	 * @example
	 *
	 * function Foo() {
	 *   this.a = 1;
	 * }
	 *
	 * _.isPlainObject(new Foo);
	 * // => false
	 *
	 * _.isPlainObject([1, 2, 3]);
	 * // => false
	 *
	 * _.isPlainObject({ 'x': 0, 'y': 0 });
	 * // => true
	 *
	 * _.isPlainObject(Object.create(null));
	 * // => true
	 */
	function isPlainObject(value) {
	  if (!isObjectLike(value) ||
	      objectToString.call(value) != objectTag || isHostObject(value)) {
	    return false;
	  }
	  var proto = getPrototype(value);
	  if (proto === null) {
	    return true;
	  }
	  var Ctor = hasOwnProperty.call(proto, 'constructor') && proto.constructor;
	  return (typeof Ctor == 'function' &&
	    Ctor instanceof Ctor && funcToString.call(Ctor) == objectCtorString);
	}

	module.exports = isPlainObject;


/***/ },

/***/ 182:
/***/ function(module, exports, __webpack_require__) {

	var overArg = __webpack_require__(183);

	/** Built-in value references. */
	var getPrototype = overArg(Object.getPrototypeOf, Object);

	module.exports = getPrototype;


/***/ },

/***/ 186:
/***/ function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(global) {/* global window */
	'use strict';

	module.exports = __webpack_require__(187)(global || window || this);

	/* WEBPACK VAR INJECTION */}.call(exports, (function() { return this; }())))

/***/ },

/***/ 187:
/***/ function(module, exports) {

	'use strict';

	module.exports = function symbolObservablePonyfill(root) {
		var result;
		var Symbol = root.Symbol;

		if (typeof Symbol === 'function') {
			if (Symbol.observable) {
				result = Symbol.observable;
			} else {
				result = Symbol('observable');
				Symbol.observable = result;
			}
		} else {
			result = '@@observable';
		}

		return result;
	};


/***/ },

/***/ 188:
/***/ function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(process) {'use strict';

	exports.__esModule = true;
	exports["default"] = combineReducers;

	var _createStore = __webpack_require__(180);

	var _isPlainObject = __webpack_require__(181);

	var _isPlainObject2 = _interopRequireDefault(_isPlainObject);

	var _warning = __webpack_require__(189);

	var _warning2 = _interopRequireDefault(_warning);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }

	function getUndefinedStateErrorMessage(key, action) {
	  var actionType = action && action.type;
	  var actionName = actionType && '"' + actionType.toString() + '"' || 'an action';

	  return 'Given action ' + actionName + ', reducer "' + key + '" returned undefined. ' + 'To ignore an action, you must explicitly return the previous state.';
	}

	function getUnexpectedStateShapeWarningMessage(inputState, reducers, action) {
	  var reducerKeys = Object.keys(reducers);
	  var argumentName = action && action.type === _createStore.ActionTypes.INIT ? 'initialState argument passed to createStore' : 'previous state received by the reducer';

	  if (reducerKeys.length === 0) {
	    return 'Store does not have a valid reducer. Make sure the argument passed ' + 'to combineReducers is an object whose values are reducers.';
	  }

	  if (!(0, _isPlainObject2["default"])(inputState)) {
	    return 'The ' + argumentName + ' has unexpected type of "' + {}.toString.call(inputState).match(/\s([a-z|A-Z]+)/)[1] + '". Expected argument to be an object with the following ' + ('keys: "' + reducerKeys.join('", "') + '"');
	  }

	  var unexpectedKeys = Object.keys(inputState).filter(function (key) {
	    return !reducers.hasOwnProperty(key);
	  });

	  if (unexpectedKeys.length > 0) {
	    return 'Unexpected ' + (unexpectedKeys.length > 1 ? 'keys' : 'key') + ' ' + ('"' + unexpectedKeys.join('", "') + '" found in ' + argumentName + '. ') + 'Expected to find one of the known reducer keys instead: ' + ('"' + reducerKeys.join('", "') + '". Unexpected keys will be ignored.');
	  }
	}

	function assertReducerSanity(reducers) {
	  Object.keys(reducers).forEach(function (key) {
	    var reducer = reducers[key];
	    var initialState = reducer(undefined, { type: _createStore.ActionTypes.INIT });

	    if (typeof initialState === 'undefined') {
	      throw new Error('Reducer "' + key + '" returned undefined during initialization. ' + 'If the state passed to the reducer is undefined, you must ' + 'explicitly return the initial state. The initial state may ' + 'not be undefined.');
	    }

	    var type = '@@redux/PROBE_UNKNOWN_ACTION_' + Math.random().toString(36).substring(7).split('').join('.');
	    if (typeof reducer(undefined, { type: type }) === 'undefined') {
	      throw new Error('Reducer "' + key + '" returned undefined when probed with a random type. ' + ('Don\'t try to handle ' + _createStore.ActionTypes.INIT + ' or other actions in "redux/*" ') + 'namespace. They are considered private. Instead, you must return the ' + 'current state for any unknown actions, unless it is undefined, ' + 'in which case you must return the initial state, regardless of the ' + 'action type. The initial state may not be undefined.');
	    }
	  });
	}

	/**
	 * Turns an object whose values are different reducer functions, into a single
	 * reducer function. It will call every child reducer, and gather their results
	 * into a single state object, whose keys correspond to the keys of the passed
	 * reducer functions.
	 *
	 * @param {Object} reducers An object whose values correspond to different
	 * reducer functions that need to be combined into one. One handy way to obtain
	 * it is to use ES6 `import * as reducers` syntax. The reducers may never return
	 * undefined for any action. Instead, they should return their initial state
	 * if the state passed to them was undefined, and the current state for any
	 * unrecognized action.
	 *
	 * @returns {Function} A reducer function that invokes every reducer inside the
	 * passed object, and builds a state object with the same shape.
	 */
	function combineReducers(reducers) {
	  var reducerKeys = Object.keys(reducers);
	  var finalReducers = {};
	  for (var i = 0; i < reducerKeys.length; i++) {
	    var key = reducerKeys[i];
	    if (typeof reducers[key] === 'function') {
	      finalReducers[key] = reducers[key];
	    }
	  }
	  var finalReducerKeys = Object.keys(finalReducers);

	  var sanityError;
	  try {
	    assertReducerSanity(finalReducers);
	  } catch (e) {
	    sanityError = e;
	  }

	  return function combination() {
	    var state = arguments.length <= 0 || arguments[0] === undefined ? {} : arguments[0];
	    var action = arguments[1];

	    if (sanityError) {
	      throw sanityError;
	    }

	    if (process.env.NODE_ENV !== 'production') {
	      var warningMessage = getUnexpectedStateShapeWarningMessage(state, finalReducers, action);
	      if (warningMessage) {
	        (0, _warning2["default"])(warningMessage);
	      }
	    }

	    var hasChanged = false;
	    var nextState = {};
	    for (var i = 0; i < finalReducerKeys.length; i++) {
	      var key = finalReducerKeys[i];
	      var reducer = finalReducers[key];
	      var previousStateForKey = state[key];
	      var nextStateForKey = reducer(previousStateForKey, action);
	      if (typeof nextStateForKey === 'undefined') {
	        var errorMessage = getUndefinedStateErrorMessage(key, action);
	        throw new Error(errorMessage);
	      }
	      nextState[key] = nextStateForKey;
	      hasChanged = hasChanged || nextStateForKey !== previousStateForKey;
	    }
	    return hasChanged ? nextState : state;
	  };
	}
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(3)))

/***/ },

/***/ 189:
/***/ function(module, exports) {

	'use strict';

	exports.__esModule = true;
	exports["default"] = warning;
	/**
	 * Prints a warning in the console if it exists.
	 *
	 * @param {String} message The warning message.
	 * @returns {void}
	 */
	function warning(message) {
	  /* eslint-disable no-console */
	  if (typeof console !== 'undefined' && typeof console.error === 'function') {
	    console.error(message);
	  }
	  /* eslint-enable no-console */
	  try {
	    // This error was thrown as a convenience so that if you enable
	    // "break on all exceptions" in your console,
	    // it would pause the execution at this line.
	    throw new Error(message);
	    /* eslint-disable no-empty */
	  } catch (e) {}
	  /* eslint-enable no-empty */
	}

/***/ },

/***/ 190:
/***/ function(module, exports) {

	'use strict';

	exports.__esModule = true;
	exports["default"] = bindActionCreators;
	function bindActionCreator(actionCreator, dispatch) {
	  return function () {
	    return dispatch(actionCreator.apply(undefined, arguments));
	  };
	}

	/**
	 * Turns an object whose values are action creators, into an object with the
	 * same keys, but with every function wrapped into a `dispatch` call so they
	 * may be invoked directly. This is just a convenience method, as you can call
	 * `store.dispatch(MyActionCreators.doSomething())` yourself just fine.
	 *
	 * For convenience, you can also pass a single function as the first argument,
	 * and get a function in return.
	 *
	 * @param {Function|Object} actionCreators An object whose values are action
	 * creator functions. One handy way to obtain it is to use ES6 `import * as`
	 * syntax. You may also pass a single function.
	 *
	 * @param {Function} dispatch The `dispatch` function available on your Redux
	 * store.
	 *
	 * @returns {Function|Object} The object mimicking the original object, but with
	 * every action creator wrapped into the `dispatch` call. If you passed a
	 * function as `actionCreators`, the return value will also be a single
	 * function.
	 */
	function bindActionCreators(actionCreators, dispatch) {
	  if (typeof actionCreators === 'function') {
	    return bindActionCreator(actionCreators, dispatch);
	  }

	  if (typeof actionCreators !== 'object' || actionCreators === null) {
	    throw new Error('bindActionCreators expected an object or a function, instead received ' + (actionCreators === null ? 'null' : typeof actionCreators) + '. ' + 'Did you write "import ActionCreators from" instead of "import * as ActionCreators from"?');
	  }

	  var keys = Object.keys(actionCreators);
	  var boundActionCreators = {};
	  for (var i = 0; i < keys.length; i++) {
	    var key = keys[i];
	    var actionCreator = actionCreators[key];
	    if (typeof actionCreator === 'function') {
	      boundActionCreators[key] = bindActionCreator(actionCreator, dispatch);
	    }
	  }
	  return boundActionCreators;
	}

/***/ },

/***/ 191:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	exports.__esModule = true;

	var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

	exports["default"] = applyMiddleware;

	var _compose = __webpack_require__(192);

	var _compose2 = _interopRequireDefault(_compose);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { "default": obj }; }

	/**
	 * Creates a store enhancer that applies middleware to the dispatch method
	 * of the Redux store. This is handy for a variety of tasks, such as expressing
	 * asynchronous actions in a concise manner, or logging every action payload.
	 *
	 * See `redux-thunk` package as an example of the Redux middleware.
	 *
	 * Because middleware is potentially asynchronous, this should be the first
	 * store enhancer in the composition chain.
	 *
	 * Note that each middleware will be given the `dispatch` and `getState` functions
	 * as named arguments.
	 *
	 * @param {...Function} middlewares The middleware chain to be applied.
	 * @returns {Function} A store enhancer applying the middleware.
	 */
	function applyMiddleware() {
	  for (var _len = arguments.length, middlewares = Array(_len), _key = 0; _key < _len; _key++) {
	    middlewares[_key] = arguments[_key];
	  }

	  return function (createStore) {
	    return function (reducer, initialState, enhancer) {
	      var store = createStore(reducer, initialState, enhancer);
	      var _dispatch = store.dispatch;
	      var chain = [];

	      var middlewareAPI = {
	        getState: store.getState,
	        dispatch: function dispatch(action) {
	          return _dispatch(action);
	        }
	      };
	      chain = middlewares.map(function (middleware) {
	        return middleware(middlewareAPI);
	      });
	      _dispatch = _compose2["default"].apply(undefined, chain)(store.dispatch);

	      return _extends({}, store, {
	        dispatch: _dispatch
	      });
	    };
	  };
	}

/***/ },

/***/ 192:
/***/ function(module, exports) {

	"use strict";

	exports.__esModule = true;
	exports["default"] = compose;
	/**
	 * Composes single-argument functions from right to left. The rightmost
	 * function can take multiple arguments as it provides the signature for
	 * the resulting composite function.
	 *
	 * @param {...Function} funcs The functions to compose.
	 * @returns {Function} A function obtained by composing the argument functions
	 * from right to left. For example, compose(f, g, h) is identical to doing
	 * (...args) => f(g(h(...args))).
	 */

	function compose() {
	  for (var _len = arguments.length, funcs = Array(_len), _key = 0; _key < _len; _key++) {
	    funcs[_key] = arguments[_key];
	  }

	  if (funcs.length === 0) {
	    return function (arg) {
	      return arg;
	    };
	  } else {
	    var _ret = function () {
	      var last = funcs[funcs.length - 1];
	      var rest = funcs.slice(0, -1);
	      return {
	        v: function v() {
	          return rest.reduceRight(function (composed, f) {
	            return f(composed);
	          }, last.apply(undefined, arguments));
	        }
	      };
	    }();

	    if (typeof _ret === "object") return _ret.v;
	  }
	}

/***/ },

/***/ 193:
/***/ function(module, exports) {

	/**
	 * Copyright 2015, Yahoo! Inc.
	 * Copyrights licensed under the New BSD License. See the accompanying LICENSE file for terms.
	 */
	'use strict';

	var REACT_STATICS = {
	    childContextTypes: true,
	    contextTypes: true,
	    defaultProps: true,
	    displayName: true,
	    getDefaultProps: true,
	    mixins: true,
	    propTypes: true,
	    type: true
	};

	var KNOWN_STATICS = {
	    name: true,
	    length: true,
	    prototype: true,
	    caller: true,
	    arguments: true,
	    arity: true
	};

	var isGetOwnPropertySymbolsAvailable = typeof Object.getOwnPropertySymbols === 'function';

	module.exports = function hoistNonReactStatics(targetComponent, sourceComponent, customStatics) {
	    if (typeof sourceComponent !== 'string') { // don't hoist over string (html) components
	        var keys = Object.getOwnPropertyNames(sourceComponent);

	        /* istanbul ignore else */
	        if (isGetOwnPropertySymbolsAvailable) {
	            keys = keys.concat(Object.getOwnPropertySymbols(sourceComponent));
	        }

	        for (var i = 0; i < keys.length; ++i) {
	            if (!REACT_STATICS[keys[i]] && !KNOWN_STATICS[keys[i]] && (!customStatics || !customStatics[keys[i]])) {
	                try {
	                    targetComponent[keys[i]] = sourceComponent[keys[i]];
	                } catch (error) {

	                }
	            }
	        }
	    }

	    return targetComponent;
	};


/***/ },

/***/ 194:
/***/ function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(process) {/**
	 * Copyright 2013-2015, Facebook, Inc.
	 * All rights reserved.
	 *
	 * This source code is licensed under the BSD-style license found in the
	 * LICENSE file in the root directory of this source tree. An additional grant
	 * of patent rights can be found in the PATENTS file in the same directory.
	 */

	'use strict';

	/**
	 * Use invariant() to assert state which your program assumes to be true.
	 *
	 * Provide sprintf-style format (only %s is supported) and arguments
	 * to provide information about what broke and what you were
	 * expecting.
	 *
	 * The invariant message will be stripped in production, but the invariant
	 * will remain to ensure logic does not differ in production.
	 */

	var invariant = function(condition, format, a, b, c, d, e, f) {
	  if (process.env.NODE_ENV !== 'production') {
	    if (format === undefined) {
	      throw new Error('invariant requires an error message argument');
	    }
	  }

	  if (!condition) {
	    var error;
	    if (format === undefined) {
	      error = new Error(
	        'Minified exception occurred; use the non-minified dev environment ' +
	        'for the full error message and additional helpful warnings.'
	      );
	    } else {
	      var args = [a, b, c, d, e, f];
	      var argIndex = 0;
	      error = new Error(
	        format.replace(/%s/g, function() { return args[argIndex++]; })
	      );
	      error.name = 'Invariant Violation';
	    }

	    error.framesToPop = 1; // we don't care about invariant's own frame
	    throw error;
	  }
	};

	module.exports = invariant;

	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(3)))

/***/ },

/***/ 195:
/***/ function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(process) {'use strict';

	Object.defineProperty(exports, "__esModule", {
	  value: true
	});
	exports.default = configureStore;

	var _redux = __webpack_require__(179);

	var _reduxThunk = __webpack_require__(196);

	var _reduxThunk2 = _interopRequireDefault(_reduxThunk);

	var _reducers = __webpack_require__(197);

	var _reducers2 = _interopRequireDefault(_reducers);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

	var middlewares = [_reduxThunk2.default];
	var createLogger = __webpack_require__(200);

	if (process.env.NODE_ENV === 'development') {
	  var logger = createLogger();
	  middlewares.push(logger);
	}
	var createStoreWithMiddleware = (0, _redux.compose)(_redux.applyMiddleware.apply(undefined, middlewares), window.devToolsExtension ? window.devToolsExtension() : function (f) {
	  return f;
	})(_redux.createStore);

	function configureStore(initialState) {
	  var store = createStoreWithMiddleware(_reducers2.default, initialState);

	  if (false) {
	    // Enable Webpack hot module replacement for reducers
	    module.hot.accept('../reducers', function () {
	      var nextReducer = require('../reducers');
	      store.replaceReducer(nextReducer);
	    });
	  }

	  return store;
	}
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(3)))

/***/ },

/***/ 196:
/***/ function(module, exports) {

	'use strict';

	exports.__esModule = true;
	function createThunkMiddleware(extraArgument) {
	  return function (_ref) {
	    var dispatch = _ref.dispatch;
	    var getState = _ref.getState;
	    return function (next) {
	      return function (action) {
	        if (typeof action === 'function') {
	          return action(dispatch, getState, extraArgument);
	        }

	        return next(action);
	      };
	    };
	  };
	}

	var thunk = createThunkMiddleware();
	thunk.withExtraArgument = createThunkMiddleware;

	exports['default'] = thunk;

/***/ },

/***/ 197:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	  value: true
	});

	var _redux = __webpack_require__(179);

	var _template = __webpack_require__(198);

	var _template2 = _interopRequireDefault(_template);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

	var rootReducer = (0, _redux.combineReducers)({
	  template: _template2.default
	});

	exports.default = rootReducer;

/***/ },

/***/ 198:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	  value: true
	});

	var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol ? "symbol" : typeof obj; };

	exports.default = todos;

	var _ActionTypes = __webpack_require__(199);

	function _toConsumableArray(arr) { if (Array.isArray(arr)) { for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) { arr2[i] = arr[i]; } return arr2; } else { return Array.from(arr); } }

	var initialState = BH.STORE;

	function todos() {
	  var state = arguments.length <= 0 || arguments[0] === undefined ? initialState : arguments[0];
	  var action = arguments[1];

	  var _ret = function () {
	    switch (action.type) {
	      case _ActionTypes.ADD_TODO:
	        return {
	          v: [{
	            id: state.reduce(function (maxId, todo) {
	              return Math.max(todo.id, maxId);
	            }, -1) + 1,
	            completed: false,
	            text: action.text
	          }].concat(_toConsumableArray(state))
	        };

	      case _ActionTypes.DELETE_TODO:
	        return {
	          v: state.filter(function (todo) {
	            return todo.id !== action.id;
	          })
	        };

	      case _ActionTypes.EDIT_TODO:
	        return {
	          v: state.map(function (todo) {
	            return todo.id === action.id ? Object.assign({}, todo, { text: action.text }) : todo;
	          })
	        };

	      case _ActionTypes.COMPLETE_TODO:
	        return {
	          v: state.map(function (todo) {
	            return todo.id === action.id ? Object.assign({}, todo, { completed: !todo.completed }) : todo;
	          })
	        };

	      case _ActionTypes.COMPLETE_ALL:
	        var areAllMarked = state.every(function (todo) {
	          return todo.completed;
	        });
	        return {
	          v: state.map(function (todo) {
	            return Object.assign({}, todo, {
	              completed: !areAllMarked
	            });
	          })
	        };

	      case _ActionTypes.CLEAR_COMPLETED:
	        return {
	          v: state.filter(function (todo) {
	            return todo.completed === false;
	          })
	        };

	      default:
	        return {
	          v: state
	        };
	    }
	  }();

	  if ((typeof _ret === 'undefined' ? 'undefined' : _typeof(_ret)) === "object") return _ret.v;
	}

/***/ },

/***/ 199:
/***/ function(module, exports) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	  value: true
	});
	var ADD_TODO = exports.ADD_TODO = 'ADD_TODO';
	var DELETE_TODO = exports.DELETE_TODO = 'DELETE_TODO';
	var EDIT_TODO = exports.EDIT_TODO = 'EDIT_TODO';
	var COMPLETE_TODO = exports.COMPLETE_TODO = 'COMPLETE_TODO';
	var COMPLETE_ALL = exports.COMPLETE_ALL = 'COMPLETE_ALL';
	var CLEAR_COMPLETED = exports.CLEAR_COMPLETED = 'CLEAR_COMPLETED';

/***/ },

/***/ 200:
/***/ function(module, exports) {

	"use strict";

	function _toConsumableArray(arr) { if (Array.isArray(arr)) { for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) { arr2[i] = arr[i]; } return arr2; } else { return Array.from(arr); } }

	function _typeof(obj) { return obj && typeof Symbol !== "undefined" && obj.constructor === Symbol ? "symbol" : typeof obj; }

	var repeat = function repeat(str, times) {
	  return new Array(times + 1).join(str);
	};
	var pad = function pad(num, maxLength) {
	  return repeat("0", maxLength - num.toString().length) + num;
	};
	var formatTime = function formatTime(time) {
	  return "@ " + pad(time.getHours(), 2) + ":" + pad(time.getMinutes(), 2) + ":" + pad(time.getSeconds(), 2) + "." + pad(time.getMilliseconds(), 3);
	};

	// Use the new performance api to get better precision if available
	var timer = typeof performance !== "undefined" && typeof performance.now === "function" ? performance : Date;

	/**
	 * parse the level option of createLogger
	 *
	 * @property {string | function | object} level - console[level]
	 * @property {object} action
	 * @property {array} payload
	 * @property {string} type
	 */

	function getLogLevel(level, action, payload, type) {
	  switch (typeof level === "undefined" ? "undefined" : _typeof(level)) {
	    case "object":
	      return typeof level[type] === "function" ? level[type].apply(level, _toConsumableArray(payload)) : level[type];
	    case "function":
	      return level(action);
	    default:
	      return level;
	  }
	}

	/**
	 * Creates logger with followed options
	 *
	 * @namespace
	 * @property {object} options - options for logger
	 * @property {string | function | object} options.level - console[level]
	 * @property {boolean} options.duration - print duration of each action?
	 * @property {boolean} options.timestamp - print timestamp with each action?
	 * @property {object} options.colors - custom colors
	 * @property {object} options.logger - implementation of the `console` API
	 * @property {boolean} options.logErrors - should errors in action execution be caught, logged, and re-thrown?
	 * @property {boolean} options.collapsed - is group collapsed?
	 * @property {boolean} options.predicate - condition which resolves logger behavior
	 * @property {function} options.stateTransformer - transform state before print
	 * @property {function} options.actionTransformer - transform action before print
	 * @property {function} options.errorTransformer - transform error before print
	 */

	function createLogger() {
	  var options = arguments.length <= 0 || arguments[0] === undefined ? {} : arguments[0];
	  var _options$level = options.level;
	  var level = _options$level === undefined ? "log" : _options$level;
	  var _options$logger = options.logger;
	  var logger = _options$logger === undefined ? console : _options$logger;
	  var _options$logErrors = options.logErrors;
	  var logErrors = _options$logErrors === undefined ? true : _options$logErrors;
	  var collapsed = options.collapsed;
	  var predicate = options.predicate;
	  var _options$duration = options.duration;
	  var duration = _options$duration === undefined ? false : _options$duration;
	  var _options$timestamp = options.timestamp;
	  var timestamp = _options$timestamp === undefined ? true : _options$timestamp;
	  var transformer = options.transformer;
	  var _options$stateTransfo = options.stateTransformer;
	  var // deprecated
	  stateTransformer = _options$stateTransfo === undefined ? function (state) {
	    return state;
	  } : _options$stateTransfo;
	  var _options$actionTransf = options.actionTransformer;
	  var actionTransformer = _options$actionTransf === undefined ? function (actn) {
	    return actn;
	  } : _options$actionTransf;
	  var _options$errorTransfo = options.errorTransformer;
	  var errorTransformer = _options$errorTransfo === undefined ? function (error) {
	    return error;
	  } : _options$errorTransfo;
	  var _options$colors = options.colors;
	  var colors = _options$colors === undefined ? {
	    title: function title() {
	      return "#000000";
	    },
	    prevState: function prevState() {
	      return "#9E9E9E";
	    },
	    action: function action() {
	      return "#03A9F4";
	    },
	    nextState: function nextState() {
	      return "#4CAF50";
	    },
	    error: function error() {
	      return "#F20404";
	    }
	  } : _options$colors;

	  // exit if console undefined

	  if (typeof logger === "undefined") {
	    return function () {
	      return function (next) {
	        return function (action) {
	          return next(action);
	        };
	      };
	    };
	  }

	  if (transformer) {
	    console.error("Option 'transformer' is deprecated, use stateTransformer instead");
	  }

	  var logBuffer = [];
	  function printBuffer() {
	    logBuffer.forEach(function (logEntry, key) {
	      var started = logEntry.started;
	      var startedTime = logEntry.startedTime;
	      var action = logEntry.action;
	      var prevState = logEntry.prevState;
	      var error = logEntry.error;
	      var took = logEntry.took;
	      var nextState = logEntry.nextState;

	      var nextEntry = logBuffer[key + 1];
	      if (nextEntry) {
	        nextState = nextEntry.prevState;
	        took = nextEntry.started - started;
	      }
	      // message
	      var formattedAction = actionTransformer(action);
	      var isCollapsed = typeof collapsed === "function" ? collapsed(function () {
	        return nextState;
	      }, action) : collapsed;

	      var formattedTime = formatTime(startedTime);
	      var titleCSS = colors.title ? "color: " + colors.title(formattedAction) + ";" : null;
	      var title = "action " + (timestamp ? formattedTime : "") + " " + formattedAction.type + " " + (duration ? "(in " + took.toFixed(2) + " ms)" : "");

	      // render
	      try {
	        if (isCollapsed) {
	          if (colors.title) logger.groupCollapsed("%c " + title, titleCSS);else logger.groupCollapsed(title);
	        } else {
	          if (colors.title) logger.group("%c " + title, titleCSS);else logger.group(title);
	        }
	      } catch (e) {
	        logger.log(title);
	      }

	      var prevStateLevel = getLogLevel(level, formattedAction, [prevState], "prevState");
	      var actionLevel = getLogLevel(level, formattedAction, [formattedAction], "action");
	      var errorLevel = getLogLevel(level, formattedAction, [error, prevState], "error");
	      var nextStateLevel = getLogLevel(level, formattedAction, [nextState], "nextState");

	      if (prevStateLevel) {
	        if (colors.prevState) logger[prevStateLevel]("%c prev state", "color: " + colors.prevState(prevState) + "; font-weight: bold", prevState);else logger[prevStateLevel]("prev state", prevState);
	      }

	      if (actionLevel) {
	        if (colors.action) logger[actionLevel]("%c action", "color: " + colors.action(formattedAction) + "; font-weight: bold", formattedAction);else logger[actionLevel]("action", formattedAction);
	      }

	      if (error && errorLevel) {
	        if (colors.error) logger[errorLevel]("%c error", "color: " + colors.error(error, prevState) + "; font-weight: bold", error);else logger[errorLevel]("error", error);
	      }

	      if (nextStateLevel) {
	        if (colors.nextState) logger[nextStateLevel]("%c next state", "color: " + colors.nextState(nextState) + "; font-weight: bold", nextState);else logger[nextStateLevel]("next state", nextState);
	      }

	      try {
	        logger.groupEnd();
	      } catch (e) {
	        logger.log("—— log end ——");
	      }
	    });
	    logBuffer.length = 0;
	  }

	  return function (_ref) {
	    var getState = _ref.getState;
	    return function (next) {
	      return function (action) {
	        // exit early if predicate function returns false
	        if (typeof predicate === "function" && !predicate(getState, action)) {
	          return next(action);
	        }

	        var logEntry = {};
	        logBuffer.push(logEntry);

	        logEntry.started = timer.now();
	        logEntry.startedTime = new Date();
	        logEntry.prevState = stateTransformer(getState());
	        logEntry.action = action;

	        var returnedValue = undefined;
	        if (logErrors) {
	          try {
	            returnedValue = next(action);
	          } catch (e) {
	            logEntry.error = errorTransformer(e);
	          }
	        } else {
	          returnedValue = next(action);
	        }

	        logEntry.took = timer.now() - logEntry.started;
	        logEntry.nextState = stateTransformer(getState());

	        printBuffer();

	        if (logEntry.error) throw logEntry.error;
	        return returnedValue;
	      };
	    };
	  };
	}

	module.exports = createLogger;

/***/ },

/***/ 201:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	    value: true
	});

	var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

	var _lib = __webpack_require__(202);

	var _lib2 = _interopRequireDefault(_lib);

	var _redux = __webpack_require__(179);

	var _reactRedux = __webpack_require__(172);

	var _data = __webpack_require__(531);

	var _todos = __webpack_require__(537);

	var TodoActions = _interopRequireWildcard(_todos);

	var _Main = __webpack_require__(538);

	var _Main2 = _interopRequireDefault(_Main);

	function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj.default = obj; return newObj; } }

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

	function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

	function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; } /* CAUTION: When using the generators, this file is modified in some places.
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                *          This is done via AST traversal - Some of your formatting may be lost
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                *          in the process - no functionality should be broken though.
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                *          This modifications only run once when the generator is invoked - if
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                *          you edit them, they are not updated again.
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                */

	__webpack_require__(754);

	//import Test from '../components/Test.js'

	//import PanResponderDemo from '../examples/base/2/PanResponder/PanResponder'
	//import ViewDemo from '../examples/base/2/View/View'
	//import ScrollViewDemo from '../examples/base/2/ScrollView/ScrollView'
	//import ListViewDemo from '../examples/base/2/ListView/ListView';
	//import PickerDemo from '../examples/base/2/Picker/Picker'
	//import DatePickerIOSDemo from '../examples/base/2/DatePickerIOS/DatePickerIOS'
	//import ViewPagerDemo from '../examples/base/2/ViewPager/ViewPager'
	//import NavigatorDemo from '../examples/base/2/Navigator/Navigator'
	//
	//import AutoSizerDemo from '../examples/base/3/AutoSizer/AutoSizer';
	//import WheelerDemo from '../examples/base/3/Wheeler/Wheeler';
	//import SwiperDemo from '../examples/base/3/Swiper/Swiper';
	//
	//
	//import ScrollSyncDemo from '../examples/base/3/ScrollSync/ScrollSync';
	//import CellMeasurerDemo from '../examples/base/3/CellMeasurer/CellMeasurer';
	//import ColumnSizerDemo from '../examples/base/3/ColumnSizer/ColumnSizer';
	//import GridDemo from '../examples/base/3/Grid/Grid';
	//import CollectionDemo from '../examples/base/3/Collection/Collection';
	//import VirtualScrollDemo from '../examples/base/3/VirtualScroll/VirtualScroll';
	//import InfiniteLoaderDemo from '../examples/base/3/InfiniteLoader/InfiniteLoader';
	//import ArrowKeyStepperDemo from '../examples/base/3/ArrowKeyStepper/ArrowKeyStepper';
	//import GiftedListViewDemo from '../examples/base/3/GiftedListView/GiftedListView'
	//import SideMenu from '../examples/base/3/SideMenu/SideMenu'
	//import SortableDemo from '../examples/base/3/Sortable/Sortable'
	//import Animatable from '../examples/base/3/Animatable/Animatable'
	//
	//import TableResizeExample from '../examples/base/3/Table/ResizeExample'
	//import TableColumnGroupsExample from '../examples/base/3/Table/ColumnGroupsExample'
	//import TableFilterExample from '../examples/base/3/Table/FilterExample'
	//import TableFlexGrowExample from '../examples/base/3/Table/FlexGrowExample'
	//import TableObjectDataExample from '../examples/base/3/Table/ObjectDataExample'
	//import TableSortExample from '../examples/base/3/Table/SortExample'

	// import DialogDemo from '../examples/base/3/Dialog/Dialog'

	//import UIExplorerApp from '../examples/UIExplorer/UIExplorerApp.web'

	/* Populated by react-webpack-redux:reducer */
	var App = function (_Component) {
	    _inherits(App, _Component);

	    function App() {
	        _classCallCheck(this, App);

	        return _possibleConstructorReturn(this, (App.__proto__ || Object.getPrototypeOf(App)).apply(this, arguments));
	    }

	    _createClass(App, [{
	        key: 'render',
	        value: function render() {
	            var _props = this.props;
	            var template = _props.template;
	            var actions = _props.actions;

	            return _lib2.default.createElement(_Main2.default, { template: new _data.Template(template), actions: actions });
	        }
	    }]);

	    return App;
	}(_lib.Component);

	/* Populated by react-webpack-redux:reducer
	 *
	 * HINT: if you adjust the initial type of your reducer, you will also have to
	 *       adjust it here.
	 */


	App.propTypes = {
	    actions: _lib.PropTypes.object.isRequired,
	    template: _lib.PropTypes.object.isRequired
	};
	function mapStateToProps(state) {
	    /* Populated by react-webpack-redux:reducer */
	    var props = {
	        template: state.template
	    };
	    return props;
	}
	function mapDispatchToProps(dispatch) {
	    /* Populated by react-webpack-redux:action */
	    var actionMap = { actions: (0, _redux.bindActionCreators)(TodoActions, dispatch) };
	    return actionMap;
	}
	exports.default = (0, _reactRedux.connect)(mapStateToProps, mapDispatchToProps)(App);

/***/ },

/***/ 537:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	  value: true
	});
	exports.addTodo = addTodo;
	exports.deleteTodo = deleteTodo;
	exports.editTodo = editTodo;
	exports.completeTodo = completeTodo;
	exports.completeAll = completeAll;
	exports.clearCompleted = clearCompleted;

	var _ActionTypes = __webpack_require__(199);

	var types = _interopRequireWildcard(_ActionTypes);

	function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj.default = obj; return newObj; } }

	function addTodo(text) {
	  return { type: types.ADD_TODO, text: text };
	}

	function deleteTodo(id) {
	  return { type: types.DELETE_TODO, id: id };
	}

	function editTodo(id, text) {
	  return { type: types.EDIT_TODO, id: id, text: text };
	}

	function completeTodo(id) {
	  return { type: types.COMPLETE_TODO, id: id };
	}

	function completeAll() {
	  return { type: types.COMPLETE_ALL };
	}

	function clearCompleted() {
	  return { type: types.CLEAR_COMPLETED };
	}

/***/ },

/***/ 538:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	    value: true
	});

	var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

	var _reactAddonsPureRenderMixin = __webpack_require__(226);

	var _reactAddonsPureRenderMixin2 = _interopRequireDefault(_reactAddonsPureRenderMixin);

	var _reactMixin = __webpack_require__(223);

	var _reactMixin2 = _interopRequireDefault(_reactMixin);

	var _reactDom = __webpack_require__(34);

	var _reactDom2 = _interopRequireDefault(_reactDom);

	var _core = __webpack_require__(326);

	var _lib = __webpack_require__(202);

	var _lib2 = _interopRequireDefault(_lib);

	var _ChartComponent = __webpack_require__(539);

	var _ChartComponent2 = _interopRequireDefault(_ChartComponent);

	var _TableComponent = __webpack_require__(540);

	var _TableComponent2 = _interopRequireDefault(_TableComponent);

	var _MultiSelectorComponent = __webpack_require__(752);

	var _MultiSelectorComponent2 = _interopRequireDefault(_MultiSelectorComponent);

	var _MultiTreeSelectorComponent = __webpack_require__(753);

	var _MultiTreeSelectorComponent2 = _interopRequireDefault(_MultiTreeSelectorComponent);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

	function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

	function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

	var _Dimensions$get = _lib.Dimensions.get('window');

	var width = _Dimensions$get.width;
	var height = _Dimensions$get.height;

	var Main = function (_Component) {
	    _inherits(Main, _Component);

	    function Main(props, context) {
	        _classCallCheck(this, Main);

	        var _this = _possibleConstructorReturn(this, (Main.__proto__ || Object.getPrototypeOf(Main)).call(this, props, context));

	        console.log(props);
	        var ds = new _lib.ListView.DataSource({ rowHasChanged: function rowHasChanged(r1, r2) {
	                return r1 !== r2;
	            } });
	        var rows = props.template.getAllWidgetIds();
	        _this.state = {
	            dataSource: ds.cloneWithRows(rows)
	        };
	        return _this;
	    }

	    _createClass(Main, [{
	        key: 'render',
	        value: function render() {
	            return _lib2.default.createElement(_lib.ListView, {
	                initialListSize: 2,
	                dataSource: this.state.dataSource,
	                renderRow: this._renderRow.bind(this)
	            });
	        }
	    }, {
	        key: '_renderRow',
	        value: function _renderRow(rowData, sectionID, rowID) {
	            var template = this.props.template;

	            var widgetObj = template.getWidgetById(rowData);
	            var type = widgetObj.getType();
	            var props = {
	                key: rowData,
	                widget: widgetObj,
	                width: width,
	                height: height / 3 * 2
	            };
	            switch (type) {
	                case BICst.WIDGET.TABLE:
	                    return _lib2.default.createElement(_TableComponent2.default, props);
	                //case BICst.WIDGET.CROSS_TABLE:
	                //case BICst.WIDGET.COMPLEX_TABLE:
	                //
	                //case BICst.WIDGET.DETAIL:

	                case BICst.WIDGET.AXIS:
	                case BICst.WIDGET.ACCUMULATE_AXIS:
	                case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
	                case BICst.WIDGET.COMPARE_AXIS:
	                case BICst.WIDGET.FALL_AXIS:
	                case BICst.WIDGET.BAR:
	                case BICst.WIDGET.ACCUMULATE_BAR:
	                case BICst.WIDGET.COMPARE_BAR:
	                case BICst.WIDGET.LINE:
	                case BICst.WIDGET.AREA:
	                case BICst.WIDGET.ACCUMULATE_AREA:
	                case BICst.WIDGET.COMPARE_AREA:
	                case BICst.WIDGET.RANGE_AREA:
	                case BICst.WIDGET.COMBINE_CHART:
	                case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
	                case BICst.WIDGET.PIE:
	                case BICst.WIDGET.DONUT:
	                case BICst.WIDGET.MAP:
	                case BICst.WIDGET.GIS_MAP:
	                case BICst.WIDGET.DASHBOARD:
	                case BICst.WIDGET.BUBBLE:
	                case BICst.WIDGET.FORCE_BUBBLE:
	                case BICst.WIDGET.SCATTER:
	                case BICst.WIDGET.RADAR:
	                case BICst.WIDGET.ACCUMULATE_RADAR:
	                case BICst.WIDGET.FUNNEL:
	                    //case BICst.WIDGET.STRING:
	                    //case BICst.WIDGET.NUMBER:
	                    //case BICst.WIDGET.DATE:
	                    //case BICst.WIDGET.YEAR:
	                    //case BICst.WIDGET.QUARTER:
	                    //case BICst.WIDGET.MONTH:
	                    //case BICst.WIDGET.YMD:
	                    //case BICst.WIDGET.QUERY:
	                    //case BICst.WIDGET.RESET:
	                    //case BICst.WIDGET.CONTENT:
	                    //case BICst.WIDGET.IMAGE:
	                    //case BICst.WIDGET.WEB:
	                    return _lib2.default.createElement(_ChartComponent2.default, props);
	                case BICst.WIDGET.STRING:
	                    return _lib2.default.createElement(_MultiSelectorComponent2.default, props);
	                case BICst.WIDGET.TREE:
	                    return _lib2.default.createElement(_MultiTreeSelectorComponent2.default, props);
	                default:
	                    return null;
	            }
	        }
	    }]);

	    return Main;
	}(_lib.Component);

	Main.propTypes = {};

	_reactMixin2.default.onClass(Main, _reactAddonsPureRenderMixin2.default);
	exports.default = Main;

/***/ },

/***/ 539:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	    value: true
	});

	var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

	var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

	var _reactAddonsPureRenderMixin = __webpack_require__(226);

	var _reactAddonsPureRenderMixin2 = _interopRequireDefault(_reactAddonsPureRenderMixin);

	var _reactMixin = __webpack_require__(223);

	var _reactMixin2 = _interopRequireDefault(_reactMixin);

	var _reactDom = __webpack_require__(34);

	var _reactDom2 = _interopRequireDefault(_reactDom);

	var _core = __webpack_require__(326);

	var _lib = __webpack_require__(202);

	var _lib2 = _interopRequireDefault(_lib);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

	function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

	function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

	var Main = function (_Component) {
	    _inherits(Main, _Component);

	    //static propTypes = {
	    //    height: React.PropTypes.number.required,
	    //    id: React.PropTypes.string.required,
	    //    template: React.PropTypes.object.required
	    //};

	    function Main(props, context) {
	        _classCallCheck(this, Main);

	        return _possibleConstructorReturn(this, (Main.__proto__ || Object.getPrototypeOf(Main)).call(this, props, context));
	    }

	    _createClass(Main, [{
	        key: 'render',
	        value: function render() {
	            var widgetObj = this.props.widget;
	            var wi = widgetObj.createJson();
	            return _lib2.default.createElement(_lib.View, { ref: function ref(ob) {
	                    var w = _extends({}, wi, { page: -1 });
	                    (0, _lib.Fetch)(BH.servletURL + '?op=fr_bi_dezi&cmd=widget_setting', {
	                        method: "POST",

	                        body: JSON.stringify({ widget: w, sessionID: BH.sessionID })
	                    }).then(function (response) {
	                        return response.json(); // 转换为JSON
	                    }).then(function (data) {
	                        var vanCharts = VanCharts.init(_reactDom2.default.findDOMNode(ob));
	                        vanCharts.setOptions(Data.Utils.convertDataToChartData(data.data, wi, {
	                            click: function click(d) {
	                                console.log(d);
	                            }
	                        }));
	                    });
	                }, style: _extends({ height: this.props.height }, style.wrapper) });
	        }
	    }]);

	    return Main;
	}(_lib.Component);

	_reactMixin2.default.onClass(Main, _reactAddonsPureRenderMixin2.default);

	var style = _lib.StyleSheet.create({
	    wrapper: {
	        position: 'relative'
	    }
	});
	exports.default = Main;

/***/ },

/***/ 540:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	    value: true
	});

	var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

	var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

	var _reactAddonsPureRenderMixin = __webpack_require__(226);

	var _reactAddonsPureRenderMixin2 = _interopRequireDefault(_reactAddonsPureRenderMixin);

	var _reactMixin = __webpack_require__(223);

	var _reactMixin2 = _interopRequireDefault(_reactMixin);

	var _reactDom = __webpack_require__(34);

	var _reactDom2 = _interopRequireDefault(_reactDom);

	var _core = __webpack_require__(326);

	var _lib = __webpack_require__(202);

	var _lib2 = _interopRequireDefault(_lib);

	var _widgets = __webpack_require__(541);

	var _TableComponentHelper = __webpack_require__(751);

	var _TableComponentHelper2 = _interopRequireDefault(_TableComponentHelper);

	var _base = __webpack_require__(543);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

	function _objectWithoutProperties(obj, keys) { var target = {}; for (var i in obj) { if (keys.indexOf(i) >= 0) continue; if (!Object.prototype.hasOwnProperty.call(obj, i)) continue; target[i] = obj[i]; } return target; }

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

	function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

	function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

	var ColumnGroup = _base.Table.ColumnGroup;
	var Column = _base.Table.Column;
	var Cell = _base.Table.Cell;

	var TableComponent = function (_Component) {
	    _inherits(TableComponent, _Component);

	    function TableComponent(props, context) {
	        _classCallCheck(this, TableComponent);

	        var _this = _possibleConstructorReturn(this, (TableComponent.__proto__ || Object.getPrototypeOf(TableComponent)).call(this, props, context));

	        _this.state = {
	            data: []
	        };

	        _this._tableHelper = new _TableComponentHelper2.default(props.widget);
	        return _this;
	    }

	    _createClass(TableComponent, [{
	        key: 'componentWillMount',
	        value: function componentWillMount() {
	            this._fetchData();
	        }
	    }, {
	        key: 'componentDidMount',
	        value: function componentDidMount() {}
	    }, {
	        key: '_fetchData',
	        value: function _fetchData() {
	            var _this2 = this;

	            var wi = this.props.widget.createJson();
	            var w = _extends({
	                expander: {
	                    x: {
	                        type: true,
	                        value: [[]]
	                    },
	                    y: {
	                        type: true,
	                        value: [[]]
	                    }
	                } }, wi);
	            (0, _lib.Fetch)(BH.servletURL + '?op=fr_bi_dezi&cmd=widget_setting', {
	                method: "POST",
	                body: JSON.stringify({ widget: w, sessionID: BH.sessionID })
	            }).then(function (response) {
	                return response.json();
	            }).then(function (data) {
	                console.log(data);
	                _this2._tableHelper.setData(data);
	                _this2.forceUpdate();
	            });
	        }
	    }, {
	        key: 'render',
	        value: function render() {
	            var _props = this.props;
	            var width = _props.width;
	            var height = _props.height;

	            return _lib2.default.createElement(
	                _lib.View,
	                {
	                    style: { width: width, height: height }
	                },
	                _lib2.default.createElement(
	                    _lib.View,
	                    {
	                        style: { position: 'absolute', left: 10, right: 10, top: 10, bottom: 10 }
	                    },
	                    _lib2.default.createElement(_widgets.TableWidget, {
	                        width: width - 20,
	                        height: height - 20,
	                        freezeCols: this._tableHelper.isFreeze() ? [0] : [],
	                        columnSize: [300, 300],
	                        header: this._tableHelper.getHeader(),
	                        items: this._tableHelper.getItems(),
	                        headerCellRenderer: function headerCellRenderer(colIndex, cell) {
	                            return _lib2.default.createElement(
	                                Cell,
	                                null,
	                                cell.text
	                            );
	                        },
	                        itemsCellRenderer: function itemsCellRenderer(_ref) {
	                            var colIndex = _ref.colIndex;
	                            var rowIndex = _ref.rowIndex;
	                            var items = _ref.items;

	                            var props = _objectWithoutProperties(_ref, ['colIndex', 'rowIndex', 'items']);

	                            return _lib2.default.createElement(
	                                Cell,
	                                props,
	                                items[colIndex][rowIndex].text
	                            );
	                        }
	                    })
	                )
	            );
	        }
	    }]);

	    return TableComponent;
	}(_lib.Component);

	_reactMixin2.default.onClass(TableComponent, _reactAddonsPureRenderMixin2.default);

	var style = _lib.StyleSheet.create({
	    wrapper: {
	        position: 'relative'
	    }
	});
	exports.default = TableComponent;

/***/ },

/***/ 541:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	  value: true
	});
	exports.MultiTreeSelectorWidget = exports.MultiSelectorWidget = exports.TableWidget = undefined;

	var _TableWidget2 = __webpack_require__(542);

	var _TableWidget3 = _interopRequireDefault(_TableWidget2);

	var _MultiSelectorWidget2 = __webpack_require__(745);

	var _MultiSelectorWidget3 = _interopRequireDefault(_MultiSelectorWidget2);

	var _MultiTreeSelectorWidget2 = __webpack_require__(748);

	var _MultiTreeSelectorWidget3 = _interopRequireDefault(_MultiTreeSelectorWidget2);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

	exports.TableWidget = _TableWidget3.default;
	exports.MultiSelectorWidget = _MultiSelectorWidget3.default;
	exports.MultiTreeSelectorWidget = _MultiTreeSelectorWidget3.default;
	exports.default = module.exports;

/***/ },

/***/ 542:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	    value: true
	});

	var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

	var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

	var _reactAddonsPureRenderMixin = __webpack_require__(226);

	var _reactAddonsPureRenderMixin2 = _interopRequireDefault(_reactAddonsPureRenderMixin);

	var _reactMixin = __webpack_require__(223);

	var _reactMixin2 = _interopRequireDefault(_reactMixin);

	var _reactDom = __webpack_require__(34);

	var _reactDom2 = _interopRequireDefault(_reactDom);

	var _core = __webpack_require__(326);

	var _lib = __webpack_require__(202);

	var _lib2 = _interopRequireDefault(_lib);

	var _base = __webpack_require__(543);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

	function _objectWithoutProperties(obj, keys) { var target = {}; for (var i in obj) { if (keys.indexOf(i) >= 0) continue; if (!Object.prototype.hasOwnProperty.call(obj, i)) continue; target[i] = obj[i]; } return target; }

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

	function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

	function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

	var ColumnGroup = _base.Table.ColumnGroup;
	var Column = _base.Table.Column;
	var Cell = _base.Table.Cell;

	var TableWidget = function (_Component) {
	    _inherits(TableWidget, _Component);

	    function TableWidget(props, context) {
	        _classCallCheck(this, TableWidget);

	        var _this = _possibleConstructorReturn(this, (TableWidget.__proto__ || Object.getPrototypeOf(TableWidget)).call(this, props, context));

	        _this.state = {};
	        return _this;
	    }

	    _createClass(TableWidget, [{
	        key: 'render',
	        value: function render() {
	            var _props = this.props;
	            var isNeedFreeze = _props.isNeedFreeze;
	            var freezeCols = _props.freezeCols;
	            var columnSize = _props.columnSize;
	            var rowHeight = _props.rowHeight;
	            var headerRowHeight = _props.headerRowHeight;
	            var header = _props.header;
	            var items = _props.items;
	            var itemsCellRenderer = _props.itemsCellRenderer;
	            var headerCellRenderer = _props.headerCellRenderer;
	            var width = _props.width;
	            var height = _props.height;

	            var others = _objectWithoutProperties(_props, ['isNeedFreeze', 'freezeCols', 'columnSize', 'rowHeight', 'headerRowHeight', 'header', 'items', 'itemsCellRenderer', 'headerCellRenderer', 'width', 'height']);

	            var columns = [];
	            header.forEach(function (row, colIndex) {
	                columns.push(_lib2.default.createElement(Column, {
	                    fixed: freezeCols.indexOf(colIndex) > -1,
	                    header: headerCellRenderer(colIndex, row),
	                    cell: function cell(props) {
	                        return itemsCellRenderer(_extends({ colIndex: colIndex, items: items }, props));
	                    },
	                    width: columnSize[colIndex]
	                }));
	            });

	            return _lib2.default.createElement(
	                _base.Table,
	                _extends({
	                    rowHeight: rowHeight,
	                    groupHeaderHeight: 0,
	                    headerHeight: headerRowHeight,
	                    rowsCount: items[0] ? items[0].length : 0,
	                    width: width,
	                    height: height
	                }, others),
	                columns
	            );
	        }
	    }]);

	    return TableWidget;
	}(_lib.Component);

	TableWidget.propTypes = {};
	TableWidget.defaultProps = {
	    isNeedFreeze: true,
	    freezeCols: [],
	    columnSize: [],
	    rowHeight: 30,
	    headerRowHeight: 30,
	    header: [],
	    items: [],
	    itemsCellRenderer: _core.emptyFunction,
	    headerCellRenderer: _core.emptyFunction
	};

	_reactMixin2.default.onClass(TableWidget, _reactAddonsPureRenderMixin2.default);
	var style = _lib.StyleSheet.create({
	    region: {
	        position: 'absolute'
	    }
	});
	exports.default = TableWidget;

/***/ },

/***/ 745:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	    value: true
	});

	var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

	var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

	var _reactAddonsPureRenderMixin = __webpack_require__(226);

	var _reactAddonsPureRenderMixin2 = _interopRequireDefault(_reactAddonsPureRenderMixin);

	var _reactMixin = __webpack_require__(223);

	var _reactMixin2 = _interopRequireDefault(_reactMixin);

	var _reactDom = __webpack_require__(34);

	var _reactDom2 = _interopRequireDefault(_reactDom);

	var _core = __webpack_require__(326);

	var _lib = __webpack_require__(202);

	var _lib2 = _interopRequireDefault(_lib);

	var _base = __webpack_require__(543);

	var _Item = __webpack_require__(746);

	var _Item2 = _interopRequireDefault(_Item);

	var _MultiSelectorWidgetHelper = __webpack_require__(747);

	var _MultiSelectorWidgetHelper2 = _interopRequireDefault(_MultiSelectorWidgetHelper);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

	function _objectWithoutProperties(obj, keys) { var target = {}; for (var i in obj) { if (keys.indexOf(i) >= 0) continue; if (!Object.prototype.hasOwnProperty.call(obj, i)) continue; target[i] = obj[i]; } return target; }

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

	function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

	function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

	var MultiSelectorWidget = function (_Component) {
	    _inherits(MultiSelectorWidget, _Component);

	    function MultiSelectorWidget(props, context) {
	        _classCallCheck(this, MultiSelectorWidget);

	        var _this = _possibleConstructorReturn(this, (MultiSelectorWidget.__proto__ || Object.getPrototypeOf(MultiSelectorWidget)).call(this, props, context));

	        _this.state = {};

	        _this._helper = new _MultiSelectorWidgetHelper2.default(props);
	        _this.state = {
	            value: props.value,
	            type: props.type
	        };
	        return _this;
	    }

	    _createClass(MultiSelectorWidget, [{
	        key: 'componentWillMount',
	        value: function componentWillMount() {}
	    }, {
	        key: 'componentDidMount',
	        value: function componentDidMount() {}
	    }, {
	        key: 'componentWillReceiveProps',
	        value: function componentWillReceiveProps(nextProps) {
	            this._helper = new _MultiSelectorWidgetHelper2.default(nextProps);
	            this.setState({ value: nextProps.value, type: nextProps.type });
	        }
	    }, {
	        key: 'componentWillUpdate',
	        value: function componentWillUpdate() {}
	    }, {
	        key: 'render',
	        value: function render() {
	            var props = _objectWithoutProperties(this.props, []);

	            return _lib2.default.createElement(_base.VirtualScroll, {
	                width: props.width,
	                height: props.height,
	                overscanRowCount: 0
	                //noRowsRenderer={this._noRowsRenderer.bind(this)}
	                , rowCount: this.props.items.length,
	                rowHeight: 30,
	                rowRenderer: this._rowRenderer.bind(this)
	                //scrollToIndex={scrollToIndex}
	            });
	        }
	    }, {
	        key: '_rowRenderer',
	        value: function _rowRenderer(_ref) {
	            var _this2 = this;

	            var index = _ref.index;
	            var isScrolling = _ref.isScrolling;

	            var rowData = this._helper.getSortedItems()[index];
	            return _lib2.default.createElement(_Item2.default, _extends({ key: rowData.value, onSelected: function onSelected(sel) {
	                    if (sel) {
	                        _this2._helper.selectOneValue(rowData.value);
	                    } else {
	                        _this2._helper.disSelectOneValue(rowData.value);
	                    }
	                    _this2.setState({
	                        value: _this2._helper.getSelectedValue()
	                    });
	                } }, rowData));
	        }
	    }]);

	    return MultiSelectorWidget;
	}(_lib.Component);

	MultiSelectorWidget.propTypes = {};
	MultiSelectorWidget.defaultProps = {
	    items: []
	};

	_reactMixin2.default.onClass(MultiSelectorWidget, _reactAddonsPureRenderMixin2.default);
	var styles = _lib.StyleSheet.create({
	    region: {
	        position: 'absolute'
	    }
	});
	exports.default = MultiSelectorWidget;

/***/ },

/***/ 746:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	    value: true
	});

	var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

	var _reactAddonsPureRenderMixin = __webpack_require__(226);

	var _reactAddonsPureRenderMixin2 = _interopRequireDefault(_reactAddonsPureRenderMixin);

	var _reactMixin = __webpack_require__(223);

	var _reactMixin2 = _interopRequireDefault(_reactMixin);

	var _reactDom = __webpack_require__(34);

	var _reactDom2 = _interopRequireDefault(_reactDom);

	var _core = __webpack_require__(326);

	var _lib = __webpack_require__(202);

	var _lib2 = _interopRequireDefault(_lib);

	var _data = __webpack_require__(531);

	var _base = __webpack_require__(543);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

	function _objectWithoutProperties(obj, keys) { var target = {}; for (var i in obj) { if (keys.indexOf(i) >= 0) continue; if (!Object.prototype.hasOwnProperty.call(obj, i)) continue; target[i] = obj[i]; } return target; }

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

	function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

	function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

	var Item = function (_Component) {
	    _inherits(Item, _Component);

	    function Item(props, context) {
	        _classCallCheck(this, Item);

	        var _this = _possibleConstructorReturn(this, (Item.__proto__ || Object.getPrototypeOf(Item)).call(this, props, context));

	        _this.state = {};
	        var text = props.text;
	        var value = props.value;
	        var selected = props.selected;

	        _this.state = { text: text, value: value, selected: selected };
	        return _this;
	    }

	    _createClass(Item, [{
	        key: 'componentWillMount',
	        value: function componentWillMount() {}
	    }, {
	        key: 'componentDidMount',
	        value: function componentDidMount() {}
	    }, {
	        key: 'componentWillReceiveProps',
	        value: function componentWillReceiveProps(props) {
	            var text = props.text;
	            var value = props.value;
	            var selected = props.selected;

	            this.state = { text: text, value: value, selected: selected };
	        }
	    }, {
	        key: 'componentWillUpdate',
	        value: function componentWillUpdate() {}
	    }, {
	        key: 'render',
	        value: function render() {
	            var _this2 = this;

	            var props = _objectWithoutProperties(this.props, []);
	            var state = _objectWithoutProperties(this.state, []);

	            return _lib2.default.createElement(
	                _lib.TouchableHighlight,
	                { onPress: function onPress() {
	                        _this2.setState({
	                            selected: !_this2.state.selected
	                        }, function () {
	                            _this2.props.onSelected(_this2.state.selected);
	                        });
	                    }, underlayColor: _data.Colors.PRESS },
	                _lib2.default.createElement(
	                    _lib.View,
	                    { style: [styles.row, (0, _core.sc)([styles.selected, state.selected])] },
	                    _lib2.default.createElement(
	                        _lib.Text,
	                        null,
	                        state.value == null ? state.text : state.value
	                    )
	                )
	            );
	        }
	    }]);

	    return Item;
	}(_lib.Component);

	Item.propTypes = {};
	Item.defaultProps = {
	    text: '',
	    value: '',
	    selected: false,
	    onSelected: _core.emptyFunction
	};

	_reactMixin2.default.onClass(Item, _reactAddonsPureRenderMixin2.default);
	var styles = _lib.StyleSheet.create({
	    row: {
	        justifyContent: 'center',
	        height: 30
	    },

	    selected: {
	        backgroundColor: _data.Colors.SELECTED
	    }
	});
	exports.default = Item;

/***/ },

/***/ 747:
/***/ function(module, exports) {

	"use strict";

	Object.defineProperty(exports, "__esModule", {
	    value: true
	});

	var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

	var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

	var MultiSelectorWidgetHelper = function () {
	    function MultiSelectorWidgetHelper(props) {
	        _classCallCheck(this, MultiSelectorWidgetHelper);

	        this.items = props.items;
	        this.sorted = this.items;
	        this.value = Array.from(props.value || []);
	        this.type = props.type;
	    }

	    _createClass(MultiSelectorWidgetHelper, [{
	        key: "_selectOneValue",
	        value: function _selectOneValue(val) {
	            if (this.value.indexOf(val) === -1) {
	                this.value.push(val);
	                this.sorted = this._sortItems();
	            }
	        }
	    }, {
	        key: "_disSelectOneValue",
	        value: function _disSelectOneValue(val) {
	            var idx = void 0;
	            if ((idx = this.value.indexOf(val)) >= -1) {
	                this.value.splice(idx, 1);
	                this.sorted = this._sortItems();
	            }
	        }
	    }, {
	        key: "_sortItems",
	        value: function _sortItems() {
	            var _this = this;

	            var front = [],
	                items = [];
	            this.items.forEach(function (item) {
	                if (_this.value.indexOf(item.value) > -1) {
	                    front.push(_extends({}, item, { selected: _this.type !== 1 }));
	                } else {
	                    items.push(_extends({}, item, { selected: _this.type === 1 }));
	                }
	            });
	            return front.concat(items);
	        }
	    }, {
	        key: "selectOneValue",
	        value: function selectOneValue(val) {
	            if (this.type === 1) {
	                this._disSelectOneValue(val);
	            } else {
	                this._selectOneValue(val);
	            }
	        }
	    }, {
	        key: "disSelectOneValue",
	        value: function disSelectOneValue(val) {
	            if (this.type === 1) {
	                this._selectOneValue(val);
	            } else {
	                this._disSelectOneValue(val);
	            }
	        }
	    }, {
	        key: "getSelectedValue",
	        value: function getSelectedValue() {
	            return Array.from(this.value);
	        }
	    }, {
	        key: "getSortedItems",
	        value: function getSortedItems() {
	            return this.sorted;
	        }
	    }]);

	    return MultiSelectorWidgetHelper;
	}();

	exports.default = MultiSelectorWidgetHelper;

/***/ },

/***/ 748:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	    value: true
	});

	var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

	var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

	var _reactAddonsPureRenderMixin = __webpack_require__(226);

	var _reactAddonsPureRenderMixin2 = _interopRequireDefault(_reactAddonsPureRenderMixin);

	var _reactMixin = __webpack_require__(223);

	var _reactMixin2 = _interopRequireDefault(_reactMixin);

	var _reactDom = __webpack_require__(34);

	var _reactDom2 = _interopRequireDefault(_reactDom);

	var _core = __webpack_require__(326);

	var _lib = __webpack_require__(202);

	var _lib2 = _interopRequireDefault(_lib);

	var _base = __webpack_require__(543);

	var _Item = __webpack_require__(749);

	var _Item2 = _interopRequireDefault(_Item);

	var _MultiTreeSelectorWidgetHelper = __webpack_require__(750);

	var _MultiTreeSelectorWidgetHelper2 = _interopRequireDefault(_MultiTreeSelectorWidgetHelper);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

	function _objectWithoutProperties(obj, keys) { var target = {}; for (var i in obj) { if (keys.indexOf(i) >= 0) continue; if (!Object.prototype.hasOwnProperty.call(obj, i)) continue; target[i] = obj[i]; } return target; }

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

	function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

	function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

	var MultiTreeSelectorWidget = function (_Component) {
	    _inherits(MultiTreeSelectorWidget, _Component);

	    function MultiTreeSelectorWidget(props, context) {
	        _classCallCheck(this, MultiTreeSelectorWidget);

	        var _this = _possibleConstructorReturn(this, (MultiTreeSelectorWidget.__proto__ || Object.getPrototypeOf(MultiTreeSelectorWidget)).call(this, props, context));

	        _this.state = {};

	        _this.ds = new _lib.ListView.DataSource({ rowHasChanged: function rowHasChanged(r1, r2) {
	                return r1.value !== r2.value;
	            } });
	        _this._helper = new _MultiTreeSelectorWidgetHelper2.default(props);
	        _this.state = {
	            dataSource: _this.ds.cloneWithRows(_this._helper.getItems())
	        };
	        return _this;
	    }

	    _createClass(MultiTreeSelectorWidget, [{
	        key: 'componentWillMount',
	        value: function componentWillMount() {}
	    }, {
	        key: 'componentDidMount',
	        value: function componentDidMount() {}
	    }, {
	        key: 'componentWillReceiveProps',
	        value: function componentWillReceiveProps(nextProps) {
	            this._helper = new _MultiTreeSelectorWidgetHelper2.default(nextProps);
	            this.setState({ dataSource: this.ds.cloneWithRows(this._helper.getItems()) });
	        }
	    }, {
	        key: 'componentWillUpdate',
	        value: function componentWillUpdate() {}
	    }, {
	        key: 'render',
	        value: function render() {
	            var props = _objectWithoutProperties(this.props, []);

	            return _lib2.default.createElement(_lib.ListView, _extends({}, props, {
	                dataSource: this.state.dataSource,
	                initialListSize: 100,
	                renderRow: this._renderRow.bind(this)
	            }));
	        }
	    }, {
	        key: '_renderRow',
	        value: function _renderRow(rowData, sectionID, rowID) {
	            var _this2 = this;

	            return _lib2.default.createElement(_Item2.default, _extends({ key: rowData.value, onSelected: function onSelected(sel) {
	                    if (sel) {
	                        _this2._helper.selectOneValue(rowData.value);
	                    } else {
	                        _this2._helper.disSelectOneValue(rowData.value);
	                    }
	                    _this2.setState({
	                        dataSource: _this2.ds.cloneWithRows(_this2._helper.getItems())
	                    });
	                } }, rowData));
	        }
	    }]);

	    return MultiTreeSelectorWidget;
	}(_lib.Component);

	MultiTreeSelectorWidget.propTypes = {};
	MultiTreeSelectorWidget.defaultProps = {
	    items: []
	};

	_reactMixin2.default.onClass(MultiTreeSelectorWidget, _reactAddonsPureRenderMixin2.default);
	var styles = _lib.StyleSheet.create({
	    region: {
	        position: 'absolute'
	    }
	});
	exports.default = MultiTreeSelectorWidget;

/***/ },

/***/ 749:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	    value: true
	});

	var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

	var _reactAddonsPureRenderMixin = __webpack_require__(226);

	var _reactAddonsPureRenderMixin2 = _interopRequireDefault(_reactAddonsPureRenderMixin);

	var _reactMixin = __webpack_require__(223);

	var _reactMixin2 = _interopRequireDefault(_reactMixin);

	var _reactDom = __webpack_require__(34);

	var _reactDom2 = _interopRequireDefault(_reactDom);

	var _core = __webpack_require__(326);

	var _lib = __webpack_require__(202);

	var _lib2 = _interopRequireDefault(_lib);

	var _data = __webpack_require__(531);

	var _base = __webpack_require__(543);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

	function _objectWithoutProperties(obj, keys) { var target = {}; for (var i in obj) { if (keys.indexOf(i) >= 0) continue; if (!Object.prototype.hasOwnProperty.call(obj, i)) continue; target[i] = obj[i]; } return target; }

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

	function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

	function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

	var Item = function (_Component) {
	    _inherits(Item, _Component);

	    function Item(props, context) {
	        _classCallCheck(this, Item);

	        var _this = _possibleConstructorReturn(this, (Item.__proto__ || Object.getPrototypeOf(Item)).call(this, props, context));

	        _this.state = {};
	        var text = props.text;
	        var value = props.value;
	        var selected = props.selected;

	        _this.state = { text: text, value: value, selected: selected };
	        return _this;
	    }

	    _createClass(Item, [{
	        key: 'componentWillMount',
	        value: function componentWillMount() {}
	    }, {
	        key: 'componentDidMount',
	        value: function componentDidMount() {}
	    }, {
	        key: 'componentWillReceiveProps',
	        value: function componentWillReceiveProps(props) {
	            var text = props.text;
	            var value = props.value;
	            var selected = props.selected;

	            this.state = { text: text, value: value, selected: selected };
	        }
	    }, {
	        key: 'componentWillUpdate',
	        value: function componentWillUpdate() {}
	    }, {
	        key: 'render',
	        value: function render() {
	            var _this2 = this;

	            var props = _objectWithoutProperties(this.props, []);
	            var state = _objectWithoutProperties(this.state, []);

	            return _lib2.default.createElement(
	                _lib.TouchableHighlight,
	                { onPress: function onPress() {
	                        _this2.setState({
	                            selected: !_this2.state.selected
	                        }, function () {
	                            _this2.props.onSelected(_this2.state.selected);
	                        });
	                    }, underlayColor: _data.Colors.PRESS },
	                _lib2.default.createElement(
	                    _lib.View,
	                    { style: [styles.row, (0, _core.sc)([styles.selected, state.selected])] },
	                    _lib2.default.createElement(
	                        _lib.Text,
	                        null,
	                        state.value == null ? state.text : state.value
	                    )
	                )
	            );
	        }
	    }]);

	    return Item;
	}(_lib.Component);

	Item.propTypes = {};
	Item.defaultProps = {
	    text: '',
	    value: '',
	    selected: false,
	    onSelected: _core.emptyFunction
	};

	_reactMixin2.default.onClass(Item, _reactAddonsPureRenderMixin2.default);
	var styles = _lib.StyleSheet.create({
	    row: {
	        justifyContent: 'center',
	        height: 30
	    },

	    selected: {
	        backgroundColor: _data.Colors.SELECTED
	    }
	});
	exports.default = Item;

/***/ },

/***/ 750:
/***/ function(module, exports) {

	"use strict";

	Object.defineProperty(exports, "__esModule", {
	    value: true
	});

	var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

	var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

	var MultiSelectorWidgetHelper = function () {
	    function MultiSelectorWidgetHelper(props) {
	        _classCallCheck(this, MultiSelectorWidgetHelper);

	        this.items = props.items;
	        this.value = Array.from(props.value || []);
	        this.type = props.type;
	    }

	    _createClass(MultiSelectorWidgetHelper, [{
	        key: "_selectOneValue",
	        value: function _selectOneValue(val) {
	            if (this.value.indexOf(val) === -1) {
	                this.value.push(val);
	            }
	        }
	    }, {
	        key: "_disSelectOneValue",
	        value: function _disSelectOneValue(val) {
	            var idx = void 0;
	            if ((idx = this.value.indexOf(val)) >= -1) {
	                this.value.splice(idx, 1);
	            }
	        }
	    }, {
	        key: "selectOneValue",
	        value: function selectOneValue(val) {
	            if (this.type === 1) {
	                this._disSelectOneValue(val);
	            } else {
	                this._selectOneValue(val);
	            }
	        }
	    }, {
	        key: "disSelectOneValue",
	        value: function disSelectOneValue(val) {
	            if (this.type === 1) {
	                this._selectOneValue(val);
	            } else {
	                this._disSelectOneValue(val);
	            }
	        }
	    }, {
	        key: "getItems",
	        value: function getItems() {
	            var _this = this;

	            var front = [],
	                items = [];
	            this.items.forEach(function (item) {
	                if (_this.value.indexOf(item.value) > -1) {
	                    front.push(_extends({}, item, { selected: _this.type !== 1 }));
	                } else {
	                    items.push(_extends({}, item, { selected: _this.type === 1 }));
	                }
	            });
	            return front.concat(items);
	        }
	    }]);

	    return MultiSelectorWidgetHelper;
	}();

	exports.default = MultiSelectorWidgetHelper;

/***/ },

/***/ 751:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	    value: true
	});

	var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

	var _core = __webpack_require__(326);

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

	var TableComponentHelper = function () {
	    function TableComponentHelper(widget) {
	        _classCallCheck(this, TableComponentHelper);

	        this.widget = widget;
	        this.data = [];
	    }

	    _createClass(TableComponentHelper, [{
	        key: 'setData',
	        value: function setData(data) {
	            this.data = data;
	        }
	    }, {
	        key: 'getHeader',
	        value: function getHeader() {
	            var _this = this;

	            var ids = this.widget.getAllUsedTargetIds();
	            var result = [{
	                text: '行表头'
	            }];
	            ids.forEach(function (id) {
	                var dim = _this.widget.getDimensionOrTargetById(id);
	                result.push({
	                    text: dim.getName()
	                });
	            });
	            return result;
	        }
	    }, {
	        key: 'getItems',
	        value: function getItems() {
	            var dimensionIds = this.widget.getAllUsedDimensionIds();
	            var targetIds = this.widget.getAllUsedTargetIds();
	            var result = [];
	            var track = function track(node, layer) {
	                if (!node) {
	                    return;
	                }
	                if (node.n) {
	                    if (!result[0]) {
	                        result[0] = [];
	                    }
	                    result[0].push({
	                        dId: dimensionIds[layer],
	                        text: node.n
	                    });
	                    if (node.s) {
	                        node.s.forEach(function (v, idx) {
	                            if (!result[idx + 1]) {
	                                result[idx + 1] = [];
	                            }
	                            result[idx + 1].push({
	                                dId: targetIds[idx],
	                                text: v
	                            });
	                        });
	                    }
	                }
	                if (node.c) {
	                    node.c.forEach(function (child) {
	                        track(child, layer + 1);
	                    });
	                }
	                if (!node.n) {
	                    if (!result[0]) {
	                        result[0] = [];
	                    }
	                    result[0].push({
	                        text: '汇总'
	                    });
	                    if (node.s) {
	                        node.s.forEach(function (v, idx) {
	                            if (!result[idx + 1]) {
	                                result[idx + 1] = [];
	                            }
	                            result[idx + 1].push({
	                                dId: targetIds[idx],
	                                text: v
	                            });
	                        });
	                    }
	                }
	            };
	            track(this.data.data, -1);
	            return result;
	        }
	    }, {
	        key: 'isFreeze',
	        value: function isFreeze() {
	            return this.widget.isFreeze();
	        }
	    }]);

	    return TableComponentHelper;
	}();

	exports.default = TableComponentHelper;

/***/ },

/***/ 752:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	    value: true
	});

	var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

	var _reactAddonsPureRenderMixin = __webpack_require__(226);

	var _reactAddonsPureRenderMixin2 = _interopRequireDefault(_reactAddonsPureRenderMixin);

	var _reactMixin = __webpack_require__(223);

	var _reactMixin2 = _interopRequireDefault(_reactMixin);

	var _reactDom = __webpack_require__(34);

	var _reactDom2 = _interopRequireDefault(_reactDom);

	var _core = __webpack_require__(326);

	var _lib = __webpack_require__(202);

	var _lib2 = _interopRequireDefault(_lib);

	var _base = __webpack_require__(543);

	var _data = __webpack_require__(531);

	var _widgets = __webpack_require__(541);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

	function _objectWithoutProperties(obj, keys) { var target = {}; for (var i in obj) { if (keys.indexOf(i) >= 0) continue; if (!Object.prototype.hasOwnProperty.call(obj, i)) continue; target[i] = obj[i]; } return target; }

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

	function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

	function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

	var MultiSelectorComponent = function (_Component) {
	    _inherits(MultiSelectorComponent, _Component);

	    function MultiSelectorComponent(props, context) {
	        _classCallCheck(this, MultiSelectorComponent);

	        var _this = _possibleConstructorReturn(this, (MultiSelectorComponent.__proto__ || Object.getPrototypeOf(MultiSelectorComponent)).call(this, props, context));

	        _this.state = {};
	        return _this;
	    }

	    _createClass(MultiSelectorComponent, [{
	        key: 'componentWillMount',
	        value: function componentWillMount() {}
	    }, {
	        key: 'componentDidMount',
	        value: function componentDidMount() {}
	    }, {
	        key: 'componentWillReceiveProps',
	        value: function componentWillReceiveProps() {}
	    }, {
	        key: 'componentWillUpdate',
	        value: function componentWillUpdate() {}
	    }, {
	        key: 'render',
	        value: function render() {
	            var props = _objectWithoutProperties(this.props, []);

	            var items = [];
	            for (var i = 0; i < 1000; i++) {
	                items.push({
	                    value: i
	                });
	            }
	            return _lib2.default.createElement(_widgets.MultiSelectorWidget, {
	                items: items,
	                width: props.width,
	                height: props.height
	            });
	        }
	    }]);

	    return MultiSelectorComponent;
	}(_lib.Component);

	MultiSelectorComponent.propTypes = {};
	MultiSelectorComponent.defaultProps = {};

	_reactMixin2.default.onClass(MultiSelectorComponent, _reactAddonsPureRenderMixin2.default);
	var styles = _lib.StyleSheet.create({
	    region: {
	        position: 'absolute'
	    }
	});
	exports.default = MultiSelectorComponent;

/***/ },

/***/ 753:
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	    value: true
	});

	var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

	var _reactAddonsPureRenderMixin = __webpack_require__(226);

	var _reactAddonsPureRenderMixin2 = _interopRequireDefault(_reactAddonsPureRenderMixin);

	var _reactMixin = __webpack_require__(223);

	var _reactMixin2 = _interopRequireDefault(_reactMixin);

	var _reactDom = __webpack_require__(34);

	var _reactDom2 = _interopRequireDefault(_reactDom);

	var _core = __webpack_require__(326);

	var _lib = __webpack_require__(202);

	var _lib2 = _interopRequireDefault(_lib);

	var _base = __webpack_require__(543);

	var _data = __webpack_require__(531);

	var _widgets = __webpack_require__(541);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

	function _objectWithoutProperties(obj, keys) { var target = {}; for (var i in obj) { if (keys.indexOf(i) >= 0) continue; if (!Object.prototype.hasOwnProperty.call(obj, i)) continue; target[i] = obj[i]; } return target; }

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

	function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

	function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

	var MultiTreeSelectorComponent = function (_Component) {
	    _inherits(MultiTreeSelectorComponent, _Component);

	    function MultiTreeSelectorComponent(props, context) {
	        _classCallCheck(this, MultiTreeSelectorComponent);

	        var _this = _possibleConstructorReturn(this, (MultiTreeSelectorComponent.__proto__ || Object.getPrototypeOf(MultiTreeSelectorComponent)).call(this, props, context));

	        _this.state = {};
	        return _this;
	    }

	    _createClass(MultiTreeSelectorComponent, [{
	        key: 'componentWillMount',
	        value: function componentWillMount() {}
	    }, {
	        key: 'componentDidMount',
	        value: function componentDidMount() {}
	    }, {
	        key: 'componentWillReceiveProps',
	        value: function componentWillReceiveProps() {}
	    }, {
	        key: 'componentWillUpdate',
	        value: function componentWillUpdate() {}
	    }, {
	        key: 'render',
	        value: function render() {
	            var props = _objectWithoutProperties(this.props, []);

	            var items = [];
	            for (var i = 0; i < 1000; i++) {
	                items.push({
	                    value: i
	                });
	            }
	            return _lib2.default.createElement(_widgets.MultiTreeSelectorWidget, {
	                items: items,
	                style: {
	                    width: props.width,
	                    height: props.height
	                }
	            });
	        }
	    }]);

	    return MultiTreeSelectorComponent;
	}(_lib.Component);

	MultiTreeSelectorComponent.propTypes = {};
	MultiTreeSelectorComponent.defaultProps = {};

	_reactMixin2.default.onClass(MultiTreeSelectorComponent, _reactAddonsPureRenderMixin2.default);
	var styles = _lib.StyleSheet.create({
	    region: {
	        position: 'absolute'
	    }
	});
	exports.default = MultiTreeSelectorComponent;

/***/ },

/***/ 754:
/***/ function(module, exports, __webpack_require__) {

	// style-loader: Adds some css to the DOM by adding a <style> tag

	// load the styles
	var content = __webpack_require__(755);
	if(typeof content === 'string') content = [[module.id, content, '']];
	// add the styles to the DOM
	var update = __webpack_require__(385)(content, {});
	if(content.locals) module.exports = content.locals;
	// Hot Module Replacement
	if(false) {
		// When the styles change, update the <style> tags
		if(!content.locals) {
			module.hot.accept("!!./../../node_modules/css-loader/index.js!./../../node_modules/autoprefixer-loader/index.js?browsers=last 2 version!./App.css", function() {
				var newContent = require("!!./../../node_modules/css-loader/index.js!./../../node_modules/autoprefixer-loader/index.js?browsers=last 2 version!./App.css");
				if(typeof newContent === 'string') newContent = [[module.id, newContent, '']];
				update(newContent);
			});
		}
		// When the module is disposed, remove the <style> tags
		module.hot.dispose(function() { update(); });
	}

/***/ },

/***/ 755:
/***/ function(module, exports, __webpack_require__) {

	exports = module.exports = __webpack_require__(384)();
	// imports


	// module
	exports.push([module.id, "/* Base Application Styles */\r\n\r\n", ""]);

	// exports


/***/ }

});