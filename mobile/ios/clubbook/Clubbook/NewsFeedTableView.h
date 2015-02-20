//
//  NewsFeedTableView.h
//  Clubbook
//
//  Created by Anton Semenyuk on 1/27/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "EBPhotoPagesController.h"
#import "InfiniteScrollTableView.h"
#import <UIKit/UIKit.h>

@interface NewsFeedTableView : InfiniteScrollTableView <UICollectionViewDataSource, UICollectionViewDelegate, UITableViewDelegate, UITableViewDataSource, EBPhotoPagesDelegate, EBPhotoPagesDataSource, ClubbookManagerDelegate>

@property (strong, nonatomic) NSString* newsObjectId;
@property (strong, nonatomic) NSString* type;
@property (weak, nonatomic) UIViewController* parentViewController;

- (void) initializeNewsTableType:(double) userLat userLon:(double)userLon accessToken:(NSString*) accessToken type:(NSString*) type objectId:(NSString*) objectId andParentViewCntroller:(UIViewController*) parent;

@end
