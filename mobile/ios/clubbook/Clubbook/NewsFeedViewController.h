//
//  NewsFeedTableViewController.h
//  Clubbook
//
//  Created by Anton Semenyuk on 1/8/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "BaseViewController.h"
#import "NewsView.h"


@interface NewsFeedViewController : BaseViewController <InfiniteScrollTableViewTransitionDelegate>
@property (strong, nonatomic) IBOutlet UIView *mainView;

@property (strong, nonatomic) NewsView *newsView;

@property (strong, nonatomic) NSString* type;
@property (strong, nonatomic) NSString* newsObjectId;

@end
