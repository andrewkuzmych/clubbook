//
//  EventsViewController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 2/17/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "EventsViewController.h"
#import "LocationManagerSingleton.h"

@implementation EventsViewController

- (void) viewDidLoad {
    double user_lat = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.latitude;
    double user_lon = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.longitude;
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString* user_accessToken = [defaults objectForKey:@"accessToken"];
    
    self.eventsView.eventsTable.transitionDelegate = self;
    self.eventsView.eventsTable.singlePlaceEvents = YES;
    self.eventsView.eventsTable.placeId = self.place.id;
    self.eventsView.eventsTable.type = self.place.category;
    [self.eventsView customInit:user_lat userLon:user_lon accessTOken:user_accessToken];
}

- (void) transitToNewController:(UIViewController *)controller {
    [UIView beginAnimations:@"animation" context:nil];
    [UIView setAnimationDuration:0.5];
    [self.navigationController pushViewController:controller animated:NO];
    [UIView setAnimationTransition:UIViewAnimationTransitionFlipFromLeft forView:self.navigationController.view cache:NO];
    [UIView commitAnimations];
}

@end
