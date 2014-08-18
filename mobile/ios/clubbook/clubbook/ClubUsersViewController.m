//
//  ClubUsersViewController.m
//  Clubbook
//
//  Created by Andrew on 7/28/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "ClubUsersViewController.h"
#import "HeaderView.h"
#import "ClubFooterView.h"
#import "LocationManagerSingleton.h"
#import "LocationHelper.h"
#import "UIImageView+WebCache.h"
#import "UserViewController.h"
#import "ProfileCell.h"
#import "Constants.h"
#import "CSNotificationView.h"
#import "Cloudinary.h"
#import "ClubViewController.h"
#import "GlobalVars.h"

@interface ClubUsersViewController ()
{
    Place *_place;
    int minUserCount;
}

@property (nonatomic, strong) HeaderView *headerView;
@property (nonatomic, strong) ClubFooterView *clubFooterView;

@end

@implementation ClubUsersViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    if (self.hasBack) {
        self.navigationItem.leftBarButtonItem = nil;
    }

    
    // Do any additional setup after loading the view.
    minUserCount = 10;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *accessToken = [defaults objectForKey:@"accessToken"];
        
        [self._manager retrievePlace:self.placeId accessToken:accessToken];
        [self showProgress:YES title:nil];
    });
    
    self.profileCollection.dataSource = self;
    self.profileCollection.delegate = self;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)didReceivePlace:(Place *)place
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        self.headerView.checkinButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:16.0];
        self.headerView.checkinCountLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:14.0];
        self.headerView.friendsCountLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:14.0];
        self.headerView.checkinCountTitleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:12.0];
        self.headerView.checkinCountTitleLabel.text = NSLocalizedString(@"checkedIn", nil);
        self.headerView.friendsCountTitleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:12.0];
        self.headerView.friendsCountTitleLabel.text = NSLocalizedString(@"friends_lower", nil);
        self.headerView.clubNameText.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:19.0];
        self.headerView.openTodayLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:13.0];
        self.headerView.workingHoursLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:11.0];
        self.headerView.clubInfoButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:14.0];
        self.headerView.clubDistanceText.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:15.0];
        self.clubFooterView.clubUsersLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:16.0];
        self.clubFooterView.checkinLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:20.0];
        self.clubFooterView.usersLeftToCheckinLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:16.0];
        
        // GlobalVars *globalVars=[GlobalVars getInstance];
        CLLocation *loc = [[CLLocation alloc] initWithLatitude:[place.lat doubleValue] longitude:[place.lon doubleValue]];
        
        CLLocationDistance distance = [[LocationManagerSingleton sharedSingleton].locationManager.location distanceFromLocation:loc];
        place.disatance = distance;
        
        self.headerView.clubNameText.text = place.title;
        
        if (place.todayWorkingHours != nil) {
            
            if ([place.todayWorkingHours.status isEqualToString:@"opened"] ) {
                 self.headerView.workingHoursLabel.text = [NSString stringWithFormat:@"%@ - %@", place.todayWorkingHours.startTime, place.todayWorkingHours.endTime];
            } else
                 self.headerView.workingHoursLabel.text = NSLocalizedString(@"closed", nil);
        } else {
            self.headerView.workingHoursLabel.text = NSLocalizedString(@"unknown", nil);
        }
        
        int disatanceInt = (int)place.disatance;
        self.headerView.clubDistanceText.text = [LocationHelper convertDistance:disatanceInt];
        
        [self.headerView.clubAvatarImage setImageWithURL:[NSURL URLWithString:place.avatar] placeholderImage:[UIImage imageNamed:@"Default.png"]];
        
        BOOL isCheckinHere = [LocationHelper isCheckinHere:place];
        if(isCheckinHere){
            [self.headerView.checkinButton setSecondState:NSLocalizedString(@"checkout", nil)];
        } else {
            [self.headerView.checkinButton setMainState:NSLocalizedString(@"checkin", nil)];
        }
        
        self.headerView.checkinCountLabel.text = [NSString stringWithFormat:@"%d",place.countOfUsers];
        self.headerView.friendsCountLabel.text = [NSString stringWithFormat:@"%d",place.friendsCount];

        _place = place;

        if (place.users.count < minUserCount) {
            NSString *userList = @"";
            for (User *user in place.users) {
                if([userList length] > 0)
                    userList = [NSString stringWithFormat:@"%@,%@",userList, user.name];
                else
                    userList = user.name;
            }
            
            if([userList length] == 0)
                userList = NSLocalizedString(@"nonePeople", nil);
            self.clubFooterView.clubUsersLabel.text = userList;
            
            self.clubFooterView.usersLeftToCheckinLabel.text = [NSString stringWithFormat:NSLocalizedString(@"usersLeftToChecdkin", nil),(minUserCount - place.users.count)];

        }
        
        [self updateFooter];
         
        [self.profileCollection reloadData];
    });
}

- (void) updateFooter
{
    if (_place.users.count < minUserCount) {
        [self.clubFooterView expand];
    } else {
        [self.clubFooterView collapse];
    }
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    if(_place == nil)
    {
        return 0;
    } else{
        if(_place.users.count < minUserCount)
            return minUserCount;
        else
            return _place.users.count;
    }
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{

    BOOL isCheckinHere = [LocationHelper isCheckinHere:_place];
    if (_place.users.count < minUserCount) {
        // not enough users to show profiles
        NSString * text = [NSString stringWithFormat:NSLocalizedString(@"usersLeftToChecdkin", nil),(minUserCount - _place.users.count)];
        
        [CSNotificationView showInViewController:self
                                       tintColor:[UIColor colorWithRed:153/255.0f green:0/255.0f blue:217/255.0f alpha:1]
                                           image:nil
                                         message:text
                                        duration:kCSNotificationViewDefaultShowDuration];
    } else {
        User *user = _place.users[indexPath.row];
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *userId = [defaults objectForKey:@"userId"];
        
        if (!isCheckinHere && !user.isFriend)
            // cannot see profile when you are not checked in and not friend
            [CSNotificationView showInViewController:self
                                           tintColor:[UIColor colorWithRed:153/255.0f green:0/255.0f blue:217/255.0f alpha:1]
                                               image:nil
                                            message:NSLocalizedString(@"needToCheckinFirst", nil)
                                            duration:kCSNotificationViewDefaultShowDuration];
        else if([user.id isEqualToString:userId])
            // cannot see own profile :)
            [CSNotificationView showInViewController:self
                                           tintColor:[UIColor colorWithRed:153/255.0f green:0/255.0f blue:217/255.0f alpha:1]
                                               image:nil
                                             message:NSLocalizedString(@"cannotSeeOwnPofile", nil)
                                            duration:kCSNotificationViewDefaultShowDuration];
        else
            [self performSegueWithIdentifier: @"onUser" sender: indexPath];

    }
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(NSIndexPath *)indexPath
{
    if([[segue identifier] isEqualToString:@"onUser"]){
        UserViewController *userController =  [segue destinationViewController];
        User *user = _place.users[indexPath.row];
        userController.userId = user.id;
        userController.currentPlace = _place;
        userController.clubCheckinName = _place.title;
    } else if ([[segue identifier] isEqualToString:@"onClubInfo"]) {
        ClubViewController *clubController =  [segue destinationViewController];
        clubController.place = _place;
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
        [self updateFooter];
        
    }
    
    return reusableview;
}


- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath{
    static NSString *identifier = @"Cell";
    ProfileCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:identifier forIndexPath:indexPath];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        if (_place.users.count < minUserCount) {
            if (indexPath.row < _place.users.count)
                [cell.profileAvatar setImage:[UIImage imageNamed:@"avatar_invisible.png"]];
            else
                [cell.profileAvatar setImage:[UIImage imageNamed:@"avatar_empty.png"]];
            cell.friendIcon.hidden = YES;
        }
        else {
            User *user = _place.users[indexPath.row];
            cell.friendIcon.hidden = !user.isFriend;
            
            // transform avatar
            CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
            CLTransformation *transformation = [CLTransformation transformation];
            [transformation setParams: @{@"width": @120, @"height": @120}];
            NSString * avatarUrl  = [cloudinary url: [user.avatar valueForKey:@"public_id"] options:@{@"transformation": transformation}];
            [cell.profileAvatar setImageWithURL:[NSURL URLWithString:avatarUrl] placeholderImage:[UIImage imageNamed:@"Default.png"]];
        }
    });
    return cell;
}

- (void)didCheckin:(User *) user userInfo:(NSObject *)userInfo
{
    //[self hideProgress];
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    [self._manager retrievePlace:self.placeId accessToken:accessToken];
    
    CbButton* checkinButton = (CbButton *) userInfo;
    [checkinButton setSecondState:NSLocalizedString(@"Checkout", nil)];
    [LocationHelper startLocationUpdate:_place];
}

- (void)didCheckout:(User *) user userInfo:(NSObject *)userInfo
{
    //[self hideProgress];
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    [self._manager retrievePlace:self.placeId accessToken:accessToken];
    
    CbButton* checkinButton = (CbButton *) userInfo;
    [checkinButton setMainState:NSLocalizedString(@"Checkin", nil)];
    [LocationHelper stopTimer];
}

- (IBAction)checkinAction:(CbButton *)sender {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    
    CbButton* checkinButton = (CbButton *) sender;
    
    CLLocation *loc = [[CLLocation alloc] initWithLatitude:[_place.lat doubleValue] longitude:[_place.lon doubleValue]];
    
    if (checkinButton.isCheckin) {
        [self showProgress:NO title:NSLocalizedString(@"checking_out", nil)];
        [self._manager checkout:_place.id accessToken:accessToken userInfo:sender];
    } else {
        if([GlobalVars getInstance].MaxCheckinRadius  > (int)[[LocationManagerSingleton sharedSingleton].locationManager.location distanceFromLocation:loc]) {
            [self showProgress:NO title:NSLocalizedString(@"checking_in", nil)];
            [self._manager checkin:_place.id accessToken:accessToken userInfo:sender];
        }
        else {
            [checkinButton setMainState:NSLocalizedString(@"Checkin", nil)];
            
            NSString *message = [NSString stringWithFormat:NSLocalizedString(@"checkin_distance", nil), [GlobalVars getInstance].MaxCheckinRadius];
            [CSNotificationView showInViewController:self
                                           tintColor:[UIColor colorWithRed:153/255.0f green:0/255.0f blue:217/255.0f alpha:1]
                                               image:nil
                                             message:message
                                            duration:kCSNotificationViewDefaultShowDuration];
        }
    }
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



/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
