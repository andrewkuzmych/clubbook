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

- (void) customInit:(double)userLat userLon:(double)userLon accessTOken:(NSString*)accessToken {
    if (self.eventsTable) {
        self.eventsTable.infiniteDelegate = self;
        [self.eventsTable initData:userLat userLon:userLon accessToken:accessToken];
        [self.eventsTable makeInitialLoad];
    }
}

- (void)tableIsLoading {
    [self.activityIndicator setHidden:NO];
    [self.noDataLabel setHidden:YES];
}

- (void) tableIsEmpty {
    [self.activityIndicator setHidden:YES];
    [self.noDataLabel setHidden:NO];
}

- (void) tableNotEmpty {
    [self.activityIndicator setHidden:YES];
    [self.noDataLabel setHidden:YES];
}


/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
