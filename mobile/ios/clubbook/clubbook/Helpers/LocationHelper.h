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

@property (strong, nonatomic) NSString* placeId;

+ (instancetype)sharedInstance;
- (NSString*) convertDistance:(NSInteger) distance;
- (void)addCheckin:(Place *) club;
- (void) removeCheckin;
- (BOOL) isCheckinHere:(Place *) club;
- (Place *) getCheckinClub;

@end
