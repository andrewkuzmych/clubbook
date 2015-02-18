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
    
    self.eventsView = [[EventsView alloc] init];
    
    self.eventsView.eventsTable.transitionDelegate = self;
    self.eventsView.eventsTable.singlePlaceEvents = YES;
    
    if (self.event.place != nil) {
        self.eventsView.eventsTable.placeId = self.event.place.id;
        self.eventsView.eventsTable.placeType = self.event.place.category;
    }
    
    if (self.event.dj != nil) {
        self.eventsView.eventsTable.placeId = self.event.dj.djId;
        self.eventsView.eventsTable.placeType = @"dj";
    }
    
    [self.eventsView customInitType:@"" userLat:user_lat userLon:user_lon accessTOken:user_accessToken];

    self.eventsView.frame = CGRectMake(0, 0, self.mainView.frame.size.width, self.mainView.frame.size.height);
    [self.eventsView.segmaentControl setHidden:YES];
    [self.mainView addSubview:self.eventsView];
}

- (void) transitToNewController:(UIViewController *)controller {
    [UIView beginAnimations:@"animation" context:nil];
    [UIView setAnimationDuration:0.5];
    [self.navigationController pushViewController:controller animated:NO];
    [UIView setAnimationTransition:UIViewAnimationTransitionFlipFromLeft forView:self.navigationController.view cache:NO];
    [UIView commitAnimations];
}

@end
