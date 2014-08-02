//
//  RegViewController.h
//  Clubbook
//
//  Created by Andrew on 6/20/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BaseTableViewController.h"

@interface RegViewController : BaseTableViewController  <UIPickerViewDelegate>

@property (weak, nonatomic) IBOutlet UITextField *nameText;
@property (weak, nonatomic) IBOutlet UITextField *genderName;
@property (weak, nonatomic) IBOutlet UITextField *emailText;
@property (weak, nonatomic) IBOutlet UITextField *passText;
@property (weak, nonatomic) IBOutlet UIButton *regButton;
@property (weak, nonatomic) IBOutlet UITextField *dobText;
@property (weak, nonatomic) IBOutlet UITextField *countryText;
- (IBAction)addProfileImage:(id)sender;
@property (weak, nonatomic) IBOutlet UIButton *profileImage;
- (IBAction)registerAction:(id)sender;
@end
