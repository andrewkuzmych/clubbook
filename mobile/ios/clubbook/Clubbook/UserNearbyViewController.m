//
//  UserCheckinsViewController.m
//  Clubbook
//
//  Created by Andrew on 10/23/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "UserNearbyViewController.h"
#import "HeaderView.h"
#import "ClubFooterView.h"
#import "LocationManagerSingleton.h"
#import "LocationHelper.h"
#import "UIImageView+WebCache.h"
#import "UserProfileViewController.h"
#import "ProfileCell.h"
#import "Constants.h"
#import "CSNotificationView.h"
#import "Cloudinary.h"
#import "ClubViewController.h"
#import "GlobalVars.h"
#import "TransitionFromClubUsersToUser.h"
#import "ProfilePagesViewController.h"
#import "SVPullToRefresh.h"
#import "FilterMenuTableViewCell.h"

#define FilterCellHeight 40

@interface UserNearbyViewController ()<UINavigationControllerDelegate>{
    BOOL isInitialLoad;
    NSMutableArray *_users;
    NSArray* filterOptions;
    BOOL isFilterBarShown;
    NSString* selectedFilterMode;
    int distanceKm;
    NSString* gender;
    BOOL showAll;
}

@property (nonatomic) BOOL isLoaded;
@end

@implementation UserNearbyViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"All";// NSLocalizedString(@"checkedIn", nil);
    selectedFilterMode = @"all";
    gender = @"";
    showAll = YES;
    self.profileCollection.dataSource = self;
    self.profileCollection.delegate = self;
    
    UIBarButtonItem *filterButton = [[UIBarButtonItem alloc]
                                   initWithTitle:@"Filter"
                                   style:UIBarButtonItemStyleBordered
                                   target:self
                                   action:@selector(handleFilterButton)];
    self.navigationItem.rightBarButtonItem = filterButton;
    isFilterBarShown = NO;
    
    self.filterMenuTable.delegate = self;
    self.filterMenuTable.dataSource = self;
    self.filterMenuTable.separatorStyle = UITableViewCellSeparatorStyleNone;
    
    filterOptions = [NSArray arrayWithObjects:@"all", @"male", @"female", nil];
    
    float height = FilterCellHeight * [filterOptions count];
    [self replaceTopConstraintOnView:self.filterMenuTable withConstant: -height];
    
    NSDictionary *textAttributes = [NSDictionary dictionaryWithObjectsAndKeys:[UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:14], NSFontAttributeName, nil];
    [self.usersSegment setTitleTextAttributes:textAttributes forState:UIControlStateNormal];
    
    [self.usersSegment setTitle:[NSString stringWithFormat:NSLocalizedString(@"all", nil)] forSegmentAtIndex:0];
    [self.usersSegment setTitle:[NSString stringWithFormat:NSLocalizedString(@"checkedIn", nil)] forSegmentAtIndex:1];
    
    distanceKm = 20;
    self.distance.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:14];
    [self.distance setText:[NSString stringWithFormat:@"%d%@", distanceKm, NSLocalizedString(@"kilometers", nil)]];
    
    self.clubFooterView.footerInfoLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:18];
   
    __weak UserNearbyViewController *weakSelf = self;
    
    // setup pull-to-refresh
    [self.profileCollection addPullToRefreshWithActionHandler:^{
        [weakSelf insertRowAtTop];
    }];
    
    // setup infinite scrolling
    [self.profileCollection addInfiniteScrollingWithActionHandler:^{
        [weakSelf insertRowAtBottom];
    }];

    [self loadUsers:30 skip:0];
    
    [PubNub setDelegate:self];

}

- (void)insertRowAtTop {
      [self loadUsers:30 skip:0];
    //[self loadClub:10 skip:0];
}

- (void)insertRowAtBottom {
    int countToSkip = (int)[_users count];
    [self loadUsers:30 skip:countToSkip];
}

- (void)pubnubClient:(PubNub *)client didReceiveMessage:(PNMessage *)message
{
    if ([message.channel.name isEqualToString:@"checkin"] ) {
        self.isLoaded = NO;
        [self loadUsers:30 skip:0];
    }
}


- (void)didUpdateLocation
{
    [self loadUsers:30 skip:0];
}

- (void)didFailUpdateLocation
{
   // [self noLocation];
}

- (void)didReceiveUsersCheckedin:(NSArray *)users
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.clubFooterView.loadingIndicator stopAnimating];
        self.clubFooterView.loadingIndicator.hidden = YES;
        
        
        if (isInitialLoad) {
            _users = [users mutableCopy];
        } else {
            [_users addObjectsFromArray:users];
        }
        
        [self.clubFooterView.loadingIndicator stopAnimating];
        self.clubFooterView.loadingIndicator.hidden = YES;
       
        [self.profileCollection.pullToRefreshView stopAnimating];
        [self.profileCollection.infiniteScrollingView stopAnimating];
        
        //self.title = [NSString stringWithFormat:@"%@ (%lu)", NSLocalizedString(@"checkedIn", nil), [users count]];
        
        self.clubFooterView.footerInfoLabel.hidden = YES;
        self.clubFooterView.footerInfoLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:18];
        if (_users.count == 0) {
            self.clubFooterView.footerInfoLabel.hidden = NO;
            self.clubFooterView.footerInfoLabel.text = NSLocalizedString(@"noUsersCheckedIn", nil);
        }

        [self.profileCollection reloadData];
    });

}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.delegate = self;
    self.navigationController.navigationBar.translucent = NO;
    //Google Analytics
    id tracker = [[GAI sharedInstance] defaultTracker];
    [tracker set:kGAIScreenName
           value:@"Club Users Yesterday Screen"];
    [tracker send:[[GAIDictionaryBuilder createAppView] build]];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return _users.count;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    
    self.isLoaded = NO;

    [self performSegueWithIdentifier: @"onUsers" sender: indexPath];
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(NSIndexPath *)indexPath
{
    if([[segue identifier] isEqualToString:@"onUsers"]){
        ProfilePagesViewController *profilePagesViewController =  [segue destinationViewController];
        profilePagesViewController.profiles =_users;
        profilePagesViewController.index = indexPath.row;
   } else if([[segue identifier] isEqualToString:@"onUser"]){
        UserProfileViewController *userController =  [segue destinationViewController];
        User *user = _users[indexPath.row];
        userController.user= user;
        self.isLoaded = NO;
        //userController.currentPlace = self.place;
        //userController.clubCheckinName = self.place.title;
    }
}

- (UICollectionReusableView *)collectionView:(UICollectionView *)collectionView viewForSupplementaryElementOfKind:(NSString *)kind atIndexPath:(NSIndexPath *)indexPath {
    UICollectionReusableView *reusableview = nil;
    
    if (kind == UICollectionElementKindSectionHeader) {
        // load header
        self.headerView = [collectionView dequeueReusableSupplementaryViewOfKind:UICollectionElementKindSectionHeader withReuseIdentifier:@"header" forIndexPath:indexPath];
        reusableview = self.headerView;
        
    }
    
    if (kind == UICollectionElementKindSectionFooter) {
        self.clubFooterView = [collectionView dequeueReusableSupplementaryViewOfKind:UICollectionElementKindSectionFooter withReuseIdentifier:@"footer" forIndexPath:indexPath];
        
        reusableview = self.clubFooterView;
        
    }
    
    return reusableview;
}


- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath{
    static NSString *identifier = @"Cell";
    ProfileCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:identifier forIndexPath:indexPath];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        User *user = _users[indexPath.row];
        cell.friendIcon.hidden = !user.isFriend;
        
        // transform avatar
        CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
        CLTransformation *transformation = [CLTransformation transformation];
        [transformation setParams: @{@"width": @120, @"height": @120}];
        NSString * avatarUrl  = [cloudinary url: [user.avatar valueForKey:@"public_id"] options:@{@"transformation": transformation}];
        [cell.profileAvatar sd_setImageWithURL:[NSURL URLWithString:avatarUrl] placeholderImage:[UIImage imageNamed:@"avatar_empty.png"]];
    });
    return cell;
}

#pragma mark UINavigationControllerDelegate methods

- (id<UIViewControllerAnimatedTransitioning>)navigationController:(UINavigationController *)navigationController
                                  animationControllerForOperation:(UINavigationControllerOperation)operation
                                               fromViewController:(UIViewController *)fromVC
                                                 toViewController:(UIViewController *)toVC {
    // Check if we're transitioning from this view controller to a DSLSecondViewController
    if (fromVC == self && [toVC isKindOfClass:[UserProfileViewController class]]) {
        return [[TransitionFromClubUsersToUser alloc] init];
    }
    else {
        return nil;
    }
}


- (void)loadUsers:(int)take skip:(int)skip
{
    //if (self.isLoaded || [LocationManagerSingleton sharedSingleton].locationManager.location == nil) {
    //    return;
    //}
    
    isInitialLoad = NO;
    if (skip == 0) {
        isInitialLoad = YES;
        if (_users.count == 0) {
            if (self.isLoaded == YES)
            {
                return;
            }
            //[self showProgress:NO title:nil];
        }
        
    }

    self.isLoaded = YES;
    //[self yesLocation];
    [self.clubFooterView.loadingIndicator startAnimating];
    self.clubFooterView.loadingIndicator.hidden = NO;
    dispatch_async(dispatch_get_main_queue(), ^{
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *accessToken = [defaults objectForKey:@"accessToken"];
        double lat = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.latitude;
        double lng = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.longitude;

        [self._manager receivedUsers:showAll gender:gender take:take skip:skip lat:lat lon:lng distance:distanceKm accessToken:accessToken];
    });
}

#pragma mark

- (ProfileCell*)collectionViewCellForThing:(User*)user {
    NSUInteger userIndex = [_users indexOfObject:user];
    if (userIndex == NSNotFound) {
        return nil;
    }
    //static NSString *identifier = @"Cell";
    //ProfileCell *cell = [self.profileCollection dequeueReusableCellWithReuseIdentifier:identifier forIndexPath:userIndex];
    
    return (ProfileCell*)[self.profileCollection cellForItemAtIndexPath:[NSIndexPath indexPathForRow:userIndex inSection:0]];
}

- (IBAction)sliderChanged:(id)sender {
    
    int sliderValue;
    sliderValue = (int)lroundf(self.sliderControl.value);
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
            km = 15 ;
            break;
        case 8:
            km = 20;
            break;
        default:
            break;
    }
    return km;
}

- (IBAction)sliderTouchUp:(id)sender
{
    int sliderValue;
    sliderValue = (int)lroundf(self.sliderControl.value);
    [self.sliderControl setValue:sliderValue animated:YES];
    
    distanceKm = [self convertToKm:sliderValue];
    self.isLoaded = NO;
    [self loadUsers:30 skip:0];
    
    [self.distance setText:[NSString stringWithFormat:@"%d%@", distanceKm, NSLocalizedString(@"kilometers", nil)]];
}

- (void) handleFilterButton {
    if(!isFilterBarShown) {
        isFilterBarShown = YES;
        self.filterMenuTable.allowsSelection = YES;
        [self replaceTopConstraintOnView:self.filterMenuTable withConstant: 0];
    } else {
        [self replaceTopConstraintOnView:self.filterMenuTable withConstant: -self.filterMenuTable.frame.size.height];
        self.filterMenuTable.allowsSelection = NO;
        isFilterBarShown = NO;
    }
    [self animateConstraints];
}

- (void) hideFilterMenu {
    isFilterBarShown = NO;
    self.filterMenuTable.allowsSelection = NO;
    [self replaceTopConstraintOnView:self.filterMenuTable withConstant: -self.filterMenuTable.frame.size.height];
    [self animateConstraints];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return [filterOptions count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *cellIdentifier = @"FilterMenuTableViewCell";
    
    FilterMenuTableViewCell *cell = [self.filterMenuTable dequeueReusableCellWithIdentifier:cellIdentifier forIndexPath:indexPath];
    [cell setSelectedImage: NO];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    NSString* rowType = filterOptions[indexPath.row];
    if ([rowType compare:@"all"] == NSOrderedSame) {
        cell.filterLabel.text = @"All";
        if ([selectedFilterMode compare:@"all"] == NSOrderedSame) {
            [cell setSelectedImage:YES];
        }
    } else if ([rowType compare:@"male"] == NSOrderedSame) {
        cell.filterLabel.text = @"Male";
        if ([selectedFilterMode compare:@"male"] == NSOrderedSame) {
            [cell setSelectedImage:YES];
        }
    } else if ([rowType compare:@"female"] == NSOrderedSame) {
        cell.filterLabel.text = @"Female";
        if ([selectedFilterMode compare:@"female"] == NSOrderedSame) {
            [cell setSelectedImage:YES];
        }
    } else if ([rowType compare:@"friends"] == NSOrderedSame) {
        cell.filterLabel.text = @"Show Friends first";
    }

    return cell;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return FilterCellHeight;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    NSUInteger row = indexPath.row;
    NSString* newMode = [filterOptions objectAtIndex:row];
    if ([selectedFilterMode compare:newMode] != NSOrderedSame) {
        selectedFilterMode = newMode;
        [self.filterMenuTable deselectRowAtIndexPath:indexPath animated:NO];
        
        NSString* filterType = [selectedFilterMode capitalizedString];
        NSString* newTitle = [NSString stringWithFormat:@"%@", filterType];
        self.title = newTitle;
        
        [self.filterMenuTable reloadData];
        
        if([selectedFilterMode compare:@"all"] == NSOrderedSame) {
            gender = @"";
        }
        else {
            gender = selectedFilterMode;
        }
        [self filteredOptionsChanged];
    }
    [self hideFilterMenu];
}

-(void) filterForOption:(NSString*) option {
    gender = option;

    [self filteredOptionsChanged];
}

-(void) filteredOptionsChanged {
    [_users removeAllObjects];
    [self.profileCollection reloadData];
    
    self.isLoaded = YES;
    [self.clubFooterView.loadingIndicator startAnimating];
    self.clubFooterView.loadingIndicator.hidden = NO;
    dispatch_async(dispatch_get_main_queue(), ^{
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *accessToken = [defaults objectForKey:@"accessToken"];
        double lat = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.latitude;
        double lng = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.longitude;
        
        [self._manager receivedUsers:showAll gender:gender take:30 skip:0 lat:lat lon:lng distance:distanceKm accessToken:accessToken];
    });
    
}

- (IBAction)segmentChanged:(id)sender {
    if([self.usersSegment selectedSegmentIndex] == 0){
        showAll = YES;
    } else if([self.usersSegment selectedSegmentIndex] == 1){
        showAll = NO;
    }
    [self filteredOptionsChanged];
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

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end