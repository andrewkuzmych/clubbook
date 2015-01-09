//
//  FriendsViewController.m
//  Clubbook
//
//  Created by Andrew on 7/31/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "FriendsViewController.h"
#import "FriendCell.h"
#import "Constants.h"
#import "UIImageView+WebCache.h"
#import "Cloudinary.h"
#import "UserProfileViewController.h"
#import "ProfilePagesViewController.h"

@interface FriendsViewController ()
{
    NSArray *_friends;
    BOOL isFriends;
    BOOL firstFriendLoad;
    int friendsCount;
    int pendingCount;
}

@end

@implementation FriendsViewController

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
    
    self.noFriendsAdded.hidden = YES;
    firstFriendLoad = YES;
    self.friendsTable.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
    // Do any additional setup after loading the view.
    
    NSDictionary *textAttributes = [NSDictionary dictionaryWithObjectsAndKeys:[UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:14], NSFontAttributeName, nil];
    [self.segmentControl setTitleTextAttributes:textAttributes forState:UIControlStateNormal];

    [self.segmentControl setTitle:[NSString stringWithFormat:NSLocalizedString(@"friends", nil)] forSegmentAtIndex:0];
    [self.segmentControl setTitle:[NSString stringWithFormat:NSLocalizedString(@"pending_requests", nil)] forSegmentAtIndex:1];
    [self.segmentControl setTitle:[NSString stringWithFormat:NSLocalizedString(@"add_friends", nil)] forSegmentAtIndex:2];
    
    self.noFriendsAdded.font = [UIFont fontWithName:NSLocalizedString(@"fontBold", nil) size:18.0];
    self.noFriendsAdded.text =  NSLocalizedString(@"noFriendsAdded", nil);
    
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //Google Analytics
    self.screenName = @"Friends Screen";
    
    if (self.segmentControl.selectedSegmentIndex == 0) {
        [self loadFriends];
    } else if (self.segmentControl.selectedSegmentIndex == 1)  {
        [self loadPendingFriends];
    }
}

- (void) loadFriends
{
    isFriends = YES;
    if (_friends.count == 0) {
        [self showProgress:NO title:nil];
    }
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    [self._manager retrieveFriends:userId accessToken:accessToken];
}

- (void) loadPendingFriends
{
    isFriends = NO;
    if (_friends.count == 0) {
        [self showProgress:NO title:nil];
    }
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    [self._manager retrievePendingFriends:userId accessToken:accessToken];
}

- (void)didRetrieveFriends:(FriendsResult *)friendsResult
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        _friends = friendsResult.friends;
        friendsCount = friendsResult.countOfFriends;
        pendingCount = friendsResult.countOfPendings;
        
        [self updateFriendsPendingsCount:friendsCount pendingCountParam:pendingCount];
        
        self.noFriendsAdded.hidden = (_friends.count > 0);
        self.noFriendsAdded.text =  NSLocalizedString(@"noFriendsAdded", nil);
        self.friendsTable.hidden = NO;
        self.friendsTable.dataSource = self;
        self.friendsTable.delegate = self;
        [self.friendsTable reloadData];
        
        // if no friends -> go to add friend tab
        if (firstFriendLoad) {
            firstFriendLoad= NO;
            self.noFriendsAdded.hidden = YES;
            if (friendsCount == 0 && pendingCount == 0) {
                [self.segmentControl setSelectedSegmentIndex:2];
                self.findFriendsContainer.hidden = NO;
            } else if (friendsCount == 0 && pendingCount > 0) {
                [self.segmentControl setSelectedSegmentIndex:1];
                 [self loadPendingFriends];
            }

        }
    });
}

- (void)didRetrievePendingFriends:(FriendsResult *)friendsResult
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        
        _friends = friendsResult.friends;
        
        friendsCount = friendsResult.countOfFriends;
        pendingCount = friendsResult.countOfPendings;
        
        [self updateFriendsPendingsCount:friendsCount pendingCountParam:pendingCount];
        
        self.noFriendsAdded.hidden = (_friends.count > 0);
        self.noFriendsAdded.text =  NSLocalizedString(@"noRequestsAdded", nil);
        self.friendsTable.hidden = NO;
        self.friendsTable.dataSource = self;
        self.friendsTable.delegate = self;
        [self.friendsTable reloadData];
    });

}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
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
    
    cell.acceptButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:12];
    [cell.acceptButton setTitle:NSLocalizedString(@"accept", nil) forState:UIControlStateNormal];
    
    cell.deleteButton.titleLabel.font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:12];
    [cell.deleteButton setTitle:NSLocalizedString(@"remove", nil) forState:UIControlStateNormal];
    
    //Conversation *conv = [chat.conversations objectAtIndex:0];
    if (user.currentCheckinClubName != nil) {
        [cell.friendCheckin setText: [NSString stringWithFormat:@"%@ %@", NSLocalizedString(@"checked_in", nil), user.currentCheckinClubName]];
        cell.friendCheckin.textColor = [UIColor colorWithRed:(64/255.0) green:(209/255.0) blue:(18/255.0) alpha:1.0];
    } else{
        [cell.friendCheckin setText: NSLocalizedString(@"not_checked_in", nil)];
        cell.friendCheckin.textColor = [UIColor colorWithRed:(98/255.0) green:(98/255.0) blue:(98/255.0) alpha:1.0];
    }
    
    cell.actionView.hidden = isFriends;
    cell.nextImage.hidden = !isFriends;
    
    [cell.acceptButton setTag:indexPath.row];
    [cell.deleteButton setTag:indexPath.row];

    // transform avatar
    CLCloudinary *cloudinary = [[CLCloudinary alloc] initWithUrl: Constants.Cloudinary];
    CLTransformation *transformation = [CLTransformation transformation];
    [transformation setParams: @{@"width": @100, @"height": @100, @"crop": @"thumb", @"gravity": @"face"}];
    
    NSString * avatarUrl  = [cloudinary url: [user.avatar valueForKey:@"public_id"] options:@{@"transformation": transformation}];
    
    [cell.friendAvatar sd_setImageWithURL:[NSURL URLWithString:avatarUrl] placeholderImage:[UIImage imageNamed:@"avatar_empty"]];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSIndexPath *selectedIndexPath = [self.friendsTable indexPathForSelectedRow];
    User *friend = _friends[selectedIndexPath.row];
    [self performSegueWithIdentifier: @"onUsers" sender: friend];
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(User *)sender
{
    if([[segue identifier] isEqualToString:@"onUser"]){
        UserProfileViewController *userController =  [segue destinationViewController];
        //userController.userId = sender.id;
        userController.user = sender;
        userController.clubCheckinName = sender.currentCheckinClubName;
    }
    
    if([[segue identifier] isEqualToString:@"onUsers"]){
        ProfilePagesViewController *profilePagesViewController =  [segue destinationViewController];
        //userController.userId = sender.id;
        NSIndexPath *selectedIndexPath = [self.friendsTable indexPathForSelectedRow];
        profilePagesViewController.index = selectedIndexPath.row;
        profilePagesViewController.profiles = _friends;
        //profilePagesViewController.clubCheckinName = sender.currentCheckinClubName;
    }
}

- (void)updateFriendsPendingsCount:(int) friendsCountParam pendingCountParam:(int)pendingCountParam
{
    [self.segmentControl setTitle:[NSString stringWithFormat:@"%@ (%d)", NSLocalizedString(@"friends", nil), friendsCountParam] forSegmentAtIndex:0];
    [self.segmentControl setTitle:[NSString stringWithFormat:@"%@ (%d)", NSLocalizedString(@"pending_requests", nil), pendingCountParam] forSegmentAtIndex:1];
}

- (void)didConfirmFriend:(User *)user
{
    [self hideProgress];
    [self loadPendingFriends];
    [self updateFriendsPendingsCount:friendsCount pendingCountParam:pendingCount];
}

- (void)didRemoveFriendRequest:(User *)user
{
    [self hideProgress];
    [self loadPendingFriends];
}

- (IBAction)segmentChanged:(id)sender {
    self.findFriendsContainer.hidden = YES;
    if([sender selectedSegmentIndex] == 0){
        [self loadFriends];
        
    }
    else if([sender selectedSegmentIndex] == 1){
        [self loadPendingFriends];
    }else if([sender selectedSegmentIndex] == 2){
        self.findFriendsContainer.hidden = NO;
    }
    
}

- (void)actionSheet:(UIActionSheet *)popup clickedButtonAtIndex:(NSInteger)buttonIndex
{
    switch (buttonIndex) {
        case 0:
        {
            // click yes - delete user friend request
            NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
            NSString *userId = [defaults objectForKey:@"userId"];
            NSString *accessToken = [defaults objectForKey:@"accessToken"];
            
            User *user = _friends[popup.tag];
            [self._manager removeFriendRequest:userId friendId:user.id accessToken:accessToken];
        }
        case 1:
            // click cancel
            break;
        default:
            break;
    }
    
}

- (IBAction)acceptFriendAction:(UIButton *)sender {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    NSString *accessToken = [defaults objectForKey:@"accessToken"];
    
    User *user = _friends[sender.tag];
    [self._manager confirmFriendRequest:userId friendId:user.id accessToken:accessToken];
}

- (IBAction)deleteFriendAction:(UIButton *)sender {
    UIActionSheet *popup = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"youSure", nil) delegate:self cancelButtonTitle:NSLocalizedString(@"cancel", nil) destructiveButtonTitle:nil otherButtonTitles:
                            NSLocalizedString(@"yes", nil),
                            nil];
    [popup setTag:sender.tag];
    [popup showInView:[UIApplication sharedApplication].keyWindow];
    
}


@end
