//
//  FindMoreFriendsViewController.h
//  Clubbook
//
//  Created by Andrew on 10/8/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "BaseViewController.h"

@interface FindMoreFriendsViewController : BaseViewController<UITableViewDelegate, UITableViewDataSource>
@property (weak, nonatomic) IBOutlet UIButton *inviteFriendsButton;
@property (weak, nonatomic) IBOutlet UIButton *connectFacebookButton;
@property (weak, nonatomic) IBOutlet UILabel *orLabel;
@property (weak, nonatomic) IBOutlet UILabel *findFriendsDescLabel;
@property (weak, nonatomic) IBOutlet UITableView *friendsTableView;
- (IBAction)connectFbAction:(id)sender;
- (IBAction)inviteFriendsAction:(id)sender;
- (IBAction)addFriendAction:(id)sender;

@end
