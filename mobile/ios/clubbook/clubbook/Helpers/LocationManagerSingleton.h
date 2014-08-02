//
//  LocationManagerSingleton.h
//  Clubbook
//
//  Created by Andrew on 7/16/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <MapKit/MapKit.h>
#import "LocationManagerProtocol.h"

@interface LocationManagerSingleton : NSObject <CLLocationManagerDelegate>

@property (nonatomic, strong) CLLocationManager* locationManager;
@property (weak, nonatomic) id<LocationManagerProtocol> delegate;

+ (LocationManagerSingleton*)sharedSingleton;

-(void) startLocating;
-(void) endLocating;

@end
