//
//  NewsFeedCell.h
//  Clubbook
//
//  Created by Anton Semenyuk on 1/8/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface NewsFeedSingleImageCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIImageView *avatarImageView;
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UILabel *dateLabel;
@property (weak, nonatomic) IBOutlet UITextView *messageTextView;
@property (weak, nonatomic) IBOutlet UIImageView *photoView;
@property (weak, nonatomic) IBOutlet UIView *footerView;

@end
