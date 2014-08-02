//
//  SidebarViewController.m
//  SidebarDemo
//
//  Created by Simon on 29/6/13.
//  Copyright (c) 2013 Appcoda. All rights reserved.
//

#import "SidebarViewController.h"
#import "SWRevealViewController.h"
#import "MenuCell.h"
#import "Cloudinary.h"
#import "Constants.h"
#import "UIImageView+WebCache.h"

@interface SidebarViewController (){
    long unreadMessagesCount;
    long pendingFriendsCount;
}

@property (nonatomic, strong) NSArray *menuItems;
@end

@implementation SidebarViewController

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    self.tableView.backgroundColor = [UIColor colorWithRed:52/256.0 green:3/256.0 blue:69/256.0 alpha:1.0];
    
    _menuItems = @[@"title", @"clubs", @"club_feature", @"messages", @"friends", @"settings", @"share"];
    
   // self.nameLabel.font = [UIFont fontWithName:@"AzoftSans-Bold" size:15];

}

- (void)viewWillAppear:(BOOL)animated
{
    //Pubnub staff
    [PubNub setDelegate:self];
    
    // clear badge value
    [[UIApplication sharedApplication] setApplicationIconBadgeNumber:0];
    
    [self loadData];
    //_ unreadMessages
}

- (void)loadData
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    [self._manager unreadMessages:userId];
}


- (void)pubnubClient:(PubNub *)client didReceiveMessage:(PNMessage *)message {
    PNLog(PNLogGeneralLevel, self, @"PubNub client received message: %@", message);
    
    NSString *msg = [message.message valueForKey:@"msg"];
    NSString *type = [message.message valueForKey:@"type"];
    NSString *user_from = [message.message valueForKey:@"user_from"];
    NSString *user_to = [message.message valueForKey:@"user_to"];
    
    [self loadData];
}

- (void)didUnreadMessages:(UnreadMessages *)unreadMessages
{
    unreadMessagesCount = unreadMessages.countOfUnreadChats;
    pendingFriendsCount = unreadMessages.countOfPendingFriends;
    [self.tableView reloadData];
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    return [self.menuItems count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSString *CellIdentifier = [self.menuItems objectAtIndex:indexPath.row];
    
    MenuCell *cell =  [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
   // cell.backgroundColor = [UIColor clearColor];
    
    // set data for user profile
    if ([CellIdentifier isEqualToString:@"title"]){
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        NSString *userName = [defaults objectForKey:@"userName"];
        NSDictionary *userAvatar = [defaults objectForKey:@"userAvatar"];
        NSString *userGender = [defaults objectForKey:@"userGender"];
        NSString *userAge = [defaults objectForKey:@"userAge"];
        cell.nameLabel.text = userName;
        cell.ganderLabel.text = userGender;
        
        cell.ageLabel.text = @"-";
        if (userAge != nil) {
            cell.ageLabel.text = [NSString stringWithFormat:@"%@, %@",userAge, userGender];
        }
        
        // transform avatar
        CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
        CLTransformation *transformation = [CLTransformation transformation];
        [transformation setParams: @{@"width": @100, @"height": @100, @"crop": @"thumb", @"gravity": @"face"}];
        
        NSString * avatarUrl  = [cloudinary url: [userAvatar valueForKey:@"public_id"] options:@{@"transformation": transformation}];
        
        [cell.avatarImage setImageWithURL:[NSURL URLWithString:avatarUrl] placeholderImage:[UIImage imageNamed:@"Default.png"]];
        
    } else if ([CellIdentifier isEqualToString:@"messages"]) {
        cell.countLabel.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:15];
        cell.countLabel.hidden = (unreadMessagesCount == 0);
        cell.countLabel.text = [NSString stringWithFormat:@"%ld", unreadMessagesCount];
    } else if ([CellIdentifier isEqualToString:@"friends"]) {
        cell.countLabel.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:15];
        cell.countLabel.hidden = (pendingFriendsCount == 0);
        cell.countLabel.text = [NSString stringWithFormat:@"%ld", pendingFriendsCount];
    }
   
    cell.titleLabel.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:15];
    cell.nameLabel.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:16];
    cell.ageLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:12];
    cell.ganderLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:12];
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        return 124;
    } else {
        return 44;
    }
}

- (void)failedWithError:(NSError *)error
{

}


- (void) prepareForSegue: (UIStoryboardSegue *) segue sender: (id) sender
{
    // Set the title of navigation bar by using the menu items
    NSIndexPath *indexPath = [self.tableView indexPathForSelectedRow];
    UINavigationController *destViewController = (UINavigationController*)segue.destinationViewController;
    destViewController.title = NSLocalizedString([_menuItems objectAtIndex:indexPath.row], nil);//[[_menuItems objectAtIndex:indexPath.row] capitalizedString];

    
    if ( [segue isKindOfClass: [SWRevealViewControllerSegue class]] ) {
        SWRevealViewControllerSegue *swSegue = (SWRevealViewControllerSegue*) segue;
        
        swSegue.performBlock = ^(SWRevealViewControllerSegue* rvc_segue, UIViewController* svc, UIViewController* dvc) {
            
            UINavigationController* navController = (UINavigationController*)self.revealViewController.frontViewController;
            [navController setViewControllers: @[dvc] animated: NO ];
            [self.revealViewController setFrontViewPosition: FrontViewPositionLeft animated: YES];
        };
        
    }
    
}

@end
