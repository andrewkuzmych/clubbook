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
#import "UserViewController.h"

@interface FriendsViewController ()
{
    NSArray *_friends;
    BOOL isFriends;
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
    self.friendsTable.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
    // Do any additional setup after loading the view.
    
    NSDictionary *textAttributes = [NSDictionary dictionaryWithObjectsAndKeys:[UIFont fontWithName:@"TitilliumWeb-Regular" size:12], UITextAttributeFont, nil];
    [self.segmentControl setTitleTextAttributes:textAttributes forState:UIControlStateNormal];

    [self.segmentControl setTitle:[NSString stringWithFormat:NSLocalizedString(@"friends", nil)] forSegmentAtIndex:0];
    [self.segmentControl setTitle:[NSString stringWithFormat:NSLocalizedString(@"pending_requests", nil)] forSegmentAtIndex:1];
}

- (void)viewWillAppear:(BOOL)animated
{

    if (self.segmentControl.selectedSegmentIndex == 0) {
        [self loadFriends];
    } else {
        [self loadPendingFriends];
    }
}

- (void) loadFriends
{
    isFriends = YES;
    [self showProgress:NO title:nil];
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    [self._manager retrieveFriends:userId];
}

- (void) loadPendingFriends
{
    isFriends = NO;
    [self showProgress:NO title:nil];
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *userId = [defaults objectForKey:@"userId"];
    [self._manager retrievePendingFriends:userId];
}

- (void)didRetrieveFriends:(NSArray *)friends
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        _friends = friends;
        
        self.friendsTable.hidden = NO;
        self.friendsTable.dataSource = self;
        self.friendsTable.delegate = self;
        [self.friendsTable reloadData];
    });
}

- (void)didRetrievePendingFriends:(NSArray *)friends
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self hideProgress];
        _friends = friends;
        
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
    
    cell.friendName.font = [UIFont fontWithName:@"TitilliumWeb-Bold" size:17];
    [cell.friendName setText:user.name];
    
    cell.friendCheckin.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:12];
    
    cell.acceptButton.titleLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:12];
    [cell.acceptButton setTitle:NSLocalizedString(@"accept", nil) forState:UIControlStateNormal];
    
    cell.deleteButton.titleLabel.font = [UIFont fontWithName:@"TitilliumWeb-Regular" size:12];
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
    
    [cell.friendAvatar setImageWithURL:[NSURL URLWithString:avatarUrl] placeholderImage:[UIImage imageNamed:@"Default.png"]];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSIndexPath *selectedIndexPath = [self.friendsTable indexPathForSelectedRow];
    User *friend = _friends[selectedIndexPath.row];
    [self performSegueWithIdentifier: @"onUser" sender: friend];
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(User *)sender
{
    if([[segue identifier] isEqualToString:@"onUser"]){
        UserViewController *userController =  [segue destinationViewController];
        userController.userId = sender.id;
        userController.clubCheckinName = sender.currentCheckinClubName;
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

- (void)didConfirmFriend:(User *)user
{
    [self hideProgress];
    [self loadPendingFriends];
}

- (void)didRemoveFriendRequest:(User *)user
{
    [self hideProgress];
    [self loadPendingFriends];
}


- (IBAction)segmentChanged:(id)sender {
    if([sender selectedSegmentIndex] == 0){
        [self loadFriends];
        
    }
    else if([sender selectedSegmentIndex] == 1){
        [self loadPendingFriends];
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
            
            User *user = _friends[popup.tag];
            [self._manager removeFriendRequest:userId friendId:user.id];
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
    
    User *user = _friends[sender.tag];
    [self._manager confirmFriendRequest:userId friendId:user.id];
}

- (IBAction)deleteFriendAction:(UIButton *)sender {
    UIActionSheet *popup = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"youSure", nil) delegate:self cancelButtonTitle:NSLocalizedString(@"cancel", nil) destructiveButtonTitle:nil otherButtonTitles:
                            NSLocalizedString(@"yes", nil),
                            nil];
    [popup setTag:sender.tag];
    [popup showInView:[UIApplication sharedApplication].keyWindow];
    
}


@end
