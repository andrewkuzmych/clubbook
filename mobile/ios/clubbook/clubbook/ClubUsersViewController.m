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
    // Do any additional setup after loading the view.
    minUserCount = 10;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *userId = [defaults objectForKey:@"userId"];
        
        [self._manager retrievePlace:self.placeId userId:userId];
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
        self.headerView.checkinButton.titleLabel.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:14.0];
        self.headerView.checkinCountLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:14.0];
        self.headerView.friendsCountLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:14.0];
        self.headerView.clubNameText.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:17.0];
        self.headerView.openTodayLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:12.0];
        self.headerView.workingHoursLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:10.0];
        self.headerView.clubInfoButton.titleLabel.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:15.0];
        self.headerView.clubDistanceText.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:14.0];
        self.clubFooterView.clubUsersLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:14.0];
        self.clubFooterView.checkinLabel.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:18.0];
        self.clubFooterView.usersLeftToCheckinLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:15.0];
        
        // GlobalVars *globalVars=[GlobalVars getInstance];
        CLLocation *loc = [[CLLocation alloc] initWithLatitude:[place.lat doubleValue] longitude:[place.lon doubleValue]];
        
        CLLocationDistance distance = [[LocationManagerSingleton sharedSingleton].locationManager.location distanceFromLocation:loc];
        place.disatance = distance;
        
        self.headerView.clubNameText.text = place.title;
       // self.clubFooterView.clubAddressText.text = place.address;
        
        UITapGestureRecognizer *tgr = [[UITapGestureRecognizer alloc]
                                       initWithTarget:self action:@selector(handleGesture:)];
        tgr.numberOfTapsRequired = 1;
        tgr.numberOfTouchesRequired = 1;
        [self.headerView.clubMapView addGestureRecognizer:tgr];
        self.headerView.clubMapView.scrollEnabled = NO;
        self.headerView.clubMapView.zoomEnabled = NO;

        MKCoordinateRegion newRegion;
        newRegion.center.latitude = [place.lat doubleValue];
        newRegion.center.longitude = [place.lon doubleValue];
        newRegion.span.latitudeDelta =0.00523;
        newRegion.span.longitudeDelta=0.00523;
        
        
        CLLocationCoordinate2D coordinate;
        coordinate.latitude = [place.lat doubleValue];
        coordinate.longitude = [place.lon doubleValue];
        
        MKPointAnnotation * annotation = [[MKPointAnnotation alloc] init];
        [annotation setCoordinate:coordinate];
        
        [self.headerView.clubMapView removeAnnotations:self.headerView.clubMapView.annotations];
        [self.headerView.clubMapView addAnnotation:annotation];
        [self.headerView.clubMapView setRegion:newRegion];
        
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
            [self.headerView.checkinButton setSecondState:NSLocalizedString(@"Checkout", nil)];
        } else {
            [self.headerView.checkinButton setMainState:NSLocalizedString(@"Checkin", nil)];
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

- (void)handleGesture:(UIGestureRecognizer *)gestureRecognizer
{
    if (gestureRecognizer.state != UIGestureRecognizerStateEnded)
        return;
    
    MKPlacemark* place = [[MKPlacemark alloc] initWithCoordinate: CLLocationCoordinate2DMake([_place.lat doubleValue], [_place.lon doubleValue]) addressDictionary: nil];
    MKMapItem* destination = [[MKMapItem alloc] initWithPlacemark: place];
    destination.name = _place.title;
    NSArray* items = [[NSArray alloc] initWithObjects: destination, nil];
    NSDictionary* options = [[NSDictionary alloc] initWithObjectsAndKeys:
                             MKLaunchOptionsDirectionsModeWalking,
                             MKLaunchOptionsDirectionsModeKey, nil];
    [MKMapItem openMapsWithItems: items launchOptions: options];
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
        NSString * text = [NSString stringWithFormat:NSLocalizedString(@"usersLeftToChecdkin", nil),(minUserCount - _place.users.count)];
        
        [CSNotificationView showInViewController:self
                                       tintColor:[UIColor colorWithRed:0.000 green:0.6 blue:1.000 alpha:1]
                                           image:nil
                                         message:text
                                        duration:kCSNotificationViewDefaultShowDuration];
    } else if (!isCheckinHere) {
        User *user = _place.users[indexPath.row];
        if(!user.isFriend)
            [CSNotificationView showInViewController:self
                                           tintColor:[UIColor colorWithRed:0.000 green:0.6 blue:1.000 alpha:1]
                                               image:nil
                                             message:NSLocalizedString(@"needToCheckinFirst", nil)
                                            duration:kCSNotificationViewDefaultShowDuration];
        else
            [self performSegueWithIdentifier: @"onUser" sender: indexPath];

    } else {
        [self performSegueWithIdentifier: @"onUser" sender: indexPath];
    }
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(NSIndexPath *)indexPath
{
    if([[segue identifier] isEqualToString:@"onUser"]){
        UserViewController *userController =  [segue destinationViewController];
        User *user = _place.users[indexPath.row];
        userController.userId = user.id;
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
                [cell.profileAvatar setImage:[UIImage imageNamed:@"emptyUserH.png"]];
            else
                [cell.profileAvatar setImage:[UIImage imageNamed:@"emptyUser.png"]];
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
    NSString *userId = [defaults objectForKey:@"userId"];
    [self._manager retrievePlace:self.placeId userId:userId];
    
    CbButton* checkinButton = (CbButton *) userInfo;
    [checkinButton setSecondState:NSLocalizedString(@"Checkout", nil)];
    [LocationHelper startLocationUpdate:_place];
}

- (void)didCheckout:(User *) user userInfo:(NSObject *)userInfo
{
    //[self hideProgress];
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    [self._manager retrievePlace:self.placeId userId:userId];
    
    CbButton* checkinButton = (CbButton *) userInfo;
    [checkinButton setMainState:NSLocalizedString(@"Checkin", nil)];
    [LocationHelper stopTimer];
}

- (IBAction)checkinAction:(CbButton *)sender {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    
    CbButton* checkinButton = (CbButton *) sender;
    
    CLLocation *loc = [[CLLocation alloc] initWithLatitude:[_place.lat doubleValue] longitude:[_place.lon doubleValue]];
    
    if (checkinButton.isCheckin) {
        [self showProgress:NO title:NSLocalizedString(@"checking_out", nil)];
        [self._manager checkout:_place.id userId:userId userInfo:sender];
    } else {
        if(Constants.MaxCheckinRadius > (int)[[LocationManagerSingleton sharedSingleton].locationManager.location distanceFromLocation:loc]) {
            [self showProgress:NO title:NSLocalizedString(@"checking_in", nil)];
            [self._manager checkin:_place.id userId:userId userInfo:sender];
        }
        else {
            [checkinButton setMainState:NSLocalizedString(@"Checkin", nil)];
        }
    }
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
