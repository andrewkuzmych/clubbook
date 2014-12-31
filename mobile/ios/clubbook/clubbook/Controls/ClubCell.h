//
//  ClubCell.h
//  Clubbook
//
//  Created by Andrew on 6/23/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CbButton.h"
#import "AddFriendButton.h"

@interface ClubCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *clubNameText;
@property (weak, nonatomic) IBOutlet UIImageView *clubAvatar;
@property (weak, nonatomic) IBOutlet UILabel *distanceLabel;
@property (weak, nonatomic) IBOutlet UIImageView *userCountImage;
@property (weak, nonatomic) IBOutlet UILabel *userCountLabel;
@property (weak, nonatomic) IBOutlet UILabel *userCountLabelTitle;
@property (weak, nonatomic) IBOutlet UIImageView *friendsCountImage;
@property (weak, nonatomic) IBOutlet UILabel *friendsCountLabel;
@property (weak, nonatomic) IBOutlet UILabel *friendsCountLabelTitle;
@property (weak, nonatomic) IBOutlet UILabel *closingLabel;
@property (weak, nonatomic) IBOutlet UILabel *closingValueLabel;
@property (weak, nonatomic) IBOutlet CbButton *viewYesterdayButton;
@property (weak, nonatomic) IBOutlet CbButton *checkinButton;
@property (weak, nonatomic) IBOutlet UIImageView *arrowImage;

@end
