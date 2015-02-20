//
//  UserNewsFeedViewController.h
//  Clubbook
//
//  Created by Anton Semenyuk on 2/20/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NewsView.h"
#import "BaseViewController.h"

@interface UserNewsFeedViewController : BaseViewController <InfiniteScrollTableViewTransitionDelegate>
@property (weak, nonatomic) IBOutlet UISegmentedControl *segmentControl;
@property (weak, nonatomic) IBOutlet UIView *mainView;

@property (strong, nonatomic) NewsView *newsView;

@end
