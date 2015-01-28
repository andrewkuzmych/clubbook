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
#import "NewsFeedViewController.h"

@interface ClubProfileTabBarViewController ()

@end

@implementation ClubProfileTabBarViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    if (self.place != nil) {
        self.title = self.place.title;
        
        NSMutableDictionary *controllersArray = [[NSMutableDictionary alloc] init];
        
        UIStoryboard *newsStoryboard = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle: nil];
        NewsFeedViewController *newsController  = [newsStoryboard instantiateViewControllerWithIdentifier:@"news"];
        newsController.type = @"club";
        newsController.newsObjectId = self.place.id;
        newsController.title = @"News";
        
        [controllersArray setObject:newsController forKey:@"news"];
        
        for (UIViewController *v in self.viewControllers) {
            if ([v isKindOfClass:[ClubViewParallaxControllerViewController class]]) {
                 ClubViewParallaxControllerViewController *parallaxController =  (ClubViewParallaxControllerViewController*)v;
                 parallaxController.place = self.place;
                 parallaxController.title = @"About";
                 [controllersArray setObject:parallaxController forKey:@"about"];
                 }
            else if ([v isKindOfClass:[ClubPhotGalleryCollectionViewController class]]) {
                ClubPhotGalleryCollectionViewController *photoGallery =  (ClubPhotGalleryCollectionViewController*)v;
                photoGallery.place = self.place;
                photoGallery.title = @"Photos";
                [controllersArray setObject:photoGallery forKey:@"photo"];
            }
            else if ([v isKindOfClass:[ClubSubscribeSettingsTableViewController class]]) {
                ClubSubscribeSettingsTableViewController *subscribeController =  (ClubSubscribeSettingsTableViewController*)v;
                subscribeController.place = self.place;
                subscribeController.title = @"Favorite";
                [controllersArray setObject:subscribeController forKey:@"fav"];
            }
        }
        
        //order of controllers should be: info, news, photos, favorite
        NSMutableArray* orderedArrays = [[NSMutableArray alloc] init];
        [orderedArrays addObject:[controllersArray objectForKey:@"about"]];
        [orderedArrays addObject:[controllersArray objectForKey:@"news"]];
        [orderedArrays addObject:[controllersArray objectForKey:@"photo"]];
        [orderedArrays addObject:[controllersArray objectForKey:@"fav"]];
        
        self.viewControllers = orderedArrays;
        
        [[UITabBar appearance] setBarTintColor:[UIColor whiteColor]];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end