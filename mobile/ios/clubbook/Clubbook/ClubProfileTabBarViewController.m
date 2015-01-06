//
//  ClubProfileTabBarViewController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 1/2/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "ClubProfileTabBarViewController.h"
#import "ClubViewParallaxControllerViewController.h"
#import "ClubSubscribeSettingsTableViewController.h"
#import "ClubPhotGalleryCollectionViewController.h"

@interface ClubProfileTabBarViewController ()

@end

@implementation ClubProfileTabBarViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    if (self.place != nil) {
        self.title = self.place.title;
        
        for (UIViewController *v in self.viewControllers) {
            if ([v isKindOfClass:[ClubViewParallaxControllerViewController class]])
                 {
                     ClubViewParallaxControllerViewController *parallaxController =  (ClubViewParallaxControllerViewController*)v;
                     parallaxController.place = self.place;
                     parallaxController.title = @"About";
                 }
            if ([v isKindOfClass:[ClubSubscribeSettingsTableViewController class]])
            {
                ClubSubscribeSettingsTableViewController *subscribeController =  (ClubSubscribeSettingsTableViewController*)v;
                subscribeController.place = self.place;
                subscribeController.title = @"Favorite";
            }
            if ([v isKindOfClass:[ClubPhotGalleryCollectionViewController class]])
            {
                ClubPhotGalleryCollectionViewController *photoGallery =  (ClubPhotGalleryCollectionViewController*)v;
                photoGallery.place = self.place;
                photoGallery.title = @"Photos";
            }

        }

    [[UITabBar appearance] setBarTintColor:[UIColor whiteColor]];
    
    }
    
    
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
