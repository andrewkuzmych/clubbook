//
//  Created by Clubbok.
//
#import "PlacesViewController.h"
#import "Place.h"
#import "LocationManagerSingleton.h"
#import "GlobalVars.h"
#import "PlacesTabView.h"
#import "EventsView.h"
#import "ClubsInfiniteTableView.h"

@interface PlacesViewController ()<UINavigationControllerDelegate, UINavigationBarDelegate>{
    BOOL isSearchBarShown;
    CGFloat lastContentOffset;
    
    double user_lat;
    double user_lon;
    NSString* user_accessToken;
}
@end

@implementation PlacesViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self._manager getConfig];
    
    user_lat = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.latitude;
    user_lon = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.longitude;
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    user_accessToken = [defaults objectForKey:@"accessToken"];

    [self initFilterTabBar];
    [self initSearchBar];
    
}

- (void) initFilterTabBar {
    [self.slideTabBarView setTabBarHeight:40.0];
    [self.slideTabBarView setAutoresizingMask:UIViewAutoresizingFlexibleBottomMargin | UIViewAutoresizingFlexibleWidth];
    [self.slideTabBarView setTabBarBackgroundColor:[UIColor colorWithRed:0.651 green:0 blue:0.867 alpha:1]];
    [self.slideTabBarView setSeparatorStyle:SPSlideTabBarSeparatorStyleNone];
    [self.slideTabBarView setBarButtonTitleColor:[UIColor colorWithRed:192/255.0f green:154/255.0f blue:234/255.0f alpha:1.0f]];
    [self.slideTabBarView setSelectedViewColor:[UIColor whiteColor]];
    
    //remove black line above filtertab
    UINavigationBar *navigationBar = self.navigationController.navigationBar;
    
    [navigationBar setBackgroundImage:[UIImage new]
                       forBarPosition:UIBarPositionAny
                           barMetrics:UIBarMetricsDefault];
    
    [navigationBar setShadowImage:[UIImage new]];

    PlacesTabView *clubView = [[PlacesTabView alloc] init];
    self.clubTable = [[ClubsInfiniteTableView alloc] initWithFrame:CGRectZero type:@"club" userLat:user_lat userLon:user_lon accessToken:user_accessToken];
    self.clubTable.transitionDelegate = self;
    [clubView addTableToTheView:self.clubTable];
    [self.slideTabBarView addPageView:clubView ForTitle:@"Clubs"];
    
    PlacesTabView *barsView = [[PlacesTabView alloc] init];
    self.barsTable = [[ClubsInfiniteTableView alloc] initWithFrame:CGRectZero type:@"bar" userLat:user_lat userLon:user_lon accessToken:user_accessToken];
    self.barsTable.transitionDelegate = self;
    [barsView addTableToTheView:self.barsTable];
    [self.slideTabBarView addPageView:barsView ForTitle:@"Bars & Cafes"];
    
    PlacesTabView *festivalsView = [[PlacesTabView alloc] init];
    self.festivalsTable = [[ClubsInfiniteTableView alloc] initWithFrame:CGRectZero type:@"festival" userLat:user_lat userLon:user_lon accessToken:user_accessToken];
    self.festivalsTable.transitionDelegate = self;
    [festivalsView addTableToTheView:self.festivalsTable];
    [self.slideTabBarView addPageView:festivalsView ForTitle:@"Festivals"];
    
    PlacesTabView *djsView = [[PlacesTabView alloc] init];
    self.djsTable = [[DjTableView alloc] initWithFrame:CGRectZero userLat:user_lat userLon:user_lon accessToken:user_accessToken];
    self.djsTable.transitionDelegate = self;
    [djsView addTableToTheView:self.djsTable];
    [self.slideTabBarView addPageView:djsView ForTitle:@"DJs & Bands"];
    
    self.eventView = [[EventsView alloc] init];
    [self.eventView customInit:user_lat userLon:user_lon accessTOken:user_accessToken];
    self.eventView.eventsTable.transitionDelegate = self;
    [self.slideTabBarView addPageView:self.eventView ForTitle:@"Events"];
}

- (void) initSearchBar {
    //set up search field
    self.searchBar.barTintColor = [UIColor colorWithRed:0.651 green:0 blue:0.867 alpha:1];
    
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
    
    [self.view setBackgroundColor:[UIColor colorWithRed:0.651 green:0 blue:0.867 alpha:1]];
 
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

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBar.translucent = NO;
}


- (void)didUpdateUserLocation:(NSString *)result
{
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
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
   if ([searchWord isEmpty]) {
       if ([self currentTableIsRefreshing]) {
           return;
       }
       [self updateTableOnSearchWord:@""];
   }
   else {
       [self updateTableOnSearchWord:searchWord];
   }
}

- (void) updateTableOnSearchWord:(NSString*) searchWord {
    InfiniteScrollTableView* table = [self activeInfiniteScrollTable];
    if (table) {
        if ([searchWord isEqualToString:@""]) {
            [table refreshData];
        } else {
           [table searchForWord:searchWord];
        }
        
    }
}

- (BOOL) currentTableIsRefreshing {
    InfiniteScrollTableView* table = [self activeInfiniteScrollTable];
    if (table) {
        return table.isRefreshing;
    }
    else {
        return NO;
    }
}

- (InfiniteScrollTableView*) activeInfiniteScrollTable {
    NSUInteger index = self.slideTabBarView.selectedPageIndex;
    if (index == 0) {
        return self.clubTable;
    }
    else if (index == 1) {
        return self.barsTable;
    }
    else if (index == 2) {
        return self.festivalsTable;
    }
    else if (index == 3) {
        return self.djsTable;
    }
    else if (index == 4) {
        return self.eventView.eventsTable;
    }
    return nil;
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

- (void) transitToNewController:(UIViewController *)controller {
    [UIView beginAnimations:@"animation" context:nil];
    [UIView setAnimationDuration:0.5];
    [self.navigationController pushViewController:controller animated:NO];
    [UIView setAnimationTransition:UIViewAnimationTransitionFlipFromLeft forView:self.navigationController.view cache:NO];
    [UIView commitAnimations];
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

@end
