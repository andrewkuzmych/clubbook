//
//  MessageCell.h
//  Clubbook
//
//  Created by Andrew on 7/12/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface MessageCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIImageView *userAvatar;
@property (weak, nonatomic) IBOutlet UILabel *userName;
@property (weak, nonatomic) IBOutlet UILabel *messageLabel;
@property (weak, nonatomic) IBOutlet UILabel *unreadMsgCount;
@property (weak, nonatomic) IBOutlet UILabel *lastMessageLabel;

@end
