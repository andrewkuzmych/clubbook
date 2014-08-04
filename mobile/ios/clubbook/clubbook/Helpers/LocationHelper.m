//
//  LocationHelper.m
//  Clubbook
//
//  Created by Andrew on 7/1/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "LocationHelper.h"
#import "ClubbookManager.h"
#import "ClubbookCommunicator.h"
#import "Place.h"
#import "GlobalVars.h"
#import "Constants.h"
#import "LocationManagerSingleton.h"

@implementation LocationHelper
static NSTimer* locationUpdateTimer;
static ClubbookManager* manager;
static int failedCheckinCount = 0;
static int maxFailedCheckin = 0;
static Place * clubCheckin;
//static CLLocationManager * locationManager;

+ (NSString*) convertDistance:(NSInteger) distance;
{
    if (distance < 1000) {
        NSNumber *disatanceDouble = [NSNumber numberWithInt:distance];
        
        return [NSString stringWithFormat: @"%@%@",[disatanceDouble stringValue], [NSString stringWithFormat:NSLocalizedString(@"meters", nil)]];
    } else {
        NSNumberFormatter *fmt = [[NSNumberFormatter alloc] init];
        [fmt setPositiveFormat:@"0.#"];
        double doubleDistance = (double)distance/1000;
        
        NSString *stringDistance = [fmt stringFromNumber:[NSNumber numberWithDouble:doubleDistance]];
        return [NSString stringWithFormat: @"%@%@",stringDistance, [NSString stringWithFormat:NSLocalizedString(@"kilometers", nil)]];
    }
}

+ (BOOL) isCheckinHere:(Place *) club
{
    if(clubCheckin == nil || club == nil) {
        return NO;
    }
   
    if ([club.id isEqualToString:clubCheckin.id]) {
        return YES;
    }
    return NO;
}

+ (void)startLocationUpdate:(Place *) club
{
    manager = [[ClubbookManager alloc] init];
    manager.communicator = [[ClubbookCommunicator alloc] init];
    manager.communicator.delegate = manager;
    manager.delegate = [self class];
    
    [self stopTimer];
    
    clubCheckin = club;
    NSTimeInterval time = 5;
	locationUpdateTimer =
    [NSTimer scheduledTimerWithTimeInterval:time
                                     target:[self class]
                                   selector:@selector(updateLocation:)
                                   userInfo:nil
                                    repeats:YES];
}


+ (void)updateLocation:(NSTimer*)theTimer {
    CLLocation *loc = [[CLLocation alloc] initWithLatitude:[clubCheckin.lat doubleValue] longitude:[clubCheckin.lon doubleValue]];
    
    //GlobalVars *globalVars=[GlobalVars getInstance];
    if(Constants.MaxCheckinRadius <  (int)[[LocationManagerSingleton sharedSingleton].locationManager.location distanceFromLocation:loc])
    {
        [self checkout];
        return;
    }
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    
    [manager updateCheckin:clubCheckin.id userId:userId];
}

+ (void)checkout {
    //checkout
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    [manager checkout:clubCheckin.id userId:userId userInfo:nil];
    
    [self stopTimer];
}

+ (void)didCheckout:(User *) user userInfo:(NSObject *)userInfo
{

}

+ (void) stopTimer{
    clubCheckin = nil;
    [locationUpdateTimer invalidate];
}

+ (void)didUpdateCheckin:(User *)user
{
    failedCheckinCount = 0;
}

+ (void)failedWithError:(NSError *)error
{
    failedCheckinCount +=1;
    if (failedCheckinCount >= maxFailedCheckin) {
        [self checkout];
    }
}

@end
