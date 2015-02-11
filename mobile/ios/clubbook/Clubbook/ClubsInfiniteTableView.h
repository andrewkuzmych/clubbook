//
//  ClubsInfiniteTableView.h
//  Clubbook
//
//  Created by Anton Semenyuk on 2/11/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "InfiniteScrollTableView.h"

@interface ClubsInfiniteTableView : InfiniteScrollTableView <UITableViewDelegate, UITableViewDataSource>

@property (strong, nonatomic) NSMutableArray *places;

- (id)initWithFrame:(CGRect)frame userLat:(double)userLat userLon:(double)userLon accessToken:(NSString *)accessToken;

- (void) insertRowAtTop;
- (void) insertRowAtBottom;
- (void) makeInitialLoad;

@end
