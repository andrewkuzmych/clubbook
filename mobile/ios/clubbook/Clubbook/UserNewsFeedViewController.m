//
//  UserNewsFeedViewController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 2/20/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "UserNewsFeedViewController.h"
#import "LocationManagerSingleton.h"

@interface UserNewsFeedViewController ()

@end

@implementation UserNewsFeedViewController

- (void) viewDidLoad {
    [super viewDidLoad];
    double user_lat = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.latitude;
    double user_lon = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.longitude;
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString* user_accessToken = [defaults objectForKey:@"accessToken"];
    
    self.newsView = [[NewsView alloc] init];
    
    self.newsView.newsTable.transitionDelegate = self;
    
    [self.newsView customInitType:user_lat userLon:user_lon accessToken:user_accessToken type:@"user" objectId:@"" andParentViewCntroller:self];
    
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


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
- (IBAction)handleSegmentChange:(id)sender {
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
