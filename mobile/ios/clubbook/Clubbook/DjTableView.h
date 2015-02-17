//
//  DjTableView.h
//  Clubbook
//
//  Created by Anton Semenyuk on 2/17/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "InfiniteScrollTableView.h"

@interface DjTableView : InfiniteScrollTableView

- (id)initWithFrame:(CGRect)frame userLat:(double)userLat userLon:(double)userLon accessToken:(NSString *)accessToken;

- (void) refreshData;
- (void) loadMoreData;

@end
