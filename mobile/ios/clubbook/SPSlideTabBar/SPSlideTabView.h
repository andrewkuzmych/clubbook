//
//  SPSlideTabView.h
//  SPSlideTabView
//
//  Created by Spring on 14-5-22.
//  Copyright (c) 2014年 aokizen. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "SPSlideTabBar.h"

@protocol SPSlideTabViewDelegate;

@interface SPSlideTabView : UIView

@property (assign, nonatomic) CGFloat tabBarHeight;

@property (assign, nonatomic) NSUInteger selectedPageIndex;

@property (strong, nonatomic) NSArray *pageViewContainerPanels;

@property (strong, nonatomic) SPSlideTabBar *tabBar;
@property (weak, nonatomic) id<SPSlideTabViewDelegate> delegate;


- (void)addPageView:(UIView *)pageView ForTitle:(NSString *)title;
- (void)removeAllPageViews;
- (void)setNeedsRender;

#pragma mark - style
- (void)setTabBarHeight:(CGFloat)height;
- (void)setSelectedViewSizeToFit:(BOOL)fit;
- (void)setSelectedViewColor:(UIColor *)selectedViewColor;
- (void)setBarButtonMinWidth:(CGFloat)barButtonMinWidth;
- (void)setSeparatorStyle:(SPSlideTabBarSeparatorStyle)separatorStyle;
- (void)setSeparatorColor:(UIColor *)separatorColor;
- (void)setSeparatorLineInsetTop:(CGFloat)separatorLineInsetTop;
- (void)setTabBarBackgroundColor:(UIColor *)color;
- (void)setBarButtonTitleColor:(UIColor *)titleColor;
- (void)setBarButtonTitleFont:(UIFont *)titleFont;

@end

@protocol SPSlideTabViewDelegate <NSObject>


- (void)slideTabView:(SPSlideTabView *)slideTabView didScrollToPageIndex:(NSInteger)pageIndex;

@end