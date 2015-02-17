//
//  EventsTableView.m
//  Clubbook
//
//  Created by Anton Semenyuk on 2/12/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "EventsTableView.h"
#import "EventsCell.h"
#import "Event.h"
#import "LocationHelper.h"
#import "EventDetailsViewController.h"

@implementation EventsTableView

- (id)initWithFrame:(CGRect)frame type:(NSString*) type userLat:(double)userLat userLon:(double)userLon accessToken:(NSString *)accessToken{
    self = [super initWithFrame:frame userLat:userLat userLon:userLon accessToken:accessToken];
    if (self) {
        UINib *nib = [UINib nibWithNibName:@"EventCell" bundle:nil];
        [self registerNib:nib forCellReuseIdentifier:@"EventCell"];
    }
    
    return self;
}

- (void) refreshData {
    [super refreshData];
    [self loadEventsTake:10 skip:0];
}

- (void) loadMoreData {
    [super loadMoreData];
    int countToSkip = (int)[self.dataArray count];
    [self loadEventsTake:10 skip:countToSkip];
}

- (void) initData:(double)userLat userLon:(double)userLon accessToken:(NSString *)accessToken {
    [super initData:userLat userLon:userLon accessToken:accessToken];
    UINib *nib = [UINib nibWithNibName:@"EventsCell" bundle:nil];
    [self registerNib:nib forCellReuseIdentifier:@"EventsCell"];
}

- (void) searchForWord:(NSString*) searchWord {
    [super searchForWord:searchWord];
    [self.manager retrieveEvents:self.userLat lon:self.userLon take:10 skip:0 distance:0 search:searchWord accessToken:self.accessToken];
}

- (void)loadEventsTake:(int)take skip:(int)skip
{
    if (!self.singlePlaceEvents) {
       [self.manager retrieveEvents:self.userLat lon:self.userLon take:take skip:skip distance:0 search:@"" accessToken:self.accessToken];
    }
    else {
       [self.manager retrieveEventsById:self.placeId type:self.type accessToken:self.accessToken];
    }
}

- (void)didReceiveEvents:(NSArray *)events
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self updateTableWithData:events];
    });
}

- (NSString*) formatTimeToEventLabelText:(Event*) event {
    NSString* timeToEvent = @"";
    
    //check in event already started
    NSTimeInterval daysNow = [[NSDate date] timeIntervalSinceReferenceDate];
    NSTimeInterval daysStart = [event.startTime timeIntervalSinceReferenceDate];
    NSTimeInterval daysEnd = [event.endTime timeIntervalSinceReferenceDate];
    
    if (daysNow >= daysStart && daysNow <= daysEnd) {
        return @"Started";
    }
    else {
        NSDateComponentsFormatter *formatter = [[NSDateComponentsFormatter alloc] init];
        formatter.allowedUnits = NSCalendarUnitMonth | NSCalendarUnitDay | NSCalendarUnitHour | NSCalendarUnitMinute;
        formatter.unitsStyle = NSDateComponentsFormatterUnitsStyleShort;
        NSString* string = [formatter stringFromDate:[NSDate date] toDate:event.endTime];
        return string;
    }
    
    return timeToEvent;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    EventsCell *cell = (EventsCell*)[self dequeueReusableCellWithIdentifier:@"EventsCell" forIndexPath:indexPath];
    
    Event* event = [self.dataArray objectAtIndex:indexPath.row];
    
    [cell.nameLabel setText:event.title];
    
    NSString* placeString = [NSString stringWithFormat:@"@%@", event.locationName, nil];
    [cell.placeLabel setText:placeString];
    
    int disatanceInt = (int)event.distance;
    [cell.distanceLabel setText:[LocationHelper convertDistance:disatanceInt]];
    
    NSString* statusString = [self formatTimeToEventLabelText:event];
    if ([statusString isEqualToString:@"Started"]) {
        [cell.statusLabel setText:statusString];
    }
    else {
        [cell.statusLabel setTextColor:[UIColor grayColor]];
        [cell.statusLabel setFont:[UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:12.0]];
        [cell.statusLabel setText:statusString];
    }
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSIndexPath *selectedIndexPath = [self indexPathForSelectedRow];
    Event *event = self.dataArray[selectedIndexPath.row];
    
    UIStoryboard *eventStoryboard = [UIStoryboard storyboardWithName:@"EventsStoryboard" bundle: nil];
    EventDetailsViewController *eventController  = [eventStoryboard instantiateInitialViewController];
    eventController.event = event;
    
    [self transitToController:eventController];
    
    [self deselectRowAtIndexPath:indexPath animated:NO];
}


@end
