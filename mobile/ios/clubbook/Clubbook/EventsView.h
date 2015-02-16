//
//  EventsView.h
//  Clubbook
//
//  Created by Anton Semenyuk on 2/12/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "EventsTableView.h"

@interface EventsView : UIView <InfiniteScrollTableViewDelegate>
@property (weak, nonatomic) IBOutlet UISegmentedControl *segmaentControl;
@property (weak, nonatomic) IBOutlet EventsTableView *eventsTable;
@property (weak, nonatomic) IBOutlet UILabel *activityIndicator;
@property (weak, nonatomic) IBOutlet UILabel *noDataLabel;

- (void) customInit:(double)userLat userLon:(double)userLon accessTOken:(NSString*)accessToken;
@end
