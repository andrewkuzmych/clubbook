//
//  UserProfileViewController.h
//  Clubbook
//
//  Created by Andrew on 10/13/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "BaseTableViewController.h"
#import <UIKit/UIKit.h>
#import "CbLabel.h"
#import "AddFriendButton.h"

@interface UserProfileViewController : BaseTableViewController<UIScrollViewDelegate, UIActionSheetDelegate>
//@property (strong, nonatomic) NSString *userId;
@property (assign, nonatomic) NSInteger index;

@property (weak, nonatomic) IBOutlet UIImageView *friendImage;
@property (weak, nonatomic) IBOutlet UIScrollView *imageScrollView;
@property (weak, nonatomic) IBOutlet UIPageControl *imagePageView;
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet AddFriendButton *addFriendButton;
@property (weak, nonatomic) IBOutlet UIButton *connectButton;
@property (weak, nonatomic) IBOutlet UIImageView *connectImage;
@property (weak, nonatomic) IBOutlet UILabel *ageLabel;
@property (weak, nonatomic) IBOutlet UILabel *countryLabel;
@property (weak, nonatomic) IBOutlet UILabel *aboutMeTitleLabel;
@property (weak, nonatomic) IBOutlet UILabel *aboutMeLabel;
@property (weak, nonatomic) IBOutlet UILabel *checkinLabel;
@property (weak, nonatomic) IBOutlet UIScrollView *userPhotosScroll;
@property (weak, nonatomic) IBOutlet AddFriendButton *blockUnblockButton;
@property (weak, nonatomic) IBOutlet UIScrollView *mainScroll;
@property (weak, nonatomic) IBOutlet UIButton *placeCheckinButton;
@property (weak, nonatomic) IBOutlet UILabel *ageCountryLabel;
@property (weak, nonatomic) IBOutlet UIPageControl *imagePageControl;

@property (assign, nonatomic) BOOL isFromChat;

@property (strong, nonatomic) Place *currentPlace;
@property (weak, nonatomic) User *user;
@property (weak, nonatomic) NSString *clubCheckinName;
- (IBAction)chatAction:(id)sender;
- (IBAction)addFriendAction:(AddFriendButton *)sender;
- (IBAction)blockUnblockAction:(id)sender;
- (IBAction)imageAction:(id)sender;
- (IBAction)placeCheckinAction:(id)sender;


@end
