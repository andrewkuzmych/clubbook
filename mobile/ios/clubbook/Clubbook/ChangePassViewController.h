//
//  ChangePassViewController.h
//  Clubbook
//
//  Created by Andrew on 9/25/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BaseTableViewController.h"

@interface ChangePassViewController : BaseTableViewController
@property (weak, nonatomic) IBOutlet UITextField *currentPassText;
@property (weak, nonatomic) IBOutlet UITextField *passText;
@property (weak, nonatomic) IBOutlet UITextField *confirmNewPassText;
@property (weak, nonatomic) IBOutlet UIButton *resetPassButton;
- (IBAction)resetPassAction:(id)sender;

@end
