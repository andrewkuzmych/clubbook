//
//  ClubUsersViewController.m
//  Clubbook
//
//  Created by Andrew on 7/28/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "ClubCheckinsViewController.h"
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
#import "ClubUsersYesterdayViewController.h"
#import "ProfilePagesViewController.h"
#import "ClubViewParallaxControllerViewController.h"
#import "ClubProfileTabBarViewController.h"

@interface ClubCheckinsViewController ()<UINavigationControllerDelegate>
{
    NSArray *_users;
    BOOL isCheckedIn;
}

@end

@implementation ClubCheckinsViewController
{
    NSString* accessToken;
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}


- (void)viewDidLoad {
    [super viewDidLoad];
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    accessToken = [defaults objectForKey:@"accessToken"];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [self._manager retrievePlaceUsers:self.place.id accessToken:accessToken];
    });
    
    [PubNub setDelegate:self];
    self.profileCollection.dataSource = self;
    self.profileCollection.delegate = self;
    [self.checkinButton setHidden:YES];
    self.edgesForExtendedLayout = UIRectEdgeNone;
    self.extendedLayoutIncludesOpaqueBars = NO;
    self.automaticallyAdjustsScrollViewInsets = NO;
    
    [self populateData];
}

- (void)pubnubClient:(PubNub *)client didReceiveMessage:(PNMessage *)message {
    if ([message.channel.name isEqualToString:@"checkin"] ) {
        
        NSDictionary *dataJson = [message.message valueForKey:@"data"];
        NSString *club  = [dataJson valueForKey:@"club"];
        
        if ([club isEqualToString:self.place.id] ) {
            [self._manager retrievePlaceUsers:self.place.id accessToken:accessToken];
        }
    }
}

- (void)populateData {
    CLLocation *loc = [[CLLocation alloc] initWithLatitude:[self.place.lat doubleValue] longitude:[self.place.lon doubleValue]];
    
    CLLocationDistance distance = [[LocationManagerSingleton sharedSingleton].locationManager.location distanceFromLocation:loc];
    self.place.distance = distance;
    WorkingHour *hours = self.place.todayWorkingHours;
    if (hours != nil) {
        if ([hours.status isEqualToString:@"opened"] ) {
            self.openCloseLabel.text = NSLocalizedString(@"Open", nil);
        }
        else {
            self.openCloseLabel.text = NSLocalizedString(@"Closed", nil);
        }
        if (hours.startTime && hours.endTime) {
            self.hoursLabel.text = [NSString stringWithFormat:@"%@ - %@", hours.startTime, hours.endTime];
        }
    }
    else {
        self.openCloseLabel.text = @"";
        self.hoursLabel.text = NSLocalizedString(@"unknown", nil);
    }
    
    int disatanceInt = (int)self.place.distance;
    self.distanceLabel.text = [LocationHelper convertDistance:disatanceInt];
    
    BOOL check = [LocationHelper isCheckinHere:self.place];
    [self.checkinButton changeStatus:check];
    [self.checkinButton setHidden:NO];
    
    self.checkinCount.text = [NSString stringWithFormat:@"%d",self.place.countOfUsers];
    self.friendsCount.text = [NSString stringWithFormat:@"%d",self.place.friendsCount];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
     self.navigationController.delegate = self;
    
    //Google Analytics
    id tracker = [[GAI sharedInstance] defaultTracker];
    [tracker set:kGAIScreenName
           value:@"Club Users Screen"];
    [tracker send:[[GAIDictionaryBuilder createAppView] build]];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];

    if (self.navigationController.delegate == self) {
        self.navigationController.delegate = nil;
    }
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)didReceivePlaceUsers:(NSArray *)users;
{
    dispatch_async(dispatch_get_main_queue(), ^{
        _users = users;
        int friendsCount = 0;

        for (User *user in users) {
            if (user.isFriend) {
                 friendsCount++;
            }
        }
       
        self.checkinCount.text = [NSString stringWithFormat:@"%lu",(unsigned long)users.count];
        self.friendsCount.text = [NSString stringWithFormat:@"%d",friendsCount];
        
        [self.profileCollection reloadData];
    });
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    if (_place != nil) {
        return _users.count;
    }
    return 0;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
    BOOL isCheckinHere = [LocationHelper isCheckinHere:_place];
    User *user = _users[indexPath.row];
   
    if (!isCheckinHere && !user.isFriend)
        // cannot see profile when you are not checked in and not friend
        [CSNotificationView showInViewController:self
                                       tintColor:[UIColor colorWithRed:153/255.0f green:0/255.0f blue:217/255.0f alpha:1]
                                           image:nil
                                         message:NSLocalizedString(@"needToCheckinFirst", nil)
                                        duration:kCSNotificationViewDefaultShowDuration];
    else {
        UIStoryboard* storyBoard = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle:nil];
        ProfilePagesViewController *profilePagesViewController = [storyBoard instantiateViewControllerWithIdentifier:@"ProfilePages"];
        profilePagesViewController.profiles =_users;
        profilePagesViewController.index = indexPath.row;
        profilePagesViewController.currentPlace = self.place;
        [[self navigationController] pushViewController:profilePagesViewController animated:YES];
    }
    
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

- (void)didCheckin:(User *) user userInfo:(NSObject *)userInfo
{
    [self hideProgress];
    [self._manager retrievePlaceUsers:self.place.id accessToken:accessToken];
    
    [self checkinStatus:YES];
}

- (void)didCheckout:(User *) user userInfo:(NSObject *)userInfo
{
    [self hideProgress];
    [self._manager retrievePlaceUsers:self.place.id accessToken:accessToken];
    
    [self checkinStatus:NO];
}

- (void) checkinStatus:(BOOL) status {
    [self.checkinButton changeStatus:status];
    if (status) {
       [LocationHelper addCheckin:_place];
    }
    else {
        if ([LocationHelper isCheckinHere:self.place]) {
            [LocationHelper removeCheckin];
        }
    }
}

- (IBAction)checkinAction:(UIButton *)sender {
   CLLocation *loc = [[CLLocation alloc] initWithLatitude:[_place.lat doubleValue] longitude:[_place.lon doubleValue]];
   
   if(self.checkinButton.statusOn) {
      [self showProgress:NO title:NSLocalizedString(@"checking_out", nil)];
      [self._manager checkout:_place.id accessToken:accessToken userInfo:sender];
   }
   else {
        if([GlobalVars getInstance].MaxCheckinRadius  > (int)[[LocationManagerSingleton sharedSingleton].locationManager.location distanceFromLocation:loc]) {
            if([[NSUserDefaults standardUserDefaults] boolForKey:@"checkinInfoDisplayed"] == FALSE) {
                [[NSUserDefaults standardUserDefaults] setBool:TRUE forKey:@"checkinInfoDisplayed"];
                [[NSUserDefaults standardUserDefaults] synchronize];
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@""
                                                                message:NSLocalizedString(@"checkinInfo", nil)
                                                               delegate:self
                                                      cancelButtonTitle:NSLocalizedString(@"gotCheckinInfo", nil)
                                                      otherButtonTitles:nil];
                [alert show];
            }
            else {
                [self showProgress:NO title:NSLocalizedString(@"checking_in", nil)];
                [self._manager checkin:_place.id accessToken:accessToken userInfo:nil];
            }
        }
        else {
            NSString *message = NSLocalizedString(@"checkin_distance", nil);
            [CSNotificationView showInViewController:self
                                       tintColor:[UIColor colorWithRed:153/255.0f green:0/255.0f blue:217/255.0f alpha:1]
                                           image:nil
                                         message:message
                                        duration:kCSNotificationViewDefaultShowDuration];
        }
    }
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    [self showProgress:NO title:NSLocalizedString(@"checking_in", nil)];
    [self._manager checkin:_place.id accessToken:accessToken userInfo:nil];
}

- (IBAction)directionAction:(id)sender {
    MKPlacemark* place = [[MKPlacemark alloc] initWithCoordinate: CLLocationCoordinate2DMake([_place.lat doubleValue], [_place.lon doubleValue]) addressDictionary: nil];
    MKMapItem* destination = [[MKMapItem alloc] initWithPlacemark: place];
    destination.name = _place.title;
    NSArray* items = [[NSArray alloc] initWithObjects: destination, nil];
    NSDictionary* options = [[NSDictionary alloc] initWithObjectsAndKeys:
                             MKLaunchOptionsDirectionsModeWalking,
                             MKLaunchOptionsDirectionsModeKey, nil];
    [MKMapItem openMapsWithItems: items launchOptions: options];
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

#pragma mark

- (ProfileCell*)collectionViewCellForThing:(User*)user {
    NSUInteger userIndex = [_users indexOfObject:user];
    if (userIndex == NSNotFound) {
        return nil;
    }
    return (ProfileCell*)[self.profileCollection cellForItemAtIndexPath:[NSIndexPath indexPathForRow:userIndex inSection:0]];
}

@end
