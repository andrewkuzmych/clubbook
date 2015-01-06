//
//  PhotoSlideViewController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 1/6/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "PhotoSlideViewController.h"
#import "UIImageView+WebCache.h"
#import "Cloudinary.h"
#import "Constants.h"
#import "ChatViewController.h"
#import "UIButton+WebCache.h"
#import "UIView+StringTagAdditions.h"
#import "CSNotificationView.h"
#import "LocationHelper.h"
#import "TransitionFromUserToClubUsers.h"
#import "ClubUsersViewController.h"
#import "ClubUsersYesterdayViewController.h"
#import "UserCheckinsViewController.h"

@interface PhotoSlideViewController (){
    float oldX;
}


@end

@implementation PhotoSlideViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self.imageScrollView.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    for (int i = 0; i < [self.photosArrays count]; i++) {
        CGRect frame;
        frame.origin.x = self.imageScrollView.frame.size.width * i;
        frame.origin.y = 0;
        frame.size = self.imageScrollView.frame.size;
        
        UIImageView *imageView = [[UIImageView alloc] initWithFrame:frame];
        imageView.contentMode = UIViewContentModeScaleAspectFit;
        
        imageView.backgroundColor = [UIColor blackColor];
        
        [imageView setImage:[self.photosArrays objectAtIndex:i]];

        [self.imageScrollView addSubview:imageView];
        
    }
    
    self.imageScrollView.delegate = self;
    
    self.imagePageControl.numberOfPages = self.imageScrollView.subviews.count;
    
    self.imageScrollView.contentSize = CGSizeMake(self.imageScrollView.frame.size.width * [self.photosArrays count]  , self.imageScrollView.frame.size.height + 2);
    [self scrollToItemAt:self.selectedIndex];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    //Google Analytics
    id tracker = [[GAI sharedInstance] defaultTracker];
    [tracker set:kGAIScreenName
           value:@"Club Photos Screen"];
    [tracker send:[[GAIDictionaryBuilder createAppView] build]];
}

-(void) scrollToItemAt:(NSUInteger) index{
    [self.imageScrollView setContentOffset: CGPointMake(self.imageScrollView.contentOffset.x, oldX  )];
    self.imagePageControl.currentPage = index;
    CGFloat pageWidth = self.imageScrollView.frame.size.width;
    CGFloat point = (index * pageWidth);
    [self.imageScrollView setContentOffset: CGPointMake(point, oldX  )];
}

#pragma mark - UIScrollView Delegate
- (void)scrollViewDidScroll:(UIScrollView *)sender
{
    if (sender == self.imageScrollView) {
        [self.imageScrollView setContentOffset: CGPointMake(self.imageScrollView.contentOffset.x, oldX  )];
        
        // Update the page when more than 50% of the previous/next page is visible
        CGFloat pageWidth = self.imageScrollView.frame.size.width;
        int page = floor((self.imageScrollView.contentOffset.x - pageWidth / 2) / pageWidth) + 1;
        self.imagePageControl.currentPage = page;
        self.selectedIndex = page - 1;
    }
}
@end