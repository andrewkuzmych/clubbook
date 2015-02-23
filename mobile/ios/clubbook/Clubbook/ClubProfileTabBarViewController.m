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
#import "EventsViewController.h"

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
                newsController.type = self.place.category;
                newsController.newsObjectId = self.place.id;
            }
            else if ([v isKindOfClass:[ClubCheckinsViewController class]]) {
                ClubCheckinsViewController *clubController =  (ClubCheckinsViewController *)v;
                clubController.title = @"Checked-in";
                clubController.place = self.place;
            }
            else if ([v isKindOfClass:[EventsViewController class]]) {
                EventsViewController* controller = (EventsViewController*)v;
                controller.place = self.place;
                controller.dj = nil;
                controller.title = @"Events";
            }
        }
        self.navigationItem.backBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"" style:UIBarButtonItemStylePlain target:nil action:nil];
        [[UITabBar appearance] setBarTintColor:[UIColor whiteColor]];
    }
    self.delegate = self;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void) tabBarController:(UITabBarController *)tabBarController didSelectViewController:(UIViewController *)viewController {
    [self.navigationController setNavigationBarHidden:NO animated:NO];
}

@end
