//
//  HeaderView.m
//  Clubbook
//
//  Created by Andrew on 6/30/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "HeaderView.h"

@implementation HeaderView{
float oldX;
}

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
   // self.imageScrollView.delegate = self;

    return self;
}

#pragma mark - UIScrollView Delegate
/*- (void)scrollViewDidScroll:(UIScrollView *)sender
{
    [self.imageScrollView setContentOffset: CGPointMake(self.imageScrollView.contentOffset.x, oldX  )];
    
    // Update the page when more than 50% of the previous/next page is visible
    CGFloat pageWidth = self.imageScrollView.frame.size.width;
    int page = floor((self.imageScrollView.contentOffset.x - pageWidth / 2) / pageWidth) + 1;
    self.imagePageControl.currentPage = page;
}*/


/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

@end
