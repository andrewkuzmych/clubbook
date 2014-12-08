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
#import "ClubUsersViewController.h"
#import "TransitionFromClubListToClub.h"
#import "FastCheckinViewController.h"
#import "SVPullToRefresh.h"
#import "SPSlideTabButton.h"

@interface MainViewController ()<UINavigationControllerDelegate>{
   // NSArray *_places;
    BOOL isInitialLoad;
    int distanceKm;
    NSString* selectedClubType;
    OBAlert * alert;
}

@property (nonatomic) NSTimer* locationUpdateTimer;
@property (nonatomic) BOOL isLoaded;
@property (nonatomic) Place* checkinPlace;
@end

@implementation MainViewController

//#pragma mark UINavigationControllerDelegate methods
//
//- (id<UIViewControllerAnimatedTransitioning>)navigationController:(UINavigationController *)navigationController
//                                  animationControllerForOperation:(UINavigationControllerOperation)operation
//                                               fromViewController:(UIViewController *)fromVC
//                                                 toViewController:(UIViewController *)toVC {
//    // Check if we're transitioning from this view controller to a DSLSecondViewController
//    if (fromVC == self && [toVC isKindOfClass:[ClubUsersViewController class]]) {
//        return [[TransitionFromClubListToClub alloc] init];
//    }
//    else {
//        return nil;
//    }
//}

-(BOOL)shouldAutorotate
{
    return YES;
}

-(NSUInteger)supportedInterfaceOrientations
{
    return UIInterfaceOrientationMaskPortrait;
}


- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.clubTable.dataSource = self;
    self.clubTable.delegate = self;
    
    // Do any additional setup after loading the view.
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(appplicationIsActive:)
                                                 name:UIApplicationDidBecomeActiveNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(applicationEnteredForeground:)
                                                 name:UIApplicationWillEnterForegroundNotification
                                               object:nil];


    self.title = NSLocalizedString(@"clubs", nil);
    alert = [[OBAlert alloc] initInViewController:self];
    
    distanceKm = 5;
    
    self.distance.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:14];
    [self.distance setText:[NSString stringWithFormat:@"%d%@", distanceKm, NSLocalizedString(@"kilometers", nil)]];
    
    [LocationManagerSingleton sharedSingleton].delegate = self;
    [[LocationManagerSingleton sharedSingleton] startLocating];
    
    self.clubTable.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
    
    NSDictionary *textAttributes = [NSDictionary dictionaryWithObjectsAndKeys:[UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:15], UITextAttributeFont, nil];
    [self.segmentControl setTitleTextAttributes:textAttributes forState:UIControlStateNormal];
    
    [self.segmentControl setTitle:[NSString stringWithFormat:NSLocalizedString(@"nearby", nil)] forSegmentAtIndex:0];
    [self.segmentControl setTitle:[NSString stringWithFormat:NSLocalizedString(@"az", nil)] forSegmentAtIndex:1];
    
    [self._manager getConfig];
    
    __weak MainViewController *weakSelf = self;
    
    // setup pull-to-refresh
    [self.clubTable addPullToRefreshWithActionHandler:^{
        [weakSelf insertRowAtTop];
    }];
    
    // setup infinite scrolling
    [self.clubTable addInfiniteScrollingWithActionHandler:^{
        [weakSelf insertRowAtBottom];
    }];
    
    //setup filter tab bar
    selectedClubType = nil;
    
    self.filterTabBar = [[SPSlideTabBar alloc] initWithFrame:CGRectMake(0, 0, self.filterTabView.frame.size.width, self.filterTabView.frame.size.height)];
    [self.filterTabBar setAutoresizingMask:UIViewAutoresizingFlexibleBottomMargin | UIViewAutoresizingFlexibleWidth];
    [self.filterTabBar setBackgroundColor:[UIColor colorWithRed:0.651 green:0 blue:0.867 alpha:1]];
    [self.filterTabBar setSeparatorStyle:SPSlideTabBarSeparatorStyleNone];
    [self.filterTabBar setBarButtonTitleColor:[UIColor colorWithRed:192/255.0f green:154/255.0f blue:234/255.0f alpha:1.0f]];
    [self.filterTabBar setSelectedButtonColor:[UIColor whiteColor]];
    [self.filterTabBar setSelectedViewColor:[UIColor whiteColor]];
    [self.filterTabBar setSlideDelegate:self];
    [self.filterTabView addSubview:self.filterTabBar];
    
    NSString* allOption = [NSString stringWithFormat:@"%@", NSLocalizedString(@"all", nil)];
    [self.filterTabBar addTabForTitle:allOption];
    [self.filterTabBar setSelectedIndex:0];
    
    //remove black line above filtertab
    UINavigationBar *navigationBar = self.navigationController.navigationBar;
    
    [navigationBar setBackgroundImage:[UIImage new]
                       forBarPosition:UIBarPositionAny
                           barMetrics:UIBarMetricsDefault];
    
    [navigationBar setShadowImage:[UIImage new]];
    
    //set view on first filter option
    [self loadAllTypeClubs];

}

- (void)appplicationIsActive:(NSNotification *)notification {
    [LocationManagerSingleton sharedSingleton].delegate = self;
    [[LocationManagerSingleton sharedSingleton] startLocating];
}

- (void)applicationEnteredForeground:(NSNotification *)notification {
    [LocationManagerSingleton sharedSingleton].delegate = self;
    [[LocationManagerSingleton sharedSingleton] startLocating];
}

- (void)didGetConfig:(Config *)config
{
    [GlobalVars getInstance].MaxCheckinRadius = config.maxCheckinRadius;
    [GlobalVars getInstance].MaxFailedCheckin = config.maxFailedCheckin;
    [GlobalVars getInstance].CheckinUpdateTime = config.checkinUpdateTime;
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    
    // Stop being the navigation controller's delegate
    if (self.navigationController.delegate == self) {
        self.navigationController.delegate = nil;
    }
    self.navigationController.navigationBar.translucent = YES;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBar.translucent = NO;
    
    // Set outself as the navigation controller's delegate so we're asked for a transitioning object
    self.navigationController.delegate = self;
    // hide back button
    [[UIBarButtonItem appearance] setBackButtonTitlePositionAdjustment:UIOffsetMake(0, -60)
                                                         forBarMetrics:UIBarMetricsDefault];
    
    self.screenName = @"Main Screen";
    //[self.navigationController setNavigationBarHidden:NO];
    
    //Pubnub staff
    PNConfiguration *myConfig = [PNConfiguration configurationForOrigin:@"pubsub.pubnub.com"  publishKey: Constants.PubnabPubKay subscribeKey:Constants.PubnabSubKay secretKey:nil];
    
    [PubNub disconnect];
    [PubNub setConfiguration:myConfig];
    
    [PubNub connectWithSuccessBlock:^(NSString *origin) {
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
 
    NSString *channal = [NSString stringWithFormat:@"checkin"];
    [PubNub subscribeOnChannel:[PNChannel channelWithName:channal shouldObservePresence:YES]];
    
   }

- (void)noLocation
{//self.isViewLoaded &&
    //if (self.view.window){
        // viewController is visible
        dispatch_async(dispatch_get_main_queue(), ^{
            [alert showAlertWithText:NSLocalizedString(@"no_location_msg", nil) titleText:NSLocalizedString(@"no_location_title", nil)];
        });
  //  }
}

- (void)yesLocation
{
    if (self.isViewLoaded && self.view.window){
        dispatch_async(dispatch_get_main_queue(), ^{
            [alert removeAlert];
        });
    }
}

- (void)insertRowAtTop {
    [self loadClubType:selectedClubType take:10 skip:0];
}

- (void)insertRowAtBottom {
    
    int countToSkip = [self.places count];
     [self loadClubType:selectedClubType take:10 skip:countToSkip];

    //[self.clubTable.infiniteScrollingView stopAnimating];
}

- (void)didReceivePlaces:(NSArray *)places andTypes:(NSArray *)types
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        
        if (isInitialLoad) {
            _places = [places mutableCopy];
            
            if (types) {
                if ([types count] != 0) {
                    for (NSString* option in types) {
                         NSString* filterOption = [NSString stringWithFormat:@"%@", NSLocalizedString(option, nil)];
                        filterOption = [option capitalizedString];

                        [self.filterTabBar addTabForTitle:filterOption];
                    }
                }
            }
        } else {
            [_places addObjectsFromArray:places];
        }
        
        self.title = [NSString stringWithFormat:@"%@", NSLocalizedString(@"clubs", nil)];
        
        self.clubTable.hidden = NO;
        
        [self.clubTable.pullToRefreshView stopAnimating];
        [self.clubTable.infiniteScrollingView stopAnimating];
        CGPoint positin = self.clubTable.contentOffset;
        [self.clubTable reloadData];
        [self.clubTable setContentOffset:positin animated:NO];

        
        
        // update user location
        double lat = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.latitude;
        double lng = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.longitude;
        
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *accessToken = [defaults objectForKey:@"accessToken"];
        
        [self._manager updateUserLocation:lat lon:lng accessToken:accessToken];
    });
}

- (void)didUpdateUserLocation:(NSString *)result
{
}

- (void)didUpdateLocation
{
    [self yesLocation];
    [self loadAllTypeClubs];
}

- (void)didFailUpdateLocation
{
    [self noLocation];
}

- (void) loadAllTypeClubs {
    [self loadClubType:@"" take:10 skip:0];
}

- (void) filterForType:(NSString*) type {
    [_places removeAllObjects];
    [self.clubTable reloadData];
    
    self.isLoaded = YES;
    double lat = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.latitude;
    double lng = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.longitude;
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];

    [self._manager retrievePlaces:lat lon:lng take:10 skip:0 distance:0 type:type accessToken:accessToken];
}

- (void)loadClubType:(NSString*) type take:(int)take skip:(int)skip
{
    isInitialLoad = NO;
    if (skip == 0) {
        isInitialLoad = YES;
        if (_places.count == 0) {
            if (self.isLoaded == YES)
            {
                return;
            }
            [self showProgress:NO title:nil];
        }

    }
    
    self.isLoaded = YES;
    double lat = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.latitude;
    double lng = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.longitude;
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    
   // if (_places.count == 0) {
   //     [self showProgress:NO title:nil];
   // }
    [self._manager retrievePlaces:lat lon:lng take:take skip:skip distance:0 type:type accessToken:accessToken];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _places.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    ClubCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell" forIndexPath:indexPath];
    Place *place = _places[indexPath.row];
    
    cell.clubNameText.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:17];
    [cell.clubNameText setText:place.title];
    
    cell.distanceLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:12];
    
    cell.userCountLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:12];
    
    cell.friendsCountLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:12];
    
    cell.userCountLabelTitle.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:10];
    
    cell.userCountLabelTitle.text = NSLocalizedString(@"checkedIn", nil);
    
    cell.friendsCountLabelTitle.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:10];
    
    cell.friendsCountLabelTitle.text = NSLocalizedString(@"friends_lower", nil);
    
    cell.closingLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:10];
    
    cell.closingValueLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:10];
    
    cell.checkinButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:15];
    
    [cell.checkinButton setMainState:NSLocalizedString(@"Checkin", nil)];
    
    int disatanceInt = (int)place.distance;
    
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
    
    UIStoryboard *mainStoryboard = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle: nil];
    ClubUsersViewController *clubController  = [mainStoryboard instantiateViewControllerWithIdentifier:@"club"];
    clubController.place = place;//place.id;
    clubController.hasBack = YES;
    self.isLoaded = NO;
    // ClubUsersViewController *clubController =  [segue ClubUsersViewController];
    [UIView beginAnimations:@"animation" context:nil];
    [UIView setAnimationDuration:0.5];
    [self.navigationController pushViewController: clubController animated:NO];
    [UIView setAnimationTransition:UIViewAnimationTransitionFlipFromLeft forView:self.navigationController.view cache:NO];
    [UIView commitAnimations];
    
    //[self performSegueWithIdentifier: @"onClub" sender: place];
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(NSString *)sender
{
    if([[segue identifier] isEqualToString:@"onClub"]){
        ClubUsersViewController *clubController =  [segue destinationViewController];
        //NSIndexPath *selectedIndexPath = [self.clubTable indexPathForSelectedRow];
        Place *place = (Place*) sender;
        clubController.place = place;//place.id;
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
 /*   int sliderValue;
    sliderValue = lroundf(self.sliderControl.value);
    [self.sliderControl setValue:sliderValue animated:YES];
    
    distanceKm = [self convertToKm:sliderValue];
    self.isLoaded = NO;
    [self loadClub];
    
    [self.distance setText:[NSString stringWithFormat:@"%d%@", distanceKm, NSLocalizedString(@"kilometers", nil)]];*/
}


- (IBAction)segmentChanged:(id)sender {
    [self sortPlaces];
}

-(void)sortPlaces{
    NSString *sortProperty;
    if([self.segmentControl selectedSegmentIndex] == 0){
        sortProperty = @"distance";
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
  
    [LocationHelper addCheckin:self.checkinPlace];
 
    [self performSegueWithIdentifier: @"onClub" sender: self.checkinPlace];
}

- (void)didCheckout:(User *) user userInfo:(NSObject *)userInfo
{
    [self hideProgress];
    CbButton* checkinButton = (CbButton *) userInfo;
    
    [checkinButton setMainState:NSLocalizedString(@"Checkin", nil)];
    
    [LocationHelper removeCheckin];
}

#pragma mark - SPSlideTabBarDelegate
- (void)barButtonClicked:(SPSlideTabButton *)button {
    [self filterForOption:button.tag];
}

-(void) filterForOption:(NSUInteger) index {
    [self.filterTabBar setSelectedIndex:index];
    if (index == 0) {
        selectedClubType = @"";
        [self filterForType:selectedClubType];
    }
    else {
        NSString* typeString = [self.filterTabBar getButtonTitleAtIndex:index];
        selectedClubType = [typeString lowercaseString];
        [self filterForType:selectedClubType];
    }
}

@end
