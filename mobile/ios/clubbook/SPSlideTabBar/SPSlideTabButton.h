//
//  SPSlideTabButton.h
//  SPSlideTabView
//
//  Created by Spring on 14-5-23.
//  Copyright (c) 2014年 aokizen. All rights reserved.
//

#import <UIKit/UIKit.h>


#define kSlideTabButtonMinWidth 80

@interface SPSlideTabButton : UIButton

@property (assign, nonatomic) CGFloat minWidth;

- (id)initWithTitle:(NSString *)title WithHeight:(CGFloat)height;

- (CGFloat)widthToFit;
- (void)fitSize;

@end
