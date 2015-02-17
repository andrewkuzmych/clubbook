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
#import "Place.h"

@interface EventsViewController : BaseViewController <InfiniteScrollTableViewTransitionDelegate>
@property (weak, nonatomic) IBOutlet EventsView *eventsView;

@property (strong, nonatomic) Place* place;

@end
