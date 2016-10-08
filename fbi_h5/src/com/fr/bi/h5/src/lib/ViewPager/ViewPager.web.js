/**
 * Copyright (c) 2015-present, Alibaba Group Holding Limited.
 * All rights reserved.
 *
 * @providesModule ReactViewPager
 *
 */
'use strict';

import React, { PropTypes, cloneElement } from 'react';
import ReactDOM from 'react-dom';
import assign from 'object-assign';
import View from '../View/View.web';
import Animated from '../Animated/Animated.web';
import Dimensions from '../Dimensions/Dimensions.web';
import PanResponder from '../PanResponder/PanResponder.web';
import dismissKeyboard from '../Utilties/dismissKeyboard.web';
import { Mixin as NativeMethodsMixin } from '../Utilties/NativeMethodsMixin.web';
import mixin from 'react-mixin';

const deviceSize = Dimensions.get('window');
const VIEWPAGER_REF = 'viewpager';

class ViewPager extends React.Component {

  static propTypes = {
    /**
     * Index of initial page that should be selected. Use `setPage` method to
     * update the page, and `onPageSelected` to monitor page changes
     */
    initialPage: PropTypes.number,

    /**
     * Executed when transitioning between pages (ether because of animation for
     * the requested page change or when user is swiping/dragging between pages)
     * The `event.nativeEvent` object for this callback will carry following data:
     *  - position - index of first page from the left that is currently visible
     *  - offset - value from range [0,1) describing stage between page transitions.
     *    Value x means that (1 - x) fraction of the page at "position" index is
     *    visible, and x fraction of the next page is visible.
     */
    onPageScroll: PropTypes.func,

    /**
     * This callback will be caleld once ViewPager finish navigating to selected page
     * (when user swipes between pages). The `event.nativeEvent` object passed to this
     * callback will have following fields:
     *  - position - index of page that has been selected
     */
    onPageSelected: PropTypes.func,

    /**
     * Determines whether the keyboard gets dismissed in response to a drag.
     *   - 'none' (the default), drags do not dismiss the keyboard.
     *   - 'on-drag', the keyboard is dismissed when a drag begins.
     */
    keyboardDismissMode: PropTypes.oneOf([
      'none', // default
      'on-drag'
    ])
  }

  static defaultProps = {
    initialPage: 0
  }

  state = {
    selectedPage: this.props.initialPage,
    pageWidth: deviceSize.width,
    pageCount: this.props.children.length,
    offsetLeft: new Animated.Value(0)
  }

  getInnerViewNode() {
    return ReactDOM.findDOMNode(this.refs[VIEWPAGER_REF]).childNodes[0];
  }

  componentWillMount() {
    // let { offsetLeft, selectedPage } = this.state;

    // offsetLeft.addListener(({value}) => {
      // bad performance
      // this._onPageScroll({
      //  nativeEvent: {
      //    position: selectedPage,
      //    offset: value - selectedPage
      //  }
      // });
    // });

    this._panResponder = PanResponder.create({
      onStartShouldSetPanResponder: () => true,
      onMoveShouldSetPanResponder: this._shouldSetPanResponder.bind(this),
      onPanResponderGrant: () => { },
      onPanResponderMove: this._panResponderMove.bind(this),
      // onPanResponderTerminationRequest: () => true,
      onPanResponderRelease: this._panResponderRelease.bind(this),
      onPanResponderTerminate: () => { }
    });
  }

  componentDidMount() {
    this.setPage(this.state.selectedPage);
  }

  _childrenWithOverridenStyle() {
    // Override styles so that each page will fill the parent. Native component
    // will handle positioning of elements, so it's not important to offset
    // them correctly.
    return React.Children.map(this.props.children, function(child) {
      let style = assign({}, child.props.style, {width: deviceSize.width});
      let newProps = {
        style: style,
        collapsable: false
      };
      return cloneElement(child, assign({}, child.props, newProps));
    });
  }

  render() {
    let children = this._childrenWithOverridenStyle();

    let { offsetLeft, pageWidth, pageCount } = this.state;
    let width = pageWidth * pageCount;
    let count = pageCount - 1;

    let translateX = offsetLeft.interpolate({
      inputRange: [0, count],
      outputRange: [0, -(pageWidth * count)],
      extrapolate: 'clamp'
    });

    return (<View ref={VIEWPAGER_REF}
      style={this.props.style}
      {...this._panResponder.panHandlers}
    >
      <Animated.View style={{
        width: width,
        position: 'absolute',
        top: 0,
        left: translateX,
        bottom: 0,
        flexDirection: 'row'
      }}>
        {children}
      </Animated.View>
    </View>);
  }

  _onPageScroll(event) {
    if (this.props.onPageScroll) {
      this.props.onPageScroll(event);
    }
    if (this.props.keyboardDismissMode === 'on-drag') {
      dismissKeyboard();
    }
  }

  _shouldSetPanResponder(e) {
      // return false;
    if (this._scrolling) {
      this.state.offsetLeft.stopAnimation(()=> {
        this._scrolling = false;
      });
      return false;
    }

    return true;
  }

  _panResponderMove(ev, {dx, dy}) {
      if(Math.abs(dy) > Math.abs(dx)){
          return;
      }
    let val = this.state.selectedPage + dx / this.state.pageWidth * -1;
    this.state.offsetLeft.setValue(val);
  }

  _panResponderRelease(ev, {dx}) {
    let { selectedPage, pageWidth } = this.state;
    let range = Math.abs(dx) / pageWidth;
    let threshold = 1 / 5;

    if (range > threshold) {
      if (dx > 0) {
        selectedPage -= 1; // TODO step?
      }else {
        selectedPage += 1;
      }
    }

    this.setPage(selectedPage);
  }

  setPage(index) {
    if (index < 0) {
      index = 0;
    }else if (index >= this.state.pageCount) {
      index = this.state.pageCount - 1;
    }

    this._scrolling = true;

    Animated.spring(this.state.offsetLeft, {
      toValue: index,
      bounciness: 0,
      restSpeedThreshold: 1
    }).start(() => {

      this._onPageScroll({
        nativeEvent: {
          position: index,
          offset: 0
        }
      });

      this._scrolling = false;

      this.setState({
        selectedPage: index
      }, () => {
        this.props.onPageSelected && this.props.onPageSelected({nativeEvent: {position: index}});
      });
    });
  }

}

mixin.onClass(ViewPager, NativeMethodsMixin);
//autobind(ViewPager);

ViewPager.isReactNativeComponent = true;

export default ViewPager;
