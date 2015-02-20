//
//  EventDetailsViewController.m
//  Clubbook
//
//  Created by Anton Semenyuk on 2/13/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import "EventDetailsViewController.h"
#import "DateHelper.h"
#import <FacebookSDK/FacebookSDK.h>
#import <EventKit/EventKit.h>
#import "ClubProfileTabBarViewController.h"
#import "UIImageView+WebCache.h"
#import <MapKit/MapKit.h>

@interface EventDetailsViewController ()

@end

@implementation EventDetailsViewController
{
    EKEventStore *store;
    BOOL isAccessToEventStoreGranted;
}

- (void)viewDidLoad {
    [super viewDidLoad];

    [self.nameLabel setText:self.event.title];
    [self.placeNameLabel setText:self.event.locationName];
    
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"dd/MM/yyyy"];

    NSString* dateString = [formatter stringFromDate:self.event.startTime];
    [self.dateLabel setText:dateString];
    NSString* timeOnly;
    if ([[DateHelper sharedSingleton] is24hFormat]) {
        timeOnly = [[DateHelper sharedSingleton] get24hTime:self.event.startTime];
    }
    else {
        timeOnly = [[DateHelper sharedSingleton] get12hTime:self.event.startTime];
    }
    
    if ([self.event.photos count] > 0) {
        NSString* firstPhoto = [self.event.photos objectAtIndex:0];
        [self.coverPhotoView sd_setImageWithURL:[NSURL URLWithString:firstPhoto]];
    }
    
    [self.timeLabel setText:timeOnly];
    
    NSString* descText = self.event.eventDescription;
    
    [self.descriptionText setText:descText];
    
    self.edgesForExtendedLayout = UIRectEdgeNone;
    self.extendedLayoutIncludesOpaqueBars = NO;
    self.automaticallyAdjustsScrollViewInsets = NO;
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)handleShareButton {
    // Check if the Facebook app is installed and we can present the share dialog
    FBLinkShareParams *params = [[FBLinkShareParams alloc] init];
    params.link = [NSURL URLWithString:self.event.share];
    
    // If the Facebook app is installed and we can present the share dialog
    if ([FBDialogs canPresentShareDialogWithParams:params]) {
        [FBDialogs presentShareDialogWithLink:params.link
                                      handler:^(FBAppCall *call, NSDictionary *results, NSError *error) {
                                          if(error) {
                                              NSLog(@"Error publishing story: %@", error.description);
                                          } else {
                                              // Success
                                              NSLog(@"result %@", results);
                                          }
                                      }];
    } else {
        // Present the feed dialog
    }
}

- (IBAction)handleTicketsButton:(id)sender {
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:self.event.buyTickets]];
}
- (IBAction)handleLocationButton:(id)sender {
    double lat = [[self.event.location objectForKey:@"lat"] doubleValue];
    double lon = [[self.event.location objectForKey:@"lon"] doubleValue];
    
    MKPlacemark* place = [[MKPlacemark alloc] initWithCoordinate: CLLocationCoordinate2DMake(lat, lon) addressDictionary: nil];
    MKMapItem* destination = [[MKMapItem alloc] initWithPlacemark: place];
    destination.name = self.event.title;
    NSArray* items = [[NSArray alloc] initWithObjects: destination, nil];
    NSDictionary* options = [[NSDictionary alloc] initWithObjectsAndKeys:
                             MKLaunchOptionsDirectionsModeWalking,
                             MKLaunchOptionsDirectionsModeKey, nil];
    [MKMapItem openMapsWithItems: items launchOptions: options];
}

- (EKEventStore *)eventStore {
    if (!store) {
        store = [[EKEventStore alloc] init];
    }
    return store;
}

- (void)updateAuthorizationStatusToAccessEventStore {
    // 2
    EKAuthorizationStatus authorizationStatus = [EKEventStore authorizationStatusForEntityType:EKEntityTypeReminder];
    
    switch (authorizationStatus) {
            // 3
        case EKAuthorizationStatusDenied:
        case EKAuthorizationStatusRestricted: {
            isAccessToEventStoreGranted = NO;
            UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Access Denied"
                                                                message:@"This app doesn't have access to your Reminders." delegate:nil
                                                      cancelButtonTitle:@"Dismiss" otherButtonTitles:nil];
            [alertView show];
            break;
        }
            
            // 4
        case EKAuthorizationStatusAuthorized:
            isAccessToEventStoreGranted = YES;
            break;
            
            // 5
        case EKAuthorizationStatusNotDetermined: {
            [store requestAccessToEntityType:EKEntityTypeReminder
                                            completion:^(BOOL granted, NSError *error) {
                                            }];
            break;
        }
    }
}

- (IBAction)handleReminderButton:(id)sender {
    EKEventStore *eventDB = [[EKEventStore alloc] init];
    EKEvent *myEvent  = [EKEvent eventWithEventStore:eventDB];
    
    myEvent.title     = @"New Event";
    myEvent.startDate = [[NSDate alloc] init];
    myEvent.endDate   = [[NSDate alloc] init];
    myEvent.allDay = YES;
    myEvent.notes = @"Test";
    
    [myEvent setCalendar:[eventDB defaultCalendarForNewEvents]];
    
    NSError *err;
    
    [eventDB saveEvent:myEvent span:EKSpanThisEvent error:&err];
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    
    for (UITouch *t in touches) {
        CGPoint p = [t locationInView:self.view];
        UIView *v = [self.view hitTest:p withEvent:event];
        if (v == self.shareView) {
            [self handleShareButton];
        }

    }
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    float height = 60.0;
    if (indexPath.row == 0) {
        height = 280.0;
    }
    if (indexPath.row == 1) {
         CGSize maximumLabelSize = CGSizeMake(280,9999);
         
         UILabel *gettingSizeLabel = [[UILabel alloc] init];
         gettingSizeLabel.font = self.descriptionText.font;
         gettingSizeLabel.text = self.descriptionText.text;
         gettingSizeLabel.numberOfLines = self.descriptionText.numberOfLines;
         gettingSizeLabel.lineBreakMode = self.descriptionText.lineBreakMode;
         
         CGSize expectedLabelSize = [gettingSizeLabel sizeThatFits:maximumLabelSize];
         
         //adjust the label the the new height.
         CGRect newFrame = self.descriptionText.frame;
         newFrame.size.height = expectedLabelSize.height;
         self.descriptionText.frame = newFrame;
         height = newFrame.origin.y + expectedLabelSize.height + 10;
         
         [self.descriptionText sizeToFit];
     }
    
    return height;
}


/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
