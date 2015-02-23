//
//  EventsView.m
//  Clubbook
//
//  Created by Anton Semenyuk on 2/12/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "EventsView.h"

@implementation EventsView

- (id) init {
    NSArray* subviewArray = [[NSBundle mainBundle] loadNibNamed:@"EventsView" owner:self options:nil];
    id mainView = [subviewArray objectAtIndex:0];
    
    return mainView;
}

- (id) initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
    }
    return self;
}

- (id) initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    if (self) {
    }
    return self;
}

- (void) awakeFromNib {
}

- (void) customInitType:(NSString*) eventsType userLat:(double)userLat userLon:(double)userLon accessTOken:(NSString*)accessToken {
    if (self.eventsTable) {
        self.eventsTable.infiniteDelegate = self;
        [self.eventsTable initData:userLat userLon:userLon accessToken:accessToken];
        self.eventsTable.eventTypes = eventsType;
        self.eventsTable.sortBy = @"";
        [self.noDataLabel setText:@"No Events"];
        [self.eventsTable makeInitialLoad];
    }
}

- (void)tableIsLoading {
    [self.eventsTable setHidden:YES];
    [self.activityIndicator setHidden:NO];
    [self.noDataLabel setHidden:YES];
}

- (void) tableIsEmpty {
    [self.eventsTable setHidden:YES];
    [self.activityIndicator setHidden:YES];
    [self.noDataLabel setHidden:NO];
}

- (void) tableNotEmpty {
    [self.eventsTable setHidden:NO];
    [self.activityIndicator setHidden:YES];
    [self.noDataLabel setHidden:YES];
}

- (IBAction)handleSortingChange:(id)sender {
    if (self.segmaentControl.selectedSegmentIndex == 1) {
        self.eventsTable.sortBy = @"date";
    }
    else {
        self.eventsTable.sortBy = @"";
    }
    [self.eventsTable refreshData];
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
