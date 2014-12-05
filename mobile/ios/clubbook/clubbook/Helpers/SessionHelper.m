//
//  SessionHelper.m
//  Clubbook
//
//  Created by Andrew on 7/14/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "SessionHelper.h"
#import <Parse/Parse.h>
#import "LocationHelper.h"

@implementation SessionHelper

+(void) StoreUser:(User *)user
{
    PFInstallation *currentInstallation = [PFInstallation currentInstallation];
    [currentInstallation addUniqueObject:[NSString stringWithFormat:@"user_%@", user.id] forKey:@"channels"];
    [currentInstallation saveInBackground];
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:user.id forKey:@"userId"];
    [defaults setObject:user.name forKey:@"userName"];
    [defaults setObject:user.email forKey:@"userEmail"];
    [defaults setObject:user.gender forKey:@"userGender"];
    [defaults setObject:user.avatar forKey:@"userAvatar"];
    [defaults setObject:user.access_token forKey:@"accessToken"];
    [defaults setBool:user.isFb forKey:@"isFb"];

    [defaults setObject:(user.push) ? @"true" : @"false" forKey:@"userPush"];

    if (user.age != nil) {
       [defaults setObject:user.age forKey:@"userAge"];
    }

    [defaults synchronize];
}

+(void) DeleteUser
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    PFInstallation *currentInstallation = [PFInstallation currentInstallation];
    [currentInstallation removeObject:[NSString stringWithFormat:@"user_%@", userId] forKey:@"channels"];
    [currentInstallation saveInBackground];
    
    [defaults setObject:@"" forKey:@"userId"];
    [defaults setObject:@"" forKey:@"userName"];
    [defaults setObject:@"" forKey:@"userEmail"];
    [defaults setObject:@"" forKey:@"userGender"];
    [defaults setObject:@"" forKey:@"userAvatar"];
    [defaults setObject:@"" forKey:@"userAge"];
    [defaults setObject:@"" forKey:@"accessToken"];
    
    [LocationHelper removeCheckin];
    
    [defaults synchronize];
}

@end
