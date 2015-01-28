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
#import "ClubUsersYesterdayViewController.h"
#import "ClubProfileTabBarViewController.h"

#define CLUBS_STRING @"Clubs"
#define CLUBS_TYPE  @"club"
#define BARS_STRING @"Cafe/Bars"
#define BARS_TYPE   @"bar"
#define EVENTS_STRING @"Events"

@interface MainViewController ()<UINavigationControllerDelegate, UINavigationBarDelegate>{
    BOOL isRefreshing;
    NSString* selectedClubType;
    
    BOOL isSearchBarShown;
    CGFloat lastContentOffset;
    
    double user_lat;
    double user_lon;
    NSString* user_accessToken;
}
@end

@implementation MainViewController

- (void)viewDidLoad
{
    [super viewDidLoad];

    self.clubTable.dataSource = self;
    self.clubTable.delegate = self;

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
    
    [self initFilterTabBar];
    [self initSearchBar];
    [self.eventsTable initializeNewsTableType:@"news" objectId:@"" andParentViewCntroller:(UIViewController*) self];
    
    user_lat = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.latitude;
    user_lon = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.longitude;
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    user_accessToken = [defaults objectForKey:@"accessToken"];
    
    selectedClubType = CLUBS_TYPE;
    [self.activityIndicator setHidden:NO];
    [self loadAllTypeClubs];
}

- (void) initFilterTabBar {
    selectedClubType = nil;
    //setup filter tab bar
    self.filterTabBar = [[SPSlideTabBar alloc] initWithFrame:CGRectMake(0, 0, self.filterTabView.frame.size.width, self.filterTabView.frame.size.height)];
    [self.filterTabBar setAutoresizingMask:UIViewAutoresizingFlexibleBottomMargin | UIViewAutoresizingFlexibleWidth];
    [self.filterTabBar setBackgroundColor:[UIColor colorWithRed:0.651 green:0 blue:0.867 alpha:1]];
    [self.filterTabBar setSeparatorStyle:SPSlideTabBarSeparatorStyleNone];
    [self.filterTabBar setBarButtonTitleColor:[UIColor colorWithRed:192/255.0f green:154/255.0f blue:234/255.0f alpha:1.0f]];
    [self.filterTabBar setSelectedButtonColor:[UIColor whiteColor]];
    [self.filterTabBar setSelectedViewColor:[UIColor whiteColor]];
    [self.filterTabBar setSlideDelegate:self];
    [self.filterTabView addSubview:self.filterTabBar];
    
    //remove black line above filtertab
    UINavigationBar *navigationBar = self.navigationController.navigationBar;
    
    [navigationBar setBackgroundImage:[UIImage new]
                       forBarPosition:UIBarPositionAny
                           barMetrics:UIBarMetricsDefault];
    
    [navigationBar setShadowImage:[UIImage new]];
    
    [self.filterTabBar addTabForTitle:CLUBS_STRING];
    [self.filterTabBar addTabForTitle:BARS_STRING];
    [self.filterTabBar addTabForTitle:EVENTS_STRING];
    [self.filterTabBar setSelectedIndex:0];
}

- (void) initSearchBar {
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
    
    [self.view setBackgroundColor:self.filterTabBar.backgroundColor];
 
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
    [self loadClubType:selectedClubType take:10 skip:0 refreshing:YES];
}

- (void)insertRowAtBottom {
    int countToSkip = (int)[self.places count];
    [self loadClubType:selectedClubType take:10 skip:countToSkip refreshing:NO];
}

- (void)didReceivePlaces:(NSArray *)places andTypes:(NSArray *)types
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        [self.activityIndicator setHidden:YES];
        
        if (isRefreshing) {
            _places = [places mutableCopy];
            isRefreshing = NO;
        }
        else {
            [_places addObjectsFromArray:places];
        }

        if ([_places count] > 0) {
            [self.noResultsLabel setHidden:YES];
        }
        else {
            [self.noResultsLabel setHidden:NO];
        }

        [self.clubTable.pullToRefreshView stopAnimating];
        [self.clubTable.infiniteScrollingView stopAnimating];
        [self.clubTable reloadData];
    });
}

- (void)didUpdateUserLocation:(NSString *)result
{
}

- (void) loadAllTypeClubs {
    [self loadClubType:@"" take:10 skip:0 refreshing:YES];
}

- (void) tableWillBeRefreshed {
    [_places removeAllObjects];
    [self.clubTable reloadData];
    [self.activityIndicator setHidden:NO];
    [self.noResultsLabel setHidden:YES];
}

- (void) filterForType:(NSString*) type {
    [self tableWillBeRefreshed];
    [self._manager retrievePlaces:user_lat lon:user_lon take:10 skip:0 distance:0 type:type search:@"" accessToken:user_accessToken];
}

- (void) searchForWord:(NSString*) searchWord {
    [self tableWillBeRefreshed];
    [self._manager retrievePlaces:user_lat lon:user_lon take:10 skip:0 distance:0 type:selectedClubType search:searchWord accessToken:user_accessToken];
}

- (void)loadClubType:(NSString*) type take:(int)take skip:(int)skip refreshing:(BOOL) refreshing
{
    isRefreshing = refreshing;
    [self._manager retrievePlaces:user_lat lon:user_lon take:take skip:skip distance:0 type:type search:@"" accessToken:user_accessToken];
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
    
    int disatanceInt = (int)place.distance;
    
    [cell.distanceLabel setText:[LocationHelper convertDistance:disatanceInt]];
    
    [cell.userCountLabel setText: [NSString stringWithFormat:@"%d", place.countOfUsers]];
    
    [cell.friendsCountLabel setText: [NSString stringWithFormat:@"%d", place.friendsCount]];
    
    [cell.clubAvatar sd_setImageWithURL:[NSURL URLWithString:place.avatar] placeholderImage:[UIImage imageNamed:@"avatar_default.png"]];
  
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSIndexPath *selectedIndexPath = [self.clubTable indexPathForSelectedRow];
    Place *place = _places[selectedIndexPath.row];
    
    UIStoryboard *clubProfileStoryboard = [UIStoryboard storyboardWithName:@"ClubProfileStoryboard" bundle: nil];
    ClubUsersViewController *clubController  = [clubProfileStoryboard instantiateViewControllerWithIdentifier:@"club"];
    clubController.place = place;
    clubController.hasBack = YES;

    [UIView beginAnimations:@"animation" context:nil];
    [UIView setAnimationDuration:0.5];
    [self.navigationController pushViewController: clubController animated:NO];
    [UIView setAnimationTransition:UIViewAnimationTransitionFlipFromLeft forView:self.navigationController.view cache:NO];
    [UIView commitAnimations];
    [self.clubTable deselectRowAtIndexPath:indexPath animated:NO];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - SPSlideTabBarDelegate
- (void)barButtonClicked:(SPSlideTabButton *)button {
    [self filterForOption:button.tag];
}

-(void) filterForOption:(NSUInteger) index {
    [self.filterTabBar setSelectedIndex:index];
    NSString* typeString = [self.filterTabBar getButtonTitleAtIndex:index];
    
    if ([typeString isEqualToString:CLUBS_STRING]) {
        selectedClubType = CLUBS_TYPE;
        [self.eventsTable setHidden:YES];
        [self.clubTable setHidden:NO];
        [self filterForType:selectedClubType];
    }
    else if ([typeString isEqualToString:BARS_STRING] ) {
        selectedClubType = BARS_TYPE;
        [self.eventsTable setHidden:YES];
        [self.clubTable setHidden:NO];
        [self filterForType:selectedClubType];
    }
    else if ([typeString isEqualToString:EVENTS_STRING]) {
        if (isSearchBarShown) {
            [self handleSearchButton:nil];
        }
        [self.eventsTable setHidden:NO];
        [self.clubTable setHidden:YES];
    }
}

//search logic
- (IBAction)handleSearchButton:(id)sender {
    if(!isSearchBarShown) {
        isSearchBarShown = YES;
        [self searchTextFieldEnabled:YES];
        [self replaceTopConstraintOnView:self.searchBar withConstant: 0];
        [self.searchBar becomeFirstResponder];
    } else {
        [self replaceTopConstraintOnView:self.searchBar withConstant: -self.searchBar.frame.size.height];
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
