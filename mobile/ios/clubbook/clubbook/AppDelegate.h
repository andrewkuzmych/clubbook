//
//  AppDelegate.h
//  SidebarDemo
//
//  Created by Simon on 28/6/13.
//  Copyright (c) 2013 Appcoda. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreLocation/CoreLocation.h>
#import <FacebookSDK/FacebookSDK.h>

@interface AppDelegate : UIResponder <UIApplicationDelegate, CLLocationManagerDelegate, PNDelegate, UIAlertViewDelegate>{
}

@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) UINavigationController *mainNC;
@property (nonatomic, assign) BOOL appUsageCheckEnabled;


- (void) sendRequest;
- (void)sendRequestToiOSFriends;
@end
