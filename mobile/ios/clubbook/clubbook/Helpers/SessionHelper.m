//
//  SessionHelper.m
//  Clubbook
//
//  Created by Andrew on 7/14/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "SessionHelper.h"
#import <Parse/Parse.h>

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

    if (user.age != nil) {
       [defaults setObject:user.age forKey:@"userAge"];
    }

    [defaults synchronize];
    
}

@end
