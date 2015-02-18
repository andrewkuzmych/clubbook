//
//  Event.h
//  Clubbook
//
//  Created by Anton Semenyuk on 2/12/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Place.h"
#import "DJ.h"

@interface Event : NSObject

@property (strong, nonatomic) NSDate* startTime;
@property (strong, nonatomic) NSDate* endTime;
@property (strong, nonatomic) NSString* title;
@property (strong, nonatomic) NSString* share;
@property (strong, nonatomic) NSString* buyTickets;
@property (strong, nonatomic) NSString* eventDescription;
@property (strong, nonatomic) NSArray* photos;
@property (strong, nonatomic) NSString* locationName;
@property (strong, nonatomic) NSMutableDictionary* location;
@property (strong, nonatomic) NSString* address;
@property (strong, nonatomic) Place* place;
@property (strong, nonatomic) DJ* dj;
@property double distance;

@end
