//
//  UserViewController.h
//  Clubbook
//
//  Created by Andrew on 7/2/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BaseTableViewController.h"
#import "CbLabel.h"
#import "AddFriendButton.h"

@interface UserViewController : BaseTableViewController<UIScrollViewDelegate>
@property (strong, nonatomic) NSString *userId;
@property (weak, nonatomic) IBOutlet UIScrollView *imageScrollView;
@property (weak, nonatomic) IBOutlet UIPageControl *imagePageView;
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet AddFriendButton *addFriendButton;
@property (weak, nonatomic) IBOutlet UIButton *connectButton;
@property (weak, nonatomic) IBOutlet UILabel *ageLabel;
@property (weak, nonatomic) IBOutlet UILabel *countryLabel;
@property (weak, nonatomic) IBOutlet UILabel *aboutMeTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *aboutMeLabel;
- (IBAction)chatAction:(id)sender;
- (IBAction)addFriendAction:(AddFriendButton *)sender;

@end
