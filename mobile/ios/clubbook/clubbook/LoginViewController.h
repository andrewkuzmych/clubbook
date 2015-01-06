//
//  LoginViewController.h
//  Clubbook
//
//  Created by Andrew on 6/23/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BaseTableViewController.h"

@interface LoginViewController :  BaseTableViewController
@property (weak, nonatomic) IBOutlet UITextField *emailText;
@property (weak, nonatomic) IBOutlet UITextField *passText;
@property (weak, nonatomic) IBOutlet UIButton *loginButton;
@property (weak, nonatomic) IBOutlet UIButton *forgotPassButton;
- (IBAction)forgotPassAction:(id)sender;
- (IBAction)loginAction:(id)sender;

@end
