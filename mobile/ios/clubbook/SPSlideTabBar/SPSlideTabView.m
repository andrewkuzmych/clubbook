//
//  SPSlideTabView.m
//  SPSlideTabView
//
//  Created by Spring on 14-5-22.
//  Copyright (c) 2014年 aokizen. All rights reserved.
//

#import "SPSlideTabView.h"

#import "SPSlideTabButton.h"

#define KVO_CONTEXT_SCROLL_CONTENT_OFFSET @"KVO_CONTEXT_SCROLL_CONTENT_OFFSET"

#define iPad UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad
#define kSlideVelocity 2000.0
#define kSlideMaxSeconds (iPad ? 0.35 : 0.25)

@interface SPSlideTabView () <UIScrollViewDelegate, SPSlideTabBarDelegate> {
    CGPoint _locationBeforePan;
    CGPoint _locationAfterPan;
}

@property (strong, nonatomic) UIScrollView *scrollView;

@end

@implementation SPSlideTabView

@synthesize tabBarHeight = _tabBarHeight;

- (void)dealloc {
    [self.scrollView removeObserver:self forKeyPath:@"contentOffset"];
    self.delegate = nil;
}

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInit];
    }
    return self;
}

- (id)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self) {
        [self commonInit];
    }
    return self;
}

- (void)commonInit {
    
    self.pageViewContainerPanels = [NSMutableArray array];
    
    self.tabBar = [[SPSlideTabBar alloc] initWithFrame:CGRectMake(0, 0, self.frame.size.width, [self tabBarHeight])];
    [self.tabBar setAutoresizingMask:UIViewAutoresizingFlexibleBottomMargin | UIViewAutoresizingFlexibleWidth];
    [self.tabBar setSlideDelegate:self];
    [self addSubview:self.tabBar];
    
    self.scrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, self.tabBar.frame.size.height, self.frame.size.width, self.frame.size.height - self.tabBar.frame.size.height)];
    [self.scrollView setAutoresizingMask:UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth];
    [self.scrollView setContentInset:UIEdgeInsetsZero];
    [self.scrollView setBackgroundColor:[UIColor clearColor]];
    [self.scrollView setShowsHorizontalScrollIndicator:NO];
    [self.scrollView setShowsVerticalScrollIndicator:NO];
    [self.scrollView setBounces:NO];
    [self.scrollView setAlwaysBounceVertical:NO];
    [self.scrollView setAlwaysBounceHorizontal:NO];
    [self.scrollView setPagingEnabled:YES];
    [self.scrollView setClipsToBounds:YES];
    [self.scrollView setContentSize:self.scrollView.bounds.size];
    [self addSubview:self.scrollView];
    
    [self.scrollView setDelegate:self];
    [self.scrollView addObserver:self forKeyPath:@"contentOffset" options:0 context:KVO_CONTEXT_SCROLL_CONTENT_OFFSET];
    
}

#pragma mark - layout
- (void)layoutSubviews {
    [super layoutSubviews];
    
    for (int i = 0; i < self.pageViewContainerPanels.count; i ++) {
        UIView *panel = [self.pageViewContainerPanels objectAtIndex:i];
        [panel setFrame:CGRectMake(self.frame.size.width * i, 0, self.scrollView.frame.size.width, self.scrollView.frame.size.height)];
    }
    
    if (self.pageViewContainerPanels.count > 0) {
        [self.scrollView setContentSize:CGSizeMake(CGRectGetMaxX(((UIView *)self.pageViewContainerPanels.lastObject).frame), self.scrollView.frame.size.height)];
    }
    
    [self.scrollView setContentOffset:CGPointMake(self.frame.size.width * self.selectedPageIndex, 0) animated:NO];}

#pragma mark - override
- (void)setFrame:(CGRect)frame {
    [super setFrame:frame];
    
    [self.scrollView setContentSize:CGSizeMake(self.scrollView.contentSize.width, self.scrollView.frame.size.height)];
}

#pragma mark - public
- (void)addPageView:(UIView *)pageView ForTitle:(NSString *)title {
    [self.tabBar addTabForTitle:title];
    
    UIView *containerPanel = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.scrollView.frame.size.width, self.scrollView.frame.size.height)];
    
    
    if (self.pageViewContainerPanels.count > 0) {
        UIView *lastPanel = [self.pageViewContainerPanels lastObject];
        [containerPanel setFrame:CGRectMake(CGRectGetMaxX(lastPanel.frame), containerPanel.frame.origin.y, containerPanel.frame.size.width, containerPanel.frame.size.height)];
    }
    
    CGRect frame = pageView.frame;
    frame.origin = CGPointZero;
    frame.size = containerPanel.frame.size;
    pageView.frame = frame;
    [pageView setAutoresizingMask:UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight];
    
    
    [self styleContainerPanel:containerPanel];
    
    
    NSMutableArray *array = [NSMutableArray arrayWithArray:self.pageViewContainerPanels];
    [array addObject:containerPanel];
    self.pageViewContainerPanels = array;
    
    
    [containerPanel addSubview:pageView];
    [self.scrollView addSubview:containerPanel];
    
    [self.scrollView setContentSize:CGSizeMake(CGRectGetMaxX(containerPanel.frame), self.scrollView.frame.size.height)];
    
}

- (void)removeAllPageViews {
    for (UIView *panels in self.pageViewContainerPanels) {
        [panels removeFromSuperview];
    }
    self.pageViewContainerPanels = nil;
    
    [self.tabBar removeAllTabs];
}

- (void)setNeedsRender {
    
    [self.tabBar setSelectedIndex:self.selectedPageIndex];
    
    [self.scrollView setContentSize:CGSizeMake(self.scrollView.contentSize.width, self.scrollView.frame.size.height)];
    
    [self.scrollView setContentOffset:CGPointMake(self.frame.size.width * self.selectedPageIndex, 0) animated:NO];
    
    [self.tabBar setSelectedIndex:self.selectedPageIndex];
    
    if (self.delegate) {
        [self.delegate slideTabView:self didScrollToPageIndex:self.selectedPageIndex];
    }
}

#pragma mark - Private
- (void)scrollToPage:(NSUInteger)page {
    
    CGFloat targetOffsetX = self.frame.size.width * page;
    float seconds = fabs(targetOffsetX - self.scrollView.contentOffset.x) / kSlideVelocity;
    
    if (seconds > kSlideMaxSeconds) {
        seconds = kSlideMaxSeconds;
    }

    [UIView animateWithDuration:seconds animations:^(void) {
        [self.scrollView setContentOffset:CGPointMake(self.frame.size.width * page, 0) animated:NO];
    }completion:^(BOOL finished) {
        
        [self.tabBar setSelectedIndex:page];

        [self.scrollView setContentSize:CGSizeMake(self.scrollView.contentSize.width, self.scrollView.frame.size.height)];
        
        if (self.selectedPageIndex != page) {
            
            [self setSelectedPageIndex:page];
            
            if (self.delegate) {
                [self.delegate slideTabView:self didScrollToPageIndex:self.selectedPageIndex];
            }
        }
    }];
}

#pragma mark - style
- (void)styleContainerPanel:(UIView *)containerPanel {
    
    [containerPanel setAutoresizingMask:UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight];
    
    [containerPanel setBackgroundColor:[UIColor clearColor]];
}

#pragma mark - setter / getter

- (void)setSelectedPageIndex:(NSUInteger)selectedPageIndex {
    _selectedPageIndex = selectedPageIndex;
}

- (UIView *)selectedPageView {
    UIView *selectedPageView = [self.pageViewContainerPanels objectAtIndex:self.selectedPageIndex];
    return selectedPageView;
}

- (UIView *)previousPageView {
    if (self.selectedPageIndex > 0) {
        return [self.pageViewContainerPanels objectAtIndex:self.selectedPageIndex - 1];
    }
    
    return nil;
}

- (UIView *)nextPageView {
    if (self.selectedPageIndex + 1 < self.pageViewContainerPanels.count) {
        return [self.pageViewContainerPanels objectAtIndex:self.selectedPageIndex + 1];
    }
    
    return nil;
}

- (CGFloat)tabBarHeight {
    if (_tabBarHeight) {
        return _tabBarHeight;
    }
    
    return 49;
}

- (void)setTabBarHeight:(CGFloat)height {
    _tabBarHeight = height;
    
    [self.tabBar setFrame:CGRectMake(self.tabBar.frame.origin.x, self.tabBar.frame.origin.y, self.tabBar.frame.size.width, self.tabBarHeight)];
    [self.scrollView setFrame:CGRectMake(self.scrollView.frame.origin.x,self.tabBarHeight, self.scrollView.frame.size.width, self.frame.size.height - self.tabBarHeight)];
}

#pragma mark - style setter
- (void)setSelectedViewColor:(UIColor *)selectedViewColor {
    [self.tabBar setSelectedViewColor:selectedViewColor];
}

- (void)setSelectedViewSizeToFit:(BOOL)fit {
    [self.tabBar setSelectedViewSizeToFit:fit];
}

- (void)setBarButtonMinWidth:(CGFloat)barButtonMinWidth {
    [self.tabBar setBarButtonMinWidth:barButtonMinWidth];
}

- (void)setSeparatorStyle:(SPSlideTabBarSeparatorStyle)separatorStyle {
    [self.tabBar setSeparatorStyle:separatorStyle];
}

- (void)setSeparatorColor:(UIColor *)separatorColor {
    [self.tabBar setSeparatorColor:separatorColor];
}

- (void)setSeparatorLineInsetTop:(CGFloat)separatorLineInsetTop {
    [self.tabBar setSeparatorLineInsetTop:separatorLineInsetTop];
}

- (void)setTabBarBackgroundColor:(UIColor *)color {
    [self.tabBar setBackgroundColor:color];
}

- (void)setBarButtonTitleColor:(UIColor *)titleColor {
    [self.tabBar setBarButtonTitleColor:titleColor];
}

- (void)setBarButtonTitleFont:(UIFont *)titleFont {
    [self.tabBar setBarButtonTitleFont:titleFont];
}

#pragma mark - UIScrollViewDelegate
//- (void)scrollViewDidScroll:(UIScrollView *)scrollView  {
//
//    CGFloat pageWidth = scrollView.frame.size.width;
//    int page = floor((scrollView.contentOffset.x - pageWidth / 2) / pageWidth) + 1;
//
//    if (page != self.selectedPageIndex) {
//        [self scrollToPage:page];
//    }
//}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView {
    
	CGFloat pageWidth = scrollView.frame.size.width;
    int page = floor((scrollView.contentOffset.x - pageWidth / 2) / pageWidth) + 1;
    
    if (page != self.selectedPageIndex) {
        [self scrollToPage:page];
    }
}

#pragma mark - Observer
- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    if (object == self.scrollView) {
        if (context == KVO_CONTEXT_SCROLL_CONTENT_OFFSET) {

            [self.tabBar setScrollOffsetPercentage:self.scrollView.contentOffset.x / (CGFloat)self.scrollView.frame.size.width - self.selectedPageIndex];
        }
    }
}

#pragma mark - SPSlideTabBarDelegate
- (void)barButtonClicked:(SPSlideTabButton *)button {
    [self scrollToPage:button.tag];
}

@end
