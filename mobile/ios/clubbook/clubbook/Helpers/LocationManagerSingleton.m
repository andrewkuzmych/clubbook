//
//  LocationManagerSingleton.m
//  Clubbook
//
//  Created by Andrew on 7/16/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "LocationManagerSingleton.h"
#import "LocationManagerProtocol.h"

@implementation LocationManagerSingleton

@synthesize locationManager;

- (id)init {
    self = [super init];
    
    if(self) {
        self.locationManager = [CLLocationManager new];
        [self.locationManager setDelegate:self];
        [self.locationManager setDistanceFilter:200];
        [self.locationManager setHeadingFilter:kCLHeadingFilterNone];
        if ([self.locationManager respondsToSelector:@selector(requestWhenInUseAuthorization)]){
            [self.locationManager requestWhenInUseAuthorization];
        }
        //[self.locationManager startUpdatingLocation];
        //do any more customization to your location manager
    }
    
    return self;
}

-(void) startLocating;
{
    if ([CLLocationManager locationServicesEnabled]) {
        [self.locationManager stopUpdatingLocation];
        [self.locationManager startUpdatingLocation];
    } else {
        [self.delegate didFailUpdateLocation];
    }
}

-(void) endLocating
{
    [self.locationManager stopUpdatingLocation];
}

+ (LocationManagerSingleton*)sharedSingleton {
    static LocationManagerSingleton* sharedSingleton;
    if(!sharedSingleton) {
        @synchronized(sharedSingleton) {
            sharedSingleton = [LocationManagerSingleton new];
        }
    }
    
    return sharedSingleton;
}

- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations
{
    [self.delegate didUpdateLocation];
}

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error
{
    
    switch([error code])
    {
        case kCLErrorNetwork: // general, network-related error
        {
            [self.delegate didFailUpdateLocation];
        }
            break;
        case kCLErrorDenied:{
            [self.delegate didFailUpdateLocation];
        }
            break;
        case kCLErrorHeadingFailure:{
            //UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"user has denied to use current Location " delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil, nil];
            //[alert show];
        }
            break;
        case kCLErrorLocationUnknown:{
            //UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"user has denied to use current Location " delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil, nil];
            //[alert show];
        }
            break;
        default:
        {
            //UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"unknown network error" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil, nil];
            //[alert show];
        }
            break;
    }
}
@end