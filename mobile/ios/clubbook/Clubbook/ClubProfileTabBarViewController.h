//
//  ClubProfileTabBarViewController.h
//  Clubbook
//
//  Created by Anton Semenyuk on 1/2/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "Place.h"

@interface ClubProfileTabBarViewController : UITabBarController <UITabBarControllerDelegate>

@property (strong, nonatomic) Place* place;

@end
