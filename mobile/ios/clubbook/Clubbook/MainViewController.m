//
//  Created by Clubbok.
//

#import <QuartzCore/QuartzCore.h>

#import "MainViewController.h"
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
#import "Constants.h"
#import "LocationManagerSingleton.h"
#import "GlobalVars.h"
#import "ClubUsersViewController.h"
#import "TransitionFromClubListToClub.h"
#import "FastCheckinViewController.h"
#import "SVPullToRefresh.h"
#import "SPSlideTabButton.h"

@interface MainViewController ()<UINavigationControllerDelegate, UINavigationBarDelegate>{
    BOOL isInitialLoad;
    NSString* selectedClubType;
    
    BOOL isSearchBarShown;
    
    CGFloat lastContentOffset;
    UIView* blankView;
}

@property (nonatomic) NSTimer* locationUpdateTimer;
@property (nonatomic) BOOL isLoaded;
@property (nonatomic) Place* checkinPlace;
@end

@implementation MainViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.clubTable.dataSource = self;
    self.clubTable.delegate = self;
    
    self.title = NSLocalizedString(@"Going Out", nil);
    self.clubTable.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
    
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
    
    //set up search field
    self.searchBar.barTintColor = self.filterTabBar.backgroundColor;
    
    //remove black line under searchbox
    self.searchBar.layer.borderWidth = 2;
    self.searchBar.layer.borderColor = [[UIColor colorWithRed:0.651 green:0 blue:0.867 alpha:1] CGColor];
    
    //set placeholder text
    self.searchBar.placeholder = [NSString stringWithFormat:@"%@", NSLocalizedString(@"Search clubs, bars, events, etc. by name", nil)];
    [self changeSearchKeyboardButtonTitle];
    
    //hide searchbar
    [self replaceTopConstraintOnView:self.searchBar withConstant: -self.searchBar.frame.size.height];
    isSearchBarShown = NO;
    self.searchBar.delegate = self;
    
    //set view on first filter option
    selectedClubType = @"";
    [self loadAllTypeClubs];
    
    [self.view setBackgroundColor:self.filterTabBar.backgroundColor];
}

-(BOOL)shouldAutorotate {
    return YES;
}

-(NSUInteger)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskPortrait;
}

- (void)didGetConfig:(Config *)config {
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
    self.navigationController.navigationBar.translucent = NO;
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

- (void)insertRowAtTop {
    [self loadClubType:selectedClubType take:10 skip:0];
}

- (void)insertRowAtBottom {
    int countToSkip = (int)[self.places count];
    [self loadClubType:selectedClubType take:10 skip:countToSkip];
}

- (void)didReceivePlaces:(NSArray *)places andTypes:(NSArray *)types
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        [self.activityIndicator setHidden:YES];
        
        if (isInitialLoad) {
            _places = [places mutableCopy];
            
            if (types) {
                if ([types count] != 0) {
                    for (NSString* option in types) {
                        if ([option isKindOfClass:[NSString class]]) {
                            NSString* filterOption = [NSString stringWithFormat:@"%@", NSLocalizedString(option, nil)];
                            filterOption = [option capitalizedString];
                            
                            [self.filterTabBar addTabForTitle:filterOption];
                        }
                    }
                }
            }
        } else {
            [_places addObjectsFromArray:places];
        }
        
        self.title = [NSString stringWithFormat:@"%@", NSLocalizedString(@"Going Out", nil)];
        
        self.clubTable.hidden = NO;
        if ([_places count] > 0) {
            [self.noResultsLabel setHidden:YES];
        }
        else {
            [self.noResultsLabel setHidden:NO];
        }

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

- (void) loadAllTypeClubs {
    [self loadClubType:@"" take:10 skip:0];
}

- (void) tableWillBeRefreshed {
    [_places removeAllObjects];
    [self.clubTable reloadData];
    [self.activityIndicator setHidden:NO];
    [self.noResultsLabel setHidden:YES];
}

- (void) filterForType:(NSString*) type {
    [self tableWillBeRefreshed];
    
    self.isLoaded = YES;
    double lat = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.latitude;
    double lng = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.longitude;
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];

    [self._manager retrievePlaces:lat lon:lng take:10 skip:0 distance:0 type:type search:@"" accessToken:accessToken];
}

- (void) searchForWord:(NSString*) searchWord {
    [self tableWillBeRefreshed];
    
    self.isLoaded = YES;
    double lat = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.latitude;
    double lng = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.longitude;
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    
    [self._manager retrievePlaces:lat lon:lng take:10 skip:0 distance:0 type:@"" search:searchWord accessToken:accessToken];
}

- (void)loadClubType:(NSString*) type take:(int)take skip:(int)skip
{
    isInitialLoad = NO;
    if (skip == 0) {
        isInitialLoad = YES;
        if (_places.count == 0) {
            if (self.isLoaded == YES)
            {
                [self.clubTable.pullToRefreshView stopAnimating];
                [self.clubTable.infiniteScrollingView stopAnimating];
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
    
    [self._manager retrievePlaces:lat lon:lng take:take skip:skip distance:0 type:type search:@"" accessToken:accessToken];
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
    
    [cell.clubAvatar sd_setImageWithURL:[NSURL URLWithString:place.avatar] placeholderImage:[UIImage imageNamed:@"avatar_default.png"]];
    
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

    [UIView beginAnimations:@"animation" context:nil];
    [UIView setAnimationDuration:0.5];
    [self.navigationController pushViewController: clubController animated:NO];
    [UIView setAnimationTransition:UIViewAnimationTransitionFlipFromLeft forView:self.navigationController.view cache:NO];
    [UIView commitAnimations];
    [self.clubTable deselectRowAtIndexPath:indexPath animated:NO];
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(NSString *)sender
{
    if([[segue identifier] isEqualToString:@"onClub"]){
        ClubUsersViewController *clubController =  [segue destinationViewController];
        Place *place = (Place*) sender;
        clubController.place = place;
        clubController.hasBack = YES;
        self.isLoaded = NO;
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
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

//search logic
- (IBAction)handleSearchButton:(id)sender {
    if(!isSearchBarShown) {
        isSearchBarShown = YES;
        [self searchTextFieldEnabled:YES];
        [self.filterTabBar setEnabled:NO];
        [self replaceTopConstraintOnView:self.searchBar withConstant: 0];
        //show all places to search
        if ([self.filterTabBar selectedIndex] != 0) {
            [self filterForOption:0];
        }
        [self.searchBar becomeFirstResponder];
    } else {
        [self replaceTopConstraintOnView:self.searchBar withConstant: -self.searchBar.frame.size.height];
        [self.filterTabBar setEnabled:YES];
        [self searchTextFieldEnabled:NO];
        isSearchBarShown = NO;
        [self.searchBar resignFirstResponder];
    }
    [self animateConstraints];
}

- (void)searchBarCancelButtonClicked:(UISearchBar *)searchBar {
    [self.searchBar resignFirstResponder];
    [self.filterTabBar setEnabled:YES];
    isSearchBarShown = NO;
    [self replaceTopConstraintOnView:self.searchBar withConstant: -self.searchBar.frame.size.height];
    [self animateConstraints];
}

- (void) searchBarSearchButtonClicked:(UISearchBar *)searchBar {
    [self handleSearchButton:nil];
}

- (void) searchTextFieldEnabled:(BOOL) enabled {
    UITextField *txfSearchField = [self.searchBar valueForKey:@"_searchField"];
    [txfSearchField setEnabled:enabled];
}

- (void) searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText {
   NSString* searchWord = self.searchBar.text;
  [self searchForWord:searchWord];
}

- (void) changeSearchKeyboardButtonTitle {
    for (UIView *subview in self.searchBar.subviews)
    {
        for (UIView *subSubview in subview.subviews)
        {
            if ([subSubview conformsToProtocol:@protocol(UITextInputTraits)])
            {
                UITextField *textField = (UITextField *)subSubview;
                textField.returnKeyType = UIReturnKeyDone;
                textField.enablesReturnKeyAutomatically = NO;
                break;
            }
        }
    }
}

//animation logic
- (void)replaceTopConstraintOnView:(UIView *)view withConstant:(float)constant
{
    [self.view.constraints enumerateObjectsUsingBlock:^(NSLayoutConstraint *constraint, NSUInteger idx, BOOL *stop) {
        if ((constraint.firstItem == view) && (constraint.firstAttribute == NSLayoutAttributeTop)) {
            constraint.constant = constant;
        }
    }];
}

- (void)animateConstraints
{
    [UIView animateWithDuration:0.5 animations:^{
        [self.view layoutIfNeeded];
    }];
}

-(void) scrollViewDidScroll:(UIScrollView *)scrollView {
    
    if(self.clubTable.pullToRefreshView.state || scrollView.contentOffset.y <= -10) { 
       return;
    }
    //scrolled up
    if (lastContentOffset < scrollView.contentOffset.y - 10) {
        [self.searchBar setHidden:YES];
        if (isSearchBarShown) {
           [self handleSearchButton:nil];
        }
        [[self navigationController] setNavigationBarHidden:YES animated:YES];
    }
    //scrolled down
    else if (lastContentOffset > scrollView.contentOffset.y + 5)  {
        [[self navigationController] setNavigationBarHidden:NO animated:YES];
     }
    lastContentOffset = scrollView.contentOffset.y;
}


-(void) scrollViewDidEndDecelerating:(UIScrollView *)scrollView {
    if ([self.searchBar isHidden] && ![[self navigationController] isNavigationBarHidden]) {
        [self.searchBar setHidden:NO];
    }
}
@end
