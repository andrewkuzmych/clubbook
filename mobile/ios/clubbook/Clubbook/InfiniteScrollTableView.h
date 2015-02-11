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

@interface InfiniteScrollTableView : UITableView <ClubbookManagerDelegate>

@property (nonatomic, weak) id <InfiniteScrollTableViewDelegate> infiniteDelegate;
@property (strong, nonatomic) ClubbookManager* manager;
@property double userLat;
@property double userLon;
@property (strong, nonatomic) NSString* accessToken;

- (id)initWithFrame:(CGRect)frame userLat:(double)userLat userLon:(double)userLon accessToken:(NSString*) accessToken;
- (void) stopAnimation;
- (void) tableLoadedEmpty:(BOOL)empty;
- (void) tableIsInitialLoading;

//overload this methods
- (void) insertRowAtTop;
- (void) insertRowAtBottom;
- (void) makeInitialLoad;

@end
