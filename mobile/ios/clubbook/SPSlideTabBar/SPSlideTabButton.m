//
//  SPSlideTabButton.m
//  SPSlideTabView
//
//  Created by Spring on 14-5-23.
//  Copyright (c) 2014年 aokizen. All rights reserved.
//

#import "SPSlideTabButton.h"

#define kContenPadding 8

@implementation SPSlideTabButton

@synthesize minWidth = _minWidth;

- (id)initWithTitle:(NSString *)title WithHeight:(CGFloat)height {
    
    self = [SPSlideTabButton buttonWithType:UIButtonTypeCustom];
    if (self) {
    
        [self setBackgroundColor:[UIColor clearColor]];
        [self setTitle:title forState:UIControlStateNormal];
        [self setFrame:CGRectMake(self.frame.origin.x, self.frame.origin.y, 0, height)];
        [self fitSize];
    }
    
    return self;
}

- (CGFloat)widthToFit {
    CGSize size = [self sizeThatFits:CGSizeMake(CGFLOAT_MAX, self.frame.size.height)];
    return size.width;
}

- (void)fitSize {
    CGSize size = [self sizeThatFits:CGSizeMake(CGFLOAT_MAX, self.frame.size.height)];
    size.width += kContenPadding * 2;
    if (size.width < self.minWidth) {
        size.width = self.minWidth;
    }
    [self setFrame:CGRectMake(self.frame.origin.x, self.frame.origin.y, size.width, self.frame.size.height)];
}

#pragma mark - setter / getter
- (CGFloat)minWidth {
    if (_minWidth) {
        return _minWidth;
    }
    
    return kSlideTabButtonMinWidth;
}

@end
