//
//  InfiniteScrollTableView.h
//  Clubbook
//
//  Created by Anton Semenyuk on 2/11/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ClubbookManager.h"

@protocol InfiniteScrollTableViewDelegate <NSObject>
- (void) tableIsLoading;
- (void) tableIsEmpty;
- (void) tableNotEmpty;
@end //end protocol

@interface InfiniteScrollTableView : UITableView <ClubbookManagerDelegate, UITableViewDelegate, UITableViewDataSource>

@property (nonatomic, weak) id <InfiniteScrollTableViewDelegate> infiniteDelegate;
@property (strong, nonatomic) ClubbookManager* manager;
@property double userLat;
@property double userLon;
@property (strong, nonatomic) NSString* accessToken;
@property BOOL isRefreshing;

@property (strong, nonatomic) NSMutableArray *dataArray;

- (id)initWithFrame:(CGRect)frame userLat:(double)userLat userLon:(double)userLon accessToken:(NSString*) accessToken;
- (void) stopAnimation;
- (void) tableLoadedEmpty:(BOOL)empty;
- (void) tableIsInitialLoading;
- (void) updateTableWithData:(NSArray*) data;
- (void) insertRowAtTop;
- (void) insertRowAtBottom;

//overload this methods
- (void) makeInitialLoad;
- (void) refreshData;
- (void) loadMoreData;

@end
