//
//  SPSlideTabBar.m
//  SPSlideTabView
//
//  Created by Spring on 14-5-22.
//  Copyright (c) 2014年 aokizen. All rights reserved.
//

#import "SPSlideTabBar.h"

#import "SPSlideTabButton.h"

@interface SPSlideTabBar ()

@property (strong, nonatomic) NSMutableArray *buttons;

@property (strong, nonatomic) UIView *selectedView;

@property (strong, nonatomic) NSMutableArray *lineViews;

@end

@implementation SPSlideTabBar

@synthesize selectedIndex = _selectedIndex;
@synthesize selectedViewColor = _selectedViewColor;
@synthesize selectedViewSizeToFit = _selectedViewSizeToFit;
@synthesize barButtonMinWidth = _barButtonMinWidth;
@synthesize separatorStyle = _separatorStyle;
@synthesize separatorColor = _separatorColor;
@synthesize separatorLineInsetTop = _separatorLineInsetTop;
@synthesize barButtonTitleColor = _barButtonTitleColor;
@synthesize barButtonTitleFont = _barButtonTitleFont;

- (void)dealloc {
    self.delegate = nil;
    self.slideDelegate = nil;
}

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInit];
    }
    return self;
}

- (void)commonInit {
    
    [self setBackgroundColor:[UIColor clearColor]];
    
    self.buttons = [NSMutableArray array];
    self.lineViews = [NSMutableArray array];

    self.selectedView = [[UIView alloc] initWithFrame:CGRectMake(0, self.frame.size.height - 2, 0, 2)];
    [self addSubview:self.selectedView];
    
    self.selectedViewSizeToFit = YES;
    
    [self reset];
}

- (void)reset {
    
    [self setContentInset:UIEdgeInsetsMake(0, self.contentInset.left, self.contentInset.bottom, self.contentInset.right)];
    [self setShowsHorizontalScrollIndicator:NO];
    [self setShowsVerticalScrollIndicator:NO];
    
    
    [self.selectedView setBackgroundColor:[self selectedViewColor]];
    
    if (self.lineViews.count > 0) {
        [self.lineViews makeObjectsPerformSelector:@selector(removeFromSuperview)];
        [self.lineViews removeAllObjects];
    }
    
}

- (void)drawRect:(CGRect)rect {
    [super drawRect:rect];
    
    [self reset];
    
    if (self.separatorStyle != SPSlideTabBarSeparatorStyleNone) {
        
        if ([self barButtons].count > 1) {
            
            for (int i = 1; i < [self barButtons].count; i++) {
                SPSlideTabButton *button = [[self barButtons] objectAtIndex:i];
                
                UIView *line = [[UIView alloc] initWithFrame:CGRectMake(button.frame.origin.x - 0.5, [self separatorLineInsetTop], 1, button.frame.size.height - [self separatorLineInsetTop] * 2)];
                [line setBackgroundColor:[self separatorColor]];
                [line setAutoresizingMask:UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleHeight];
                [self.lineViews addObject:line];
                [self addSubview:line];
                
            }
        }
        
    }

}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    CGFloat originX = 0;
    for (int i = 0; i < [self barButtons].count; i ++) {
        SPSlideTabButton *button = [[self barButtons] objectAtIndex:i];
        [button setMinWidth:self.barButtonMinWidth];
        [button fitSize];
        
        CGRect frame = button.frame;
        frame.origin.x = originX;
        button.frame = frame;
        
        originX += frame.size.width;
    }
    
    [self setContentSize:CGSizeMake(originX, self.frame.size.height)];

}

#pragma mark - Action
- (IBAction)barButtonClicked:(SPSlideTabButton *)sender {
    if (self.slideDelegate) {
        [self.slideDelegate barButtonClicked:sender];
    }
    
    [UIView animateWithDuration:0.2 animations:^(void) {
        
        [self scrollRectToVisible:[self selectedButton].frame animated:NO];
    }];
}

#pragma mark - Public
- (void)addTabForTitle:(NSString *)title {
    SPSlideTabButton *button = [[SPSlideTabButton alloc] initWithTitle:title WithHeight:self.frame.size.height];
    [button setTitleColor:[self barButtonTitleColor] forState:UIControlStateNormal];
    button.titleLabel.font = [self barButtonTitleFont];
    [button setMinWidth:self.barButtonMinWidth];
    [button fitSize];
    
    if ([self barButtons].count > 0) {
        
        SPSlideTabButton *lastButton = [[self barButtons] lastObject];
        CGRect frame = button.frame;
        frame.origin.x = CGRectGetMaxX(lastButton.frame);
        button.frame = frame;
    }
    
    [button addTarget:self action:@selector(barButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
    [button setTag:[self barButtons].count];
    
    
    [self.buttons addObject:button];
    
    [self addSubview:button];
    
    [self setContentSize:CGSizeMake(CGRectGetMaxX(button.frame), self.frame.size.height)];
    
    [self setNeedsDisplay];
}

- (void)removeAllTabs {
    for (UIView *button in self.buttons) {
        [button removeFromSuperview];
    }
    [self.buttons removeAllObjects];
    
    for (UIView *line in self.lineViews) {
        [line removeFromSuperview];
    }
    [self.lineViews removeAllObjects];
}

- (void)setScrollOffsetPercentage:(CGFloat)percentage {

    SPSlideTabButton *targetButton;
    
    if (percentage > 0.0) {
        NSInteger targetIndex = self.selectedIndex + 1;
        if (targetIndex < [self barButtons].count) {
            targetButton = [self nextButton];
        }
    }
    else if (percentage < 0.0){
        NSInteger targetIndex = self.selectedIndex - 1;
        if (targetIndex >= 0) {
            targetButton = [self previousButton];
        }
    }
    else {
        targetButton = [self selectedButton];
    }
    
    if(targetButton) {
        
        SPSlideTabButton *originButton = [self selectedButton];
        
        CGRect originFrame = [self selectionFrameForBarButton:originButton];
        CGRect targetFrame = [self selectionFrameForBarButton:targetButton];
        
        CGRect frame = self.selectedView.frame;
        frame.origin.y = self.frame.size.height - frame.size.height;
        frame.origin.x = originFrame.origin.x + (targetFrame.origin.x - originFrame.origin.x) * fabs(percentage);
        frame.size.width = originFrame.size.width + (targetFrame.size.width - originFrame.size.width) * fabs(percentage);
        
        //if ((frame.origin.x - self.selectedView.frame.origin.x) * (targetFrame.origin.x - originFrame.origin.x) > 0) { // 往同一方向滑动
            self.selectedView.frame = frame;
 
        //}

    }
}

#pragma private
- (void)fixSelectedView {
    
    if ([self selectedButton]) {
    
        [UIView animateWithDuration:0.2 animations:^(void) {

            self.selectedView.frame = [self selectionFrameForBarButton:[self selectedButton]];
            
        } completion:^(BOOL finished) {
        
            [UIView animateWithDuration:0.2 animations:^(void) {
        
                [self scrollRectToVisible:[self selectedButton].frame animated:NO];
            }];
        }];
    }
}

- (CGRect)selectionFrameForBarButton:(SPSlideTabButton *)button {

    CGFloat originX = CGRectGetMidX(button.frame) - [button widthToFit] / 2;
    CGFloat frameWidth = [button widthToFit];
    if (!self.selectedViewSizeToFit) {
        originX = CGRectGetMinX(button.frame);
        frameWidth = button.frame.size.width;
    }
    
    CGRect frame = self.selectedView.frame;
    frame.origin.x = originX;
    frame.size.width = frameWidth;
    frame.origin.y = self.frame.size.height - frame.size.height;
    frame.size.height = 2;
    
    return frame;
}

#pragma mark - getter / setter
- (void)setSelectedIndex:(NSUInteger)selectedIndex {
    
    _selectedIndex = selectedIndex;
    
    [self fixSelectedView];
}

- (NSArray *)barButtons {
    return self.buttons;
}

- (SPSlideTabButton *)previousButton {
    if (self.selectedIndex > 0) {
        return [[self barButtons] objectAtIndex:self.selectedIndex - 1];
    }
    else {
        return nil;
    }
}

- (SPSlideTabButton *)nextButton {
    if (self.selectedIndex < [self barButtons].count) {
        return [[self barButtons] objectAtIndex:self.selectedIndex + 1];
    }
    else {
        return nil;
    }
}

- (SPSlideTabButton *)selectedButton {
    
    if ([self barButtons].count > 0) {
        return [[self barButtons] objectAtIndex:self.selectedIndex];
    }
    
    return nil;
}


- (UIColor *)selectedViewColor {
    if (_selectedViewColor) {
        return _selectedViewColor;
    }
    
    return [UIColor magentaColor];
}


- (void)setSelectedViewColor:(UIColor *)selectedViewColor {
    _selectedViewColor = selectedViewColor;
    
    [self.selectedView setBackgroundColor:_selectedViewColor];
}

- (void)setSelectedViewSizeToFit:(BOOL)selectedViewSizeToFit {
    _selectedViewSizeToFit = selectedViewSizeToFit;
    
    [self fixSelectedView];
}

- (CGFloat)barButtonMinWidth {
    if (_barButtonMinWidth) {
        return _barButtonMinWidth;
    }
    
    return kSlideTabButtonMinWidth;
}

- (void)setBarButtonMinWidth:(CGFloat)barButtonMinWidth {
    
    _barButtonMinWidth = barButtonMinWidth;
    
    [self setNeedsLayout];
    [self layoutIfNeeded];
    
    [self setNeedsDisplay];
    
    [self fixSelectedView];
}


- (SPSlideTabBarSeparatorStyle)separatorStyle {
    if (_separatorStyle) {
        return _separatorStyle;
    }
    
    return SPSlideTabBarSeparatorStyleSingleLine;
}

- (void)setSeparatorStyle:(SPSlideTabBarSeparatorStyle)separatorStyle {
    _separatorStyle = separatorStyle;
    
    [self setNeedsDisplay];
}

- (UIColor *)separatorColor {
    if (_separatorColor) {
        return _separatorColor;
    }
    
    return [UIColor clearColor];
}

- (void)setSeparatorColor:(UIColor *)separatorColor {
    _separatorColor = separatorColor;
    
    [self setNeedsDisplay];
}

- (void)setSeparatorLineInsetTop:(CGFloat)separatorLineInsetTop {
    _separatorLineInsetTop = separatorLineInsetTop;
    
    [self setNeedsDisplay];
}

- (UIColor *)barButtonTitleColor {
    if (_barButtonTitleColor) {
        return _barButtonTitleColor;
    }
    
    return [UIColor blueColor];
}

- (void)setBarButtonTitleColor:(UIColor *)titleColor {
    _barButtonTitleColor = titleColor;
    
    if ([self barButtons].count) {
        [[self barButtons] enumerateObjectsUsingBlock:^(id obj, NSUInteger index, BOOL *stop) {
            SPSlideTabButton *button = (SPSlideTabButton *)obj;
            [button setTitleColor:self.barButtonTitleColor forState:UIControlStateNormal];
        }];
    }
}

- (UIFont *)barButtonTitleFont {
    if (_barButtonTitleFont) {
        return _barButtonTitleFont;
    }
    
    return [UIFont systemFontOfSize:17];
}

- (void)setBarButtonTitleFont:(UIFont *)barButtonTitleFont {
    _barButtonTitleFont = barButtonTitleFont;
    
    if ([self barButtons].count) {
        
        for (int i = 0; i < [self barButtons].count; i++){
            SPSlideTabButton *button = [[self barButtons] objectAtIndex:i];
            button.titleLabel.font = [self barButtonTitleFont];
        }
        
        [self setNeedsLayout];
    }
}

@end
