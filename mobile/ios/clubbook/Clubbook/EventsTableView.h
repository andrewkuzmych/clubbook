//
//  EventsTableView.h
//  Clubbook
//
//  Created by Anton Semenyuk on 2/12/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "InfiniteScrollTableView.h"

@interface EventsTableView : InfiniteScrollTableView

@property BOOL singlePlaceEvents;
@property (strong, nonatomic) NSString* placeId;
@property (strong, nonatomic) NSString* placeType;
@property (strong, nonatomic) NSString* eventTypes;

@property (strong, nonatomic) NSString* sortBy;

- (void) refreshData;
- (void) loadMoreData;

@end
