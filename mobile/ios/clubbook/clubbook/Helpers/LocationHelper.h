//
//  LocationHelper.h
//  Clubbook
//
//  Created by Andrew on 7/1/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ClubbookManager.h"
#import "ClubbookCommunicator.h"

@interface LocationHelper : NSObject <ClubbookManagerDelegate>

+ (NSString*) convertDistance:(NSInteger) distance;
+ (void)startLocationUpdate:(Place *) club;
+ (void) stopTimer;
+ (BOOL) isCheckinHere:(Place *) club;

@end
