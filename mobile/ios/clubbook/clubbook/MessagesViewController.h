//
//  MessagesViewController.h
//  Clubbook
//
//  Created by Andrew on 7/12/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import "BaseViewController.h"
#import "SWRevealViewController.h"
@interface MessagesViewController : BaseViewController<UITableViewDelegate, UITableViewDataSource, PNDelegate, SWRevealViewControllerDelegate>

@property (weak, nonatomic) IBOutlet UITableView *messageTable;
@property (weak, nonatomic) IBOutlet UILabel *noMesssageLabel;

@end
