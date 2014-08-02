//
//  ClubFooterView.m
//  Clubbook
//
//  Created by Andrew on 7/25/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "ClubFooterView.h"


@implementation ClubFooterView


- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

- (void)collapse
{
    CGRect frame= self.frame;
    frame.size.height = 0;
    self.frame = frame;
}

- (void)expand
{
    CGRect frame= self.frame;
    frame.size.height = self.footerContainer.frame.size.height;
    self.frame = frame;
}
@end
