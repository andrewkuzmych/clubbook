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

- (void) insertRowAtTop {
}

- (void) insertRowAtBottom {
}

- (void) makeInitialLoad {
    [self tableIsInitialLoading];
    [self setHidden:YES];
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

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
