//
//  FindMoreFriendsViewController.m
//  Clubbook
//
//  Created by Andrew on 10/8/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "FindMoreFriendsViewController.h"
#import "AppDelegate.h"
#import "FriendCell.h"
#import "Constants.h"
#import "UIImageView+WebCache.h"
#import "Cloudinary.h"
#import "UserViewController.h"
#import "ProfilePagesViewController.h"

@interface FindMoreFriendsViewController (){
    NSArray *_friends;
    NSMutableArray *_friendIds;
}
@end

@implementation FindMoreFriendsViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.friendsTableView.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
    
    self.orLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:12];
    self.orLabel.text = NSLocalizedString(@"or", nil);
    self.findFriendsDescLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:14];
    self.findFriendsDescLabel.text = NSLocalizedString(@"whoIsOn", nil);
    
    self.inviteFriendsButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:14];
    [self.inviteFriendsButton setTitle:NSLocalizedString(@"invateFbFriends", nil) forState:UIControlStateNormal];
   
    self.connectFacebookButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:14];
    [self.connectFacebookButton setTitle:NSLocalizedString(@"findFbFriends", nil) forState:UIControlStateNormal];
    
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBar.translucent = NO;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBar.translucent = NO;
}

- (void)loadFriends:(NSMutableArray *)friendIds {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    
    [self._manager findFbFriends:friendIds accessToken:accessToken];
}

- (void)loadFriendsFromFb {
    [FBRequestConnection startWithGraphPath:@"/me/friends"
                                 parameters:nil
                                 HTTPMethod:@"GET"
                          completionHandler:^(
                                              FBRequestConnection *connection,
                                              id result,
                                              NSError *error
                                              ) {
                              if (!error) {
                                  // Get the result
                                  NSArray *resultData = result[@"data"];
                                  // Check we have data
                                  if ([resultData count] > 0) {
                                      // Loop through the friends returned
                                      NSMutableArray *friendIds = [[NSMutableArray alloc] init];
                                      for (NSDictionary *friendObject in resultData)
                                      {
                                          NSString *friendId = friendObject[@"id"];
                                          [friendIds addObject:friendId];
                                          //  - (void)findFbFriends:(NSArray *) fb_ids accessToken:(NSString *) accessToken;
                                      }
                                      
                                      _friendIds = friendIds;
                                      [self loadFriends:friendIds];
                                      
                                  }
                              }
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

- (IBAction)connectFbAction:(id)sender {
    
    if (FBSession.activeSession.isOpen) {
       [self loadFriendsFromFb];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{
            NSArray *permissions = [[NSArray alloc] initWithObjects: @"public_profile", @"user_friends", nil];
            [FBSession openActiveSessionWithReadPermissions:permissions
                                               allowLoginUI:YES
                                          completionHandler:^(FBSession *session,
                                                              FBSessionState status,
                                                              NSError *error)
             {
                 if (FB_ISSESSIONOPENWITHSTATE(status)) {
                     [self loadFriendsFromFb];
                 }
             }];
        });
    }
}

- (IBAction)inviteFriendsAction:(id)sender {
    AppDelegate *appDelegate =
    (AppDelegate *) [[UIApplication sharedApplication] delegate];
    if (FBSession.activeSession.isOpen) {
        //[appDelegate sendRequest];
        [self sendRequest];

    } else {
        dispatch_async(dispatch_get_main_queue(), ^{
        NSArray *permissions = [[NSArray alloc] initWithObjects: @"public_profile", @"user_friends", nil];
        [FBSession openActiveSessionWithReadPermissions:permissions
                                           allowLoginUI:YES
                                      completionHandler:^(FBSession *session,
                                                          FBSessionState status,
                                                          NSError *error)
         {
             if (FB_ISSESSIONOPENWITHSTATE(status)) {
                 [self sendRequest];
             }
         }];
        });
    }

}

- (IBAction)addFriendAction:(AddFriendButton *)sender {
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    
    User *user = _friends[sender.tag];
    
    if ([sender.friendState isEqualToString:NSLocalizedString(@"noneFriend", nil)]) {
        [self showProgress:NO title:NSLocalizedString(@"processing", nil)];
        [self._manager sendFriendReguest:userId friendId:user.id accessToken:accessToken];
    } else if ([sender.friendState isEqualToString:NSLocalizedString(@"receiveRequest", nil)]){
        [self showProgress:NO title:NSLocalizedString(@"processing", nil)];
        [self._manager confirmFriendRequest:userId friendId:user.id accessToken:accessToken];
    } else if ([sender.friendState isEqualToString:NSLocalizedString(@"friend", nil)]){
        UIActionSheet *popup = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"youSureUnfriend", nil) delegate:self cancelButtonTitle:NSLocalizedString(@"cancel", nil) destructiveButtonTitle:nil otherButtonTitles:
                                NSLocalizedString(@"yes", nil),
                                nil];
        [popup showInView:[UIApplication sharedApplication].keyWindow];
    }
}

- (void)didSendFriend:(User *)user
{
    [self hideProgress];
    [self loadFriends:_friendIds];
}

- (void)didConfirmFriend:(User *)user
{
    [self hideProgress];
    [self loadFriends:_friendIds];
}

- (void)didRemoveFriend:(User *)user
{
    [self hideProgress];
    [self loadFriends:_friendIds];
}


- (void)sendRequest {
    NSError *error;
    
    // Display the requests dialog
    [FBWebDialogs
     presentRequestsDialogModallyWithSession:nil
     message:@"Add me as your friend on Clubbook? If you download the app we can interact with other users in a fun and easy way, and find our friends in the Clubs when we go out."
     title:nil
     parameters:nil //params
     handler:^(FBWebDialogResult result, NSURL *resultURL, NSError *error) {
         if (error) {
             // Error launching the dialog or sending the request.
             NSLog(@"Error sending request.");
         } else {
             if (result == FBWebDialogResultDialogNotCompleted) {
                 // User clicked the "x" icon
                 NSLog(@"User canceled request.");
             } else {
                 // Handle the send request callback
                 NSDictionary *urlParams = [self parseURLParams:[resultURL query]];
                 if (![urlParams valueForKey:@"request"]) {
                     // User clicked the Cancel button
                     NSLog(@"User canceled request.");
                 } else {
                     // User clicked the Send button
                     NSString *requestID = [urlParams valueForKey:@"request"];
                     NSMutableArray *fbIds= [NSMutableArray array];
                     for(NSString *key in urlParams)
                     {
                         if (![key isEqualToString: @"request"]) {
                             [fbIds addObject:urlParams[key]];

                         }
                     }
                     NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
                     NSString *userId = [defaults objectForKey:@"userId"];
                     NSString *accessToken = [defaults objectForKey:@"accessToken"];
                     
                     [self._manager inviteFbFriends:userId fb_ids:fbIds accessToken:accessToken];
                         
                     NSLog(@"Request ID: %@", requestID);
                 }
             }
         }
     }];
}

- (void)didInviteFbFriends:(NSString *)result
{

}

- (void)didFindFbFriends:(NSArray *)users
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        _friends = users;
        self.friendsTableView.dataSource = self;
        self.friendsTableView.delegate = self;
        [self.friendsTableView reloadData];
    });
}


- (NSDictionary*)parseURLParams:(NSString *)query {
    NSArray *pairs = [query componentsSeparatedByString:@"&"];
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    for (NSString *pair in pairs) {
        NSArray *kv = [pair componentsSeparatedByString:@"="];
        NSString *val =
        [kv[1] stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        params[kv[0]] = val;
        NSLog(@"%@", kv[0]);
    }
    return params;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _friends.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    FriendCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell" forIndexPath:indexPath];
    User *user = _friends[indexPath.row];
    
    cell.friendName.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:17];
    [cell.friendName setText:user.name];
    
    cell.friendCheckin.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:11];
    cell.addFriendButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:14.0];
    
    
    //Conversation *conv = [chat.conversations objectAtIndex:0];
    if (user.currentCheckinClubName != nil) {
        [cell.friendCheckin setText: [NSString stringWithFormat:@"%@ %@", NSLocalizedString(@"checked_in", nil), user.currentCheckinClubName]];
        cell.friendCheckin.textColor = [UIColor colorWithRed:(64/255.0) green:(209/255.0) blue:(18/255.0) alpha:1.0];
    } else{
        [cell.friendCheckin setText: NSLocalizedString(@"not_checked_in", nil)];
        cell.friendCheckin.textColor = [UIColor colorWithRed:(98/255.0) green:(98/255.0) blue:(98/255.0) alpha:1.0];
    }
    
    cell.actionView.hidden = NO;
    
    [cell.addFriendButton setTag:indexPath.row];
    // transform avatar
    CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
    CLTransformation *transformation = [CLTransformation transformation];
    [transformation setParams: @{@"width": @100, @"height": @100, @"crop": @"thumb", @"gravity": @"face"}];
    
    NSString * avatarUrl  = [cloudinary url: [user.avatar valueForKey:@"public_id"] options:@{@"transformation": transformation}];
    
    [cell.friendAvatar setImageWithURL:[NSURL URLWithString:avatarUrl] placeholderImage:[UIImage imageNamed:@"avatar_empty"]];
    
    [cell.addFriendButton setButtonState:user.friend_status];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSIndexPath *selectedIndexPath = [self.friendsTableView indexPathForSelectedRow];
    User *friend = _friends[selectedIndexPath.row];
    [self performSegueWithIdentifier: @"onUsers" sender: friend];
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(User *)sender
{
    if([[segue identifier] isEqualToString:@"onUsers"]){
        //UserViewController *userController =  [segue destinationViewController];
        ProfilePagesViewController *profilePagesViewController =  [segue destinationViewController];
        profilePagesViewController.profiles = _friends;
        NSIndexPath *selectedIndexPath = [self.friendsTableView indexPathForSelectedRow];
        profilePagesViewController.index = selectedIndexPath.row;
        //userController.user = sender;
        //userController.clubCheckinName = sender.currentCheckinClubName;
    }
}


@end
