//
//  ProfilePagesViewController.h
//  Clubbook
//
//  Created by Andrew on 10/25/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Place.h"

@interface ProfilePagesViewController : UIViewController<UIPageViewControllerDataSource>

@property (strong, nonatomic) UIPageViewController *pageController;
@property (strong, nonatomic) NSArray *profiles;
@property (assign, nonatomic) NSUInteger index;
@property (strong, nonatomic) Place *currentPlace;

@end
