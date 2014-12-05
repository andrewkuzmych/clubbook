//
//  UserCheckinsViewController.m
//  Clubbook
//
//  Created by Andrew on 10/23/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "UserCheckinsViewController.h"
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

@interface UserCheckinsViewController ()<UINavigationControllerDelegate>{
    BOOL isInitialLoad;
    NSMutableArray *_users;
    int distanceKm;
}

@property (nonatomic) BOOL isLoaded;
@end

@implementation UserCheckinsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.title = @"User Profiles";// NSLocalizedString(@"checkedIn", nil);
    self.profileCollection.dataSource = self;
    self.profileCollection.delegate = self;
    
    NSDictionary *textAttributes = [NSDictionary dictionaryWithObjectsAndKeys:[UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:14], UITextAttributeFont, nil];
    [self.usersSegment setTitleTextAttributes:textAttributes forState:UIControlStateNormal];
    
    [self.usersSegment setTitle:[NSString stringWithFormat:NSLocalizedString(@"all", nil)] forSegmentAtIndex:0];
    [self.usersSegment setTitle:[NSString stringWithFormat:NSLocalizedString(@"checkedIn", nil)] forSegmentAtIndex:1];
    
    distanceKm = 20;
    self.distance.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:14];
    [self.distance setText:[NSString stringWithFormat:@"%d%@", distanceKm, NSLocalizedString(@"kilometers", nil)]];
    
    self.clubFooterView.footerInfoLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:18];
    //[LocationManagerSingleton sharedSingleton].delegate = self;
    ///[[LocationManagerSingleton sharedSingleton] startLocating];
    
    __weak UserCheckinsViewController *weakSelf = self;
    
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
    int countToSkip = [_users count];
    [self loadUsers:30 skip:countToSkip];
    //[self loadUsers];
    //int countToSkip = [self.places count];
    //[self loadClub:10 skip:countToSkip];
    
    //[self.clubTable.infiniteScrollingView stopAnimating];
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
        [cell.profileAvatar setImageWithURL:[NSURL URLWithString:avatarUrl] placeholderImage:[UIImage imageNamed:@"avatar_empty.png"]];
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

        [self._manager receivedUsers:true take:take skip:skip lat:lat lon:lng distance:distanceKm accessToken:accessToken];
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
    sliderValue = lroundf(self.sliderControl.value);
    [self.sliderControl setValue:sliderValue animated:YES];
    
    distanceKm = [self convertToKm:sliderValue];
    self.isLoaded = NO;
    [self loadUsers:30 skip:0];
    
    [self.distance setText:[NSString stringWithFormat:@"%d%@", distanceKm, NSLocalizedString(@"kilometers", nil)]];
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
