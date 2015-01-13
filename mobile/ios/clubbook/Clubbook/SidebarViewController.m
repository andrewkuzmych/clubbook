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

#import "SidebarViewController.h"
#import "SWRevealViewController.h"
#import "MenuCell.h"
#import "Cloudinary.h"
#import "Constants.h"
#import "UIImageView+WebCache.h"
#import "LocationHelper.h"
#import "ClubUsersViewController.h"
#import "GlobalVars.h"
#import "MainMenuCollectionViewCell.h"
#import "MainViewController.h"



@interface SidebarViewController (){
    int unreadMessagesCount;
    int pendingFriendsCount;
    int fastCheckinPlaces;
}

@property (nonatomic, strong) NSArray *menuItems;
@end

@implementation SidebarViewController

- (void)viewDidLoad
{
    [super viewDidLoad];

    self.view.backgroundColor = [UIColor colorWithRed:52/256.0 green:3/256.0 blue:69/256.0 alpha:1.0];
    [self.backgroundImageView setImage: [UIImage imageNamed:@"menu_background.png"]];
    self.backgroundImageView.contentMode = UIViewContentModeScaleAspectFill;
    
    self.menuCollectionView.delaysContentTouches = NO;
    
    _menuItems = @[@"goingout", @"usersnearby", @"yesterday", @"messages", @"friends", @"settings", @"profile", @"fastcheckin"];
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
   
    [self loadData];
}

- (void)loadData
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];

    [self.menuCollectionView reloadData];
    
    [self._manager unreadMessages:accessToken];
}

- (void)pubnubClient:(PubNub *)client didReceiveMessage:(PNMessage *)message
{
    //PNLog(PNLogGeneralLevel, self, @"PubNub client received message: %@", message);
    
    /*NSString *msg = [message.message valueForKey:@"msg"];
    NSString *type = [message.message valueForKey:@"type"];
    NSString *user_from = [message.message valueForKey:@"user_from"];
    NSString *user_to = [message.message valueForKey:@"user_to"];*/
    
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
    if([[segue identifier] isEqualToString:@"onClub"]){
        ClubUsersViewController *clubController = [segue destinationViewController];
        Place *place = (Place*) sender;
        clubController.hasBack = NO;
        clubController.place = place;
    }
 
    if([[segue identifier] isEqualToString:@"yesterday"]){
        MainViewController *mainController = [segue destinationViewController];
        mainController.showYesterdayPlaces = YES;
    }
    
    // Set the title of navigation bar by using the menu items
    NSIndexPath *indexPath = [[self.menuCollectionView indexPathsForSelectedItems] objectAtIndex:0];
    MainMenuCollectionViewCell* selectedItem = (MainMenuCollectionViewCell*)[self.menuCollectionView cellForItemAtIndexPath:indexPath];
    
    UINavigationController *destViewController = (UINavigationController*)segue.destinationViewController;
    destViewController.title = selectedItem.menuLabel.text;

    if ( [segue isKindOfClass: [SWRevealViewControllerSegue class]] ) {
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
    cell.bigNotificationNumber.backgroundColor = cell.notificationNumberLabel.backgroundColor;
    
    
    cell.notificationNumberLabel.layer.cornerRadius = 15;
    [cell.notificationNumberLabel.layer setMasksToBounds:YES];
    
    NSUInteger item = indexPath.item;
    NSString* currentItemId = [_menuItems objectAtIndex:item];
  
    if ([currentItemId isEqualToString:@"goingout"]) {
        cell.menuLabel.text = @"Going Out";
    }
    else if ([currentItemId isEqualToString:@"usersnearby"]) {
        cell.menuLabel.text = @"Users Nearby";
    }
    else if ([currentItemId isEqualToString:@"yesterday"]) {
        cell.menuLabel.text = @"Yesterday";
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
    else if ([currentItemId isEqualToString:@"settings"]) {
        cell.menuLabel.text = @"Settings";
    }
    else if ([currentItemId isEqualToString:@"profile"]) {
        cell.menuLabel.text = @"Profile";
    }
    else if ([currentItemId isEqualToString:@"fastcheckin"]) {
        [cell.bigNotificationNumber setHidden:NO];
        NSString* number = [NSString stringWithFormat:@"%d", fastCheckinPlaces];
        [cell.bigNotificationNumber setText:number];
        [cell.icon setBackgroundColor:[UIColor clearColor]];
        [cell.icon.layer setBorderColor:[UIColor whiteColor].CGColor];
        cell.menuLabel.text = @"Fast Checkin";
    }
    
    return cell;
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath  {
    MainMenuCollectionViewCell* cell = (MainMenuCollectionViewCell*)[self.menuCollectionView cellForItemAtIndexPath:indexPath];
    [cell highlightIcon];
    NSUInteger selectedItem = indexPath.item;
    NSString* segueId = [_menuItems objectAtIndex:selectedItem];
    NSString* messegesID = @"messages";
    if ([segueId isEqualToString:messegesID]) {
        unreadMessagesCount = 0;
    }
    [self performSegueWithIdentifier:segueId sender:self];
    
}

@end
