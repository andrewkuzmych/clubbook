//
//  InfiniteScrollTableView.m
//  Clubbook
//
//  Created by Anton Semenyuk on 2/11/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "InfiniteScrollTableView.h"
#import "SVPullToRefresh.h"
#import "ClubbookCommunicator.h"

@implementation InfiniteScrollTableView


- (id)initWithFrame:(CGRect)frame userLat:(double)userLat userLon:(double)userLon accessToken:(NSString *)accessToken{
    self = [super initWithFrame:frame];
    
    if (self) {
        self.manager = [[ClubbookManager alloc] init];
        self.manager.communicator = [[ClubbookCommunicator alloc] init];
        self.manager.communicator.delegate = self.manager;
        self.manager.delegate = self;
                                 
        self.userLat = userLat;
        self.userLon = userLon;
        self.accessToken = accessToken;
        
        __weak InfiniteScrollTableView *weakSelf = self;
        
        // setup pull-to-refresh
         [self addPullToRefreshWithActionHandler:^{
         [weakSelf insertRowAtTop];
         }];
         
         // setup infinite scrolling
         [self addInfiniteScrollingWithActionHandler:^{
         [weakSelf insertRowAtBottom];
         }];
        
        self.dataArray = [[NSMutableArray alloc] init];
        self.delegate = self;
        self.dataSource = self;
        
    }
    return self;
}

- (void) stopAnimation {
    [self.pullToRefreshView stopAnimating];
    [self.infiniteScrollingView stopAnimating];
}

- (void) tableIsInitialLoading {
    if([[self infiniteDelegate] respondsToSelector:@selector(tableIsLoading)]) {
        [self.infiniteDelegate tableIsLoading];
    }
}

- (void) updateTableWithData:(NSArray*) data {
    if (self.isRefreshing) {
        self.dataArray = [data mutableCopy];
        self.isRefreshing = NO;
    }
    else {
        [self.dataArray addObjectsFromArray:data];
    }
    
    BOOL loadedEmpty = [self.dataArray count] <= 0;
    
    if (!loadedEmpty) {
        [self setHidden:NO];
    }
    [self tableLoadedEmpty:loadedEmpty];
    
    [self stopAnimation];
    [self reloadData];
}

- (void) insertRowAtTop {
    [self refreshData];
}

- (void) insertRowAtBottom {
    self.isRefreshing = NO;
    [self loadMoreData];
}

- (void) transitToController:(UIViewController *)controller {
    if([[self transitionDelegate] respondsToSelector:@selector(transitToNewController:)]) {
        [self.transitionDelegate transitToNewController:controller];
    }
}

- (void) makeInitialLoad {
    [self tableIsInitialLoading];
    [self setHidden:YES];
    [self refreshData];
}

- (void) loadMoreData {
}

- (void) refreshData {
    self.isRefreshing = YES;
}

- (void) tableLoadedEmpty:(BOOL)empty {
    if (empty) {
        if([[self infiniteDelegate] respondsToSelector:@selector(tableIsEmpty)]) {
            [self.infiniteDelegate tableIsEmpty]; }
    }
    else {
        if([[self infiniteDelegate] respondsToSelector:@selector(tableNotEmpty)]) {
            [self.infiniteDelegate tableNotEmpty]; }
    }
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.dataArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell* cell = [[UITableViewCell alloc] init];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 75.0f;
}


/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
