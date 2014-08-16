 //
//  ViewController.m
//  SidebarDemo
//
//  Created by Simon on 28/6/13.
//  Copyright (c) 2013 Appcoda. All rights reserved.
//

#import "MainViewController.h"
#import <QuartzCore/QuartzCore.h>
#import "SWRevealViewController.h"
#import "ClubCell.h"
#import "Place.h"
#import "UIImageView+WebCache.h"
#import "ClubUsersViewController.h"
#import "GlobalVars.h"
#import "LocationHelper.h"
#import "Constants.h"
#import "ErrorViewController.h"
#import "NoLocationViewController.h"
#import "OBAlert.h"
#import "Constants.h"
#import "LocationManagerSingleton.h"
#import "GlobalVars.h"


@interface MainViewController (){
    NSArray *_places;
    int distanceKm;
    OBAlert * alert;
}

@property (nonatomic) NSTimer* locationUpdateTimer;
@property (nonatomic) BOOL isLoaded;
@property (nonatomic) Place* checkinPlace;
@end

@implementation MainViewController


- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.title = @"Clubs";
    alert = [[OBAlert alloc] initInViewController:self];
    
    distanceKm = 5;
    
    self.distance.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:14];
    [self.distance setText:[NSString stringWithFormat:@"%d%@", distanceKm, NSLocalizedString(@"kilometers", nil)]];
    
    
    [LocationManagerSingleton sharedSingleton].delegate = self;
    [[LocationManagerSingleton sharedSingleton] startLocating];
    
    self.clubTable.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
    
    NSDictionary *textAttributes = [NSDictionary dictionaryWithObjectsAndKeys:[UIFont fontWithName:@"TitilliumWeb-Regular" size:12], UITextAttributeFont, nil];
    [self.segmentControl setTitleTextAttributes:textAttributes forState:UIControlStateNormal];
    
    [self.segmentControl setTitle:[NSString stringWithFormat:NSLocalizedString(@"nearby", nil)] forSegmentAtIndex:0];
    [self.segmentControl setTitle:[NSString stringWithFormat:NSLocalizedString(@"az", nil)] forSegmentAtIndex:1];
    
    [self._manager getConfig];
}

- (void)didGetConfig:(Config *)config
{
    [GlobalVars getInstance].MaxCheckinRadius = config.maxCheckinRadius;
    [GlobalVars getInstance].MaxFailedCheckin = config.maxFailedCheckin;
    [GlobalVars getInstance].CheckinUpdateTime = config.checkinUpdateTime;
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}

- (void)viewWillAppear:(BOOL)animated
{
    [self loadClub];
    [self.navigationController setNavigationBarHidden:NO];
    
    //Pubnub staff
    PNConfiguration *myConfig = [PNConfiguration configurationForOrigin:@"pubsub.pubnub.com"  publishKey: Constants.PubnabPubKay subscribeKey:Constants.PubnabSubKay secretKey:nil];
    
    [PubNub disconnect];
    [PubNub setConfiguration:myConfig];
    
    [PubNub connectWithSuccessBlock:^(NSString *origin) {
        PNLog(PNLogGeneralLevel, self, @"{BLOCK} PubNub client connected to: %@", origin);
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *userId = [defaults objectForKey:@"userId"];
        NSString *channal = [NSString stringWithFormat:@"%message_%@", userId];
        [PubNub subscribeOnChannel:[PNChannel channelWithName:channal shouldObservePresence:YES]];
    }
     
                   errorBlock:^(PNError *connectionError) {
                       if (connectionError.code == kPNClientConnectionFailedOnInternetFailureError) {
                             PNLog(PNLogGeneralLevel, self, @"Connection will be established as soon as internet connection will be restored");
                        }
                             
                   }];
}

- (void)noLocation
{
    if (self.isViewLoaded && self.view.window){
        // viewController is visible
        dispatch_async(dispatch_get_main_queue(), ^{
            [alert showAlertWithText:NSLocalizedString(@"no_location_msg", nil) titleText:NSLocalizedString(@"no_location_title", nil)];
        });
    }
}

- (void)yesLocation
{
    if (self.isViewLoaded && self.view.window){
        dispatch_async(dispatch_get_main_queue(), ^{
            [alert removeAlert];
        });
    }
}


- (void)didReceivePlaces:(NSArray *)places
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        _places = places;
        [self sortPlaces];
        
        for (Place *place in places) {
            CLLocation *loc = [[CLLocation alloc] initWithLatitude:[place.lat doubleValue] longitude:[place.lon doubleValue]];
            CLLocationDistance distance = [[LocationManagerSingleton sharedSingleton].locationManager.location distanceFromLocation:loc];
            place.disatance = distance;
        }
        
       
        self.clubTable.hidden = NO;
        self.clubTable.dataSource = self;
        self.clubTable.delegate = self;
        [self.clubTable reloadData];
    });
}

- (void)didUpdateLocation
{
    [self yesLocation];
    [self loadClub];
}

- (void)didFailUpdateLocation
{
    [self noLocation];
}

- (void)loadClub
{
    if (self.isLoaded || [LocationManagerSingleton sharedSingleton].locationManager.location == nil) {
        return;
    }
    self.isLoaded = YES;
    double lat = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.latitude;
    double lng = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.longitude;
        
    [self showProgress:NO title:nil];
    [self._manager retrievePlaces:distanceKm lat:lat lon:lng];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _places.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    ClubCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell" forIndexPath:indexPath];
    Place *place = _places[indexPath.row];
    
    cell.clubNameText.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:17];
    [cell.clubNameText setText:place.title];
    
    cell.distanceLabel.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:12];
    
    cell.userCountLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:12];
    
    cell.friendsCountLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:12];
    
    cell.userCountLabelTitle.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:12];
    
    cell.userCountLabelTitle.text = NSLocalizedString(@"checkedIn", nil);
    
    cell.friendsCountLabelTitle.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:12];
    
    cell.friendsCountLabelTitle.text = NSLocalizedString(@"friends_lower", nil);
    
    cell.closingLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:10];
    
    cell.closingValueLabel.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:10];
    
    cell.checkinButton.titleLabel.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:15];
    
    //[cell.checkinButton setBackgroundColor:[UIColor colorWithRed:92/255.0 green:142/255.0 blue:95/255.0 alpha:1.0] forState:UIControlStateNormal];
    
    //[cell.checkinButton setBackgroundColor:[UIColor colorWithRed:115/255.0 green:178/255.0 blue:119/255.0 alpha:1.0] forState:UIControlStateHighlighted];
    
    //[cell.checkinButton setBackgroundColor:<#(UIColor *)#>]
    
    [cell.checkinButton setMainState:NSLocalizedString(@"Checkin", nil)];
    
    int disatanceInt = (int)place.disatance;
    
    [cell.distanceLabel setText:[LocationHelper convertDistance:disatanceInt]];
    
    [cell.userCountLabel setText: [NSString stringWithFormat:@"%d", place.countOfUsers]];
    
    [cell.friendsCountLabel setText: [NSString stringWithFormat:@"%d", place.friendsCount]];
    
    [cell.clubAvatar setImageWithURL:[NSURL URLWithString:place.avatar] placeholderImage:[UIImage imageNamed:@"avatar_default.png"]];
    
    BOOL isCheckinHere = [LocationHelper isCheckinHere:place];
    if(isCheckinHere){
        [cell.checkinButton setSecondState:NSLocalizedString(@"Checkout", nil)];
    } else {
        [cell.checkinButton setMainState:NSLocalizedString(@"Checkin", nil)];
    }
    
    [cell.checkinButton setTag:indexPath.row];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSIndexPath *selectedIndexPath = [self.clubTable indexPathForSelectedRow];
    Place *place = _places[selectedIndexPath.row];
    
    [self performSegueWithIdentifier: @"onClub" sender: place];
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(NSString *)sender
{
    if([[segue identifier] isEqualToString:@"onClub"]){
        ClubUsersViewController *clubController =  [segue destinationViewController];
        //NSIndexPath *selectedIndexPath = [self.clubTable indexPathForSelectedRow];
        Place *place = (Place*) sender;
        clubController.placeId = place.id;
        clubController.hasBack = YES;
        self.isLoaded = NO;
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)SliderChanged:(id)sender {

    int sliderValue;
    sliderValue = lroundf(self.sliderControl.value);
    distanceKm = [self convertToKm:sliderValue];
    [self.distance setText:[NSString stringWithFormat:@"%d%@", distanceKm, NSLocalizedString(@"kilometers", nil)]];
}

- (int)convertToKm:(int)sliderValue
{
    int km = 1;
    switch(sliderValue) {
        case 1:
            km = 1;
            break;
        case 2:
            km = 2;
            break;
        case 3:
            km = 3;
            break;
        case 4:
            km = 4;
            break;
        case 5:
            km = 5;
            break;
        case 6:
            km = 10;
            break;
        case 7:
            km = 20 ;
            break;
        case 8:
            km = 30;
            break;
        case 9:
            km = 50;
            break;
        case 10:
            km = 100;
            break;
            
        default:
            break;
    }
    return km;
}

- (IBAction)sliderTouchUp:(id)sender
{
    int sliderValue;
    sliderValue = lroundf(self.sliderControl.value);
    [self.sliderControl setValue:sliderValue animated:YES];
    
    distanceKm = [self convertToKm:sliderValue];
    self.isLoaded = NO;
    [self loadClub];
    
    [self.distance setText:[NSString stringWithFormat:@"%d%@", distanceKm, NSLocalizedString(@"kilometers", nil)]];
}

- (IBAction)checkinAction:(UIButton *)sender
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    
    self.checkinPlace = _places[sender.tag];
    CbButton* checkinButton = (CbButton *) sender;

    CLLocation *loc = [[CLLocation alloc] initWithLatitude:[self.checkinPlace.lat doubleValue] longitude:[self.checkinPlace.lon doubleValue]];
    
    GlobalVars *globalVars=[GlobalVars getInstance];
    if (checkinButton.isCheckin) {
         [self._manager checkout:self.checkinPlace.id userId:userId userInfo:sender];
    } else {
        if([GlobalVars getInstance].MaxCheckinRadius > (int)[[LocationManagerSingleton sharedSingleton].locationManager.location distanceFromLocation:loc]) {
            [self showProgress:NO title: NSLocalizedString(@"checking_in", nil)];
            [self._manager checkin:self.checkinPlace.id userId:userId userInfo:sender];
            
        }
        else {
            [self showProgress:NO title:NSLocalizedString(@"checking_out", nil)];
            [checkinButton setMainState:NSLocalizedString(@"Checkin", nil)];
        }
    }
}

- (IBAction)segmentChanged:(id)sender {
    [self sortPlaces];
}

-(void)sortPlaces{
    NSString *sortProperty;
    if([self.segmentControl selectedSegmentIndex] == 0){
        sortProperty = @"disatance";
    }
    else if([self.segmentControl selectedSegmentIndex] == 1){
        sortProperty = @"title";
    }
    
    NSSortDescriptor *sortDescriptor;
    sortDescriptor = [[NSSortDescriptor alloc] initWithKey:sortProperty
                                                     ascending:YES];
    NSArray *sortDescriptors = [NSArray arrayWithObject:sortDescriptor];
    _places = [_places sortedArrayUsingDescriptors:sortDescriptors];
    [self.clubTable reloadData];

}

- (void)didCheckin:(User *) user userInfo:(NSObject *)userInfo
{
    [self hideProgress];
    CbButton* checkinButton = (CbButton *) userInfo;
    
    [checkinButton setSecondState:NSLocalizedString(@"Checkout", nil)];
  
    [LocationHelper startLocationUpdate:self.checkinPlace];
 
    [self performSegueWithIdentifier: @"onClub" sender: self.checkinPlace];
}

- (void)didCheckout:(User *) user userInfo:(NSObject *)userInfo
{
    [self hideProgress];
    CbButton* checkinButton = (CbButton *) userInfo;
    
    [checkinButton setMainState:NSLocalizedString(@"Checkin", nil)];
    
    [LocationHelper stopTimer];
}

@end
