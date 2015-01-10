//
//  FriendsViewController.h
//  Clubbook
//
//  Created by Andrew on 7/31/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BaseViewController.h"

@interface FriendsViewController : BaseViewController<UITableViewDelegate, UITableViewDataSource, UIActionSheetDelegate>
@property (weak, nonatomic) IBOutlet UILabel *noFriendsAdded;
@property (weak, nonatomic) IBOutlet UITableView *friendsTable;
@property (weak, nonatomic) IBOutlet UISegmentedControl *segmentControl;
@property (weak, nonatomic) IBOutlet UIView *findFriendsContainer;
- (IBAction)segmentChanged:(id)sender;
- (IBAction)acceptFriendAction:(UIButton *)sender;
- (IBAction)deleteFriendAction:(UIButton *)sender;


@end
