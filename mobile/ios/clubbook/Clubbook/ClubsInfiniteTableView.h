//
//  ClubsInfiniteTableView.h
//  Clubbook
//
//  Created by Anton Semenyuk on 2/11/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "InfiniteScrollTableView.h"

@interface ClubsInfiniteTableView : InfiniteScrollTableView

@property (strong, nonatomic)NSString* type;

- (id)initWithFrame:(CGRect)frame type:(NSString*) type userLat:(double)userLat userLon:(double)userLon accessToken:(NSString *)accessToken;

- (void) refreshData;
- (void) loadMoreData;

@end
