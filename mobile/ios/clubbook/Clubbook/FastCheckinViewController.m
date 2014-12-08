//
//  FastCheckinViewController.m
//  Clubbook
//
//  Created by Andrew on 10/25/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "FastCheckinViewController.h"
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
#import "CSNotificationView.h"

@interface FastCheckinViewController (){
    BOOL isLoaded;
    CbButton *tempButton;
}

@end

@implementation FastCheckinViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.clubTable.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
    
    self.title = NSLocalizedString(@"clubs", nil);
    
    self.clubTable.hidden = NO;
    self.clubTable.dataSource = self;
    self.clubTable.delegate = self;
    
    self.noCheckinsLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:18];
    self.noCheckinsLabel.text = NSLocalizedString(@"noClubCheckins", nil);
    self.noCheckinsLabel.hidden = YES;
    [self loadClub:10 skip:0];
    //[self sortPlaces];
    // Do any additional setup after loading the view.
}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    isLoaded = NO;
    //[LocationManagerSingleton sharedSingleton].delegate = self;
    //[[LocationManagerSingleton sharedSingleton] startLocating];
    
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    
    Place *place = _places[tempButton.tag];
    [self showProgress:NO title:NSLocalizedString(@"checking_in", nil)];
     [self._manager checkin:place.id accessToken:accessToken userInfo:tempButton];
}

/*- (void)didUpdateLocation
{
    //[self yesLocation];
    [self loadClub:10 skip:0];
}*/

/*- (void)didFailUpdateLocation
{
    //[self noLocation];
}
*/

- (void)loadClub:(int)take skip:(int)skip
{

    if (isLoaded) {
        return;
    }
    isLoaded = YES;
    double lat = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.latitude;
    double lng = [LocationManagerSingleton sharedSingleton].locationManager.location.coordinate.longitude;
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    
    [self._manager retrievePlaces:lat lon:lng take:take skip:skip distance:1 accessToken:accessToken];
}

- (void)didReceivePlaces:(NSArray *)places
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        
        _places = [places mutableCopy];
        
        [self.clubTable reloadData];
        
        self.noCheckinsLabel.hidden = _places.count > 0;

        //[self sortPlaces];
        //[self.clubTable reloadData];
    });
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
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
    
    cell.checkinButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:15];
    
    [cell.checkinButton setMainState:NSLocalizedString(@"checkin", nil)];
    
    int disatanceInt = (int)place.distance;
    
    [cell.distanceLabel setText:[LocationHelper convertDistance:disatanceInt]];
 
    [cell.clubAvatar setImageWithURL:[NSURL URLWithString:place.avatar] placeholderImage:[UIImage imageNamed:@"avatar_default.png"]];
    
    BOOL isCheckinHere = [LocationHelper isCheckinHere:place];
    if(isCheckinHere){
        [cell.checkinButton setSecondState:NSLocalizedString(@"checkout", nil)];
    } else {
        [cell.checkinButton setMainState:NSLocalizedString(@"checkin", nil)];
    }
    
    [cell.checkinButton setTag:indexPath.row];
    
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSIndexPath *selectedIndexPath = [self.clubTable indexPathForSelectedRow];
    Place *place = _places[selectedIndexPath.row];
    
    [self pushToClub:place];
    
}


/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

- (void)pushToClub:(Place *)place
{
    UIStoryboard *mainStoryboard = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle: nil];
    ClubUsersViewController *clubController  = [mainStoryboard instantiateViewControllerWithIdentifier:@"club"];
    clubController.place = place;//place.id;
    clubController.hasBack = YES;
    //self.isLoaded = NO;
    // ClubUsersViewController *clubController =  [segue ClubUsersViewController];
    [UIView beginAnimations:@"animation" context:nil];
    [UIView setAnimationDuration:0.5];
    [self.navigationController pushViewController: clubController animated:NO];
    [UIView setAnimationTransition:UIViewAnimationTransitionFlipFromLeft forView:self.navigationController.view cache:NO];
    [UIView commitAnimations];
}


- (IBAction)checkinAction:(id)sender {
 
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    CbButton* checkinButton = (CbButton *) sender;
    Place *place = _places[checkinButton.tag];
    tempButton = checkinButton;
    
    CLLocation *loc = [[CLLocation alloc] initWithLatitude:[place.lat doubleValue] longitude:[place.lon doubleValue]];
    
    if (checkinButton.isCheckin) {
        [self showProgress:NO title:NSLocalizedString(@"checking_out", nil)];
        [self._manager checkout:place.id accessToken:accessToken userInfo:checkinButton];
    } else {
        if([GlobalVars getInstance].MaxCheckinRadius  > (int)[[LocationManagerSingleton sharedSingleton].locationManager.location distanceFromLocation:loc]) {
            
            if([[NSUserDefaults standardUserDefaults] boolForKey:@"checkinInfoDisplayed"] == FALSE)
            {
                [[NSUserDefaults standardUserDefaults] setBool:TRUE forKey:@"checkinInfoDisplayed"];
                [[NSUserDefaults standardUserDefaults] synchronize];
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@""
                                                                message:NSLocalizedString(@"checkinInfo", nil)
                                                               delegate:self
                                                      cancelButtonTitle:NSLocalizedString(@"gotCheckinInfo", nil)
                                                      otherButtonTitles:nil];
                [alert show];
                
            } else {
                [self showProgress:NO title:NSLocalizedString(@"checking_in", nil)];
                [self._manager checkin:place.id accessToken:accessToken userInfo:checkinButton];
            }
        }
        else {
            [checkinButton setMainState:NSLocalizedString(@"checkin", nil)];
            
            NSString *message = NSLocalizedString(@"checkin_distance", nil);
            [CSNotificationView showInViewController:self
                                           tintColor:[UIColor colorWithRed:153/255.0f green:0/255.0f blue:217/255.0f alpha:1]
                                               image:nil
                                             message:message
                                            duration:kCSNotificationViewDefaultShowDuration];
        }
    }

}

- (void)didCheckin:(User *) user userInfo:(NSObject *)userInfo
{
    [self hideProgress];
    CbButton* checkinButton = (CbButton *) userInfo;
    //NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    //NSString *accessToken = [defaults objectForKey:@"accessToken"];
    //[self._manager retrievePlaceUsers:self.place.id accessToken:accessToken];
    
    //[checkinButton setSecondState:NSLocalizedString(@"checkout", nil)];
    
    //NSIndexPath *selectedIndexPath = [self.clubTable indexPathForSelectedRow];
    Place *place = _places[checkinButton.tag];
    [LocationHelper addCheckin:place];
    
    [self pushToClub:place];
}

- (void)didCheckout:(User *) user userInfo:(NSObject *)userInfo
{
    [self hideProgress];
    CbButton* checkinButton = (CbButton *) userInfo;
    //NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    //NSString *accessToken = [defaults objectForKey:@"accessToken"];
    //[self._manager retrievePlaceUsers:self.place.id accessToken:accessToken];
    
   // CbButton* checkinButton = (CbButton *) self.headerView.checkinButton;
    [checkinButton setMainState:NSLocalizedString(@"checkin", nil)];
    [LocationHelper removeCheckin];
}

@end
