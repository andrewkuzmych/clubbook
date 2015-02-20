//
//  SidebarViewController.m
//  SidebarDemo
//
//  Created by Simon on 29/6/13.
//  Copyright (c) 2013 Appcoda. All rights reserved.
//

#import <Parse/Parse.h>
#import <QuartzCore/QuartzCore.h>

#import "GAIFields.h"
#import "GAIDictionaryBuilder.h"
#import "GAI.h"

#import "MainMenuViewController.h"
#import "SWRevealViewController.h"
#import "MainMenuCollectionViewCell.h"
#import "PlacesViewController.h"
#import "UserNewsFeedViewController.h"
#import "LocationManagerSingleton.h"


@interface MainMenuViewController (){
    int unreadMessagesCount;
    int pendingFriendsCount;
    int fastCheckinPlaces;
    NSString* accessToken;
    double userLat;
    double userLon;
}

@property (nonatomic, strong) NSArray *menuItems;
@end

@implementation MainMenuViewController

- (void)viewDidLoad
{
    [super viewDidLoad];

    self.view.backgroundColor = [UIColor colorWithRed:52/256.0 green:3/256.0 blue:69/256.0 alpha:1.0];
    [self.backgroundImageView setImage: [UIImage imageNamed:@"menu_background.png"]];
    self.backgroundImageView.contentMode = UIViewContentModeScaleAspectFill;
    
    self.menuCollectionView.delaysContentTouches = NO;
    
    _menuItems = @[@"places", @"usersnearby", @"news", @"messages", @"friends", @"history", @"profile",  @"settings", @"checkin"];
    [self.revealViewController setDelegate:self];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //Google Analytics
    id tracker = [[GAI sharedInstance] defaultTracker];
    [tracker set:kGAIScreenName
           value:@"Sidebar Screen"];
    [tracker send:[[GAIDictionaryBuilder createAppView] build]];
    
    //Pubnub staff
    [PubNub setDelegate:self];
   
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    accessToken = [defaults objectForKey:@"accessToken"];
    CLLocation *userLocation = [LocationManagerSingleton sharedSingleton].locationManager.location;
    userLon = userLocation.coordinate.longitude;
    userLat = userLocation.coordinate.latitude;

    self.revealViewController.panGestureRecognizer.enabled = NO;
    
    [self loadData];
}

- (void)loadData
{
    [self._manager retrieveNotifications:userLat lon:userLon accessToken:accessToken];
}

- (void)pubnubClient:(PubNub *)client didReceiveMessage:(PNMessage *)message
{
    [self loadData];
}

- (void)didReceivedNotifications:(ClubbookNotifications *)notifications
{
    unreadMessagesCount = notifications.countOfUnreadChats;
    pendingFriendsCount = notifications.countOfPendingFriends;
    fastCheckinPlaces = notifications.fastCheckinPlaces;
    
    [self.menuCollectionView reloadData];
}

- (void) prepareForSegue: (UIStoryboardSegue *) segue sender: (id) sender
{    
    // Set the title of navigation bar by using the menu items
    NSIndexPath *indexPath = [[self.menuCollectionView indexPathsForSelectedItems] objectAtIndex:0];
    MainMenuCollectionViewCell* selectedItem = (MainMenuCollectionViewCell*)[self.menuCollectionView cellForItemAtIndexPath:indexPath];
    
    UINavigationController *destViewController = (UINavigationController*)segue.destinationViewController;
    destViewController.title = selectedItem.menuLabel.text;

    if ([segue isKindOfClass: [SWRevealViewControllerSegue class]] ) {
        SWRevealViewControllerSegue *swSegue = (SWRevealViewControllerSegue*) segue;
        
        swSegue.performBlock = ^(SWRevealViewControllerSegue* rvc_segue, UIViewController* svc, UIViewController* dvc) {
            
            UINavigationController* navController = (UINavigationController*)self.revealViewController.frontViewController;
                [navController setViewControllers: @[dvc] animated: NO ];
                [self.revealViewController setFrontViewPosition: FrontViewPositionLeft animated: YES];
        }; 
    }
}

#pragma mark - CollectionView
#pragma mark DataSource

-(NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
{
    return 1;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:      (NSInteger)section
{
    return [_menuItems count];
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    MainMenuCollectionViewCell *cell = (MainMenuCollectionViewCell *)[self.menuCollectionView dequeueReusableCellWithReuseIdentifier:@"MenuCell" forIndexPath:indexPath];
    
    [cell setIconBackground:[UIColor colorWithRed:0.277 green:0.002 blue:0.371 alpha:0.500]];
    cell.icon.layer.cornerRadius = 32;
    cell.icon.layer.borderWidth = 2.0f;
    cell.icon.layer.borderColor = [UIColor colorWithWhite:0.891 alpha:0.600].CGColor;
    [cell.icon.layer setMasksToBounds:YES];
    
    cell.bigNotificationNumber.layer.cornerRadius = 32;
    [cell.bigNotificationNumber.layer setMasksToBounds:YES];
    [cell.bigNotificationNumber setHidden:YES];
    cell.bigNotificationNumber.backgroundColor = cell.notificationNumberLabel.backgroundColor;
    
    
    cell.notificationNumberLabel.layer.cornerRadius = 15;
    [cell.notificationNumberLabel.layer setMasksToBounds:YES];
    
    NSUInteger item = indexPath.item;
    NSString* currentItemId = [_menuItems objectAtIndex:item];
  
    if ([currentItemId isEqualToString:@"places"]) {
        cell.menuLabel.text = @"Places to Go";
    }
    else if ([currentItemId isEqualToString:@"usersnearby"]) {
        cell.menuLabel.text = @"Users Nearby";
    }
    else if ([currentItemId isEqualToString:@"history"]) {
        cell.menuLabel.text = @"History";
    }
    else if ([currentItemId isEqualToString:@"messages"]) {
        if (unreadMessagesCount > 0) {
            [cell.notificationNumberLabel setHidden:NO];
            [cell.notificationNumberLabel setText:[NSString stringWithFormat:@"%d", unreadMessagesCount, nil]];
        }
        else {
            [cell.notificationNumberLabel setHidden:YES];
        }
        cell.menuLabel.text = @"Messages";
    }
    else if ([currentItemId isEqualToString:@"friends"]) {
        if (pendingFriendsCount > 0) {
            [cell.notificationNumberLabel setHidden:NO];
            [cell.notificationNumberLabel setText:[NSString stringWithFormat:@"%d", pendingFriendsCount, nil]];
        }
        else {
            [cell.notificationNumberLabel setHidden:YES];
        }
        cell.menuLabel.text = @"Friends";
    }
    else if ([currentItemId isEqualToString:@"news"]) {
        cell.menuLabel.text = @"News";
    }
    else if ([currentItemId isEqualToString:@"settings"]) {
        cell.menuLabel.text = @"Settings";
    }
    else if ([currentItemId isEqualToString:@"profile"]) {
        cell.menuLabel.text = @"Profile";
    }
    else if ([currentItemId isEqualToString:@"checkin"]) {
        [cell.bigNotificationNumber setHidden:NO];
        NSString* number = [NSString stringWithFormat:@"%d", fastCheckinPlaces];
        [cell.bigNotificationNumber setText:number];
        [cell.icon setBackgroundColor:[UIColor clearColor]];
        [cell.icon.layer setBorderColor:[UIColor whiteColor].CGColor];
        cell.menuLabel.text = @"Check-in/out";
    }
    
    return cell;
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath  {
    MainMenuCollectionViewCell* cell = (MainMenuCollectionViewCell*)[self.menuCollectionView cellForItemAtIndexPath:indexPath];
    [cell highlightIcon];
    NSUInteger selectedItem = indexPath.item;
    NSString* segueId = [_menuItems objectAtIndex:selectedItem];

    [self performSegueWithIdentifier:segueId sender:self];
    
}

-(void)revealController:(SWRevealViewController *)revealController didMoveToPosition:(FrontViewPosition)position {
    if (position == FrontViewPositionRight) {
        [self loadData];
    }
}

@end
