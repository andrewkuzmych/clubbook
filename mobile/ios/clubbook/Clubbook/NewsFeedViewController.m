//
//  NewsFeedTableViewController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 1/8/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "NewsFeedViewController.h"
#import "LocationManagerSingleton.h"

@implementation NewsFeedViewController

- (void) viewDidLoad {
    self.edgesForExtendedLayout = UIRectEdgeNone;
    self.extendedLayoutIncludesOpaqueBars = NO;
    self.automaticallyAdjustsScrollViewInsets = NO;
    
    double user_lat = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.latitude;
    double user_lon = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.longitude;
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString* user_accessToken = [defaults objectForKey:@"accessToken"];
    
    self.newsView = [[NewsView alloc] init];
    
    self.newsView.newsTable.transitionDelegate = self;
    
    [self.newsView customInitType:user_lat userLon:user_lon accessToken:user_accessToken type:self.type objectId:self.newsObjectId andParentViewCntroller:self];
    
    self.newsView.frame = CGRectMake(0, 0, self.mainView.frame.size.width, self.mainView.frame.size.height);
    [self.mainView addSubview:self.newsView];
}

- (void) transitToNewController:(UIViewController *)controller {
    [UIView beginAnimations:@"animation" context:nil];
    [UIView setAnimationDuration:0.5];
    [self.navigationController pushViewController:controller animated:NO];
    [UIView setAnimationTransition:UIViewAnimationTransitionFlipFromLeft forView:self.navigationController.view cache:NO];
    [UIView commitAnimations];
}


@end
