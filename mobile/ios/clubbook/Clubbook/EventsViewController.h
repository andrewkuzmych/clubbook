//
//  EventsViewController.h
//  Clubbook
//
//  Created by Anton Semenyuk on 2/17/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BaseViewController.h"
#import "EventsView.h"
#import "Event.h"
#import "Place.h"
#import "DJ.h"

@interface EventsViewController : BaseViewController <InfiniteScrollTableViewTransitionDelegate>

@property (strong, nonatomic) IBOutlet UIView *mainView;
@property (strong, nonatomic) EventsView *eventsView;

@property (strong, nonatomic) Place* place;
@property (strong, nonatomic) DJ* dj;

@end
