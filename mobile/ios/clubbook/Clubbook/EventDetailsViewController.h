//
//  EventDetailsViewController.h
//  Clubbook
//
//  Created by Anton Semenyuk on 2/13/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Event.h"
#import "BaseTableViewController.h"

@interface EventDetailsViewController : BaseTableViewController
@property (weak, nonatomic) IBOutlet UIImageView *coverPhotoView;
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UILabel *placeNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *dateLabel;
@property (weak, nonatomic) IBOutlet UILabel *timeLabel;
@property (weak, nonatomic) IBOutlet UILabel *descriptionText;
@property (weak, nonatomic) IBOutlet UIButton *reminderButton;
@property (weak, nonatomic) IBOutlet UIButton *ticketsButton;
@property (weak, nonatomic) IBOutlet UIView *shareView;
@property (weak, nonatomic) IBOutlet UIButton *locationButton;



@property (strong, nonatomic) Event* event;

@end
