//
//  NewsView.h
//  Clubbook
//
//  Created by Anton Semenyuk on 2/19/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NewsFeedTableView.h"

@interface NewsView : UIView <InfiniteScrollTableViewDelegate>
@property (weak, nonatomic) IBOutlet NewsFeedTableView *newsTable;
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *activityIndicator;
@property (weak, nonatomic) IBOutlet UILabel *noDataLabel;

- (void) customInitType:(double) userLat userLon:(double)userLon accessToken:(NSString*) accessToken type:(NSString*) type objectId:(NSString*) objectId andParentViewCntroller:(UIViewController*) parent;
@end
