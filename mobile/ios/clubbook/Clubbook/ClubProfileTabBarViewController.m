//
//  ClubProfileTabBarViewController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 1/2/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "ClubProfileTabBarViewController.h"
#import "ClubCheckinsViewController.h"
#import "ClubViewParallaxControllerViewController.h"
#import "ClubPhotGalleryCollectionViewController.h"
#import "NewsFeedViewController.h"

@interface ClubProfileTabBarViewController ()

@end

@implementation ClubProfileTabBarViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    if (self.place != nil) {
        self.title = self.place.title;
        
        for (UIViewController *v in self.viewControllers) {
            if ([v isKindOfClass:[ClubViewParallaxControllerViewController class]]) {
                ClubViewParallaxControllerViewController *parallaxController =  (ClubViewParallaxControllerViewController*)v;
                parallaxController.place = self.place;
                parallaxController.title = @"About";
            }
            else if ([v isKindOfClass:[NewsFeedViewController class]]) {
                NewsFeedViewController *newsController =  (NewsFeedViewController *)v;
                newsController.title = @"News";
            }
            else if ([v isKindOfClass:[ClubCheckinsViewController class]]) {
                ClubCheckinsViewController *clubController =  (ClubCheckinsViewController *)v;
                clubController.title = @"Checked-in";
                clubController.place = self.place;
            }
        }

        [[UITabBar appearance] setBarTintColor:[UIColor whiteColor]];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end
